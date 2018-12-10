package partie.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import partie.core.TypeDefence;
import partie.core.TypeUnite;
import partie.rmi.JoueurPartieImpl;

public class InterfacePartie  extends JFrame {
	
	private JoueurPartieImpl joueurP;
	private Menu currentMenu;
	private Color color;

	private JPanel panGeneral;
	
	private JPanel panTop;
	private JLabel labArgent;
	
	private JPanel panBot;
	public enum Menu {GENERAL, UNITES, DEFENCES, DEF1, DEF2, DEF3}
	private JPanel panMenu;
	private JButton bUnites, bDefences;
	private JPanel panUnites;
	private JButton bUnite1, bUnite2, bUnite3, bBackU;
	private JPanel panDefences;
	private JButton bDef1, bDef2, bDef3, bBackD;
	private JPanel panDef1, panDef2, panDef3;
	
	private JPanel panGroupe;
	private JLabel labGroupe;
	
	private String pathFondMenu1 = "/Bordure1.jpg";
	private String pathFondMenu2 = "/Bordure2.jpg";
	private String pathFondMenu3 = "/Bordure3.jpg";
	private String pathIcon = "/icon.jpg";
	
	private JPanel panCenter;
	
	/**
	 * Constructeur de l'interface graphique<li>
	 *  Initialisation des différents JPanel et boutons
	 * @param j
	 * 			Joueur auquel l'interface est liée
	 */
	public InterfacePartie(JoueurPartieImpl j) {
		this.joueurP = j;
		this.color = defineColor();
		this.setIconImage(new ImageIcon(getClass().getResource(pathIcon)).getImage());
		
		this.setTitle("Age Of War");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //ferme le programme quand on ferme la fenetre
		this.setLocationRelativeTo(null); //place la fenetre au milieu de l'ecran
		this.setVisible(true);
		
		
		panGeneral = new JPanel();
		panGeneral.setLayout(new BorderLayout());
		panGeneral.setBackground(Color.BLACK);
		this.setContentPane(panGeneral);

		//----------------------------------------------------------------
		//Construction du Panel haut (Info)
		//----------------------------------------------------------------
		panTop = new PanelImageFond(pathFondMenu2);
		panTop.setLayout(new BorderLayout());
		
		//Label de l'argent
		labArgent = new JLabel(String.format("Argent : %d", joueurP.getArgent()));
		labArgent.setForeground(Color.WHITE);
		labArgent.setHorizontalAlignment(labArgent.CENTER);
		labArgent.setPreferredSize(new Dimension(300,30));
		panTop.add(labArgent, BorderLayout.CENTER);
		
		JPanel panBotCenter = new PanelImageFond(pathFondMenu3);
		panBotCenter.setLayout(new BorderLayout());
		panBotCenter.setPreferredSize(new Dimension(300,70));
		JLabel label = new JLabel("CAMP " + joueurP.getCamp());
		label.setHorizontalAlignment(label.CENTER);
		label.setForeground(color);
		panBotCenter.add(label, BorderLayout.CENTER);
		panTop.add(panBotCenter, BorderLayout.NORTH);
		
		

		//----------------------------------------------------------------
		//Construction du Panel bas (Menu)
		//----------------------------------------------------------------
		panBot = new JPanel();
		panBot.setLayout(new BorderLayout());
		panBot.setBackground(defineColor());
		this.currentMenu = Menu.GENERAL;

		// Construction du Menu General
		panMenu = createPanelMenu(Menu.GENERAL);
		
		bUnites = new ButtonImageFond(pathFondMenu3, 70, 45, new ButtonMenu(this, Menu.UNITES));
		bDefences = new ButtonImageFond(pathFondMenu3, 70, 45, new ButtonMenu(this, Menu.DEFENCES));

		JPanel panM = new PanelImageFond(pathFondMenu1);
		panM.add(bUnites);
		panM.add(bDefences);
		panMenu.add(panM, BorderLayout.CENTER);
		
		//Construction du Menu des Unites
		panUnites = createPanelMenu(Menu.UNITES);
		
		bUnite1 = new ButtonImageFond(pathFondMenu3, 65, 45, new ButtonUnite(this, TypeUnite.CAC));
		bUnite2 = new ButtonImageFond(pathFondMenu3, 65, 45, new ButtonUnite(this, TypeUnite.DISTANT));
		bUnite3 = new ButtonImageFond(pathFondMenu3, 65, 45, new ButtonUnite(this, TypeUnite.TANK));
		
		bBackU = new ButtonImageFond(pathFondMenu3, 41, 41, new ButtonBack(this));

		JPanel panU = new PanelImageFond(pathFondMenu1);
		panU.setBackground(Color.WHITE);
		panU.add(bUnite1);
		panU.add(bUnite2);
		panU.add(bUnite3);
		panU.add(bBackU);
		panUnites.add(panU, BorderLayout.CENTER);
		
		//Construction du Menu des defences
		panDefences = createPanelMenu(Menu.DEFENCES);
		
		bDef1 = new ButtonImageFond(pathFondMenu3, 65, 45, new ButtonMenu(this, Menu.DEF1));
		bDef2 = new ButtonImageFond(pathFondMenu3, 65, 45, new ButtonMenu(this, Menu.DEF2));
		bDef3 = new ButtonImageFond(pathFondMenu3, 65, 45, new ButtonMenu(this, Menu.DEF3));
		
		bBackD = new ButtonImageFond(pathFondMenu3, 41, 41, new ButtonBack(this));

		JPanel panD = new PanelImageFond(pathFondMenu1);
		panD.setBackground(Color.WHITE);
		panD.add(bDef1);
		panD.add(bDef2);
		panD.add(bDef3);
		panD.add(bBackD);
		panDefences.add(panD, BorderLayout.CENTER);
	
		//Construction du Menu de chaque Def
		panDef1 = createPanDef(Menu.DEF1);
		panDef2 = createPanDef(Menu.DEF2);
		panDef3 = createPanDef(Menu.DEF3);
		
		switchMenu(Menu.GENERAL);
		
		
		
		//Construction du Menu des groupes
		panGroupe = new JPanel();
		panGroupe.setPreferredSize(new Dimension(250, 80));
		panGroupe.setLayout(new BorderLayout());
		JPanel pan2 = new PanelImageFond(pathFondMenu2);
		labGroupe = new JLabel();
		labGroupe.setText(String.format("Groupe selectionné : %d", joueurP.getGroupeSelect()));
		labGroupe.setHorizontalAlignment(labGroupe.CENTER);
		labGroupe.setForeground(Color.WHITE);
		pan2.add(labGroupe);
		panGroupe.add(pan2, BorderLayout.NORTH);
		//JPanel panGroupeA = new PanelImageFond(pathFondMenu1);

		JPanel panGroupeA = new JPanel();
		panGroupeA.setBackground(Color.BLACK);
		panGroupeA.add(new ButtonImageFond(pathFondMenu3, 70, 45, new ButtonGroupe(this, 1)));
		panGroupeA.add(new ButtonImageFond(pathFondMenu3, 70, 45, new ButtonGroupe(this, 2)));
		panGroupeA.add(new ButtonImageFond(pathFondMenu3, 70, 45, new ButtonGroupe(this, 3)));	
		panGroupe.add(panGroupeA, BorderLayout.CENTER);
		
		panBot.add(panGroupe, BorderLayout.NORTH);
		
		

		//----------------------------------------------------------------
		//Construction du Panel central (Champ de bataille)
		//----------------------------------------------------------------
		panCenter = new JPanel();
		panCenter.setPreferredSize(new Dimension(300, 350));
		panCenter.setBackground(Color.BLACK);
		
		
		
		panGeneral.add(panTop, BorderLayout.NORTH);
		panGeneral.add(panBot, BorderLayout.SOUTH);
		panGeneral.add(panCenter, BorderLayout.CENTER);
		
		this.pack();
	}
	
	
	
