package partie.core;

import java.awt.Graphics;
import java.util.HashMap;

import partie.rmi.JoueurPartieImpl;

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
	public void attaque(Entite e) {
		cooldown = vitesseA;
		e.takeDamage(this.degatA);
	}
	
	/**
	 * Dit si l'unit� peut attaquer
	 * @return vrai si le cooldown est � 0 et faux sinon
	 */
	public boolean canAttack() { 
		return cooldown == 0;
	}
	
	/**
	 * Met � jour l'unit�<li>
	 * 	Diminue le cooldown gr�ce � dt
	 * @param dt
	 * 			Temps depuis la derni�re mis � jour
	 */
	public void update(float dt, boolean estPremiere, Groupe grp, HashMap<Integer, Armee> entites, HashMap<Integer, JoueurPartieImpl> joueurs) {
		if (cooldown > 0) {
			cooldown -= dt;
			if (cooldown < 0) {
				cooldown = 0;
			}
		}
		
		if (estPremiere) {
			// Calcule et renvoie l'unit� la plus proche � distance d'attaque de U
			Entite entiteCible = entiteAAttaquer(entites); 
			
			if (entiteCible != null) {
				// Si la cible pointe sur une entit�, U attaque la cible
				attaqueEntite(entiteCible, entites, joueurs); 
			} else { 
				// Sinon, si la cible est null, on calcule le deplacement de U
				// d la distance entre U et l'objectif
				float d = (float)Math.sqrt(Outils.norme2AB(grp.getObjectif(), this.getPosition()));
				// dd la distance de deplacement de U pendant dt en fonction de sa vitesse
				float dd = (dt / 1000)  * (float)this.getVitesseDeplacement() / d;

				// Th�or�me de Thales => on calcule dx et dy les d�placements en x et y
				float dx = dd * (grp.getObjectif().x - this.getPosition().x);
				float dy = dd * (grp.getObjectif().y - this.getPosition().y);
				this.deplacement(dx, dy); // application de ces d�placements
				
			}
		} else {
			// Calcule et renvoie l'unit� la plus proche � distance d'attaque de U
			Entite entiteCible = entiteAAttaquer(entites);
			
			if (entiteCible != null) {
				attaqueEntite(entiteCible, entites, joueurs);
			} else {
				// U0 pointe sur la (i)e unit� du groupe (l'unit� devant U)
				Unite u0 = grp.getUnites().get(grp.getUnites().indexOf(this) - 1);// U pointe sur la (i + 1)e unit� du groupe
				
				// d la distance entre U et U0
				float d = (float)Math.sqrt(Outils.norme2AB(u0.getPosition(), this.getPosition()));
				if (d > (this.getRayonUnite() + u0.getRayonUnite())) { //G�re les collisions
					// Si U n'est pas en contact de U0, alors U avance
					// idem
					float dd = (dt / 1000)  * (float)this.getVitesseDeplacement() / d;

					float dx = dd * (u0.getPosition().x - this.getPosition().x);
					float dy = dd * (u0.getPosition().y - this.getPosition().y);
					this.deplacement(dx, dy);
				}
			}
		}
		
	}
	
	
	/**
	 * D�finit l'entit� que u doit attaquer 
	 * @param unite
	 * 				l'attaquant
	 * @return une entit� � port�e de l'attaquant et rien si aucune entit� v�rifie ce crit�re  
	 */
	private Entite entiteAAttaquer(HashMap<Integer, Armee> entites) {
		Entite e = null;
		
		// Parcourt toutes les entit�s de la partie
		for (Integer i : entites.keySet()) {
			// Regarde seulelement les unit�s des autres camps
			if (i != this.getCamp()) {
				
				if (aPorteeDe(entites.get(i).getBase(), this)) {
					// Si la base est � port�e d'attaque, alors elle devient la cible
					e = entites.get(i).getBase();
				}
				
				// Parcourt toutes les unit�s
				for (int j = 0; j < entites.get(i).getGroupes().size(); j++) {
					for (Unite u : entites.get(i).getGroupes().get(j).getUnites()) {
						if (aPorteeDe(u, this)) {
							// Si l'unit� est � port�e d'attaque, alors elle devient la cible 
							// Remarque : les unit�s sont prioritaires face aux bases
							e = u;
						}
					}
				}
			}
		}
		
		return e;
		
	}
	
	
	/**
	 * G�re l'attaque de l'unit� u sur l'entit� cible
	 * @param u
	 * 				attaquant
	 * @param cible
	 * 				entit� qui se fait attaquer
	 */
	private void attaqueEntite(Entite cible, HashMap<Integer, Armee> entites, HashMap<Integer, JoueurPartieImpl> joueurs) {
		// Si U peut attaquer (le cooldown de U est � 0)
		if (this.canAttack()) {
			// U attaque la cible et renvoie vrai si elle tue la cible, faux sinon
			this.attaque(cible);
			if (cible.estMorte()) {
				if (cible instanceof Base) {
					// Si l'unit� tu�e est une base
					System.out.println("Joueur " + cible.getCamp() + "�limin�");
				} else 
					if (cible instanceof Unite) {
						// Si l'unit� tu�e est une unit�
						// Donne l'argent de l'�limination au joueur "assassin"
						joueurs.get(this.getCamp()).ajouterArgent(((Unite)cible).getCout());
						entites.get(cible.getCamp()).supprimerUnite((Unite)cible);
				}
			}
		}
	}
	
	
	
	/**
	 * Calcule si l'entit� e est � port�e d'attaque de l'attaquant u
	 * @param e
	 * 				l'entit� cible 
	 * @param u
	 * 				l'attaquant
	 * @return vrai si l'attaquant est � port�e de tir de la cible et faux sinon
	 */
	private boolean aPorteeDe(Entite e, Unite u) {
		int dx = (int)Math.abs(u.getPosition().x - e.getPosition().x); // Distance selon x de E et U
		int dy = (int)Math.abs(u.getPosition().y - e.getPosition().y); // Distance selon y de E et U
		float dMin = u.getPorteeA() + e.getRayonUnite(); // Distance minimum pour que U puisse attaquer E
		
		// Si dx ou dy plus grand que dMin, alors inutile de calculer la vraie distance entre E et U ("pseudo optimisation") 
		if (dx <= dMin && dy <= dMin) { 
			// d : distance entre E et U
			float d = (float)Math.sqrt(Outils.norme2AB(u.getPosition(), e.getPosition()));
			return (d <= dMin);
		}
		else return false;
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
