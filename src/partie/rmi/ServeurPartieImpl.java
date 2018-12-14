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
	
		int offSet = 40;
		// Initialise les 4 armées
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
			// Permet de gérer la fréquence de calcul
			if (dt > LIMITEUR) { 
				
				// Met ï¿½ jour tous les groupes un par un
				for (Integer i : entites.keySet()) {
					entites.get(i).update(dt, entites, joueurs);
				}
				
				for (Integer i : joueurs.keySet()) {
					joueurs.get(i).update(entites);
				}
				dt = 0;
			}

			previousTime = currentTime;
			
		}
	}
	
	


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
		
		Unite u = uniteXmlLoader.createUnite(typeU, camp, entites.get(camp).getBase().getPosition());
		entites.get(camp).getGroupes().get(grpSelect - 1).addUnite(u); // (grpSelect - 1) car grpSelect commence à 1 (et les listes à 0)

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


	public boolean aDefence(int camp, Menu menu) {
		return (entites.get(camp).getBase().getDefence(menu) != null);
	}
	
	
	public static void main(String[] args) {
		new ServeurPartieImpl();
	}

}
