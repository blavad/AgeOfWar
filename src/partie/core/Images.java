package partie.core;

import java.awt.Image;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class Images {
	
	private HashMap<String, Image> images;
	
	public Images() {
		images = new HashMap<String, Image>();
		images.put("Vaisseau0", new ImageIcon(getClass().getResource("/" + "Vaisseau0.png")).getImage());
		images.put("Vaisseau1", new ImageIcon(getClass().getResource("/" + "Vaisseau1.png")).getImage());
		images.put("Vaisseau2", new ImageIcon(getClass().getResource("/" + "Vaisseau2.png")).getImage());
		images.put("Vaisseau3", new ImageIcon(getClass().getResource("/" + "Vaisseau3.png")).getImage());
		images.put("Defense1", new ImageIcon(getClass().getResource("/" + "Defense1.png")).getImage());
		images.put("Defense2", new ImageIcon(getClass().getResource("/" + "Defense2.png")).getImage());
		images.put("Defense3", new ImageIcon(getClass().getResource("/" + "Defense3.png")).getImage());
	}
	
	public Image getImage(String name) {
		return images.get(name);
	}

}
