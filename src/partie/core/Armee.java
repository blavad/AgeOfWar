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
	 * Met � jour toutes les unit�s de l'arm�e
	 * @param dt
	 * 			Temps depuis la derniere mis � jour
	 * @param entites
	 * 			Ensemble des entit�s du jeu
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
	 * Dessine toutes les unites de l'arm�e
	 * @param g
	 * 			Graphics
	 * @param ratio
	 * 			Ratio d'affichage
	 * @param offSet
	 * 			Vect2 : d�calage en x et y
	 */
	public void draw(Graphics g, float ratio, Vect2 offSet) {
		// On v�rifie si la base existe
		if (this.getBase() != null) {
			// On dessine la base sur le plateau
			this.getBase().draw(g, ratio, offSet);
		}
		for (int j = 0; j < this.getGroupes().size(); j++) {
			this.getGroupes().get(j).draw(g, ratio, offSet);
		}
	}
	
	/**
	 * Supprime l'unit� en param�tre de la liste des unit�s du serveur
	 * @param u
	 * 				L'unit� � supprimer
	 */
	public void supprimerUnite(Unite u) {
		// Parcourt tous les groupes et supprimes U lorsque celle-ci est trouv�e
		for (Groupe g : this.getGroupes()) {
			g.getUnites().remove(u);
		}
	}
}
