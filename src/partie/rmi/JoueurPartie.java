package partie.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

import partie.core.Armee;

public interface JoueurPartie extends Remote {
	
	void update(HashMap<Integer, Armee> entites) throws RemoteException;
	void ajouterArgent(int arg) throws RemoteException;
	void start() throws RemoteException;
	void meurt() throws RemoteException;
	void pret() throws RemoteException;
	void finPartie(int joueurGagnant) throws RemoteException;
	void quitterPartie() throws RemoteException;
	void decoForcee() throws RemoteException;
}
