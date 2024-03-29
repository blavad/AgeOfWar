package lobby.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import lobby.core.Client;
import lobby.core.Partie;
import lobby.rmi.PartieCompleteException;
import lobby.rmi.PartieExistanteException;
import lobby.rmi.ServeurParties;

/** Classe de gestion du choix de la partie
 * 
 * @author DHT
 *
 */
public class FenetreChoixPartie extends JPanel {

	/** Fenetre parent */
	FenetreClient parent;

	/** Le panel qui gere l'affichage des parties */
	PanelParties pan_parties;
	
	JLabel text;
	
	/** Les boutons d'action */
	JButton creerP = new JButton("Creer Partie"), joindreP = new JButton("Rejoindre Partie");
	
	
	/** Constructeur de la fenetre du client de parties
	 * 
	 * @param client le joueur associe
	 * @param serveur le serveur de parties
	 * 
	 */
	public FenetreChoixPartie(FenetreClient parent, Client client, ServeurParties serveur){
		super();
		this.parent = parent;
		
		initComponent();
	}
	
	/** Initialise les composants de la fenetre
	 * 
	 */
	private void initComponent() {
		
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(400,350));
		this.setBackground(FenetreClient.COLOR_BACKGROUND);
		
		text = new JLabel("  Parties en attente");
		text.setFont(new Font("Serif", Font.BOLD, 18));
		text.setForeground(FenetreClient.COLOR_TEXT);
		text.setBorder(BorderFactory.createLineBorder(FenetreClient.COLOR_CONTOUR,2));
	    
		// Creation du panel de liste de parties
		pan_parties = new PanelParties();
		
		// Creation de la liste boutons 
		JPanel panelBoutton = new JPanel();
		panelBoutton.setLayout(new FlowLayout());
		
		creerP.setBackground(FenetreClient.COLOR_BACKGROUND);
		creerP.setForeground(FenetreClient.COLOR_TEXT);
		creerP.addActionListener(new NouvellePartieControleur(this));
		panelBoutton.add(creerP);
		
		joindreP.setBackground(FenetreClient.COLOR_BACKGROUND);
		joindreP.setForeground(FenetreClient.COLOR_TEXT);
		joindreP.addActionListener(new JoindrePartieControleur(this));
		panelBoutton.add(joindreP);
		

		this.add(text, BorderLayout.NORTH);
		this.add(new JScrollPane(pan_parties.getJListPartie()), BorderLayout.CENTER);
		this.add(panelBoutton,BorderLayout.SOUTH);	

		this.setBackground(FenetreClient.COLOR_BACKGROUND);
	}
	
	/** Mets a jour les liste des parties */
	public void updateParties(ArrayList<Partie> lparties) {
		this.pan_parties.updatePartie(lparties);
	}
	
}

/** Controleur du bouton de creation de parties
 * 
 * @author DHT
 * @version 1.0
 *
 */
class NouvellePartieControleur implements ActionListener {
	
	/** La fenetre d'appel */
	private FenetreChoixPartie fenetre;
	
	/** Constructeur du controleur
	 * 
	 * @param f la fenetre liee au controleur 
	 * 
	 *  */
	public NouvellePartieControleur(FenetreChoixPartie f){
		this.fenetre =f;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DialogPartie dialogP = new DialogPartie(this.fenetre.parent, "Creer nouvelle partie", true, fenetre.parent.client);
		Partie p = dialogP.showDialogPartie();
		if (p!= null){
			try {
				fenetre.parent.serveur.creerPartie(p);
				fenetre.creerP.setEnabled(false);
				fenetre.joindreP.setEnabled(false);
				fenetre.parent.client.setPartie(p);
				fenetre.parent.fen_partie.updateComponent();
			} catch (RemoteException e1) {
				DialogBox.error(fenetre.parent,"Connection serveur interrompue ! ");
			} catch (PartieExistanteException e2) {
				DialogBox.error(fenetre.parent,e2.getMessage());
			}
		}
	}
}

/** Controleur du bouton pour rejoindre une partie
 * 
 * @author DHT
 * @version 1.0
 *
 */
class JoindrePartieControleur implements ActionListener {
	
	/** La fenetre d'appel */
	private FenetreChoixPartie fenetre;
	
	/** Constructeur du controleur
	 * 
	 * @param f la fenetre liee au controleur 
	 * 
	 */
	public JoindrePartieControleur(FenetreChoixPartie f){
		this.fenetre =f;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Partie p = this.fenetre.pan_parties.getJListPartie().getSelectedValue();
			String mdp;
			boolean is_autorized = true;
			if (p.estPrivee()){
				mdp = DialogBox.infoPlayer(null, "Mot de passe");
				is_autorized = p.verifierMDP(mdp);
			}
			if (is_autorized){
				fenetre.parent.serveur.rejoindrePartie(p , this.fenetre.parent.client);
				fenetre.creerP.setEnabled(false);
				fenetre.joindreP.setEnabled(false);
				fenetre.parent.client.setPartie(p);
				fenetre.parent.fen_partie.updateComponent();
			} else {
				DialogBox.error(this.fenetre.parent, "Mot de passe incorrect");
			}
		} catch (RemoteException e1) {
			DialogBox.error(fenetre.parent,"Connection serveur interrompue ! ");
		} catch (PartieCompleteException e2){
			DialogBox.error(fenetre.parent,e2.getMessage());
		} catch (NullPointerException e3){
			DialogBox.error(fenetre.parent,"Il faut selectionner une partie avant de la rejoindre !");
		}
	}
}
