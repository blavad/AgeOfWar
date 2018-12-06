package lobby.core;

import java.io.Serializable;

import lobby.ihm.FenetreClient;
import lobby.rmi.ServeurParties;

public class Client implements Serializable {

	/**
	 * Le pseudo du joueur
	 */
	private String pseudo;
	
	/**
	 * La partie du joueur
	 */
	private Partie maPartie;
	
	/**
	 * La fenetre du joueur
	 */
	public FenetreClient fenetreConnexion;
	
	public Client(String pseudo){
		this.pseudo = pseudo;
	}
	
	public Client(ServeurParties serveur, String pseudo){
		this.pseudo = pseudo;
		this.fenetreConnexion = new FenetreClient(this, serveur);
	}

	/**
	 * Getter du pseudo du joueur
	 * @return String
	 */
	public String getPseudo(){
		return this.pseudo;
	}
	
	/**
	 * Getter de la partie du joueur
	 * @return Partie la partie du joueur
	 */
	public Partie getPartie(){
		return this.maPartie;
	}
	
	/**
	 * Getter de la fenetre du joueur
	 * @return String
	 */
	public FenetreClient getFenetre(){
		return this.fenetreConnexion;
	}
	
	/**
	 * Setter de la partie du joueur
	 * @param p la partie du joueur
	 */
	public void setPartie(Partie p){
		this.maPartie = p;
	}

	@Override
	public boolean equals(Object o){
		if (o == null) return false;
		else {
			if (o.getClass() == Client.class)
				return ((Client)o).getPseudo().equals(this.pseudo);
			else return false;
		}
	}

	@Override
	public String toString(){
		return this.pseudo;
	}
}
