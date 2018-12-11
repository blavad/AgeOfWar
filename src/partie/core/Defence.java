package partie.core;

import java.util.HashMap;

import partie.rmi.JoueurPartieImpl;

public class Defence extends Unite {

	public Defence(Vect2 pos, int vie, int camp, int rayonEntite, int cout, int degatA, float vitesseA, float porteeA,
			float vitesseDeDeplacement) {
		super(pos, vie, camp, rayonEntite, cout, degatA, vitesseA, porteeA, vitesseDeDeplacement);
		
	}
	
	/***
	 * Met � jour l'unit�<li>
	 * 	Diminue le cooldown gr�ce � dt<li>
	 *  D�termine l'action de l'unit� en fonction de son environnement
	 * @param dt
	 * 			Temps depuis la derniere mise � jour
	 * @param estPremiere
	 * 			Repr�sente la position de l'unit� dans son groupe
	 * @param entites
	 * 			Ensemble des unit�s de la partie
	 * @param joueurs
	 * 			Ensemble des joueurs de la partie
	 */
	public void update(float dt, HashMap<Integer, Armee> entites, HashMap<Integer, JoueurPartieImpl> joueurs) {
		if (cooldown > 0) {
			cooldown -= dt;
			if (cooldown < 0) {
				cooldown = 0;
			}
		}
		
		// Calcule et renvoie l'unit� la plus proche � distance d'attaque de U
		Entite entiteCible = entiteAAttaquer(entites); 
		
		if (entiteCible != null) {
			// Si la cible pointe sur une entit�, U attaque la cible
			attaqueEntite(entiteCible, entites, joueurs); 
		}
	}

}
