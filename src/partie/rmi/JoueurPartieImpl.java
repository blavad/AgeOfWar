package partie.rmi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import javax.swing.ImageIcon;

import partie.core.Armee;
import partie.core.Groupe;
import partie.core.TypeDefence;
import partie.core.TypeUnite;
import partie.core.UniteXmlLoader;
import partie.core.VarPartie;
import partie.core.Vect2;
import partie.ihm.InterfacePartie;
import partie.ihm.InterfacePartie.Menu;

public class JoueurPartieImpl {
	
	private ServeurPartie serveur;
	private UniteXmlLoader uniteXmlLoader;
	
	private int argent;
	private int camp;
	private int groupeSelectioner;
	private InterfacePartie interfaceP;

	private int widthP = VarPartie.WIDTH_PARTIE;
	private int heightP = VarPartie.HEIGHT_PARTIE;
	
	private Image plateau;
	
	/**
	 * Constructeur de JoueurPartieImpl<li>
	 * 	Initialise l'argent du joueur et son camp<li>
	 * 	Lance l'interface graphique
	 * @param serv
	 * 				Le pointeur sur le serveur de partie
	 * @param camp
	 * 				Le camp du joueur
	 */
	public JoueurPartieImpl(ServeurPartie serv, int camp) {
		this.argent = 1000;
		this.camp = camp;
		this.serveur = serv;
		this.plateau = new ImageIcon(getClass().getResource("/space2.jpeg")).getImage();
		this.uniteXmlLoader = new UniteXmlLoader();
		this.interfaceP = new InterfacePartie(this);
		selectionneGroupe(1); // Initialise le groupe s�lectionn� � 1
	}
	
	public int getArgent() { return this.argent; }
	public int getGroupeSelect() { return this.groupeSelectioner; }
	public int getCamp() { return this.camp; }
	public UniteXmlLoader getUniteXmlLoader() { return this.uniteXmlLoader; }
	
	/**
	 * Cr�er une unit� lorque le bouton associ� est press�<li>
	 * 	V�rifie avant la cr�ation si le joueur a assez d'argent.<li> 
	 * 	Si la cr�ation est possible, enl�ve la somme d'argent au joueur
	 * et demande au serveur de cr�er cette unit� 
	 * @param typeU
	 * 				Le type d'unit� � cr�er
	 */
	public void creerUnite(TypeUnite typeU) {
		// Recherche le cout de l'unit� cr�e avec le typeU
		int cout = uniteXmlLoader.getCout(typeU);
		
		if (prendreArgent(cout)) {
			// Si prendreArgent retourne vrai => le transfert a pu se faire 
			// On signale donc au serveur de cr�er une nouvelle unit� 
			this.serveur.ajouterUnite(camp, typeU, groupeSelectioner);
		}
	}
	
	/**
	 * Cr�er une d�fence lorsque le bouton associ� est press�<li>
	 * 	V�rifie avant la cr�ation si le joueur a assez d'argent.<li> 
	 * 	Si la cr�ation est possible, enl�ve la somme d'argent au joueur
	 * et demande au serveur de cr�er cette d�fence 
	 * @param menu
	 * 				Permet de savoir sur quel emplacement cr�er la d�fence
	 * @param type
	 * 				Type de d�fence � cr�er
	 */
	public void creerDefence(Menu menu, TypeDefence typeD) {
		if (!serveur.aDefence(camp, menu)) {
			int cout = uniteXmlLoader.getCout(typeD);
			
			if (prendreArgent(cout)) {
				// Si prendreArgent retourne vrai => le transfert a pu se faire 
				// On signale donc au serveur de cr�er une nouvelle unit� 
				this.serveur.ajouterDefence(camp, typeD, menu);
			}
		}
	}
	
	/**
	 * Vend la d�fence s�lectionn�e<li>
	 * 	Si l'emplacement de d�fence s�lectionn� n'est pas vide, 
	 * demande au serveur d'enlever cette d�fence 
	 * et ajoute l'argent de la vente au joueur
	 * @param menu
	 */
	public void vendreDefence(Menu menu) {
		this.serveur.supprimerDefence(camp, menu);
	}
	
	/**
	 * Met � jour le groupe s�lectionn�
	 * @param i
	 * 			nouveau groupe s�lectionn�
	 */
	public void selectionneGroupe(int i) {
		// Change le groupe s�lectionn�
		this.groupeSelectioner = i;
		// Pr�vient l'interface que le groupe s�lectionn� a chang�
		interfaceP.changementGroupe();
	}
	
