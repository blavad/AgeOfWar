package partie.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.HashMap;

import javax.swing.ImageIcon;

import partie.ihm.InterfacePartie.Menu;
import partie.rmi.JoueurPartie;
import partie.rmi.JoueurPartieImpl;

public class Base extends Entite {
	
	private HashMap<Menu, Defense> defenses;

	public Base(Vect2 pos, int camp) {
		super(pos, VarPartie.VIE_BASE, camp, VarPartie.RAYON_BASE);
		defenses = new HashMap<Menu, Defense>();
		setImageName("Vaisseau0");
	}
	
	public void addDef(Menu menu, Defense def) {
		if (!defenses.containsKey(menu)) defenses.put(menu, def);
		else defenses.replace(menu, def);
	}
	
	public void suppDef(Menu menu) {
		defenses.remove(menu);
	}
	public void suppToutesDef() {
		for (Menu m : defenses.keySet()) {
			defenses.remove(m);
		}
	}
	
	public Defense getDefence(Menu menu) {
		if (defenses.containsKey(menu)) return defenses.get(menu);
		else return null;
	}
	
	/**
	 * Met à jour la base<li>
	 *  Calcule les actions des defences
	 * @param dt Float
	 * @param entites HashMap<Integer, Armee>
	 * @param joueurs HashMap<Integer, JoueurPartieImpl>
	 */
	public void update(float dt, HashMap<Integer, Armee> entites, HashMap<Integer, JoueurPartie> joueurs) {
		
		for (Menu m : defenses.keySet()) {
			defenses.get(m).update(dt, entites, joueurs); // update l'unite (cooldown, ...)
		}
		
	}
	
	
	/**
	 * Dessine la base et les defenses sur le plateau
	 * @param g Graphics : Graphics du JPanel plateau
	 * @param ratio float : Ratio d'affichage
	 * @param offSet Vect2 : Decalage d'affichage en X et Y
	 * @param images Images : Classe contenant toutes les images du jeu
	 * @param campJoueurImpl int : camp du joueur
	 */
	public void draw(Graphics g, float ratio, Vect2 offSet, Images images, int campJoueurImpl) {
		float rayon = rayonEntite * ratio;
		
		int posX = (int)offSet.x + (int)Math.floor(position.x * ratio - rayon);
		int posY = (int)offSet.y + (int)Math.floor(position.y * ratio - rayon);
		int r = (int)(rayon * 2);
		
		//Dessine la base
		if (imageName != null) {
			Image image = images.getImage(imageName);
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
		if (defenses.containsKey(Menu.DEF1)) {
			defenses.get(Menu.DEF1).draw(g, ratio, offSet, new Vect2(0, - (int)Math.floor(15 * ratio)), images, campJoueurImpl);
		}
		if (defenses.containsKey(Menu.DEF2)) {
			defenses.get(Menu.DEF2).draw(g, ratio, offSet, new Vect2( (int)Math.floor(12 * ratio), (int)Math.floor(10 * ratio)), images, campJoueurImpl);
		}
		if (defenses.containsKey(Menu.DEF3)) {
			defenses.get(Menu.DEF3).draw(g, ratio, offSet, new Vect2(-(int)Math.floor(12 * ratio), (int)Math.floor(10 * ratio)), images, campJoueurImpl);
		}
		
		
		
	}
}
