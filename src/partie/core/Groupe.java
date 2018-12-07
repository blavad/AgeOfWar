package partie.core;

import java.util.ArrayList;

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

}
