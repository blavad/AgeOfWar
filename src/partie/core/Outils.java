package partie.core;

public class Outils {
	
	public static final float DeuxPif = 2 * (float)Math.PI;

	public static float norme2AB(Vect2 p1, Vect2 p2) {
		return (float)(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}
	
}
