package lobby.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import lobby.core.Client;
import lobby.core.Partie;
import lobby.ihm.DialogBox;

import partie.rmi.*;

/**
 * Le client RMI qui communique avec le serveur central pour avoir accès à la liste de parties
 * 
 * @author DHT
 * @version 1.0
 */
public class ClientPartieImpl extends UnicastRemoteObject implements ClientPartie {

	/**
	 * Les données du joueur
	 */
	Client client;
	
	
	/**
	 * Le constructeur
	 * 
	 * @param p Le pseudo du joueur
	 * @param serveur Le serveur avec lequel communique le joueur
	 */
	public ClientPartieImpl(String pseudo, ServeurParties serveur) throws RemoteException {
		super();
		this.client = new Client(serveur, pseudo);
	}

	/**
	 * Notifie le clients de modifications dans la liste de partie
	 * 
	 */
	@Override
	public void notifier(ArrayList<Partie> lparties) throws RemoteException {
		this.client.getFenetre().updateParties(lparties);
	}
	
	/**
	 * Lance la partie
	 * @param hote le pseudo du joueur hote de la partie
	 * 
	 */
	@Override
	public void lancerPartie(String hote, Integer camp) throws RemoteException {
		this.client.fenetreConnexion.setVisible(false);
		if (this.client.getPseudo().equals(hote)){
			ServeurPartieImpl jeuHote = new ServeurPartieImpl();
 		}
		else {
			Registry registry;
			ServeurPartie serveur;
			
			registry = LocateRegistry.getRegistry();
			try {
				serveur = (ServeurPartie)registry.lookup(hote);
				JoueurPartieImpl jeuClient = new JoueurPartieImpl(serveur, camp);
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
		}
	}
	

	
	
	/**
	 * La methode main qui lance le programme client et se connecte a un serveur de parties
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Registry registry;
		ServeurParties serveur;
		try {
			String pseudo = DialogBox.infoPlayer(null, "Pseudo :");
			if (args.length > 0)
				registry = LocateRegistry.getRegistry(args[0]);
			else 
				registry = LocateRegistry.getRegistry();
			
			serveur = (ServeurParties)registry.lookup("ServeurParties");
			ClientPartieImpl client = new ClientPartieImpl(pseudo, serveur);
			registry.rebind(pseudo, client);	
			serveur.connect(pseudo);
			client.client.getFenetre().showFenetre();
		} catch (RemoteException|NotBoundException e1)  {
			DialogBox.error(null, "Serveur injoignable");
		} catch (PseudoExistantException e) {
			DialogBox.error(null, e.getMessage());
		} catch (NullPointerException e) {
			DialogBox.info(null, "Triste de savoir que vous nous quittez déjà... ","smiley_triste_150x150.png");
		}
	}

}
