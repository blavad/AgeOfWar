package lobby.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import lobby.core.Client;
import lobby.core.Partie;
import lobby.rmi.SuppressionPartieException;

/** Classe du panel de gestion de la partie
 * 
 * @author DHT
 *
 */
public class FenetrePartie extends JPanel {
	
	/** Fenetre parent */
	FenetreClient parent;
	
	Partie partie;
	
	/** Text au nord du layout*/
	JLabel text;
	
	/** Nom de l'hote qui doit apparaitre en grand */
	JLabel nomHote;
	
	/** Bouton pour quitter la partie */
	JButton quitter;

	/** La liste des parties */
	private JList<Client> jl_clients;
	
	
	/** Constructeur
	 * 
	 * @param parent
	 */
	public FenetrePartie(FenetreClient parent){
		super();
		this.parent = parent; 
		this.partie = this.parent.client.getPartie();
		initComponent();
	}

	/**
	 * Initialisation des composants graphique fixe
	 */
	private void initComponent() {
		
		// Parametres de la fenetre
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(400,350));
		this.setBackground(FenetreClient.COLOR_BACKGROUND);
		
		// Initialisation du texte affichant le nom de la partie
		text = new JLabel();
		text.setFont(new Font("Serif", Font.BOLD, 18));
		text.setForeground(FenetreClient.COLOR_TEXT);
		text.setBorder(BorderFactory.createLineBorder(FenetreClient.COLOR_CONTOUR,2));
		
		// Panel du centre de la fenetre partie
		JPanel panel_centre = new JPanel();
		panel_centre.setLayout(new BorderLayout());

		// Initialisation du JLabel affichant l'hote
		nomHote = new JLabel();
		nomHote.setFont(new Font("Serif", Font.BOLD, 18));
		nomHote.setForeground(new Color(255,50,50));
		panel_centre.add(nomHote, BorderLayout.NORTH);	
		
		// Initialisation de la liste d'affichage
		jl_clients = new JList<Client>(new DefaultListModel<Client>());
	    jl_clients.setBorder(BorderFactory.createTitledBorder("  Liste joueurs"));
		panel_centre.add(new JScrollPane(jl_clients), BorderLayout.CENTER);

	    // Initialisation du bouton quitter et de son controleur
		quitter = new JButton("Quitter Partie");
		quitter.setForeground(FenetreClient.COLOR_TEXT);
		quitter.setBackground(FenetreClient.COLOR_BACKGROUND);
		quitter.addActionListener(new QuitterPartieControleur(this));
		
		// Mise en place des composants variables
		updateComponent();

		// Ajout des composants graphiques dans le panel
		this.add(text, BorderLayout.NORTH);
		this.add(panel_centre, BorderLayout.CENTER);
		this.add(quitter, BorderLayout.SOUTH);
	}
	
	/**
	 * Mise a jour des composants graphiques variables
	 */
	public void updateComponent() {
		if (parent.client.getPartie() != null ){
			this.text.setText("  Ma partie : "+ parent.client.getPartie().getName());
			this.nomHote.setText("  Hote : " + parent.client.getPartie().getHost().getPseudo());
			
			DefaultListModel<Client> model = (DefaultListModel<Client>) this.jl_clients.getModel();
			model.clear();
			for (Client cl : parent.client.getPartie().getClients()){
				model.addElement(cl);
			}
			quitter.setEnabled(true);
		}
		else {
			this.text.setText("  Ma partie : ...");
			this.nomHote.setText("");
			DefaultListModel<Client> model = (DefaultListModel<Client>) this.jl_clients.getModel();
			model.clear();
			quitter.setEnabled(false);
		}
	}

	/** Mettre a jour l'affichage de la liste des parties
	 * 
	 * @param lparties la liste des parties
	 */
	public void updatePartie(Partie p) {
			DefaultListModel<Client> model = (DefaultListModel<Client>) this.jl_clients.getModel();
			model.clear();
			for (Client cl : p.getClients()){
				if (!cl.equals(parent.client))
					model.addElement(cl);
			}
	}
}

/** Controleur du bouton pour quitter la partie
 * 
 * @author DHT
 * @version 1.0
 *
 */
class QuitterPartieControleur implements ActionListener {
	
	/** La fenetre d'appel */
	private FenetrePartie fenetre;
	
	/** Le constructeur */
	public QuitterPartieControleur(FenetrePartie fen){
		super();
		this.fenetre = fen;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		DialogBox.error(this.fenetre.parent, "Vous allez être supprimé de cette partie");
		try {
			this.fenetre.parent.serveur.quitterPartie(this.fenetre.parent.client.getPartie(), this.fenetre.parent.client);
			this.fenetre.parent.fen_choix_partie.creerP.setEnabled(true);
			this.fenetre.parent.fen_choix_partie.joindreP.setEnabled(true);
			this.fenetre.parent.client.setPartie(null);
			fenetre.updateComponent();
		} catch (RemoteException e) {
			DialogBox.error(fenetre.parent,"Connection serveur interrompue ! ");
		} catch (SuppressionPartieException e) {
			DialogBox.error(fenetre.parent, e.getMessage());
		}
	}
}
