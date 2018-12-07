package partie.rmi;

import java.util.HashMap;

import partie.core.Armee;
import partie.core.Base;
import partie.core.CacUnite;
import partie.core.Entite;
import partie.core.Groupe;
import partie.core.Outils;
import partie.core.TypeUnite;
import partie.core.Unite;
import partie.core.VarPartie;
import partie.core.Vect2;

public class ServeurPartieImpl implements ServeurPartie {
	
	private HashMap<Integer, Armee> entites;
	private HashMap<Integer, JoueurPartieImpl> joueurs;
	private boolean finPartie;
	
	private int widthP = VarPartie.WIDTH_PARTIE;
	private int heightP = VarPartie.HEIGHT_PARTIE;
	
	/**
	 * Constructeur du serveur<li>
	 * Il lance l'initialisationeet et la boucle du jeu
	 */
	public ServeurPartieImpl() {
		
		initialiserPartie();
		bouclePartie();
	}
	
	/**
	 * Initialise la partie<li>
	 * 	Créer les différents HashMap<li>
	 * 	Créer les joueurs<li>
	 * 	Initialise les armées et l'objectif de chaque groupe
	 */
	private void initialiserPartie() {
		// Initialisation des HashMap
		joueurs = new HashMap<Integer, JoueurPartieImpl>();
		entites = new HashMap<Integer, Armee>();
		
		// Initialise les 4 joueurs 
		joueurs.put(1, new JoueurPartieImpl(this, 1));
		joueurs.put(2, new JoueurPartieImpl(this, 2));
		joueurs.put(3, new JoueurPartieImpl(this, 3));
		joueurs.put(4, new JoueurPartieImpl(this, 4));
		
		int offSet = 35;
		// Initialise les 4 armées
		Armee a1 = new Armee();
		a1.setBase(new Base(new Vect2(offSet,offSet), 1));
		
		Armee a2 = new Armee();
		a2.setBase(new Base(new Vect2(widthP-offSet,offSet), 2));

		Armee a3 = new Armee();
		a3.setBase(new Base(new Vect2(widthP-offSet,heightP-offSet), 3));

		Armee a4 = new Armee();
		a4.setBase(new Base(new Vect2(offSet,heightP-offSet), 4));
		
		// Initialise les 3 objectifs de chaque armée sur chaque base énemies
		a1.getGroupes().get(0).setObjectif(a2.getBase().getPosition());
		a1.getGroupes().get(1).setObjectif(a3.getBase().getPosition());
		a1.getGroupes().get(2).setObjectif(a4.getBase().getPosition());

		a2.getGroupes().get(0).setObjectif(a3.getBase().getPosition());
		a2.getGroupes().get(1).setObjectif(a4.getBase().getPosition());
		a2.getGroupes().get(2).setObjectif(a1.getBase().getPosition());
		
		a3.getGroupes().get(0).setObjectif(a4.getBase().getPosition());
		a3.getGroupes().get(1).setObjectif(a1.getBase().getPosition());
		a3.getGroupes().get(2).setObjectif(a2.getBase().getPosition());

		a4.getGroupes().get(0).setObjectif(a1.getBase().getPosition());
		a4.getGroupes().get(1).setObjectif(a2.getBase().getPosition());
		a4.getGroupes().get(2).setObjectif(a3.getBase().getPosition());
		
		// Rajoute les armées dans le Hashmap des unités
		entites.put(1, a1);
		entites.put(2, a2);
		entites.put(3, a3);
		entites.put(4, a4);
	}
	
	/**
	 * Lance la boucle du jeu<li>
	 *  Gère les déplacements des unités<li>
	 *  Envoie aux joueurs la liste des armées pour que ces derniers puissent les afficher
	 */
	private void bouclePartie() {
		finPartie = true;
		long dt = 0;
		long previousTime = System.currentTimeMillis();
		long currentTime;
		
		while (finPartie) {
			currentTime = System.currentTimeMillis();
			dt += currentTime - previousTime;
			// Permet de gérer la fréquence de calcul
			if (dt > 1000/40) { 
				
				// Met à jour tous les groupes un par un
				for (Integer i : entites.keySet()) {
					for (int j = 0; j < entites.get(i).getGroupes().size(); j++) {
						updateGroupe(entites.get(i).getGroupes().get(j), dt);
					}
				}

				// Envoie à tous les joueurs la liste des armées afin qu'ils les affichent
				for (Integer i : joueurs.keySet()) {
					joueurs.get(i).update(entites);
				}
				dt = 0;
			}

			previousTime = currentTime;
			
		}
	}
	
