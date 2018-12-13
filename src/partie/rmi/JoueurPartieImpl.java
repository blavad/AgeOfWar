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
		selectionneGroupe(1); // Initialise le groupe sélectionné à 1
	}
	
	public int getArgent() { return this.argent; }
	public int getGroupeSelect() { return this.groupeSelectioner; }
	public int getCamp() { return this.camp; }
	public UniteXmlLoader getUniteXmlLoader() { return this.uniteXmlLoader; }
	
	/**
	 * Créer une unité lorque le bouton associé est pressé<li>
	 * 	Vérifie avant la création si le joueur a assez d'argent.<li> 
	 * 	Si la création est possible, enlève la somme d'argent au joueur
	 * et demande au serveur de créer cette unité 
	 * @param typeU
	 * 				Le type d'unité à créer
	 */
	public void creerUnite(TypeUnite typeU) {
		// Recherche le cout de l'unité crée avec le typeU
		int cout = uniteXmlLoader.getCout(typeU);
		
		if (prendreArgent(cout)) {
			// Si prendreArgent retourne vrai => le transfert a pu se faire 
			// On signale donc au serveur de créer une nouvelle unité 
			this.serveur.ajouterUnite(camp, typeU, groupeSelectioner);
		}
	}
	
	/**
	 * Créer une défence lorsque le bouton associé est pressé<li>
	 * 	Vérifie avant la création si le joueur a assez d'argent.<li> 
	 * 	Si la création est possible, enlève la somme d'argent au joueur
	 * et demande au serveur de créer cette défence 
	 * @param menu
	 * 				Permet de savoir sur quel emplacement créer la défence
	 * @param type
	 * 				Type de défence à créer
	 */
	public void creerDefence(Menu menu, TypeDefence typeD) {
		if (!serveur.aDefence(camp, menu)) {
			int cout = uniteXmlLoader.getCout(typeD);
			
			if (prendreArgent(cout)) {
				// Si prendreArgent retourne vrai => le transfert a pu se faire 
				// On signale donc au serveur de créer une nouvelle unité 
				this.serveur.ajouterDefence(camp, typeD, menu);
			}
		}
	}
	
	/**
	 * Vend la défence sélectionnée<li>
	 * 	Si l'emplacement de défence sélectionné n'est pas vide, 
	 * demande au serveur d'enlever cette défence 
	 * et ajoute l'argent de la vente au joueur
	 * @param menu
	 */
	public void vendreDefence(Menu menu) {
		this.serveur.supprimerDefence(camp, menu);
	}
	
	/**
	 * Met à jour le groupe sélectionné
	 * @param i
	 * 			nouveau groupe sélectionné
	 */
	public void selectionneGroupe(int i) {
		// Change le groupe sélectionné
		this.groupeSelectioner = i;
		// Prévient l'interface que le groupe sélectionné a changé
		interfaceP.changementGroupe();
	}
	
	/**
	 * Affiche toutes les entités sur le plateau<li>
	 *  Calcule d'abord le ratio : dimension fenêtre / taille jeu<li>
	 *  Dessine le plateau<li>
	 *  Dessine l'objectif du groupe sélectionné<li>
	 *  Dessine toutes le entités
	 * @param g
	 * 				Graphics
	 * @param entites
	 * 				HashMap avec toutes les armées
	 */
	private void draw(Graphics g, HashMap<Integer, Armee> entites) {
		
		// Calcule le ratio taille de la fenetre / taille fixe du jeu
		Vect2 ratios = new Vect2((float)interfaceP.getCenterPan().getWidth() / widthP, (float)interfaceP.getCenterPan().getHeight() / heightP);
		
		float ratioMin, ratioMax;
		int longueur; // longueur du côté du plateau (carré) => longueur = min(widthFenetre, heightFenetre)
		Vect2 offSet = new Vect2(); // Permet de centrer les Entites sur le côté le plus long
		
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
		
		
		// Parcourt toutes armées et dessine les unités 
		for (Integer i : entites.keySet()) {
			Armee a = entites.get(i);
			
			try {
				a.draw(g, ratioMin, offSet);
			} catch (ConcurrentModificationException e) {
				
			}
		}
		
		// Dessine un point noir qui représente l'ojectif du groupe selectioné
		drawObjectifSelect(g, entites.get(camp).getGroupes().get(groupeSelectioner - 1), ratioMin, offSet);
	}
	
	/**
	 * Dessine l'objectif du groupe sélectionné
	 * @param g
	 * 			Graphics
	 * @param grp
	 * 			Groupe sélectionné
	 * @param ratio
	 * 			Ratio d'affichage
	 * @param offSet
	 * 			Vect2 : décalage en x et y
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
	 * Méthode appelée par le serveur qui met à jour les données des armées
	 * @param entites
	 * 				Les armées de tous les joueurs
	 */
	public void update(HashMap<Integer, Armee> entites) {
		
		this.draw(interfaceP.getCenterPan().getGraphics(), entites);
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
		//signale à l'interface graphique que l'argent du joueur à été modifié
		this.interfaceP.changementArgent();
	}
	
	/**
	 * Retire de l'argent au joueur
	 * @param arg
	 * 			Somme à enlever
	 * @return vrai si la somme a pu étre enlevée et faux si le joueur n'a pas assez d'argent
	 */
	private boolean prendreArgent(int arg) {
		//Retourne faux si le transfert n'a pas pu se faire et vrai sinon
		int a = this.argent - arg;
		if (a < 0) {
			return false;
		}
		else {
			this.argent = a;
			//signale à l'interface graphique que l'argent du joueur à été modifié
			this.interfaceP.changementArgent();
			return true;
		}
	}

	
}
