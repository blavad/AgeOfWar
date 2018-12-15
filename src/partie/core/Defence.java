package partie.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.HashMap;

import javafx.scene.paint.Color;
import partie.rmi.JoueurPartie;
import partie.rmi.JoueurPartieImpl;

public class Defence extends Unite implements Serializable {

	public Defence(Vect2 pos, int camp, int rayonEntite, int cout, int degatA, float vitesseA, float porteeA, String imageName) {
		super(pos, 0, camp, rayonEntite, cout, degatA, vitesseA, porteeA, 0, imageName);
		
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
	public void update(float dt, HashMap<Integer, Armee> entites, HashMap<Integer, JoueurPartie> joueurs) {
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
			angle = Outils.getAngle(position, entiteCible.getPosition()) + (float)Math.PI / 2;
		}
	}
	
	/**
	 * Dessine l'unit� sur le plateau
	 * @param g
	 * 			Graphics du JPanel plateau
	 * @param ratio
	 * 			Ratio d'affichage
	 * @param offSet
	 * 			D�calage d'affichage en X et Y
	 */
	public void draw(Graphics g, float ratio, Vect2 offSet) {
		float rayon = rayonEntite * ratio;
		
		int posX = (int)offSet.x + (int)Math.floor(position.x * ratio - rayon);
		int posY = (int)offSet.y + (int)Math.floor(position.y * ratio - rayon);
		int r = (int)(rayon * 2);
		
		g.setColor(color);
		if (image != null) {
			AffineTransform rotation = new AffineTransform();
			rotation.translate(posX , posY);
			rotation.scale(r / (float)(image.getWidth(null)), r / (float)(image.getHeight(null)));
			rotation.rotate(angle, (int)(image.getWidth(null)/2),(int)(image.getHeight(null)/2));
			((Graphics2D)g).drawImage(image, rotation, null);
		}
		
		
		
		
	}

}
