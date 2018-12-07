package partie.core;

import java.awt.Color;
import java.awt.Graphics;

public abstract class Entite {
	
	protected Vect2 position;
	protected int vie, vieMax;
	protected int camp;
	
	protected Color color;
	protected int rayonUnite;
	
	/**
	 * Construteur de l'entité<li>
	 * 	Initialise toutes les variables
	 *  Définit ola couleur de l'entité avec le camp
	 * @param pos
	 * 			Position de l'entité
	 * @param vie
	 * 			Vie de l'entité
	 * @param camp
	 * 			Camp de l'entité
	 */
	public Entite(Vect2 pos, int vie, int camp) {
		this.position = new Vect2(pos.x, pos.y);
		this.vieMax = vie;
		this.vie = vie;
		this.camp = camp;
		defineColor();
	}
	
	/**
	 * Définit la couleur de l'entité grâce au camp de celle-ci
	 */
	private void defineColor() {
		switch (camp) {
		case 1 :
			color = Color.RED;
			break;
		case 2 :
			color = Color.BLUE;
			break;
		case 3 :
			color = Color.DARK_GRAY;
			break;
		case 4 :
			color = Color.MAGENTA;
			break;
		}
	}
	
	public Vect2 getPosition() { return this.position; }
	public int getVie() { return this.vie; }
	public int getCamp() { return this.camp; }
	public int getRayonUnite() { return this.rayonUnite; }
	
	/**
	 * Gère la prise de dommage<li>
	 *  Prends les dommages<li>
	 *  Vérifie si l'entité se fait tuer
	 * @param d
	 * 			Dommage pris
	 * @return vrai si l'entité est tuée et faux sinon
	 */
	public boolean takeDamage(int d) {
		int v = this.vie - d;
		if (v <= 0) {
			this.vie = 0;
			return true;
		}
		else {
			this.vie = v;
			return false;
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
	public abstract void draw(Graphics g, float ratio, Vect2 offSet);
	
	

}
