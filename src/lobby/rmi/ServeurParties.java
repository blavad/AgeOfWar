package lobby.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import lobby.core.Client;
import lobby.core.Partie;

/**
 * L'interface RMI pour un serveur de gestion des parties de jeu
 * 
 * @author DHT
 * @version 1.0
 */
public interface ServeurParties extends Remote {
	
	/**
	 * Connecte un joueur au serveur de parties
	 * 
	 * @param pseudo le pseudo du joueur qui doit etre unique pour un meme serveur de parties
	 * @throws RemoteException
	 * @throws PseudoExistantException
	 */
	public void connect(String pseudo) throws RemoteException, PseudoExistantException;
	
	/**
	 * Deconnecte un joueur du serveur de parties
	 * 
	 * @param pseudo le pseudo du joueur qui doit etre unique pour un meme serveur de parties
	 * @throws RemoteException
	 */
	public void deconnect(String pseudo) throws RemoteException;
	
	/**
	 * Cree une nouvelle partie en attente de joueur
	 * 
	 * @param partie la partie à creer
	 * @throws RemoteException
	 */
	public void creerPartie(Partie p) throws RemoteException, PartieExistanteException;
	
	/**
	 * Ajoute un joueur a une partie en attente
	 * 
	 * @param partie la partie à rejoindre
	 * @param client le client qui rejoint la partie
	 * @throws RemoteException
	 */
	public void rejoindrePartie(Partie partie, Client client) throws RemoteException, PartieCompleteException, NullPointerException;

	/**
	 * Supprime un joueur d'une partie en attente
	 * 
	 * @param partie la partie
	 * @param client le client qui quitte la partie
	 * @throws RemoteException
	 */
	public void quitterPartie(Partie partie, Client client) throws RemoteException;

	/**
	 * Lance une partie
	 * 
	 * @param partie la partie
	 * @throws RemoteException
	 */
	public void lancer(Partie partie) throws RemoteException; 
	
}
