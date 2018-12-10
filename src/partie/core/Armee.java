package partie.core;

import java.util.ArrayList;
import java.util.HashMap;

import partie.rmi.JoueurPartieImpl;

public class Armee {
	
	private ArrayList<Groupe> groupes;
	private Entite base;
	
	public Armee () {
		this.groupes = new ArrayList<Groupe>();
		this.groupes.add(new Groupe());
		this.groupes.add(new Groupe());
		this.groupes.add(new Groupe());
	}
	
	
	public ArrayList<Groupe> getGroupes() { return this.groupes; }
	public Entite getBase() { return this.base; }
	
	
	public void setBase(Entite e) {
		this.base = e;
	}
	public void addGroupe(Groupe g) {
		this.groupes.add(g);
	}
	
	
	public void update (float dt, HashMap<Integer, Armee> entites, HashMap<Integer, JoueurPartieImpl> joueurs) {
		for (int j = 0; j < this.getGroupes().size(); j++) {
			this.getGroupes().get(j).update(dt, entites, joueurs);
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
