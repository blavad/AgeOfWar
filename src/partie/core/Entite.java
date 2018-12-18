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
	 * @param pos Vect2 : Position de l'entite
	 * @param vie int : Vie max de l'entite
	 * @param camp int : Camp de l'entite
	 */
	public Entite(Vect2 pos, int vie, int camp, int rayonEntite) {
		this.position = new Vect2(pos.x, pos.y);
		this.vieMax = vie;
		this.vie = vie;
		this.camp = camp;
		this.rayonEntite = rayonEntite;
		this.color = Outils.defineColor(camp);
		this.angle = 0;
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
	 * Gère la prise de dommage<li>
	 *  Prends les dommages<li>
	 *  Verifie si l'entite se fait tuer
	 * @param d int : Dommage pris
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
	

}
