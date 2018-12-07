package lobby.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import lobby.core.Client;
import lobby.core.Partie;
import lobby.core.Parties;


/**
 * Le serveur RMI pour un serveur de gestion des parties de jeu
 * 
 * @author DHT
 * @version 1.0
 */
public class ServeurPartiesImpl extends UnicastRemoteObject implements ServeurParties {

	/**
	 * La liste des parties en attente
	 */
	private Parties parties;
	
	/**
	 * L'ensemble des joueurs connectes au serveur de partie
	 */
	private HashSet<Client> clients;

	/**
	 * Le registre RMI
	 */
	private Registry registry;
	
	
	/**
	 * Le constructeur
	 * 
	 * @param r Le registre RMI
	 */
	public ServeurPartiesImpl(Registry r) throws RemoteException {
		super();
		this.clients = new HashSet<Client>();
		this.parties = new Parties();
		this.registry = r;
		
	}
	
	/**
	 * Notifie tous les clients d'un changement dans la liste des parties (ajout, nombre de places restante,..)
	 * 
	 */
	private void notifierClients() throws RemoteException {
		for (Client cl : clients){
			try {
				ClientPartie clientDistant = (ClientPartie)registry.lookup(cl.getPseudo());
				clientDistant.notifier(this.parties.getListParties());
			} catch (RemoteException | NotBoundException e) {
				deconnect(cl.getPseudo());
			}
		}
	}
	
	/**
	 * Connecte un joueur au serveur de parties
	 * 
	 * @param pseudo le pseudo du joueur qui doit etre unique pour un meme serveur de parties
	 * @throws RemoteException
	 * @throws PseudoExistantException
	 */
	@Override
	public void connect(String pseudo) throws RemoteException, PseudoExistantException {
		for (Client client : clients){
			if (client.getPseudo().equals(pseudo))
				throw new PseudoExistantException(pseudo);
		}
		Client cl = new Client(pseudo);
		clients.add(cl);
		System.out.println("#> Nouveau client ---> " + pseudo);	
		notifierClients(); 
	}
	
	/**
	 * Deconnecte un joueur du serveur de parties
	 * 
	 * @param pseudo le pseudo du joueur qui doit etre unique pour un meme serveur de parties
	 * @throws RemoteException
	 */
	@Override
	public void deconnect(String pseudo) throws RemoteException {	
		this.parties.suppJoueur(new Client(pseudo));
		for (Client client : clients){
			if (client.getPseudo().equals(pseudo))
				clients.remove(client);
		}
		System.out.println("#> Supp client ---> " + pseudo);
		notifierClients();
	}

	/**
	 * Cree une nouvelle partie en attente de joueur
	 * 
	 * @param partie la partie à creer
	 * @throws RemoteException
	 */
	@Override
	public void creerPartie(Partie p) throws RemoteException, PartieExistanteException {
		if (this.parties.existe(p)){
			throw new PartieExistanteException(p.getName());
		}
		this.parties.getListParties().add(p);
		System.out.println("#> Nouvelle Partie ---> " + p);
		notifierClients();
	}
	
	/**
	 * Ajoute un joueur a une partie en attente
	 * 
	 * @param partie la partie à rejoindre
	 * @param client le client qui rejoint la partie
	 * @throws RemoteException
	 * @throws PartieCompleteException 
	 */
	@Override
	public void rejoindrePartie(Partie partie, Client client) throws RemoteException, PartieCompleteException, NullPointerException {
		if (this.parties.getPartie(partie).estPleine()){
			throw new PartieCompleteException();
		}
		parties.getPartie(partie).addJoueur(client);
		System.out.println("#> Mofif partie ---> " + parties.getPartie(partie));
		notifierClients();
	}
	
	/**
	 * Supprime un joueur d'une partie en attente
	 * 
	 * @param partie la partie
	 * @param client le client qui quitte la partie
	 * @throws RemoteException
	 */
	@Override
	public void quitterPartie(Partie partie, Client client) throws RemoteException {
		if (partie.getHost().equals(client)){
			parties.getListParties().remove(partie);
			System.out.println("#> Suppression partie ---> " + partie.getName());
		}
		else {
			parties.getPartie(partie).suppJoueur(client);
			System.out.println("#> Modif partie ---> " + partie.getName());
		}
		notifierClients();
	}
	
	/**
	 * Lance une partie
	 * 
	 * @param partie la partie
	 * @throws RemoteException
	 */
	@Override
	public void lancer(Partie partie) throws RemoteException {
		parties.getPartie(partie).lancer();
		parties.getListParties().remove(partie);
	}
	
	
	/**
	 * La methode main qui demarre et enregistre un service RMI pour le serveur central
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Registry registry;
			if (args.length > 0) registry = LocateRegistry.getRegistry(args[0]);
			else registry = LocateRegistry.getRegistry();
			ServeurPartiesImpl servCentral = new ServeurPartiesImpl(registry);
			registry.rebind("ServeurParties", servCentral);
			System.out.println("#> Serveur Partie lancé !!!");
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}



}
