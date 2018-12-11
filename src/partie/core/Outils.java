package partie.core;

import org.w3c.dom.Node;

public class Outils {
	
	public static final float DeuxPif = 2 * (float)Math.PI;

	public static float norme2AB(Vect2 p1, Vect2 p2) {
		return (float)(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}
	
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
	
}
