package partie.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.swing.ImageIcon;

import partie.rmi.JoueurPartie;
import partie.rmi.JoueurPartieImpl;

public class Unite extends Entite {

	transient protected int degatA;
	transient protected float vitesseA;
	transient protected float cooldown;
	protected float porteeA;
	
	transient protected float vitesseDeDeplacement;
	transient protected int cout;
	protected TypeUnite typeU;
	
	/**
	 * Constructeur de l'unite
	 * @param pos Vect2
	 * @param vie int
	 * @param camp int
	 * @param rayonEntite int
	 * @param cout int
	 * @param degatA int
	 * @param vitesseA float
	 * @param porteeA float
	 * @param vitesseDeDeplacement float
	 */
	public Unite (Vect2 pos, int vie, int camp, int rayonEntite, int cout, int degatA, float vitesseA, float porteeA, float vitesseDeDeplacement, String imageName, TypeUnite typeU) {
		super(pos, vie, camp, rayonEntite);
		cooldown = 0;
		this.cout = cout;
		this.degatA = degatA;
		this.vitesseA = vitesseA;
		this.porteeA = porteeA;
		this.vitesseDeDeplacement = vitesseDeDeplacement;
		setImageName(imageName);
		this.typeU = typeU;
		
	}
	
	public float getPorteeA() { return this.porteeA; }
	public int getCout() { return this.cout; }
	public float getVitesseDeplacement() { return this.vitesseDeDeplacement; }
	public TypeUnite getTypeUnite() { return this.typeU; }
	
	
	/**
	 * Decale l'unite
	 * @param dx
	 * 			Decalage en X
	 * @param dy
	 * 			Decalage en Y
	 */
	public void deplacement(float dx, float dy) {
		this.position.setPos(position.x + dx, position.y + dy);
	}
	
	/**
	 * Gère l'attaque de l'unite sur une cible<li>
	 * 	Change le cooldown<li>
	 *  Attaque l'entite
	 * @param e
	 * 			L'entite cible
	 * @return vrai si la cible est tuee et faux sinon
	 */
	public void attaque(Entite e) {
		cooldown = vitesseA;
		e.takeDamage(this.degatA);
	}
	
	/**
	 * Dit si l'unite peut attaquer
	 * @return vrai si le cooldown est à 0 et faux sinon
	 */
	public boolean canAttack() { 
		return cooldown == 0;
	}
	
