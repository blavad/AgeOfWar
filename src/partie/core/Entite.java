package partie.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;

public abstract class Entite implements Serializable {
	
	protected Vect2 position;
	protected int vie, vieMax;
	protected int camp;
	
	protected Color color;
	protected int rayonEntite;
	protected String imageName;
	protected float angle;
	
	/**
	 * Construteur de l'entite<li>
	 * 	Initialise toutes les variables
	 *  Definit ola couleur de l'entite avec le camp
	 * @param pos
	 * 			Position de l'entite
	 * @param vie
	 * 			Vie de l'entite
	 * @param camp
	 * 			Camp de l'entite
	 */
	public Entite(Vect2 pos, int vie, int camp, int rayonEntite) {
		this.position = new Vect2(pos.x, pos.y);
		this.vieMax = vie;
		this.vie = vie;
		this.camp = camp;
		this.rayonEntite = rayonEntite;
		defineColor();
		this.angle = 0;
	}
	
	/**
	 * Definit la couleur de l'entite gr�ce au camp de celle-ci
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
	public boolean estMorte() { return this.vie == 0; }
	public int getCamp() { return this.camp; }
	public int getRayonEntite() { return this.rayonEntite; }
	public void setImageName(String name) {
		imageName = name;
	}
	
	/**
	 * G�re la prise de dommage<li>
	 *  Prends les dommages<li>
	 *  Verifie si l'entite se fait tuer
	 * @param d
	 * 			Dommage pris
	 * @return vrai si l'entite est tuee et faux sinon
	 */
	public void takeDamage(int d) {
		int v = this.vie - d;
		if (v <= 0) {
			this.vie = 0;
		}
		else {
			this.vie = v;
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
	public abstract void draw(Graphics g, float ratio, Vect2 offSet, Images images);
	
	

}
