package partie.core;

import java.util.ArrayList;

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
 	
}
