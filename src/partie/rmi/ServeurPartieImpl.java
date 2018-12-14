package partie.rmi;

import java.util.HashMap;

import partie.core.Armee;
import partie.core.Base;
import partie.core.Defence;
import partie.core.Entite;
import partie.core.Groupe;
import partie.core.Outils;
import partie.core.TypeDefense;
import partie.core.TypeUnite;
import partie.core.Unite;
import partie.core.UniteXmlLoader;
import partie.core.VarPartie;
import partie.core.Vect2;
import partie.ihm.InterfacePartie.Menu;

public class ServeurPartieImpl implements ServeurPartie {
	
	private HashMap<Integer, Armee> entites;
	private HashMap<Integer, JoueurPartieImpl> joueurs;
	private boolean finPartie;
	private UniteXmlLoader uniteXmlLoader;
	
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
	 * 	Crï¿½er les diffï¿½rents HashMap<li>
	 * 	Crï¿½er les joueurs<li>
	 * 	Initialise les armï¿½es et l'objectif de chaque groupe
	 */
	private void initialiserPartie() {
		// Initialisation des HashMap
		joueurs = new HashMap<Integer, JoueurPartieImpl>();
		entites = new HashMap<Integer, Armee>();
		uniteXmlLoader = new UniteXmlLoader();
		
		// Initialise les 4 joueurs 
		joueurs.put(1, new JoueurPartieImpl(this, 1));
		joueurs.put(2, new JoueurPartieImpl(this, 2));
		joueurs.put(3, new JoueurPartieImpl(this, 3));
		joueurs.put(4, new JoueurPartieImpl(this, 4));
		
<<<<<<< HEAD
		int offSet = 35;
		// Initialise les 4 armees
=======
		int offSet = 40;
		// Initialise les 4 armées
>>>>>>> 8fd4f4825c6cec70a1426a89ecc1f6e9626e4b29
		Armee a1 = new Armee();
		a1.setBase(new Base(new Vect2(offSet,offSet), 1));
		
		Armee a2 = new Armee();
		a2.setBase(new Base(new Vect2(widthP-offSet,offSet), 2));

		Armee a3 = new Armee();
		a3.setBase(new Base(new Vect2(widthP-offSet,heightP-offSet), 3));

		Armee a4 = new Armee();
		a4.setBase(new Base(new Vect2(offSet,heightP-offSet), 4));
		
		// Initialise les 3 objectifs de chaque armï¿½e sur chaque base ï¿½nemies
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
		
		// Rajoute les armï¿½es dans le Hashmap des unitï¿½s
		entites.put(1, a1);
		entites.put(2, a2);
		entites.put(3, a3);
		entites.put(4, a4);
	}
	
