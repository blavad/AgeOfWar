package lobby.ihm;

import javax.swing.JList;

import lobby.core.Client;

public class FenetrePartieClient extends FenetrePartie {

	/** La liste des parties */
	private JList<Client> jl_clients;
	
	public FenetrePartieClient(FenetreClient parent) {
		super(parent);
	}

}
