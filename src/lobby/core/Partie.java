package lobby.core;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Classe qui représente une partie en attente 
 * 
 * @author DHT
 * @version 1.0
 */
public class Partie implements Serializable {

	/** L'hote de la partie */
	private Client hote;
	
	/** Les autres joueurs */
	private ArrayList<Client> clients = new ArrayList<Client>();
	
	/** Le nom de la partie et son mot de passe en cas de partie privee*/
	private String nom, mdp = null;
	
	/** Le nombre de joueur pour demarrer la partie */
	private Integer max_joueur;

	/** Variable qui indique si la partie est privee ou non */
	private Boolean privee = false;
	
	/**
	 * Le constructeur
	 * 
	 * @param h L'hote de la partie
	 * @param n Le nom de la partie
	 * @param max Le nombre de joueur maximum accepte pour jouer
	 * 
	 */
	public Partie(Client h, String n, Integer max){
		this.hote = h;
		this.nom = n;
		this.max_joueur = max;
	}
	
	/**
	 * Le constructeur
	 * 
	 * @param h L'hote de la partie
	 * @param n Le nom de la partie
	 * @param mdp Le mot de passe de la partie
	 * @param max Le nombre de joueur maximum accepte pour jouer
	 * 
	 */
	public Partie(Client h, String n, String mdp, Integer max){
		this.hote = h;
		this.nom = n;
		this.mdp = mdp;
		this.max_joueur = max;
		this.privee = true;
	}
	
	/** Ajoute un joueur à la partie 
	 * 
	 * @param c Le joueur a ajouter
	 */
	public void addJoueur(Client c){
		this.clients.add(c);
	}
	
	/** Supprime un joueur à la partie 
	 * 
	 * @param c Le joueur a supprimer
	 */
	public void suppJoueur(Client c){
		this.clients.remove(c);
	}
	
	/** Verifie si le mot de passe est correct
	 * 
	 * @param m mot de passe à vérifier
	 * @return vrai si le mor de passe est correct
	 */
	public boolean verifierMDP(String m){
		return m.equals(this.mdp);
	}
	
	
	/** 
	 * @return vrai si le nombre de place disponible est nul
	 */
	public Boolean estPleine(){
		return getNbJoueur()+1==getNbMaxJoueur();
	}
	
	public boolean estPrivee(){
		return this.mdp != null;
	}
	
	/** Lance la partie */
	public void lancer() {
		
	}

	
	/**
	 * @return le nombre de joueur maximal
	 */
	public String getName(){
		return this.nom;
	}
	
	/**
	 * @return le nombre de joueur actuel
	 */
	public Integer getNbJoueur(){
		return clients.size();
	}
	
	/**
	 * @return le nombre de joueur maximal
	 */
	public Integer getNbMaxJoueur(){
		return this.max_joueur;
	}
	
	/**
	 * @return l'hote de la partie
	 */
	public Client getHost() {
		return this.hote;
	}
	
	/**
	 * @return les clients (hors hote) de la partie
	 */
	public ArrayList<Client> getClients() {
		return this.clients;
	}

	@Override
	public boolean equals(Object o){
		return this.getName().equals(((Partie)o).getName());
	}
	
	@Override
	public String toString(){
		String lock = "";
		lock = (privee) ?"LOCK":"";
		return nom + " -- " + (clients.size()+1) +"/"+ max_joueur + " "+ lock;
	}

}