	private class ButtonImageFond extends JButton {

		private static final long serialVersionUID = 1L;
		Image fond;
		
		public ButtonImageFond (String pathImage, int w, int h, ActionListener act) {
			setPreferredSize(new Dimension(w, h));
			addActionListener(act);
			fond = new ImageIcon(getClass().getResource(pathImage)).getImage();
		}
		
		public void paintComponent(Graphics g){
	        if(fond!=null){
	            Graphics2D g2d = (Graphics2D)g;
	            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	            g2d.drawImage(fond, 0, 0, getWidth(), getHeight(), null);
	        }
	    }
		
	}
	
	/**
	 * Créer un JPanel de menu
	 * @param m
	 * 			Onglet du menu associé
	 * @return un JPanel au bon format du Menu avec le nom de l'onglet associé
	 */
	private JPanel createPanelMenu(Menu m) {
		JPanel pan = new JPanel();
		pan.setPreferredSize(new Dimension(350, 80));
		pan.setLayout(new BorderLayout());
		JPanel pan2 = new PanelImageFond(pathFondMenu2);
		JLabel lab = new JLabel(m.toString());
		lab.setHorizontalAlignment(lab.CENTER);
		lab.setForeground(Color.WHITE);
		pan2.add(lab);
		pan.add(pan2, BorderLayout.NORTH);
		
		return pan;		
	}
	