	/***
	 * Met à jour l'unite<li>
	 * 	Diminue le cooldown grâce à dt<li>
	 *  Determine l'action de l'unite en fonction de son environnement
	 * @param dt
	 * 			Temps depuis la derniere mise à jour
	 * @param estPremiere
	 * 			Represente la position de l'unite dans son groupe
	 * @param grp
	 * 			Groupe dans lequel appartient l'unite
	 * @param entites
	 * 			Ensemble des unites de la partie
	 * @param joueurs
	 * 			Ensemble des joueurs de la partie
	 */
	public void update(float dt, boolean estPremiere, Groupe grp, HashMap<Integer, Armee> entites, HashMap<Integer, JoueurPartie> joueurs) {
		if (cooldown > 0) {
			cooldown -= dt;
			if (cooldown < 0) {
				cooldown = 0;
			}
		}
		
		if (estPremiere) {
			// Calcule et renvoie l'unite la plus proche à distance d'attaque de U
			Entite entiteCible = entiteAAttaquer(entites); 
			
			if (entiteCible != null) {
				// Si la cible pointe sur une entite, U attaque la cible
				
				attaqueEntite(entiteCible, entites, joueurs); 
				angle = Outils.getAngle(position, entiteCible.getPosition()) + (float)Math.PI / 2;
			} else { 
				// Sinon, si la cible est null, on calcule le deplacement de U
				// d la distance entre U et l'objectif
				float d = (float)Math.sqrt(Outils.norme2AB(grp.getObjectif(), this.getPosition()));
				if (d >= 4) {
					// dd la distance de deplacement de U pendant dt en fonction de sa vitesse
					float dd = (dt / 1000)  * (float)this.getVitesseDeplacement() / d;
		
					// Theorème de Thales => on calcule dx et dy les deplacements en x et y
					float dx = dd * (grp.getObjectif().x - this.getPosition().x);
					float dy = dd * (grp.getObjectif().y - this.getPosition().y);
					this.deplacement(dx, dy); // application de ces deplacements
					angle = Outils.getAngle(position, grp.getObjectif()) + (float)Math.PI / 2;
				}
				
			}
		} else {
			// Calcule et renvoie l'unite la plus proche à distance d'attaque de U
			Entite entiteCible = entiteAAttaquer(entites);
			
			if (entiteCible != null) {
				attaqueEntite(entiteCible, entites, joueurs);
			} else {
				// U0 pointe sur la (i)e unite du groupe (l'unite devant U)
				Unite u0 = grp.getUnites().get(grp.getUnites().indexOf(this) - 1);// U pointe sur la (i + 1)e unite du groupe
				
				// d la distance entre U et U0
				float d = (float)Math.sqrt(Outils.norme2AB(u0.getPosition(), this.getPosition()));
				if (d > (this.getRayonEntite() + u0.getRayonEntite())) { //Gère les collisions
					// Si U n'est pas en contact de U0, alors U avance
					// idem
					float dd = (dt / 1000)  * (float)this.getVitesseDeplacement() / d;

					float dx = dd * (u0.getPosition().x - this.getPosition().x);
					float dy = dd * (u0.getPosition().y - this.getPosition().y);
					this.deplacement(dx, dy);
					angle = Outils.getAngle(position, u0.getPosition()) + (float)Math.PI / 2;
				}
			}
		}
		
	}
	
	
	/**
	 * Definit l'entite que u doit attaquer 
	 * @param unite
	 * 				l'attaquant
	 * @return une entite à portee de l'attaquant et rien si aucune entite verifie ce critère  
	 */
	protected Entite entiteAAttaquer(HashMap<Integer, Armee> entites) {
		Entite e = null;
		
		// Parcourt toutes les entites de la partie
		for (Integer i : entites.keySet()) {
			// Regarde seulelement les unites des autres camps
			if (i != this.getCamp()) {
				
				if (aPorteeDe(entites.get(i).getBase(), this)) {
					// Si la base est à portee d'attaque, alors elle devient la cible
					e = entites.get(i).getBase();
				}
				
				// Parcourt toutes les unites
				for (int j = 0; j < entites.get(i).getGroupes().size(); j++) {
					for (Unite u : entites.get(i).getGroupes().get(j).getUnites()) {
						if (aPorteeDe(u, this)) {
							// Si l'unite est à portee d'attaque, alors elle devient la cible 
							// Remarque : les unites sont prioritaires face aux bases
							e = u;
						}
					}
				}
			}
		}
		
		return e;
		
	}
	
	
	/**
	 * Gère l'attaque de l'unite u sur l'entite cible
	 * @param u
	 * 				attaquant
	 * @param cible
	 * 				entite qui se fait attaquer
	 */
	protected void attaqueEntite(Entite cible, HashMap<Integer, Armee> entites, HashMap<Integer, JoueurPartie> joueurs) {
		// Si U peut attaquer (le cooldown de U est à 0)
		if (this.canAttack()) {
			// U attaque la cible et renvoie vrai si elle tue la cible, faux sinon
			this.attaque(cible);
			if (cible.estMorte()) {
				if (cible instanceof Base) {
					// Si l'unite tuee est une base
					
					//entites.remove(cible.camp);
				} else 
					if (cible instanceof Unite) {
						// Si l'unite tuee est une unite
						// Donne l'argent de l'elimination au joueur "assassin"
						try {
							joueurs.get(this.getCamp()).ajouterArgent((int)Math.floor(((Unite)cible).getCout() * VarPartie.REMBOURSEMENT_UNITE));
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						entites.get(cible.getCamp()).supprimerUnite((Unite)cible);
				}
			}
		}
	}
	
	
	
	/**
	 * Calcule si l'entite e est à portee d'attaque de l'attaquant u
	 * @param e
	 * 				l'entite cible 
	 * @param u
	 * 				l'attaquant
	 * @return vrai si l'attaquant est à portee de tir de la cible et faux sinon
	 */
	protected boolean aPorteeDe(Entite e, Unite u) {
		int dx = (int)Math.abs(u.getPosition().x - e.getPosition().x); // Distance selon x de E et U
		int dy = (int)Math.abs(u.getPosition().y - e.getPosition().y); // Distance selon y de E et U
		float dMin = u.getPorteeA() + e.getRayonEntite(); // Distance minimum pour que U puisse attaquer E
		
		// Si dx ou dy plus grand que dMin, alors inutile de calculer la vraie distance entre E et U ("pseudo optimisation") 
		if (dx <= dMin && dy <= dMin) { 
			// d : distance entre E et U
			float d = (float)Math.sqrt(Outils.norme2AB(u.getPosition(), e.getPosition()));
			return (d <= dMin);
		}
		else return false;
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
	public void draw(Graphics g, float ratio, Vect2 offSet, Images images, int campJoueurImpl) {
		float rayon = rayonEntite * ratio;
		
		int posX = (int)offSet.x + (int)Math.floor(position.x * ratio - rayon);
		int posY = (int)offSet.y + (int)Math.floor(position.y * ratio - rayon);
		int r = (int)(rayon * 2);
		
		g.setColor(color);
		if (imageName != null) {
			Image image = images.getImage(imageName);
			AffineTransform rotation = new AffineTransform();
			rotation.translate(posX , posY);
			rotation.scale(r / (float)(image.getWidth(null)), r / (float)(image.getHeight(null)));
			rotation.rotate(angle, (int)(image.getWidth(null)/2),(int)(image.getHeight(null)/2));
			((Graphics2D)g).drawImage(image, rotation, null);
		}
		
		int posX2 = (int)offSet.x + (int)Math.floor(position.x * ratio - rayon/3);
		int posY2 = (int)offSet.y + (int)Math.floor(position.y * ratio - rayon/3);
		int r2 = (int)(rayon * 2f/3f);
		g.fillOval(posX2, posY2, r2, r2);

		if (camp == campJoueurImpl) {
			g.setColor(Color.WHITE);
			int posX3 = (int)offSet.x + (int)Math.floor((position.x - porteeA) * ratio);
			int posY3 = (int)offSet.y + (int)Math.floor((position.y - porteeA) * ratio);
			int r3 = (int)Math.floor(porteeA * ratio * 2);
			g.drawOval(posX3, posY3, r3, r3);
		}
		
		float ratioVie = (float)vie / (float)vieMax;
		int hauteurBar = (int)Math.floor(5 * ratio);
		g.setColor(color);
		g.fillRect(posX, posY - hauteurBar, (int)Math.floor(r * ratioVie), hauteurBar);
		
		
		
	}
	
	
}
