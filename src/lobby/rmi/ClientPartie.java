package lobby.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import lobby.core.Partie;

/**
 * L'interface RMI pour un client de serveur de parties
 * 
 * @author DHT
 * @version 1.0
 */
public interface ClientPartie extends Remote, Serializable {
	
	/**
	 * Notifie le clients de modifications dans la liste de partie
	 * 
	 */
	public void notifier(ArrayList<Partie> l_parties) throws RemoteException;

	public void lancerPartie(String hote, Integer camp) throws RemoteException;

}
