package partie.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashMap;

import partie.ihm.InterfacePartie.Menu;
import partie.rmi.JoueurPartieImpl;

public class Base extends Entite {
	
	private HashMap<Menu, Defence> defences;

	public Base(Vect2 pos, int camp) {
		super(pos, VarPartie.VIE_BASE, camp, VarPartie.RAYON_BASE);
		defences = new HashMap<Menu, Defence>();
		setImage("Vaisseau0.png");
	}
	
	public void addDef(Menu menu, Defence def) {
		if (!defences.containsKey(menu)) defences.put(menu, def);
		else defences.replace(menu, def);
	}
	
	public void suppDef(Menu menu) {
		defences.remove(menu);
	}
	
	public Defence getDefence(Menu menu) {
		if (defences.containsKey(menu)) return defences.get(menu);
		else return null;
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
		
		//Dessine la base
		if (image != null) {
			AffineTransform rotation = new AffineTransform();
			rotation.translate(posX , posY);
			rotation.scale(r / (float)(image.getWidth(null)), r / (float)(image.getHeight(null)));
			rotation.rotate(angle, (int)(image.getWidth(null)/2),(int)(image.getHeight(null)/2));
			((Graphics2D)g).drawImage(image, rotation, null);
		}
		
		float ratioVie = (float)vie / (float)vieMax;
		int largeurBar = (int)Math.floor(7 * ratio);
		int posXBar;
		if (position.x > VarPartie.WIDTH_PARTIE / 2.0f) {
			posXBar = posX + r;
		}
		else {
			posXBar = posX - largeurBar;
		}

		//Dessine la barre de Pv
		g.setColor(color);
		g.fillRect(posXBar, posY, largeurBar, (int)Math.floor(r * ratioVie));
		
		
		

		//Dessine les defences
		if (defences.containsKey(Menu.DEF1)) {
			defences.get(Menu.DEF1).draw(g, ratio, new Vect2(offSet.x, offSet.y - (int)Math.floor(15 * ratio)));
		}
		if (defences.containsKey(Menu.DEF2)) {
			defences.get(Menu.DEF2).draw(g, ratio, new Vect2(offSet.x + (int)Math.floor(12 * ratio), offSet.y + (int)Math.floor(10 * ratio)));
		}
		if (defences.containsKey(Menu.DEF3)) {
			defences.get(Menu.DEF3).draw(g, ratio, new Vect2(offSet.x - (int)Math.floor(12 * ratio), offSet.y + (int)Math.floor(10 * ratio)));
		}
		
		
		
	}
}
