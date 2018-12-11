package partie.core;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;

import partie.ihm.InterfacePartie.Menu;
import partie.rmi.JoueurPartieImpl;

public class Base extends Entite {
	
	private HashMap<Menu, Defence> defences;

	public Base(Vect2 pos, int camp) {
		super(pos, VarPartie.VIE_BASE, camp, VarPartie.RAYON_BASE);
		defences = new HashMap<Menu, Defence>();
	}
	
	public void addDef(Menu menu, Defence def) {
		defences.replace(menu, def);
	}
	
	public void suppDef(Menu menu) {
		defences.remove(menu);
	}
	
	/**
	 * Met à jour la base<li>
	 *  Calcule les actions des défences
	 * @param dt Float
	 * @param entites HashMap<Integer, Armee>
	 * @param joueurs HashMap<Integer, JoueurPartieImpl>
	 */
	public void update(float dt, HashMap<Integer, Armee> entites, HashMap<Integer, JoueurPartieImpl> joueurs) {
		
		for (Menu m : defences.keySet()) {
			defences.get(m).update(dt, entites, joueurs); // update l'unité (cooldown, ...)
		}
		
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
