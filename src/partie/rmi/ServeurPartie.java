package partie.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

import partie.core.Armee;
import partie.core.TypeUnite;
import partie.core.Vect2;
import partie.ihm.InterfacePartie.Menu;

public interface ServeurPartie extends Remote {

	void ajouterUnite(int camp, TypeUnite typeU, int grpSelect) throws RemoteException;
	void ajouterDefence(int camp, TypeUnite typeD, Menu menu) throws RemoteException;
	void supprimerDefence(int camp, Menu menu)throws RemoteException;
	boolean aDefence(int camp, Menu menu) throws RemoteException;
	void changeObjectifGroupe(int camp, int grpSelect, Vect2 pos) throws RemoteException;
	void initPartie() throws RemoteException;
	void startPartie() throws RemoteException;
	HashMap<Integer, Armee> getEntites() throws RemoteException;
	void suppJoueurImpl(int camp) throws RemoteException;
}

