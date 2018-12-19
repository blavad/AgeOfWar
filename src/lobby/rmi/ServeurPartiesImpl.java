package lobby.rmi;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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
				Registry clientReg = LocateRegistry.getRegistry(cl.getIp(), 1099);
				ClientPartie clientDistant = (ClientPartie)clientReg.lookup(cl.getPseudo());
				clientDistant.notifier(this.parties.getListParties());
			} catch (RemoteException | NotBoundException e) {
				deconnect(cl);
				e.printStackTrace();
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
	public void connect(Client client) throws RemoteException, PseudoExistantException {
		for (Client cl : clients){
			if (cl.getPseudo().equals(client.getPseudo()))
				throw new PseudoExistantException(cl.getPseudo());
		}
		clients.add(client);
		System.out.println("#> Nouveau client ---> " + client.getPseudo() + " @ "+client.getIp());	
		notifierClients(); 
	}
	
	/**
	 * Deconnecte un joueur du serveur de parties
	 * 
	 * @param cl le client a deconnecter
	 * @throws RemoteException
	 * @throws SuppressionPartieException 
	 */
	@Override
	public void deconnect(Client cl) throws RemoteException {
		if (cl.getPartie()!= null){	
			try {
				quitterPartie(cl.getPartie(), cl);
			} catch (SuppressionPartieException e) {}
		}
		for (Client client : clients){
			if (client.equals(cl))
				clients.remove(client);
		}
		System.out.println("#> Supp client ---> " + cl.getPseudo());
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
		System.out.println("#> Modif partie ---> " + parties.getPartie(partie));
		if (parties.getPartie(partie).estPleine()){
			lancer(parties.getPartie(partie));
		}
		notifierClients();
	}
	
	/**
	 * Supprime un joueur d'une partie en attente
	 * 
	 * @param partie la partie
	 * @param client le client qui quitte la partie
	 * @throws RemoteException
	 * @throws SuppressionPartieException 
	 */
	@Override
	public void quitterPartie(Partie partie, Client client) throws RemoteException, SuppressionPartieException {
		if (partie.getHost().equals(client)){
			if (parties.getPartie(partie).getClients().size()>0){
				throw new SuppressionPartieException("Clients en attente");
			}
			else {
				parties.getListParties().remove(parties.getPartie(partie));
				System.out.println("#> Suppression partie ---> " + partie.getName());
			}
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
		Partie partieServ = parties.getPartie(partie);
		Client hote = partieServ.getHost();
		ClientPartie hoteDistant;
		
		// Lancement du jeu 
		int camp = 1;
		try {
			Registry hoteReg = LocateRegistry.getRegistry(hote.getIp(), 1099);
			hoteDistant = (ClientPartie)hoteReg.lookup(hote.getPseudo());
			hoteDistant.creerPartie(partieServ,camp++);
			for (Client cl : parties.getPartie(partie).getClients()){
				Registry clientReg = LocateRegistry.getRegistry(cl.getIp(), 1099);
				ClientPartie clientDistant = (ClientPartie)clientReg.lookup(cl.getPseudo());
				clientDistant.creerPartie(partieServ,camp++);
			}
			hoteDistant.lancerPartie();
		} catch (NotBoundException | RemoteException e1) {
			e1.printStackTrace();
		}
		
		// Suppression des joueurs et de la partie
		for (Client cl : partieServ.getClients()){
			deconnect(cl);
		}
		deconnect(partieServ.getHost());
		parties.getListParties().remove(parties.getPartie(partie));
	}
	
	/**
	 * La methode main qui demarre et enregistre un service RMI pour le serveur central
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Registry registry = LocateRegistry.createRegistry(1099);
			ServeurPartiesImpl servCentral = new ServeurPartiesImpl(registry);
			registry.rebind("ServeurParties", servCentral);
			System.out.println("#> Serveur Partie lance !!!");
			try(final DatagramSocket socket = new DatagramSocket()){
				  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
				  String ip = socket.getLocalAddress().getHostAddress();
				  System.out.println("@ "+ip);
				} catch (SocketException | UnknownHostException e) {
					e.printStackTrace();
				}
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}



}