	@SuppressWarnings("unused")
	private class PanelImageFond extends JPanel {
		
		private static final long serialVersionUID = 1L;
		Image fond;
		
		public PanelImageFond(String path) {
			fond = new ImageIcon(getClass().getResource(path)).getImage();
		}
		
		
		@Override
	    public void paintComponent(Graphics g){
	        if(fond!=null){
	            Graphics2D g2d = (Graphics2D)g;
	            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	            g2d.drawImage(fond, 0, 0, getWidth(), getHeight(), null);
	        }
	    }
		
		
	}
	
	/**
	 * Créer un JPanel de menu (défence)
	 * @param m
	 * 			Onglet du menu associé
	 * @return un JPanel au bon format du Menu avec le nom de l'onglet associé
	 */
	private JPanel createPanDef(Menu m) {
		JPanel panP = new JPanel();
		panP.setPreferredSize(new Dimension(350, 80));
		panP.setLayout(new BorderLayout());
		
		JPanel pan2 = new PanelImageFond(pathFondMenu2);
		JLabel lab = new JLabel(m.toString());
		lab.setHorizontalAlignment(lab.CENTER);
		lab.setForeground(Color.WHITE);
		pan2.add(lab);
		panP.add(pan2, BorderLayout.NORTH);
		
		JPanel pan = new PanelImageFond(pathFondMenu1);
		pan.setBackground(Color.WHITE);
		pan.add(new ButtonImageFond(pathFondMenu3, 50, 45, new ButtonDef(this, TypeDefence.I)));
		pan.add(new ButtonImageFond(pathFondMenu3, 50, 45, new ButtonDef(this, TypeDefence.II)));
		pan.add(new ButtonImageFond(pathFondMenu3, 50, 45, new ButtonDef(this, TypeDefence.III)));
		pan.add(new ButtonImageFond(pathFondMenu3, 60, 45, new ButtonSellDef(this)));
		pan.add(new ButtonImageFond(pathFondMenu3, 41, 41, new ButtonBack(this)));
		panP.add(pan, BorderLayout.CENTER);
		
		return panP;
	}
	
	public JPanel getCenterPan() { return this.panCenter; }
	
	/**
	 * Définit la couleur du joueur suivant son camp
	 * @return la couleur du joueur
	 */
	private Color defineColor() {
		switch (joueurP.getCamp()) {
		case 1 :
			return Color.RED;
		case 2 :
			return Color.BLUE;
		case 3 :
			return Color.DARK_GRAY;
		case 4 :
			return Color.MAGENTA;
		}
		return Color.RED;
	}
	
