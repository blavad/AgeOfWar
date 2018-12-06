package lobby.rmi;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import lobby.core.Client;
import lobby.core.Partie;
import lobby.ihm.DialogBox;
import lobby.ihm.FenetreClient;

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
	 * La methode main qui lance le programme client et se connecte a un serveur de parties
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Registry registry;
		ServeurParties serveur;
		DialogBox dialog = new DialogBox();
		
		String pseudo = dialog.infoPlayer("Pseudo :");
		try {
			if (args.length > 0)
				registry = LocateRegistry.getRegistry(args[0]);
			else 
				registry = LocateRegistry.getRegistry();
			
			serveur = (ServeurParties)registry.lookup("ServeurParties");
			ClientPartieImpl client = new ClientPartieImpl(pseudo, serveur);
			registry.rebind(pseudo, client);	
			serveur.connect(pseudo);
			client.client.getFenetre().showFenetre();
		} catch (RemoteException|NotBoundException e1) {
			DialogBox.error(null, "Serveur injoignable");
		} catch (PseudoExistantException e) {
			DialogBox.error(null, e.getMessage());
		}
	}

}
