package partie.core;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import partie.rmi.JoueurPartieImpl;

public class Armee {
	
	private ArrayList<Groupe> groupes;
	private Base base;
	
	public Armee () {
		this.groupes = new ArrayList<Groupe>();
		this.groupes.add(new Groupe());
		this.groupes.add(new Groupe());
		this.groupes.add(new Groupe());
	}
	
	
	public ArrayList<Groupe> getGroupes() { return this.groupes; }
	public Entite getBase() { return this.base; }
	
	
	public void setBase(Base b) {
		this.base = b;
	}
	public void addGroupe(Groupe g) {
		this.groupes.add(g);
	}
	
	/**
	 * Met à jour toutes les unités de l'armée
	 * @param dt
	 * 			Temps depuis la derniere mis à jour
	 * @param entites
	 * 			Ensemble des entités du jeu
	 * @param joueurs
	 * 			Ensemble des joueurs
	 */
	public void update (float dt, HashMap<Integer, Armee> entites, HashMap<Integer, JoueurPartieImpl> joueurs) {
		if (base != null) this.base.update(dt, entites, joueurs);
		for (int j = 0; j < this.getGroupes().size(); j++) {
			this.getGroupes().get(j).update(dt, entites, joueurs);
		}
	}
	
	/**
	 * Dessine toutes les unites de l'armée
	 * @param g
	 * 			Graphics
	 * @param ratio
	 * 			Ratio d'affichage
	 * @param offSet
	 * 			Vect2 : décalage en x et y
	 */
	public void draw(Graphics g, float ratio, Vect2 offSet) {
		// On vérifie si la base existe
		if (this.getBase() != null) {
			// On dessine la base sur le plateau
			this.getBase().draw(g, ratio, offSet);
		}
		for (int j = 0; j < this.getGroupes().size(); j++) {
			this.getGroupes().get(j).draw(g, ratio, offSet);
		}
	}
	
	/**
	 * Supprime l'unité en paramètre de la liste des unités du serveur
	 * @param u
	 * 				L'unité à supprimer
	 */
	public void supprimerUnite(Unite u) {
		// Parcourt tous les groupes et supprimes U lorsque celle-ci est trouvée
		for (Groupe g : this.getGroupes()) {
			g.getUnites().remove(u);
		}
	}
}
