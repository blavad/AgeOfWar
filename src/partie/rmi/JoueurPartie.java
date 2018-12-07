package partie.rmi;

import java.util.HashMap;

import partie.core.Armee;

public interface JoueurPartie {
	
	void update(HashMap<Integer, Armee> entites);
	void ajouterArgent(int arg);
}
