package partie.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import partie.core.TypeDefense;
import partie.core.TypeUnite;
import partie.core.Unite;
import partie.core.Vect2;
import partie.ihm.InterfacePartie.Menu;

public interface ServeurPartie extends Remote {

	void ajouterUnite(int camp, TypeUnite typeU, int grpSelect) throws RemoteException;
	void ajouterDefence(int camp, TypeDefense typeD, Menu menu) throws RemoteException;
	void supprimerDefence(int camp, Menu menu)throws RemoteException;
	boolean aDefence(int camp, Menu menu) throws RemoteException;
	void changeObjectifGroupe(int camp, int grpSelect, Vect2 pos) throws RemoteException;
}
