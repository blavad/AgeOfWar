package partie.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingWorker;

import lobby.core.Client;
import lobby.core.Partie;
import partie.core.Armee;
import partie.core.Base;
import partie.core.Defense;
import partie.core.Entite;
import partie.core.Groupe;
import partie.core.Outils;
import partie.core.TypeUnite;
import partie.core.Unite;
import partie.core.UniteXmlLoader;
import partie.core.VarPartie;
import partie.core.Vect2;
import partie.ihm.InterfacePartie.Menu;

public class ServeurPartieImpl extends UnicastRemoteObject implements ServeurPartie  {
	
	private Partie partie;
	private Registry registry;
	private HashMap<Integer, Armee> entites;
	private HashMap<Integer, JoueurPartie> joueurs;
	//private Thread bouclePartie;
	private boolean finPartie;
	private UniteXmlLoader uniteXmlLoader;
	private int widthP = VarPartie.WIDTH_PARTIE;
	private int heightP = VarPartie.HEIGHT_PARTIE;
	
	/**
	 * Constructeur du serveur<li>
	 * Il lance l'initialisationeet et la boucle du jeu
	 */
	public ServeurPartieImpl(Partie partie) throws RemoteException {
		super();
		register();
		this.partie = partie;
	}
	
	/**
	 * Enregistre le serveur sur le registre
	 */
	private void register() {
		try {
			this.registry = LocateRegistry.getRegistry();
			registry.rebind("hote", this);
			System.out.println("hote bound");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialise la partie<li>
	 * 	Creer les differents HashMap<li>
	 * 	Creer les joueurs<li>
	 * 	Initialise les armees et l'objectif de chaque groupe
	 */
	private void initialiserPartie() {
		// Initialisation des HashMap
		joueurs = new HashMap<Integer, JoueurPartie>();
		entites = new HashMap<Integer, Armee>();
		uniteXmlLoader = new UniteXmlLoader();
		ArrayList<Client> clients = partie.getClients();
		addJoueur(partie.getHost(),1);
		int i=2;
		for (Client c : clients) {
			addJoueur(c,i);
			System.out.println(c.toString());
			i++;
		}
		
		
	}
	
	private void addJoueur(Client client, int camp) {
		float rayon = (widthP / 2) * 0.8f;
		Vect2 offSet = new Vect2(widthP / 2, heightP / 2);
		try {
			Registry clientReg = LocateRegistry.getRegistry(client.getIp(), 1099);
			joueurs.put(camp, (JoueurPartie) clientReg.lookup("joueur " + camp));
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		float angle = (float)(Math.PI * 2 * (camp - 1)) / partie.getNbMaxJoueur();
		Armee a = new Armee((new Vect2(offSet.x + (float)Math.sin(angle) * rayon, offSet.y + (float)Math.cos(angle) * rayon)), camp);
		entites.put(camp, a);
	}
	
	public void suppJoueurImpl(int camp) {
		if (camp == 1) {
			for (Integer i : joueurs.keySet()) {
				try {
					if (i != 1) {
						//joueurs.get(i).quitterPartie();
						joueurs.get(i).decoForcee();
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		joueurs.remove(camp);
		entites.get(camp).getBase().tuer();
		entites.get(camp).supprimerToutesUnites();
	}
	
	/**
	 * Lance la boucle du jeu<li>
	 *  Gere les deplacements des unites<li>
	 *  Envoie aux joueurs la liste des armees pour que ces derniers puissent les afficher
	 */
	
	public void bouclePartie() {
		finPartie = false;
		long dt = 0;
		long previousTime = System.currentTimeMillis();
		long currentTime;
		float FPSLIMIT = 40;
		float LIMITEUR = 1000/FPSLIMIT;
		
		while (!finPartie) {
			
			currentTime = System.currentTimeMillis();
			dt += currentTime - previousTime;
			// Permet de gerer la freuence de calcul
			if (dt > LIMITEUR) { 
				
				// Met a jour tous les groupes un par un
				for (Integer i : entites.keySet()) {
					if (!entites.get(i).getBase().estMorte())
						entites.get(i).update(dt, entites, joueurs);
				}
				
				for (Integer i : joueurs.keySet()) {
					try {
						joueurs.get(i).update(entites);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				
				
				dt = 0;
			}
		
			previousTime = currentTime;
			
			finPartie = (nbrSurvivant() == 1);
		}
		
		if (nbrSurvivant() == 1) {
			int gagnant = joueurGagnant();
			for (Integer i : joueurs.keySet()) {
				try {
					joueurs.get(i).finPartie(gagnant);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private int nbrSurvivant() {
		int joueurVivant = 0;
		
		for (Integer i : entites.keySet()) {
			if (!entites.get(i).getBase().estMorte())
				joueurVivant++;
		}
		
		return joueurVivant;
	}
	
	private int joueurGagnant() {
		int joueurGagnant = 0;
		
		for (Integer i : entites.keySet()) {
			if (!entites.get(i).getBase().estMorte())
				joueurGagnant = i;
		}
		
		return joueurGagnant;
	}
	
	private class BoucleWorker extends SwingWorker<Void,Void>{
		@Override
		public Void doInBackground() {
			bouclePartie();
			return null;
		}
	}
	
	public void initPartie() {
		initialiserPartie();
		System.out.println("initPartie");
		try {
			joueurs.get(1).pret();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void startPartie() {
		for (Integer j : joueurs.keySet()) {
			try {
				joueurs.get(j).start();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		BoucleWorker boucleWorker = new BoucleWorker();
		boucleWorker.execute();
	}
	/**
	 * Creer une unite selon typeU et la place dans le bon camp et le grp selectionne par le joueur
	 * @param camp int : Le camp du joueur
	 * @param typeU TypeUnite : Le tupe d'unite creee
	 * @param grpSelect int : Le groupe selectionne par le joueur lors de la creation de l'unite
	 */
	public void ajouterUnite(int camp, TypeUnite typeU, int grpSelect) {
	
		Unite u = uniteXmlLoader.createUnite(typeU, camp, entites.get(camp).getBase().getPosition());
		entites.get(camp).ajouterUnite(grpSelect - 1, u); // (grpSelect - 1) car grpSelect commence a  1 (et les listes a 0)

	}
	/**
	 * Creer une defense selon typeU et la place dans le bon camp et le grp selectionne par le joueur
	 * @param camp int : Le camp du joueur
	 * @param typeU TypeUnite : Le tupe d'unite creee
	 * @param menu TypeMenu : Emplacement de defense selectionne
	 */
	public void ajouterDefence(int camp, TypeUnite typeU, Menu menu) {
		
		Defense d = (Defense)uniteXmlLoader.createUnite(typeU, camp, entites.get(camp).getBase().getPosition());
		entites.get(camp).ajouterDefense(menu, d);
		
	}
	/**
	 * Supprime la defense selectionne
	 */
	public void supprimerDefence(int camp, Menu menu) {
		Defense d = entites.get(camp).getBase().getDefence(menu); 
		if (d != null) {
			try {
				joueurs.get(camp).ajouterArgent((int)Math.floor(d.getCout() * VarPartie.REMBOURSEMENT_DEFENSE));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			entites.get(camp).supprimerDef(menu);
		}
	}
	
	
	/**
	 * Renvoie vrai si une defense est equipee a l'emplacement selectionne et faux sinon
	 */
	public boolean aDefence(int camp, Menu menu) {
		return (entites.get(camp).getBase().getDefence(menu) != null);
	}
	/**
	 * Change la position de l'objectif du groupe selectionne
	 * @param camp int : camp du groupe selectionne
	 * @param grpSelect int : groupe selectionne
	 * @param pos Vect2 : nouvelle position de l'objectif
	 */
	public void changeObjectifGroupe(int camp, int grpSelect, Vect2 pos) {
		entites.get(camp).getGroupes().get(grpSelect - 1).setObjectif(pos);
	}
	
	public HashMap<Integer, Armee> getEntites(){
		return this.entites;
	}
	
	public static void main(String[] args) {
		try {
			//on cree le registre directement au bon endroit pour les tests
			Registry registry = LocateRegistry.createRegistry(1099);
			// les joueurs sont ajoutes par serveurPartie pour respecter l'ordre d'enregistrement sur le registre
			//new ServeurPartieImpl();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	

}
