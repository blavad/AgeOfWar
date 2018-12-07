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
	 * Constructeur de l'unité
	 * @param pos
	 * 			Position de l'unité
	 * @param vie
	 * 			Vie de l'unité
	 * @param camp
	 * 			Camp de l'unité
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
	 * Met à jour l'unité<li>
	 * 	Diminue le cooldown grâce à dt
	 * @param dt
	 * 			Temps depuis la dernière mis à jour
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
	 * Décale l'unité
	 * @param dx
	 * 			Décalage en X
	 * @param dy
	 * 			Décalage en Y
	 */
	public void deplacement(float dx, float dy) {
		this.position.setPos(position.x + dx, position.y + dy);
	}
	
	/**
	 * Gère l'attaque de l'unité sur une cible<li>
	 * 	Change le cooldown<li>
	 *  Attaque l'entité
	 * @param e
	 * 			L'entité cible
	 * @return vrai si la cible est tuée et faux sinon
	 */
	public boolean attaque(Entite e) {
		cooldown = vitesseA;
		return e.takeDamage(this.degatA);
	}
	
	/**
	 * Dit si l'unité peut attaquer
	 * @return vrai si le cooldown est à 0 et faux sinon
	 */
	public boolean canAttack() { 
		return cooldown == 0;
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
	}
	
	
}
