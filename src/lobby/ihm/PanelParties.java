package lobby.ihm;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import lobby.core.Partie;
import lobby.rmi.ServeurParties;

/** Le panel de l'affichage des parties en attente
 * 
 * @author DHT
 * @version 1.0
 *
 */
public class PanelParties extends JPanel {
	
	/** La liste des parties */
	private JList<Partie> jl_parties;
	
	/** Le constructeur */
	public PanelParties(){
		super();
		ArrayList<Partie> al_parties;
		Partie[] tabParties;
		jl_parties = new JList<Partie>(new DefaultListModel<Partie>());
	}
	
	public void updatePartie(ArrayList<Partie> lparties) {
		DefaultListModel<Partie> model = (DefaultListModel<Partie>) getJListPartie().getModel();
		model.clear();
		for (Partie p : lparties){
			model.addElement(p);
		}
	}
	
	/**
	 * @return la java liste des partie 
	 */
	public JList<Partie> getJListPartie(){
		return jl_parties;
	}

}
