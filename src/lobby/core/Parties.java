package lobby.core;

import java.util.ArrayList;

import lobby.rmi.ClientPartie;
import lobby.rmi.PartieExistanteException;

public class Parties {
	/**
	 * La liste des parties en attente
	 */
	private ArrayList<Partie> parties;
	
	public Parties(){
		this.parties = new ArrayList<Partie>();
	}

	public ArrayList<Partie> getListParties() {
		return this.parties;
	}
	
	public Partie getPartie(Partie p){
		for (Partie partie : this.parties){
			if (p.equals(partie)){
				return partie;
			}
		}
		return null;
	}
	
	public boolean existe(Partie p){
		for (int i = 0; i< parties.size(); i++){
			if (p.getName().equals(parties.get(i).getName())){
				return true;
			}
		}
		return false;
	}

	public void suppJoueur(Client clientPartie) {
		for (Partie partie : this.parties){
			partie.suppJoueur(clientPartie);
		}
	}
}
