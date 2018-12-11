package partie.core;

import java.awt.Color;
import java.awt.Graphics;

public class Base extends Entite {

	public Base(Vect2 pos, int camp) {
		super(pos, VarPartie.VIE_BASE, camp, VarPartie.RAYON_BASE);
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
		float rayon = rayonEntite * ratio;
		
		int posX = (int)offSet.x + (int)Math.floor(position.x * ratio - rayon);
		int posY = (int)offSet.y + (int)Math.floor(position.y * ratio - rayon);
		int r = (int)(rayon * 2);
		
		g.setColor(color);
		g.fillOval(posX, posY, r, r);
		g.setColor(Color.BLACK);
		g.drawOval(posX, posY, r, r);
		
		
		float ratioVie = (float)vie / (float)vieMax;
		int largeurBar = (int)Math.floor(7 * ratio);
		int posXBar;
		if (position.x > VarPartie.WIDTH_PARTIE / 2.0f) {
			posXBar = posX + r;
		}
		else {
			posXBar = posX - largeurBar;
		}
		
		g.setColor(Color.RED);
		g.fillRect(posXBar, posY, largeurBar, (int)Math.floor(r * ratioVie));
	}
}
