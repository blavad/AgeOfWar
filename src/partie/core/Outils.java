package partie.core;

import java.awt.Color;

import org.w3c.dom.Node;

public class Outils {
	
	public static final float DeuxPif = 2 * (float)Math.PI;

	/**
	 * Calcule la distance en norme 2 au carre entre p1 et p2
	 * @param p1 Vect2 : Position de p1
	 * @param p2 Vect2 : Position de p2
	 * @return float : la distance en norme 2 au carre entre p1 et p2
	 */
	public static float norme2AB(Vect2 p1, Vect2 p2) {
		return (float)(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}
	
	/**
	 * @param name String : Nom de la node enfant recherchee
	 * @param node Node : node parent
	 * @return La node enfant recherchee via son nom
	 */
	public static final Node getChild(String name, Node node) {
		boolean trouve = false;
		int i = 0;
		if (node.hasChildNodes()) {
			while(!trouve && i < node.getChildNodes().getLength()) {
				if (node.getChildNodes().item(i).getNodeName().equals(name)) {
					return node.getChildNodes().item(i);
				}
				else { i++; }
			}
		}
		return null;
	}
	
	/**
	 * Calcule l'angle du vecteur ayant comme base p1 et comme extremite p2 et l'horizontale
	 * @param p1 Vect2 : Base du vecteur
	 * @param p2 Vect2 : extremite du vecteur
	 * @return float
	 */
	public static final float getAngle(Vect2 p1, Vect2 p2) {
		float dx = p2.x - p1.x;
		float dy = p2.y - p1.y;
		return (float)Math.atan2(dy, dx);
	}
	
	/**
	 * Determine si typeU correspond a une defense
	 * @param typeU TypeUnite
	 * @return vrai si typeU represente une defense et faux sinon
	 */
	public static final boolean estDefense(TypeUnite typeU) {
		return !(typeU == TypeUnite.CAC || typeU == TypeUnite.DISTANT || typeU == TypeUnite.TANK);
	}
	
	/**
	 * Definit la couleur du joueur suivant son camp
	 * @return la couleur du joueur
	 */
	public static final Color defineColor(int camp) {
		Color color;
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
		case 5 :
			color = Color.GREEN;
			break;
		case 6 :
			color = Color.CYAN;
			break;
		case 7 :
			color = Color.ORANGE;
			break;
		case 8 :
			color = Color.YELLOW;
			break;
		default :
			color = Color.RED;
		}
		return color;
	}
	
}
