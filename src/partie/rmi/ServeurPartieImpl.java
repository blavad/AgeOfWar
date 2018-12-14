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
	 * 	Cr�er les diff�rents HashMap<li>
	 * 	Cr�er les joueurs<li>
	 * 	Initialise les arm�es et l'objectif de chaque groupe
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
		// Initialise les 4 armees
		Armee a1 = new Armee();
		a1.setBase(new Base(new Vect2(offSet,offSet), 1));
		
		Armee a2 = new Armee();
		a2.setBase(new Base(new Vect2(widthP-offSet,offSet), 2));

		Armee a3 = new Armee();
		a3.setBase(new Base(new Vect2(widthP-offSet,heightP-offSet), 3));

		Armee a4 = new Armee();
		a4.setBase(new Base(new Vect2(offSet,heightP-offSet), 4));
		
		// Initialise les 3 objectifs de chaque arm�e sur chaque base �nemies
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
		
		// Rajoute les arm�es dans le Hashmap des unit�s
		entites.put(1, a1);
		entites.put(2, a2);
		entites.put(3, a3);
		entites.put(4, a4);
	}
	
	/**
	 * Lance la boucle du jeu<li>
	 *  G�re les d�placements des unit�s<li>
	 *  Envoie aux joueurs la liste des arm�es pour que ces derniers puissent les afficher
	 */
	private void bouclePartie() {
		finPartie = true;
		long dt = 0;
		long previousTime = System.currentTimeMillis();
		long currentTime;
		
		while (finPartie) {
			currentTime = System.currentTimeMillis();
			dt += currentTime - previousTime;
			// Permet de g�rer la fr�quence de calcul
			if (dt > 1000/40) { 
				
				// Met � jour tous les groupes un par un
				for (Integer i : entites.keySet()) {
					for (int j = 0; j < entites.get(i).getGroupes().size(); j++) {
						updateGroupe(entites.get(i).getGroupes().get(j), dt);
					}
				}

				// Envoie � tous les joueurs la liste des arm�es afin qu'ils les affichent
				for (Integer i : joueurs.keySet()) {
					joueurs.get(i).update(entites);
				}
				dt = 0;
			}

			previousTime = currentTime;
			
		}
	}
	
	/**
	 * Met � jour un groupe en calculant ce que fait chaque unit�
	 * @param grp
	 * 				groupe � mettre � jour
	 * @param dt
	 * 				temps depuis la derni�re maj
	 */
	public void updateGroupe(Groupe grp, float dt) {
		
		// Si le groupe n'est pas vide
		if (grp.getUnites().size() > 0) {
			// U pointe sur la premi�re unite du groupe
			Unite u = grp.getUnites().get(0); 
			u.update(dt); // update l'unit� (cooldown, ...)
			
			// Calcule et renvoie l'unit� la plus proche � distance d'attaque de U
			Entite entiteCible = entiteAAttaquer(u); 
			
			if (entiteCible != null) {
				// Si la cible pointe sur une entit�, U attaque la cible
				attaqueEntite(u, entiteCible); 
			} else { 
				// Sinon, si la cible est null, on calcule le deplacement de U
				// d la distance entre U et l'objectif
				float d = (float)Math.sqrt(Outils.norme2AB(grp.getObjectif(), u.getPosition()));
				// dd la distance de deplacement de U pendant dt en fonction de sa vitesse
				float dd = (dt / 1000)  * (float)u.getVitesseDeplacement() / d;

				// Th�or�me de Thales => on calcule dx et dy les d�placements en x et y
				float dx = dd * (grp.getObjectif().x - u.getPosition().x);
				float dy = dd * (grp.getObjectif().y - u.getPosition().y);
				u.deplacement(dx, dy); // application de ces d�placements
				
			}
			
			// On parcourt le reste du groupe
			for (int i = 1; i < grp.getUnites().size(); i++) {
				// U pointe sur la (i + 1)e unit� du groupe
				u = grp.getUnites().get(i);
				u.update(dt); // update l'unite (cooldown, ...)
				
				// Calcule et renvoie l'unit� la plus proche � distance d'attaque de U
				entiteCible = entiteAAttaquer(u);
				
				if (entiteCible != null) {
					attaqueEntite(u, entiteCible);
				} else {
					// U0 pointe sur la (i)e unit� du groupe (l'unit� devant U)
					Unite u0 = grp.getUnites().get(i-1);// U pointe sur la (i + 1)e unit� du groupe
					
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
	 * G�re l'attaque de l'unit� u sur l'entit� cible
	 * @param u
	 * 				attaquant
	 * @param cible
	 * 				entit� qui se fait attaquer
	 */
	private void attaqueEntite(Unite u, Entite cible) {
		// Si U peut attaquer (le cooldown de U est � 0)
		if (u.canAttack()) {
			// U attaque la cible et renvoie vrai si elle tue la cible, faux sinon
			boolean tue = u.attaque(cible);
			if (tue) {
				if (cible instanceof Base) {
					// Si l'unit� tu�e est une base
					System.out.println("Joueur " + cible.getCamp() + "�limin�");
				} else 
					if (cible instanceof Unite) {
						// Si l'unit� tu�e est une unit�
						// Donne l'argent de l'�limination au joueur "assassin"
						joueurs.get(u.getCamp()).ajouterArgent(((Unite)cible).getCout());
						// Supprime l'unit� tu�e de la liste d'entit�s
						supprimerUnite((Unite)cible);
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
	private Entite entiteAAttaquer(Unite unite) {
		Entite e = null;
		
		// Parcourt toutes les entit�s de la partie
		for (Integer i : entites.keySet()) {
			// Regarde seulelement les unit�s des autres camps
			if (i != unite.getCamp()) {
				
				if (aPorteeDe(entites.get(i).getBase(), unite)) {
					// Si la base est � port�e d'attaque, alors elle devient la cible
					e = entites.get(i).getBase();
				}
				
				// Parcourt toutes les unit�s
				for (int j = 0; j < entites.get(i).getGroupes().size(); j++) {
					for (Unite u : entites.get(i).getGroupes().get(j).getUnites()) {
						if (aPorteeDe(u, unite)) {
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
	 * Cr�er une unit� selon typeU et la place dans le bon camp et le grp selectionn� par le joueur
	 * @param camp
	 * 				Le camp du joueur
	 * @param typeU
	 * 				Le tupe d'unit� cr��e
	 * @param grpSelect 
	 * 				Le groupe s�lectionn� par le joueur lors de la cr�ation de l'unit�
	 */
	public void ajouterUnite(int camp, TypeUnite typeU, int grpSelect) {
		Unite u;
		
		// Cr�er la bonne unit� selon typeU et la place dans le bon camp et le grp selectionn� par le joueur
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
		entites.get(camp).getGroupes().get(grpSelect - 1).addUnite(u); // (grpSelect - 1) car grpSelect commence � 1 (et les listes � 0)
	}
	
	/**
	 * Supprime l'unit� en param�tre de la liste des unit�s du serveur
	 * @param u
	 * 				L'unit� � supprimer
	 */
	private void supprimerUnite(Unite u) {
		// Parcourt tous les groupes et supprimes U lorsque celle-ci est trouv�e
		for (Groupe g : entites.get(u.getCamp()).getGroupes()) {
			g.getUnites().remove(u);
		}
	}
	
	
	public static void main(String[] args) {
		new ServeurPartieImpl();
	}

}
