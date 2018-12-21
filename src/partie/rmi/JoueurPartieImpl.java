package partie.rmi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import javax.swing.ImageIcon;

import partie.core.Armee;
import partie.core.Groupe;
import partie.core.Images;
import partie.core.Outils;
import partie.core.TypeUnite;
import partie.core.UniteXmlLoader;
import partie.core.VarPartie;
import partie.core.Vect2;
import partie.ihm.FenetreDeco;
import partie.ihm.InterfacePartie;
import partie.ihm.InterfacePartie.Menu;
import sun.awt.WindowClosingListener;

public class JoueurPartieImpl extends UnicastRemoteObject implements JoueurPartie {
	
	private ServeurPartie serveur;
	private Registry registry;
	private UniteXmlLoader uniteXmlLoader;
	
	private Thread boucleAffichage;
	private boolean finPartie = false;
	private int argent;
	private int camp;
	private int groupeSelectioner;
	private InterfacePartie interfaceP;
	private HashMap<Integer, Armee> entites;
	private boolean estMort;

	private int widthP = VarPartie.WIDTH_PARTIE;
	private int heightP = VarPartie.HEIGHT_PARTIE;
	
	private Image plateau;
	public Images images;
	
	/**
	 * Constructeur de JoueurPartieImpl<li>
	 * 	Initialise l'argent du joueur et son camp<li>
	 * 	Lance l'interface graphique
	 * @param serv Registry : Le pointeur sur le serveur de partie
	 * @param camp int : Le camp du joueur
	 * @throws RemoteException 
	 */
	public JoueurPartieImpl(ServeurPartie serveur, int camp) throws RemoteException {
		super();
		System.out.println("Constrution joueur " + camp);
		this.argent = 1000;
		this.camp = camp;
		this.estMort = false;
		this.images = new Images();
		this.serveur = serveur;
		this.plateau = new ImageIcon(getClass().getResource("/space2.jpeg")).getImage();
		this.uniteXmlLoader = new UniteXmlLoader();
		this.interfaceP = new InterfacePartie(this);
		this.interfaceP.addWindowListener(new FermetureFenetre(this));
		selectionneGroupe(1); // Initialise le groupe selectionne à 1
		entites = new HashMap<Integer, Armee>();

		boucleAffichage = new Thread(new BoucleAffichage(this));
		
	}
	
	public int getArgent() { return this.argent; }
	public int getGroupeSelect() { return this.groupeSelectioner; }
	public int getCamp() { return this.camp; }
	public UniteXmlLoader getUniteXmlLoader() { return this.uniteXmlLoader; }
	public Images getImages() { return this.images; }
	public boolean estMort() { return this.estMort; }
	public void setFinPartie(boolean b) { this.finPartie = b; }
	public ServeurPartie getServeur() { return this.serveur; }
	
	/**
	 * Met la variable estMort du joueur a true
	 */
	public void meurt() { 
		this.estMort = true; 
	}
	/**
	 * Affiche le bouton pret sur l'interface
	 */
	public void pret() {
		this.interfaceP.pret();
	}
	
