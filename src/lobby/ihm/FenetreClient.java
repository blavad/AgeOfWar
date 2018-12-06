package lobby.ihm;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JFrame;


import lobby.core.Client;
import lobby.core.Partie;
import lobby.rmi.ServeurParties;

/** La fenetre du choix de partie du client
 * 
 * @author DHT
 * @version 1.0
 *
 */

public class FenetreClient extends JFrame {
	
	public static Color COLOR_BACKGROUND = new Color(120, 220, 120);
	public static Color COLOR_TEXT= new Color(120, 60, 70);
	public static Color COLOR_CONTOUR= new Color(255, 255, 255);

	
	/** Le joueur associe a la fenetre */
	Client client;

	/** Le serveur de parties */
	ServeurParties serveur;

	/** Fenetre de choix de partie */
	FenetreChoixPartie fen_choix_partie = null;
	
	/** Fenetre de partie */
	FenetrePartie fen_partie = null;
	
	/** Constructeur de la fenetre du client de parties
	 * 
	 * @param client le joueur associe
	 * @param serveur le serveur de parties
	 * 
	 */
	public FenetreClient(Client client, ServeurParties serveur){
		super("Age Of War - Choix Partie");
		this.client = client;
		this.serveur = serveur;
		this.fen_choix_partie = new FenetreChoixPartie(this, client, serveur);
		this.fen_partie = new FenetrePartie(this);
		
		// Deconnection totale en cas d'arret du programme
		this.addWindowListener(new WindowClose(this));	
		this.getContentPane().setLayout(new GridLayout(1, 2));
		initComponent();
	}

	/** Initialise les composants de la fenetre
	 * 
	 */
	private void initComponent() {
		this.getContentPane().add(this.fen_choix_partie);
		this.getContentPane().add(this.fen_partie);
	}

	/** Affiche la fenetre */
	public void showFenetre(){
		this.pack();
		this.setVisible(true);
	}

	/** Mets a jour les liste des parties */
	public void updateParties(ArrayList<Partie> lparties) {
		this.fen_choix_partie.updateParties(lparties);
		this.fen_partie.updatePartie(lparties);	
		
	}	
}


/** Controleur fermeture de fentre
 * 
 * @author DHT
 * @version 1.0
 *
 */
class WindowClose extends WindowAdapter {
	
	/** La fenetre d'appel */
	private FenetreClient fenetre;
	
	/** Le constructeur */
	public WindowClose(FenetreClient fen){
		super();
		this.fenetre = fen;
	}
	
	@Override
	public void windowClosing(WindowEvent we) {
		try {
			this.fenetre.serveur.deconnect(this.fenetre.client.getPseudo());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}