	/**
	 * Met à jour un groupe en calculant ce que fait chaque unité
	 * @param grp
	 * 				groupe à mettre à jour
	 * @param dt
	 * 				temps depuis la dernière maj
	 */
	public void updateGroupe(Groupe grp, float dt) {
		
		// Si le groupe n'est pas vide
		if (grp.getUnites().size() > 0) {
			// U pointe sur la première unite du groupe
			Unite u = grp.getUnites().get(0); 
			u.update(dt); // update l'unité (cooldown, ...)
			
			// Calcule et renvoie l'unité la plus proche à distance d'attaque de U
			Entite entiteCible = entiteAAttaquer(u); 
			
			if (entiteCible != null) {
				// Si la cible pointe sur une entité, U attaque la cible
				attaqueEntite(u, entiteCible); 
			} else { 
				// Sinon, si la cible est null, on calcule le deplacement de U
				// d la distance entre U et l'objectif
				float d = (float)Math.sqrt(Outils.norme2AB(grp.getObjectif(), u.getPosition()));
				// dd la distance de deplacement de U pendant dt en fonction de sa vitesse
				float dd = (dt / 1000)  * (float)u.getVitesseDeplacement() / d;

				// Théorème de Thales => on calcule dx et dy les déplacements en x et y
				float dx = dd * (grp.getObjectif().x - u.getPosition().x);
				float dy = dd * (grp.getObjectif().y - u.getPosition().y);
				u.deplacement(dx, dy); // application de ces déplacements
				
			}
			
			// On parcourt le reste du groupe
			for (int i = 1; i < grp.getUnites().size(); i++) {
				// U pointe sur la (i + 1)e unité du groupe
				u = grp.getUnites().get(i);
				u.update(dt); // update l'unite (cooldown, ...)
				
				// Calcule et renvoie l'unité la plus proche à distance d'attaque de U
				entiteCible = entiteAAttaquer(u);
				
				if (entiteCible != null) {
					attaqueEntite(u, entiteCible);
				} else {
					// U0 pointe sur la (i)e unité du groupe (l'unité devant U)
					Unite u0 = grp.getUnites().get(i-1);// U pointe sur la (i + 1)e unité du groupe
					
					// d la distance entre U et U0
					float d = (float)Math.sqrt(Outils.norme2AB(u0.getPosition(), u.getPosition()));
					if (d > (u.getRayonUnite() + u0.getRayonUnite())) {
						// Si U n'est pas en contact de U0, alors U avance
						// idem
						float dd = (dt / 1000)  * (float)u.getVitesseDeplacement() / d;
	
						float dx = dd * (u0.getPosition().x - u.getPosition().x);
						float dy = dd * (u0.getPosition().y - u.getPosition().y);
						u.deplacement(dx, dy);
					}
				}
			}
			
		}
		
	}
	
	/**
	 * Gère l'attaque de l'unité u sur l'entité cible
	 * @param u
	 * 				attaquant
	 * @param cible
	 * 				entité qui se fait attaquer
	 */
	private void attaqueEntite(Unite u, Entite cible) {
		// Si U peut attaquer (le cooldown de U est à 0)
		if (u.canAttack()) {
			// U attaque la cible et renvoie vrai si elle tue la cible, faux sinon
			boolean tue = u.attaque(cible);
			if (tue) {
				if (cible instanceof Base) {
					// Si l'unité tuée est une base
					System.out.println("Joueur " + cible.getCamp() + "éliminé");
				} else 
					if (cible instanceof Unite) {
						// Si l'unité tuée est une unité
						// Donne l'argent de l'élimination au joueur "assassin"
						joueurs.get(u.getCamp()).ajouterArgent(((Unite)cible).getCout());
						// Supprime l'unité tuée de la liste d'entités
						supprimerUnite((Unite)cible);
				}
			}
		}
	}
	
	/**
	 * Définit l'entité que u doit attaquer 
	 * @param unite
	 * 				l'attaquant
	 * @return une entité à portée de l'attaquant et rien si aucune entité vérifie ce critère  
	 */
	private Entite entiteAAttaquer(Unite unite) {
		Entite e = null;
		
		// Parcourt toutes les entités de la partie
		for (Integer i : entites.keySet()) {
			// Regarde seulelement les unités des autres camps
			if (i != unite.getCamp()) {
				
				if (aPorteeDe(entites.get(i).getBase(), unite)) {
					// Si la base est à portée d'attaque, alors elle devient la cible
					e = entites.get(i).getBase();
				}
				
				// Parcourt toutes les unités
				for (int j = 0; j < entites.get(i).getGroupes().size(); j++) {
					for (Unite u : entites.get(i).getGroupes().get(j).getUnites()) {
						if (aPorteeDe(u, unite)) {
							// Si l'unité est à portée d'attaque, alors elle devient la cible 
							// Remarque : les unités sont prioritaires face aux bases
							e = u;
						}
					}
				}
			}
		}
		
		return e;
		
	}
	
	/**
	 * Calcule si l'entité e est à portée d'attaque de l'attaquant u
	 * @param e
	 * 				l'entité cible 
	 * @param u
	 * 				l'attaquant
	 * @return vrai si l'attaquant est à portée de tir de la cible et faux sinon
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
	 * Créer une unité selon typeU et la place dans le bon camp et le grp selectionné par le joueur
	 * @param camp
	 * 				Le camp du joueur
	 * @param typeU
	 * 				Le tupe d'unité créée
	 * @param grpSelect 
	 * 				Le groupe sélectionné par le joueur lors de la création de l'unité
	 */
	public void ajouterUnite(int camp, TypeUnite typeU, int grpSelect) {
		Unite u;
		
		// Créer la bonne unité selon typeU et la place dans le bon camp et le grp selectionné par le joueur
		switch (typeU) {
		case CAC:
			u = new CacUnite(entites.get(camp).getBase().getPosition(), camp);
			break;
		case DISTANT:
			u = new CacUnite(entites.get(camp).getBase().getPosition(), camp);
			break;
		case TANK:
			u = new CacUnite(entites.get(camp).getBase().getPosition(), camp);
			break;
		default:
			u = new CacUnite(entites.get(camp).getBase().getPosition(), camp);
			break;
		
		}
		entites.get(camp).getGroupes().get(grpSelect - 1).addUnite(u); // (grpSelect - 1) car grpSelect commence à 1 (et les listes à 0)
	}
	
	/**
	 * Supprime l'unité en paramètre de la liste des unités du serveur
	 * @param u
	 * 				L'unité à supprimer
	 */
	private void supprimerUnite(Unite u) {
		// Parcourt tous les groupes et supprimes U lorsque celle-ci est trouvée
		for (Groupe g : entites.get(u.getCamp()).getGroupes()) {
			g.getUnites().remove(u);
		}
	}
	
	
	public static final void main(String[] args) {
		new ServeurPartieImpl();
	}

}