	/**
	 * Affiche le menu sélectionné 
	 * @param menu
	 * 			Nouveau menu sélectionné
	 */
	public void switchMenu(Menu menu) {
		// Rend invisible l'ancien menu
		switch (currentMenu) {
		case DEF1:
			panDef1.setVisible(false);
			break;
		case DEF2:
			panDef2.setVisible(false);
			break;
		case DEF3:
			panDef3.setVisible(false);
			break;
		case DEFENCES:
			panDefences.setVisible(false);
			break;
		case GENERAL:
			panMenu.setVisible(false);
			break;
		case UNITES:
			panUnites.setVisible(false);
			break;
		default:
			break;
		
		}
		// Affiche le menu selectionné
		switch (menu) {
		case GENERAL : 
			panBot.add(panMenu, BorderLayout.SOUTH);
			panMenu.setVisible(true);
			break;
		case UNITES :
			panBot.add(panUnites, BorderLayout.SOUTH);
			panUnites.setVisible(true);
			break;
		case DEFENCES :
			panBot.add(panDefences, BorderLayout.SOUTH);
			panDefences.setVisible(true);
			break;
		case DEF1:
			panBot.add(panDef1, BorderLayout.SOUTH);
			panDef1.setVisible(true);
			break;
		case DEF2:
			panBot.add(panDef2, BorderLayout.SOUTH);
			panDef2.setVisible(true);
			break;
		case DEF3:
			panBot.add(panDef3, BorderLayout.SOUTH);
			panDef3.setVisible(true);
			break;
		default:
			break;
		}
		
		currentMenu = menu;
	}
	
	/**
	 * Revient au menu précédent
	 */
	public void backMenu() {
		switch (currentMenu) {
		case DEF1:
			switchMenu(Menu.DEFENCES);
			break;
		case DEF2:
			switchMenu(Menu.DEFENCES);
			break;
		case DEF3:
			switchMenu(Menu.DEFENCES);
			break;
		case DEFENCES:
			switchMenu(Menu.GENERAL);
			break;
		case GENERAL:
			break;
		case UNITES:
			switchMenu(Menu.GENERAL);
			break;
		default:
			break;
		
		}
	}
	
	/**
	 * Met à jour le Label qui affiche l'argent
	 */
	public void changementArgent() {
		labArgent.setText(String.format("Argent : %d", joueurP.getArgent()));
	}
	/**
	 * Met à jour le Label qui affiche le groupe sélectionné
	 */
	public void changementGroupe() {
		labGroupe.setText(String.format("Groupe selectionné : %d", joueurP.getGroupeSelect()));
	}

	public Menu getCurrentMenu() { return this.currentMenu; }
	public JoueurPartieImpl getJoueurP() { return this.joueurP; }
	
	
	
	private class ButtonMenu implements ActionListener {

		private InterfacePartie interfaceP;
		private Menu menu;
		
		ButtonMenu (InterfacePartie i,Menu menu) {
			this.interfaceP = i;
			this.menu = menu;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			interfaceP.switchMenu(menu);
		}
	}
	private class ButtonUnite implements ActionListener {

		private InterfacePartie interfaceP;
		private TypeUnite typeU;
		
		ButtonUnite (InterfacePartie i, TypeUnite type) {
			this.interfaceP = i;
			this.typeU = type;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			interfaceP.getJoueurP().creerUnite(typeU);
		}
	}
	private class ButtonDef implements ActionListener {

		private InterfacePartie interfaceP;
		private TypeDefence typeD;
		
		ButtonDef (InterfacePartie i, TypeDefence type) {
			this.interfaceP = i;
			this.typeD = type;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			interfaceP.getJoueurP().creerDefence(interfaceP.getCurrentMenu(), typeD);
		}
	}	
	private class ButtonSellDef implements ActionListener {

		private InterfacePartie interfaceP;
		
		ButtonSellDef (InterfacePartie i) {
			this.interfaceP = i;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			interfaceP.getJoueurP().vendreDefence(interfaceP.getCurrentMenu());
		}
	}
	private class ButtonBack implements ActionListener {

	private InterfacePartie interfaceP;
		
		ButtonBack (InterfacePartie i) {
			this.interfaceP = i;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			interfaceP.backMenu();
		}
	}
	
	
	private class ButtonGroupe implements ActionListener {

		private InterfacePartie interfaceP;
		private int num;
		
		ButtonGroupe (InterfacePartie i, int num) {
			this.interfaceP = i;
			this.num = num;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			interfaceP.getJoueurP().selectionneGroupe(num);;
		}
	}

}



