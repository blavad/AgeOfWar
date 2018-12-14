package lobby.ihm;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;


import lobby.core.Client;
import lobby.core.Partie;
import lobby.rmi.ServeurParties;
import lobby.rmi.SuppressionPartieException;

/** La fenetre du choix de partie du client
 * 
 * @author DHT
 * @version 1.0
 *
 */

public class FenetreClient extends JFrame {
	
	public static Color COLOR_BACKGROUND = new Color(50, 50, 220);
	public static Color COLOR_TEXT= new Color(220, 220, 220);
	public static Color COLOR_CONTOUR= new Color(240, 240, 240);

	
	/** Le joueur associe a la fenetre */
	Client client;

	/** Le serveur de parties */
	ServeurParties serveur;

	/** Fenetre de choix de partie */
	FenetreChoixPartie fen_choix_partie = null;
	
	/** Fenetre de partie */
	FenetrePartie fen_partie = null;
	
	private String pathIcon = "/icon.jpg";
	
	/** Constructeur de la fenetre du client de parties
	 * 
	 * @param client le joueur associe
	 * @param serveur le serveur de parties
	 * 
	 */
	public FenetreClient(Client client, ServeurParties serveur){
		this.client = client;
		this.serveur = serveur;
		this.setIconImage(new ImageIcon(getClass().getResource(pathIcon)).getImage());
		
		this.setTitle("Age Of War - Choix Partie");
		this.addWindowListener(new WindowClose(this));	 //ferme le programme et déconnecte du serveur quand on ferme la fenetre
		this.setLocationRelativeTo(null); //place la fenetre au milieu de l'ecran
		
		this.fen_choix_partie = new FenetreChoixPartie(this, client, serveur);
		this.fen_partie = new FenetrePartie(this);
		
		this.getContentPane().setLayout(new GridLayout(1, 2));
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
			this.fenetre.serveur.deconnect(this.fenetre.client);
		} catch (RemoteException e) {
		} catch (SuppressionPartieException e) {
			DialogBox.error(fenetre, "Vous devez commencer par quitter la partie");
		}
		System.exit(0);
	}
}