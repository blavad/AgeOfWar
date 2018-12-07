package partie.core;

import java.awt.Color;
import java.awt.Graphics;

public class Base extends Entite {

	public Base(Vect2 pos, int camp) {
		super(pos, VarPartie.VIE_BASE, camp);
		rayonUnite = VarPartie.RAYON_BASE;
	}
	
	
	/**
	 * Dessine l'unité sur le plateau
	 * @param g
	 * 			Graphics du JPanel plateau
	 * @param ratio
	 * 			Ratio d'affichage
	 * @param offSet
	 * 			Décalage d'affichage en X et Y
	 */
	public void draw(Graphics g, float ratio, Vect2 offSet) {
		float rayon = rayonUnite * ratio;
		
		int posX = (int)offSet.x + (int)Math.floor(position.x * ratio - rayon);
		int posY = (int)offSet.y + (int)Math.floor(position.y * ratio - rayon);
		int r = (int)(rayon * 2);
		
		g.setColor(color);
		g.fillOval(posX, posY, r, r);
		g.setColor(Color.BLACK);
		g.drawOval(posX, posY, r, r);
		
	}
}