	/**
	 * Creer une unite lorque le bouton associe est presse<li>
	 * 	Regarde si c'est une defense ou une unite cree<li>
	 * 	Verifie avant la creation si le joueur a assez d'argent.<li> 
	 * 	Si la creation est possible, enlève la somme d'argent au joueur
	 * et demande au serveur de creer cette unite<li>
	 * Notifie l'interface graphique du changement
	 * @param menu Menu : Permet de determiner l'emplacement de defense selectionne s'il faut creer une defense
	 * @param typeU TypeUnite : Le type d'unite à creer
	 */
	public void creerUnite(Menu menu, TypeUnite typeU) { 
		if (!estMort) {
			try {
				if (Outils.estDefense(typeU)) {
					if (!serveur.aDefence(camp, menu)) {
						// Recherche le cout de l'unite cree avec le typeU
						int cout = uniteXmlLoader.getCout(typeU);
						if (prendreArgent(cout)) {
							// Si prendreArgent retourne vrai => le transfert a pu se faire 
							// On signale donc au serveur de creer une nouvelle unite 
							this.serveur.ajouterDefence(camp, typeU, menu);
							this.interfaceP.changementDefense(menu, cout);					}
					}
				} else {
					int cout = uniteXmlLoader.getCout(typeU);
					if (prendreArgent(cout)) {
						this.serveur.ajouterUnite(camp, typeU, groupeSelectioner);
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Vend la defence selectionnee<li>
	 * 	Si l'emplacement de defence selectionne n'est pas vide, 
	 * demande au serveur d'enlever cette defence 
	 * et ajoute l'argent de la vente au joueur<li>
	 * Notifie l'interface graphique du changement
	 * @param menu Menu : permet de savoir la defence selectionnee
	 */
	public void vendreDefence(Menu menu) {
		if (!estMort) {
			try {
				this.serveur.supprimerDefence(camp, menu);
				this.interfaceP.changementDefense(menu, 0);	
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Met a jour le groupe selectionne
	 * @param i int : nouveau groupe selectionne
	 */
	public void selectionneGroupe(int i) {
		if (!estMort) {
			// Change le groupe selectionne
			this.groupeSelectioner = i;
			// Previent l'interface que le groupe selectionne a change
			interfaceP.changementGroupe();
		}
	}
	
	/**
	 * Change la position de l'objectif du groupe selectionne
	 * @param pos Vect2 : nouvelle position
	 */
	public void changeObjectifGroupe(int x, int y) {
		if (!estMort) {
			Vect2 ratios = new Vect2((float)interfaceP.getCenterPan().getWidth() / widthP, (float)interfaceP.getCenterPan().getHeight() / heightP);
			Vect2 pos = new Vect2((float)Math.floor(x / ratios.x), (float)Math.floor(y / ratios.y));
			try {
				serveur.changeObjectifGroupe(camp, groupeSelectioner, pos);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Fonction appelee lorsque le joueur appuie sur la croix rouge de la fenetre<li>
	 *  Le supprime du serveur<li>
	 *  Arrete la boucle d'affichage
	 *  Arrete le programme
	 */
	public void quitterPartie() {
		try {
			serveur.suppJoueurImpl(camp);
			finPartie = true;
			System.out.println("quitte");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Fonction appelee lorsque l'hote ferme sa partie<li>
	 *  Supprime le joueur du serveur<li>
	 *  Arrete la boucle d'affichage<li>
	 *  Ferme l'interface de la partie<li>
	 *  Lance la fenetre de deconnection
	 */
	public void decoForcee() {
		System.out.println("deco");
		try {
			serveur.suppJoueurImpl(camp);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		finPartie = true;
		interfaceP.dispose();
		new FenetreDeco();
	}
	/**
	 * Fonction appelee lorsque la partie est fini<li>
	 *  Arrete la boucle d'affichage<li>
	 *  Demande a l'interface d'afficher le nom du gagnant
	 */
	public void finPartie(int joueurGagnant) {
		finPartie = true;
		interfaceP.finPartie(joueurGagnant);
	}
	
	/**
	 * Affiche toutes les entites sur le plateau<li>
	 *  Calcule d'abord le ratio : dimension fenêtre / taille jeu<li>
	 *  Dessine le plateau<li>
	 *  Dessine l'objectif du groupe selectionne<li>
	 *  Dessine toutes le entites
	 * @param g Graphics
	 * @param entites HashMap<Integer, Armee> : HashMap avec toutes les armees
	 */
	private void draw(Graphics g, HashMap<Integer, Armee> entites) {
		
		// Calcule le ratio taille de la fenetre / taille fixe du jeu
		Vect2 ratios = new Vect2((float)interfaceP.getCenterPan().getWidth() / widthP, (float)interfaceP.getCenterPan().getHeight() / heightP);
		
		float ratioMin, ratioMax;
		int longueur; // longueur du côte du plateau (carre) => longueur = min(widthFenetre, heightFenetre)
		Vect2 offSet = new Vect2(); // Permet de centrer les Entites sur le côte le plus long
		
		int longueurFond;
		Vect2 offSetFond = new Vect2();
		if (ratios.x < ratios.y) {
			// Si la fenêtre est plus haute que longue (width < height)
			ratioMin = ratios.x;
			longueur = interfaceP.getCenterPan().getWidth(); // longueur prend la largeur de la fenetre
			offSet.setPosY(((float)interfaceP.getCenterPan().getHeight() - longueur) / 2);
			
			ratioMax = ratios.y;
			longueurFond = interfaceP.getCenterPan().getHeight();
			offSetFond.setPosX(((float)interfaceP.getCenterPan().getWidth() - longueurFond) / 2);
		} else {
			ratioMin = ratios.y;
			longueur = interfaceP.getCenterPan().getHeight(); // longueur prend la hauteur de la fenêtre
			offSet.setPosX(((float)interfaceP.getCenterPan().getWidth() - longueur) / 2);
			
			ratioMax = ratios.x;
			longueurFond = interfaceP.getCenterPan().getWidth();
			offSetFond.setPosY(((float)interfaceP.getCenterPan().getHeight() - longueurFond) / 2);
		}
		
		// Dessine le plateau
		g.drawImage(plateau, (int)offSetFond.x, (int)offSetFond.y, longueurFond, longueurFond, null);
		
		
		// Parcourt toutes armees et dessine les unites 
		for (Integer i : entites.keySet()) {
			Armee a = entites.get(i);
			if (!a.getBase().estMorte()) {
				try {
					a.draw(g, ratioMin, offSet, images, camp);
				} catch (ConcurrentModificationException e) {
					
				}
			}
		}
		
		if (!estMort) {
			// Dessine un point noir qui represente l'ojectif du groupe selectione
			drawObjectifSelect(g, entites.get(camp).getGroupes().get(groupeSelectioner - 1), ratioMin, offSet);
		}
	}
	
	/**
	 * Dessine l'objectif du groupe selectionne
	 * @param g Graphics
	 * @param grp Groupe : Groupe selectionne
	 * @param ratio float : Ratio d'affichage
	 * @param offSet Vect2 : Decalage en x et y
	 */
	private void drawObjectifSelect(Graphics g, Groupe grp, float ratio, Vect2 offSet) {
		float rayon = VarPartie.RAYON_OBJECTIF * ratio; // Calcule le rayon du cercle à afficher en fonction du ratio
		
		int posX = (int)offSet.x + (int)Math.floor(grp.getObjectif().x * ratio - rayon);
		int posY = (int)offSet.y + (int)Math.floor(grp.getObjectif().y * ratio - rayon);
		int r = (int)(rayon * 2);
		
		g.setColor(Color.RED);
		g.fillOval(posX, posY, r, r);
	}
	
	/**
	 * Methode appelee par le serveur qui met a jour les donnees des armees
	 * @param entites HashMap<Integer, Armee> : Les armees de tous les joueurs
	 */

	public void update(HashMap<Integer, Armee> entites) {
		this.entites = entites;
	}
	
	
	
	/**
	 * Donne de l'argent au joueur<li>
	 * 	Ajoute l'argent<li>
	 * 	notifie l'interface graphique du changement
	 * @param arg
	 * 			Somme à ajouter
	 */
	public void ajouterArgent(int arg) {
		this.argent += arg; // Rajoute l'argent à la banque du joueur
		//signale à l'interface graphique que l'argent du joueur à ete modifie
		this.interfaceP.changementArgent();
	}
	
	/**
	 * Retire de l'argent au joueur
	 * @param arg int : Somme à enlever
	 * @return vrai si la somme a pu etre enlevee et faux si le joueur n'a pas assez d'argent
	 */
	private boolean prendreArgent(int arg) {
		//Retourne faux si le transfert n'a pas pu se faire et vrai sinon
		int a = this.argent - arg;
		if (a < 0) {
			return false;
		}
		else {
			this.argent = a;
			//signale à l'interface graphique que l'argent du joueur à ete modifie
			this.interfaceP.changementArgent();
			return true;
		}
	}
	
	public void start() {
		interfaceP.muteMusique();
		boucleAffichage.start();
	}
	
	
	public void boucleAffichage() {
		long dt = 0;
		long previousTime = System.currentTimeMillis();
		long currentTime;
		float FPSLIMIT = 30;
		float LIMITEUR = 1000/FPSLIMIT;
		

		while (!finPartie) {
			currentTime = System.currentTimeMillis();
			dt += currentTime - previousTime;
			//System.out.println(dt);
			
			// Permet de gerer la freuence de calcul
			if (dt > LIMITEUR && entites.values().size() > 0) { 
				this.draw(interfaceP.getCenterPan().getGraphics(), entites);
				
				dt = 0;
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			previousTime = currentTime;
			
		}
		
	}

	private class BoucleAffichage implements Runnable {
		
		private JoueurPartieImpl joueur;
		
		public BoucleAffichage(JoueurPartieImpl joueur) {
			this.joueur = joueur;
		}

		@Override
		public void run() {
			joueur.setFinPartie(false);
			joueur.boucleAffichage();
		}
		
	}
	
	private class FermetureFenetre implements WindowListener {

		JoueurPartieImpl joueur;
		
		public FermetureFenetre(JoueurPartieImpl joueur) {
			this.joueur = joueur;
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			joueur.quitterPartie();
			
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
