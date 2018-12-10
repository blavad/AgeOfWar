package partie.core;

import java.util.ArrayList;
import java.util.HashMap;

import partie.rmi.JoueurPartieImpl;

public class Groupe {
	
	private ArrayList<Unite> unites;
	private Vect2 objectif;
	
	public Groupe () {
		objectif = new Vect2();
		unites = new ArrayList<Unite>();
	}
	public Groupe (Vect2 pos) {
		objectif = new Vect2(pos.x, pos.y);
		unites = new ArrayList<Unite>();
	}
	

	public void setObjectif(Vect2 v) { this.objectif.x = v.x; this.objectif.y = v.y; }
	public Vect2 getObjectif() { return this.objectif; }
	public ArrayList<Unite> getUnites() { return this.unites; }
	
	
	public void addUnite(Unite u) {
		this.unites.add(u);
	}
	public void suppUnite(Unite u) {
		int i = this.unites.indexOf(u);
		this.unites.remove(i);
	}
	
	/**
	 * Met à jour un groupe en calculant ce que fait chaque unité
	 * @param grp
	 * 				groupe à mettre à jour
	 * @param dt
	 * 				temps depuis la dernière maj
	 */
	public void update(float dt, HashMap<Integer, Armee> entites, HashMap<Integer, JoueurPartieImpl> joueurs) {
		
		// Si le groupe n'est pas vide
		if (unites.size() > 0) {
			unites.get(0).update(dt, true, this, entites, joueurs); // update l'unité (cooldown, ...)
			
			
			// On parcourt le reste du groupe
			for (int i = 1; i < this.getUnites().size(); i++) {
				// U pointe sur la (i + 1)e unité du groupe
				this.getUnites().get(i).update(dt, false, this, entites, joueurs); // update l'unite (cooldown, ...)
				
			}
			
		}
		
		
	}
	
	

}
