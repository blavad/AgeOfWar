package lobby.ihm;

import java.awt.BorderLayout;
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
	
	JLabel text;
	
	JButton quitter;

	/** La liste des parties */
	private JList<Client> jl_clients;
	
	public FenetrePartie(FenetreClient parent){
		super();
		this.parent = parent; 
		this.partie = this.parent.client.getPartie();
		initComponent();
	}

	/**
	 * Initialisation des composants graphique fixe
	 */
	public void initComponent() {
		
		this.setLayout(new BorderLayout());
		this.setBackground(FenetreClient.COLOR_BACKGROUND);
		
		text = new JLabel();
		text.setFont(new Font("Serif", Font.BOLD, 18));
		text.setForeground(FenetreClient.COLOR_TEXT);
		text.setBackground(FenetreClient.COLOR_BACKGROUND);
		
		jl_clients = new JList<Client>(new DefaultListModel<Client>());
	    jl_clients.setBorder(BorderFactory.createTitledBorder("Autre joueurs"));

		quitter = new JButton("Quitter Partie");
		quitter.addActionListener(new QuitterPartieControleur(this));
		
		updateComponent();

		this.add(text, BorderLayout.NORTH);
		this.add(new JScrollPane(jl_clients), BorderLayout.CENTER);
		this.add(quitter, BorderLayout.SOUTH);
	}
	
	/**
	 * Mise a jour des composants graphiques variables
	 */
	public void updateComponent() {
		if (parent.client.getPartie() != null ){
			this.text.setText("Ma partie : "+ parent.client.getPartie().getName());
			
			DefaultListModel<Client> model = (DefaultListModel<Client>) this.jl_clients.getModel();
			model.clear();
			model.addElement(parent.client.getPartie().getHost());
			for (Client cl : parent.client.getPartie().getClients()){
				model.addElement(cl);
			}
			quitter.setEnabled(true);
		}
		else {
			this.text.setText("Ma partie : ???");
			DefaultListModel<Client> model = (DefaultListModel<Client>) this.jl_clients.getModel();
			model.clear();
			quitter.setEnabled(false);
		}
	}

	/** Mettre a jour l'affichage de la liste des parties
	 * 
	 * @param lparties la liste des parties
	 */
	public void updatePartie(ArrayList<Partie> lparties) {
		if (parent.client.getPartie() != null){
			for (Partie p : lparties){
				if (p.equals(this.parent.client.getPartie())){
					DefaultListModel<Client> model = (DefaultListModel<Client>) this.jl_clients.getModel();
					model.clear();
					model.addElement(p.getHost());
					for (Client cl : p.getClients()){
						model.addElement(cl);
					}
				}
			}
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
