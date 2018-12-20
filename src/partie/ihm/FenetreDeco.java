package partie.ihm;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FenetreDeco extends JFrame {

	private String pathIcon = "/icon.jpg";
	
	public FenetreDeco() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage(new ImageIcon(getClass().getResource(pathIcon)).getImage());
		this.setLocationRelativeTo(null); //place la fenetre au milieu de l'ecran
		this.setVisible(true);
		
		JPanel pan = new JPanel();
		JLabel lab = new JLabel("Vous avez ete deconnecte avec l'hote");
		lab.setPreferredSize(new Dimension(400, 150));
		lab.setHorizontalAlignment(lab.CENTER);
		lab.setVerticalAlignment(lab.CENTER);
		lab.setBackground(Color.BLACK);
		pan.add(lab);
		this.setContentPane(pan);
		
		this.pack();
		
	}
	
	
}
