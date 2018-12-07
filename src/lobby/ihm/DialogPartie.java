package lobby.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

import lobby.core.Client;
import lobby.core.Partie;

/** La boite de dialogue pour choisir les parametres de la nouvelle partie
 * 
 * @author DHT
 * @version 1.0
 *
 */
public class DialogPartie extends JDialog {
	
	/** Nom de la partie */
	JTextField nom, mdp;

	/** Nombre de joueurs */
	JComboBox<String> nb_joueur;
	
	/** Option de mot de passe */
	JCheckBox checkMDP;

	/** Image */
	JLabel icon;

	/** Verifie l'envoie des donnees*/
	boolean sendData;

	/** La partie creee */
	Partie partie = null;

	/** L'hote de la partie  */
	Client client;
	
	/**
	 * Le constructeur
	 * 
	 * @param parent la fenetre parent 
	 * @param title le titre de la boite de dialogue
	 * @param modal vrai si la boite de dialogue est modal
	 * @param client le joueur à l'origine de la nouvelle partie
	 */
	public DialogPartie(JFrame parent, String title, boolean modal, Client client) {
		super(parent,title, modal);
		this.client = client;
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.initComponent();
	}
	
	/** Initialise les composants graphiques de la boite de dialogue
	 * 
	 */
	public void initComponent(){
		
		//Icône
	    icon = new JLabel(new ImageIcon("res/age-of-war.jpg"));
	    JPanel panIcon = new JPanel();
	    panIcon.setLayout(new BorderLayout());
	    panIcon.add(icon);
		
		// Panel nom de partie
	    JPanel panNom = new JPanel();
	    panNom.setBorder(BorderFactory.createTitledBorder("Nom de la partie"));
	    nom = new JTextField();
	    nom.setPreferredSize(new Dimension(100, 25));
	    panNom.add(new JLabel("Nom :"));
	    panNom.add(nom);
	    
	    // Panel nombre de joueurs
	    JPanel panNBJ = new JPanel();
	    panNBJ.setBorder(BorderFactory.createTitledBorder("Nombre de joueur"));
	    nb_joueur = new JComboBox();
	    nb_joueur.addItem("2");
	    nb_joueur.addItem("3");
	    nb_joueur.addItem("4");
	    nb_joueur.addItem("5");
	    nb_joueur.addItem("6");
	    panNBJ.add(new JLabel("Nombre : "));
	    panNBJ.add(nb_joueur);
	    
	    // Panel mot de passe
	    JPanel panMDP = new JPanel();
	    checkMDP = new JCheckBox("Privée");
	    checkMDP.addChangeListener(new ControleurChangement(this));
	    mdp = new JTextField();
	    mdp.setPreferredSize(new Dimension(100, 25));
	    panMDP.setBorder(BorderFactory.createTitledBorder("Partie privée"));
	    panMDP.add(checkMDP);
	    panMDP.add(mdp);
	    
	    JPanel infos = new JPanel();
	    infos.setLayout(new GridLayout(3, 1));
	    infos.add(panNom);
	    infos.add(panNBJ);
	    infos.add(panMDP);
	    
	    // Control des des boutons
	    JPanel control = new JPanel();
	    JButton creerB = new JButton("Creer");
	    creerB.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent arg0) {  
	    	if (checkMDP.isSelected()){
		        partie = new Partie(client, nom.getText(), mdp.getText(), Integer.parseInt((String)nb_joueur.getSelectedItem()));
	    	}
	    	else {
		        partie = new Partie(client, nom.getText(), Integer.parseInt((String)nb_joueur.getSelectedItem()));	
	    	}
	        sendData = true;
	        setVisible(false);
	      }
	    });
	    JButton annulerB = new JButton("Annuler");
	    annulerB.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent arg0) {
	        setVisible(false);
	      }      
	    });
	    
	    control.add(creerB);
	    control.add(annulerB);
	    
	    this.getContentPane().setLayout(new BorderLayout());
	    this.getContentPane().add(panIcon, BorderLayout.CENTER);
	    this.getContentPane().add(infos, BorderLayout.EAST);
	    this.getContentPane().add(control, BorderLayout.SOUTH);

		
	}
	
	/** Affichage de la boite de dialogue
	 * 
	 * @return la partie creee (null si annulation)
	 */
	public Partie showDialogPartie(){
		this.sendData = false;
	    this.pack();
	    this.setVisible(true);
	    return (this.sendData)?this.partie: null;
	}

}



/** Controleur de changement d'etat de l'activation du mot de passe
 * 
 * @author DHT
 * @version 1.0
 *
 */
class ControleurChangement implements javax.swing.event.ChangeListener{
	
	/** La boite de dialogueappelante */
	private DialogPartie d;
	
	/** Le constructeur
	 * 
	 * @param d la boite de dialogue parent
	 */
	public ControleurChangement(DialogPartie d){
		this.d = d;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
			if (d.checkMDP.isSelected()){
				d.mdp.setEnabled(true);
			}
			else {
				d.mdp.setEnabled(false);
			}
			
	}
}
