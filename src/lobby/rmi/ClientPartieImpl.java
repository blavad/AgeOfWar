package lobby.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
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
	Registry localReg;
	
	/**
	 * Le constructeur
	 * 
	 * @param p Le pseudo du joueur
	 * @param serveur Le serveur avec lequel communique le joueur
	 */
	public ClientPartieImpl(String pseudo, ServeurParties serveur) throws RemoteException{
		super();
		this.client = new Client(serveur, pseudo);
		try {
			localReg = LocateRegistry.createRegistry(1099);
		} catch (ExportException e) {
			System.out.println("registre deja existant");
		}
		localReg = LocateRegistry.getRegistry(1099);
		localReg.rebind(pseudo, this);
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
	public void creerPartie(Partie partie, int camp) throws RemoteException {
		Client hote = partie.getHost();
		this.client.fenetreConnexion.setVisible(false);
		Registry localReg = LocateRegistry.getRegistry(1099);
		if (this.client.getPseudo().equals(hote.getPseudo())){
			new ServeurPartieImpl(partie);
 		}
		ServeurPartie serveur;
		String ip = hote.getIp();
		Registry hoteRegistry = LocateRegistry.getRegistry(ip,1099);
		try {
			serveur = (ServeurPartie)hoteRegistry.lookup("hote");
			JoueurPartieImpl jeuClient = new JoueurPartieImpl(serveur, camp);
			localReg.rebind("joueur "+camp, jeuClient);
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		
	}
	

	public void lancerPartie() {
		try {
			Registry hoteReg = LocateRegistry.getRegistry();
			ServeurPartie serveur = (ServeurPartie) hoteReg.lookup("hote");
			serveur.startPartie();
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public Client getClient() {
		return this.client;
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
			// Connection au serveur grace a une IP
			String ip = DialogBox.infoPlayer(null, "IP Serveur :");
			//String ip = "172.18.16.226";
			if (args.length > 0)
				registry = LocateRegistry.getRegistry(args[0]);
			else 
				registry = LocateRegistry.getRegistry(ip);
			serveur = (ServeurParties)registry.lookup("ServeurParties");
			
			// Creation du client avec son pseudo
			String pseudo = DialogBox.infoPlayer(null, "Pseudo :");
			ClientPartieImpl clientPartie = new ClientPartieImpl(pseudo, serveur);
			
			//registry.rebind(pseudo, client);
			serveur.connect(clientPartie.getClient());
			
			// Affiche la fenetre apres avoir connecter le joueur et correctement au serveur
			clientPartie.client.getFenetre().showFenetre();
		} catch (RemoteException|NotBoundException e1)  {
			DialogBox.error(null, "Serveur injoignable");
		} catch (PseudoExistantException e) {
			DialogBox.error(null, e.getMessage());
		} catch (NullPointerException e) {
			DialogBox.info(null, "Triste de savoir que vous nous quittez déjà... ","smiley_triste_150x150.png");
		}
	}

}