	/**
	 * Lance la boucle du jeu<li>
	 *  Gï¿½re les dï¿½placements des unitï¿½s<li>
	 *  Envoie aux joueurs la liste des armï¿½es pour que ces derniers puissent les afficher
	 */
	private void bouclePartie() {
		finPartie = true;
		long dt = 0;
		long previousTime = System.currentTimeMillis();
		long currentTime;
		float FPSLIMIT = 40;
		float LIMITEUR = 1000/FPSLIMIT;
		
		while (finPartie) {
			currentTime = System.currentTimeMillis();
			dt += currentTime - previousTime;
<<<<<<< HEAD
			// Permet de gï¿½rer la frï¿½quence de calcul
			if (dt > 1000/40) { 
=======
			// Permet de gérer la fréquence de calcul
			if (dt > LIMITEUR) { 
>>>>>>> 8fd4f4825c6cec70a1426a89ecc1f6e9626e4b29
				
				// Met ï¿½ jour tous les groupes un par un
				for (Integer i : entites.keySet()) {
					entites.get(i).update(dt, entites, joueurs);
				}
<<<<<<< HEAD

				// Envoie ï¿½ tous les joueurs la liste des armï¿½es afin qu'ils les affichent
=======
				
				
				// Envoie à tous les joueurs la liste des armées afin qu'ils les affichent
>>>>>>> 8fd4f4825c6cec70a1426a89ecc1f6e9626e4b29
				for (Integer i : joueurs.keySet()) {
					joueurs.get(i).update(entites);
				}
				dt = 0;
			}

			previousTime = currentTime;
			
		}
	}
	
<<<<<<< HEAD
	/**
	 * Met ï¿½ jour un groupe en calculant ce que fait chaque unitï¿½
	 * @param grp
	 * 				groupe ï¿½ mettre ï¿½ jour
	 * @param dt
	 * 				temps depuis la derniï¿½re maj
	 */
	public void updateGroupe(Groupe grp, float dt) {
		
		// Si le groupe n'est pas vide
		if (grp.getUnites().size() > 0) {
			// U pointe sur la premiï¿½re unite du groupe
			Unite u = grp.getUnites().get(0); 
			u.update(dt); // update l'unitï¿½ (cooldown, ...)
			
			// Calcule et renvoie l'unitï¿½ la plus proche ï¿½ distance d'attaque de U
			Entite entiteCible = entiteAAttaquer(u); 
			
			if (entiteCible != null) {
				// Si la cible pointe sur une entitï¿½, U attaque la cible
				attaqueEntite(u, entiteCible); 
			} else { 
				// Sinon, si la cible est null, on calcule le deplacement de U
				// d la distance entre U et l'objectif
				float d = (float)Math.sqrt(Outils.norme2AB(grp.getObjectif(), u.getPosition()));
				// dd la distance de deplacement de U pendant dt en fonction de sa vitesse
				float dd = (dt / 1000)  * (float)u.getVitesseDeplacement() / d;

				// Thï¿½orï¿½me de Thales => on calcule dx et dy les dï¿½placements en x et y
				float dx = dd * (grp.getObjectif().x - u.getPosition().x);
				float dy = dd * (grp.getObjectif().y - u.getPosition().y);
				u.deplacement(dx, dy); // application de ces dï¿½placements
				
			}
			
			// On parcourt le reste du groupe
			for (int i = 1; i < grp.getUnites().size(); i++) {
				// U pointe sur la (i + 1)e unitï¿½ du groupe
				u = grp.getUnites().get(i);
				u.update(dt); // update l'unite (cooldown, ...)
				
				// Calcule et renvoie l'unitï¿½ la plus proche ï¿½ distance d'attaque de U
				entiteCible = entiteAAttaquer(u);
				
				if (entiteCible != null) {
					attaqueEntite(u, entiteCible);
				} else {
					// U0 pointe sur la (i)e unitï¿½ du groupe (l'unitï¿½ devant U)
					Unite u0 = grp.getUnites().get(i-1);// U pointe sur la (i + 1)e unitï¿½ du groupe
					
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
	 * Gï¿½re l'attaque de l'unitï¿½ u sur l'entitï¿½ cible
	 * @param u
	 * 				attaquant
	 * @param cible
	 * 				entitï¿½ qui se fait attaquer
	 */
	private void attaqueEntite(Unite u, Entite cible) {
		// Si U peut attaquer (le cooldown de U est ï¿½ 0)
		if (u.canAttack()) {
			// U attaque la cible et renvoie vrai si elle tue la cible, faux sinon
			boolean tue = u.attaque(cible);
			if (tue) {
				if (cible instanceof Base) {
					// Si l'unitï¿½ tuï¿½e est une base
					System.out.println("Joueur " + cible.getCamp() + "ï¿½liminï¿½");
				} else 
					if (cible instanceof Unite) {
						// Si l'unitï¿½ tuï¿½e est une unitï¿½
						// Donne l'argent de l'ï¿½limination au joueur "assassin"
						joueurs.get(u.getCamp()).ajouterArgent(((Unite)cible).getCout());
						// Supprime l'unitï¿½ tuï¿½e de la liste d'entitï¿½s
						supprimerUnite((Unite)cible);
				}
			}
		}
	}
	
	/**
	 * Dï¿½finit l'entitï¿½ que u doit attaquer 
	 * @param unite
	 * 				l'attaquant
	 * @return une entitï¿½ ï¿½ portï¿½e de l'attaquant et rien si aucune entitï¿½ vï¿½rifie ce critï¿½re  
	 */
	private Entite entiteAAttaquer(Unite unite) {
		Entite e = null;
		
		// Parcourt toutes les entitï¿½s de la partie
		for (Integer i : entites.keySet()) {
			// Regarde seulelement les unitï¿½s des autres camps
			if (i != unite.getCamp()) {
				
				if (aPorteeDe(entites.get(i).getBase(), unite)) {
					// Si la base est ï¿½ portï¿½e d'attaque, alors elle devient la cible
					e = entites.get(i).getBase();
				}
				
				// Parcourt toutes les unitï¿½s
				for (int j = 0; j < entites.get(i).getGroupes().size(); j++) {
					for (Unite u : entites.get(i).getGroupes().get(j).getUnites()) {
						if (aPorteeDe(u, unite)) {
							// Si l'unitï¿½ est ï¿½ portï¿½e d'attaque, alors elle devient la cible 
							// Remarque : les unitï¿½s sont prioritaires face aux bases
							e = u;
						}
					}
				}
			}
		}
		
		return e;
		
	}
	
	/**
	 * Calcule si l'entitï¿½ e est ï¿½ portï¿½e d'attaque de l'attaquant u
	 * @param e
	 * 				l'entitï¿½ cible 
	 * @param u
	 * 				l'attaquant
	 * @return vrai si l'attaquant est ï¿½ portï¿½e de tir de la cible et faux sinon
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
=======
	
	
	
>>>>>>> 8fd4f4825c6cec70a1426a89ecc1f6e9626e4b29
	
	/**
	 * Crï¿½er une unitï¿½ selon typeU et la place dans le bon camp et le grp selectionnï¿½ par le joueur
	 * @param camp
	 * 				Le camp du joueur
	 * @param typeU
	 * 				Le tupe d'unitï¿½ crï¿½ï¿½e
	 * @param grpSelect 
	 * 				Le groupe sï¿½lectionnï¿½ par le joueur lors de la crï¿½ation de l'unitï¿½
	 */
	public void ajouterUnite(int camp, TypeUnite typeU, int grpSelect) {
		
<<<<<<< HEAD
		// Crï¿½er la bonne unitï¿½ selon typeU et la place dans le bon camp et le grp selectionnï¿½ par le joueur
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
		entites.get(camp).getGroupes().get(grpSelect - 1).addUnite(u); // (grpSelect - 1) car grpSelect commence ï¿½ 1 (et les listes ï¿½ 0)
=======
		Unite u = uniteXmlLoader.createUnite(typeU, camp, entites.get(camp).getBase().getPosition());
		entites.get(camp).getGroupes().get(grpSelect - 1).addUnite(u); // (grpSelect - 1) car grpSelect commence à 1 (et les listes à 0)
>>>>>>> 8fd4f4825c6cec70a1426a89ecc1f6e9626e4b29
	}

	public void ajouterDefence(int camp, TypeDefense typeD, Menu menu) {
		
		Defence d = uniteXmlLoader.createDefence(typeD, camp, entites.get(camp).getBase().getPosition());
		entites.get(camp).getBase().addDef(menu, d);
		
	}

	public void supprimerDefence(int camp, Menu menu) {
		Defence d = entites.get(camp).getBase().getDefence(menu); 
		if (d != null) joueurs.get(camp).ajouterArgent((int)Math.floor(d.getCout() * VarPartie.REMBOURSEMENT_UNITE));
		entites.get(camp).getBase().suppDef(menu);
	}
	
<<<<<<< HEAD
	/**
	 * Supprime l'unitï¿½ en paramï¿½tre de la liste des unitï¿½s du serveur
	 * @param u
	 * 				L'unitï¿½ ï¿½ supprimer
	 */
	private void supprimerUnite(Unite u) {
		// Parcourt tous les groupes et supprimes U lorsque celle-ci est trouvï¿½e
		for (Groupe g : entites.get(u.getCamp()).getGroupes()) {
			g.getUnites().remove(u);
		}
=======

	public boolean aDefence(int camp, Menu menu) {
		return (entites.get(camp).getBase().getDefence(menu) != null);
>>>>>>> 8fd4f4825c6cec70a1426a89ecc1f6e9626e4b29
	}
	
	
	public static void main(String[] args) {
		new ServeurPartieImpl();
	}

}
