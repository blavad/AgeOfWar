package partie.core;

import java.awt.Graphics;

public class Unite extends Entite {

	protected int degatA;
	protected float vitesseA;
	protected float cooldown;
	protected float porteeA;
	protected EtatUnite etat;
	
	protected float vitesseDeDeplacement;
	protected int cout;
	
	/**
	 * Constructeur de l'unit�
	 * @param pos
	 * 			Position de l'unit�
	 * @param vie
	 * 			Vie de l'unit�
	 * @param camp
	 * 			Camp de l'unit�
	 */
	public Unite (Vect2 pos, int vie, int camp) {
		super(pos, vie, camp);
		cooldown = 0;
	}
	
	public float getPorteeA() { return this.porteeA; }
	public EtatUnite getEtat() { return this.etat; }
	public int getCout() { return this.cout; }
	public float getVitesseDeplacement() { return this.vitesseDeDeplacement; }
	
	
	/**
	 * Met � jour l'unit�<li>
	 * 	Diminue le cooldown gr�ce � dt
	 * @param dt
	 * 			Temps depuis la derni�re mis � jour
	 */
	public void update(float dt) {
		if (cooldown > 0) {
			cooldown -= dt;
			if (cooldown < 0) {
				cooldown = 0;
			}
		}
	}
	
	/**
	 * D�cale l'unit�
	 * @param dx
	 * 			D�calage en X
	 * @param dy
	 * 			D�calage en Y
	 */
	public void deplacement(float dx, float dy) {
		this.position.setPos(position.x + dx, position.y + dy);
	}
	
	/**
	 * G�re l'attaque de l'unit� sur une cible<li>
	 * 	Change le cooldown<li>
	 *  Attaque l'entit�
	 * @param e
	 * 			L'entit� cible
	 * @return vrai si la cible est tu�e et faux sinon
	 */
	public boolean attaque(Entite e) {
		cooldown = vitesseA;
		return e.takeDamage(this.degatA);
	}
	
	/**
	 * Dit si l'unit� peut attaquer
	 * @return vrai si le cooldown est � 0 et faux sinon
	 */
	public boolean canAttack() { 
		return cooldown == 0;
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
		float rayon = rayonUnite * ratio;
		
		int posX = (int)offSet.x + (int)Math.floor(position.x * ratio - rayon);
		int posY = (int)offSet.y + (int)Math.floor(position.y * ratio - rayon);
		int r = (int)(rayon * 2);
		
		g.setColor(color);
		g.fillOval(posX, posY, r, r);		
	}
	
	
}
