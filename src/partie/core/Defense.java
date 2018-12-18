package partie.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.HashMap;

import javax.swing.ImageIcon;

import javafx.scene.paint.Color;
import partie.rmi.JoueurPartie;
import partie.rmi.JoueurPartieImpl;

public class Defense extends Unite implements Serializable {

	public Defense(Vect2 pos, int camp, int rayonEntite, int cout, int degatA, float vitesseA, float porteeA, String imageName) {
		super(pos, 0, camp, rayonEntite, cout, degatA, vitesseA, porteeA, 0, imageName);
		
	}
	
	/***
	 * Met à jour l'unite<li>
	 * 	Diminue le cooldown grâce à dt<li>
	 *  Determine l'action de l'unite en fonction de son environnement
	 * @param dt
	 * 			Temps depuis la derniere mise à jour
	 * @param estPremiere
	 * 			Represente la position de l'unite dans son groupe
	 * @param entites
	 * 			Ensemble des unites de la partie
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
		
		// Calcule et renvoie l'unite la plus proche à distance d'attaque de U
		Entite entiteCible = entiteAAttaquer(entites); 
		
		if (entiteCible != null) {
			// Si la cible pointe sur une entite, U attaque la cible
			attaqueEntite(entiteCible, entites, joueurs); 
			angle = Outils.getAngle(position, entiteCible.getPosition()) + (float)Math.PI / 2;
		}
	}
	
	/**
	 * Dessine l'unite sur le plateau
	 * @param g
	 * 			Graphics du JPanel plateau
	 * @param ratio
	 * 			Ratio d'affichage
	 * @param offSet
	 * 			Decalage d'affichage en X et Y
	 */
	public void draw(Graphics g, float ratio, Vect2 offSet, Images images) {
		float rayon = rayonEntite * ratio;
		
		int posX = (int)offSet.x + (int)Math.floor(position.x * ratio - rayon);
		int posY = (int)offSet.y + (int)Math.floor(position.y * ratio - rayon);
		int r = (int)(rayon * 2);
		
		g.setColor(color);
		if (imageName != null) {
			Image image = images.getImage(imageName);
			AffineTransform rotation = new AffineTransform();
			rotation.translate(posX , posY);
			rotation.scale(r / (float)(image.getWidth(null)), r / (float)(image.getHeight(null)));
			rotation.rotate(angle, (int)(image.getWidth(null)/2),(int)(image.getHeight(null)/2));
			((Graphics2D)g).drawImage(image, rotation, null);
		}
		
		
		
		
	}

}