	/**
	 * Affiche toutes les entit�s sur le plateau<li>
	 *  Calcule d'abord le ratio : dimension fen�tre / taille jeu<li>
	 *  Dessine le plateau<li>
	 *  Dessine l'objectif du groupe s�lectionn�<li>
	 *  Dessine toutes le entit�s
	 * @param g
	 * 				Graphics
	 * @param entites
	 * 				HashMap avec toutes les arm�es
	 */
	private void draw(Graphics g, HashMap<Integer, Armee> entites) {
		
		// Calcule le ratio taille de la fenetre / taille fixe du jeu
		Vect2 ratios = new Vect2((float)interfaceP.getCenterPan().getWidth() / widthP, (float)interfaceP.getCenterPan().getHeight() / heightP);
		
		float ratioMin, ratioMax;
		int longueur; // longueur du c�t� du plateau (carr�) => longueur = min(widthFenetre, heightFenetre)
		Vect2 offSet = new Vect2(); // Permet de centrer les Entites sur le c�t� le plus long
		
		int longueurFond;
		Vect2 offSetFond = new Vect2();
		if (ratios.x < ratios.y) {
			// Si la fen�tre est plus haute que longue (width < height)
			ratioMin = ratios.x;
			longueur = interfaceP.getCenterPan().getWidth(); // longueur prend la largeur de la fenetre
			offSet.setPosY(((float)interfaceP.getCenterPan().getHeight() - longueur) / 2);
			
			ratioMax = ratios.y;
			longueurFond = interfaceP.getCenterPan().getHeight();
			offSetFond.setPosX(((float)interfaceP.getCenterPan().getWidth() - longueurFond) / 2);
		} else {
			ratioMin = ratios.y;
			longueur = interfaceP.getCenterPan().getHeight(); // longueur prend la hauteur de la fen�tre
			offSet.setPosX(((float)interfaceP.getCenterPan().getWidth() - longueur) / 2);
			
			ratioMax = ratios.x;
			longueurFond = interfaceP.getCenterPan().getWidth();
			offSetFond.setPosY(((float)interfaceP.getCenterPan().getHeight() - longueurFond) / 2);
		}
		
		// Dessine le plateau
		g.drawImage(plateau, (int)offSetFond.x, (int)offSetFond.y, longueurFond, longueurFond, null);
		
		
		// Parcourt toutes arm�es et dessine les unit�s 
		for (Integer i : entites.keySet()) {
			Armee a = entites.get(i);
			
			try {
				a.draw(g, ratioMin, offSet);
			} catch (ConcurrentModificationException e) {
				
			}
		}
		
		// Dessine un point noir qui repr�sente l'ojectif du groupe selection�
		drawObjectifSelect(g, entites.get(camp).getGroupes().get(groupeSelectioner - 1), ratioMin, offSet);
	}
	
	/**
	 * Dessine l'objectif du groupe s�lectionn�
	 * @param g
	 * 			Graphics
	 * @param grp
	 * 			Groupe s�lectionn�
	 * @param ratio
	 * 			Ratio d'affichage
	 * @param offSet
	 * 			Vect2 : d�calage en x et y
	 */
	private void drawObjectifSelect(Graphics g, Groupe grp, float ratio, Vect2 offSet) {
		float rayon = VarPartie.RAYON_OBJECTIF * ratio; // Calcule le rayon du cercle � afficher en fonction du ratio
		
		int posX = (int)offSet.x + (int)Math.floor(grp.getObjectif().x * ratio - rayon);
		int posY = (int)offSet.y + (int)Math.floor(grp.getObjectif().y * ratio - rayon);
		int r = (int)(rayon * 2);
		
		g.setColor(Color.RED);
		g.fillOval(posX, posY, r, r);
	}
	
	/**
	 * M�thode appel�e par le serveur qui met � jour les donn�es des arm�es
	 * @param entites
	 * 				Les arm�es de tous les joueurs
	 */
	public void update(HashMap<Integer, Armee> entites) {
		
		this.draw(interfaceP.getCenterPan().getGraphics(), entites);
	}
	
	/**
	 * Donne de l'argent au joueur<li>
	 * 	Ajoute l'argent<li>
	 * 	notifie l'interface graphique du changement
	 * @param arg
	 * 			Somme � ajouter
	 */
	public void ajouterArgent(int arg) {
		this.argent += arg; // Rajoute l'argent � la banque du joueur
		//signale � l'interface graphique que l'argent du joueur � �t� modifi�
		this.interfaceP.changementArgent();
	}
	
	/**
	 * Retire de l'argent au joueur
	 * @param arg
	 * 			Somme � enlever
	 * @return vrai si la somme a pu �tre enlev�e et faux si le joueur n'a pas assez d'argent
	 */
	private boolean prendreArgent(int arg) {
		//Retourne faux si le transfert n'a pas pu se faire et vrai sinon
		int a = this.argent - arg;
		if (a < 0) {
			return false;
		}
		else {
			this.argent = a;
			//signale � l'interface graphique que l'argent du joueur � �t� modifi�
			this.interfaceP.changementArgent();
			return true;
		}
	}

	
}
