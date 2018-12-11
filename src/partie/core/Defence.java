package partie.core;

import java.util.HashMap;

import partie.rmi.JoueurPartieImpl;

public class Defence extends Unite {

	public Defence(Vect2 pos, int vie, int camp, int rayonEntite, int cout, int degatA, float vitesseA, float porteeA,
			float vitesseDeDeplacement) {
		super(pos, vie, camp, rayonEntite, cout, degatA, vitesseA, porteeA, vitesseDeDeplacement);
		
	}
	
	/***
	 * Met à jour l'unité<li>
	 * 	Diminue le cooldown grâce à dt<li>
	 *  Détermine l'action de l'unité en fonction de son environnement
	 * @param dt
	 * 			Temps depuis la derniere mise à jour
	 * @param estPremiere
	 * 			Représente la position de l'unité dans son groupe
	 * @param entites
	 * 			Ensemble des unités de la partie
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
		
		// Calcule et renvoie l'unité la plus proche à distance d'attaque de U
		Entite entiteCible = entiteAAttaquer(entites); 
		
		if (entiteCible != null) {
			// Si la cible pointe sur une entité, U attaque la cible
			attaqueEntite(entiteCible, entites, joueurs); 
		}
	}

}
