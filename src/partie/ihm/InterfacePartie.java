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

import partie.core.TypeDefense;
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
	public enum Menu {GENERAL, UNITES, DEFENSES, DEF1, DEF2, DEF3}
	private JPanel panMenu;
	private JPanel panUnites;
	private JPanel panDefenses;
	private JPanel panDef1, panDef2, panDef3;
	private JLabel venteDef1, venteDef2, venteDef3;
	
	private JPanel panGroupe;
	private JLabel labGroupe;
	
	private String pathFondMenu1 = "/Bordure1.jpg";
	private String pathFondMenu2 = "/Bordure2.jpg";
	private String pathFondMenu3 = "/Bordure3.jpg";
	private String pathIcon = "/icon.jpg";
	private String pathImageV1 = "/Vaisseau1.png";
	private String pathImageV2 = "/Vaisseau2.png";
	private String pathImageV3 = "/Vaisseau3.png";
	private String pathImageD1 = "/Defense1.png";
	private String pathImageD2 = "/Defense2.png";
	private String pathImageD3 = "/Defense3.png";
	private String pathFondBGroupe = "/Bordure3.jpg";
	private String pathFondBRetour = "/Bordure2.jpg";
	private String pathFondBUnite = "/Bordure1.jpg";
	
	
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
		
		JButton bUnites = buttonWithLabelFond(pathFondMenu3, "Unites", color, 90, 45, new ButtonMenu(this, Menu.UNITES));
		JButton bDefences = buttonWithLabelFond(pathFondMenu3, "Defenses", color, 90, 45, new ButtonMenu(this, Menu.DEFENSES));
		
		JPanel panM = new PanelImageFond(pathFondMenu1);
		panM.add(bUnites);
		panM.add(bDefences);
		panMenu.add(panM, BorderLayout.CENTER);
		
		//Construction du Menu des Unites
		panUnites = createPanelMenu(Menu.UNITES);
		
		JButton bUnite1 = buttonWithLabelFondImage(pathFondBUnite, pathImageV1, ""+joueurP.getUniteXmlLoader().getCout(TypeUnite.CAC), Color.ORANGE, 65, 45, new ButtonUnite(this, TypeUnite.CAC));
		JButton bUnite2 = buttonWithLabelFondImage(pathFondBUnite, pathImageV2, ""+joueurP.getUniteXmlLoader().getCout(TypeUnite.DISTANT), Color.ORANGE, 65, 45, new ButtonUnite(this, TypeUnite.DISTANT));
		JButton bUnite3 = buttonWithLabelFondImage(pathFondBUnite, pathImageV3, ""+joueurP.getUniteXmlLoader().getCout(TypeUnite.TANK), Color.ORANGE, 65, 45, new ButtonUnite(this, TypeUnite.TANK));
		
		JButton bBackU = new ButtonImageFond(pathFondBRetour, 35, 35, new ButtonBack(this));

		JPanel panU = new PanelImageFond(pathFondMenu1);
		panU.setBackground(Color.WHITE);
		panU.add(bUnite1);
		panU.add(bUnite2);
		panU.add(bUnite3);
		panU.add(bBackU);
		panUnites.add(panU, BorderLayout.CENTER);
		
		//Construction du Menu des defences
		panDefenses = createPanelMenu(Menu.DEFENSES);
		
		JButton bDef1 = buttonWithLabelFond(pathFondBUnite, "Def1", color, 65, 45, new ButtonMenu(this, Menu.DEF1));
		JButton bDef2 = buttonWithLabelFond(pathFondBUnite, "Def2", color, 65, 45, new ButtonMenu(this, Menu.DEF2));
		JButton bDef3 = buttonWithLabelFond(pathFondBUnite, "Def3", color, 65, 45, new ButtonMenu(this, Menu.DEF3));
		
		JButton bBackD = new ButtonImageFond(pathFondBRetour, 35, 35, new ButtonBack(this));

		JPanel panD = new PanelImageFond(pathFondMenu1);
		panD.setBackground(Color.WHITE);
		panD.add(bDef1);
		panD.add(bDef2);
		panD.add(bDef3);
		panD.add(bBackD);
		panDefenses.add(panD, BorderLayout.CENTER);
	
		//Construction du Menu de chaque Def
		panDef1 = createPanDef(Menu.DEF1);
		panDef2 = createPanDef(Menu.DEF2);
		panDef3 = createPanDef(Menu.DEF3);
		
		switchMenu(Menu.GENERAL);
		
		
		
		//Construction du Menu des groupes
		panGroupe = new JPanel();
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
		panGroupeA.add(buttonWithLabelFond(pathFondBGroupe, "1", Color.WHITE, 55, 40, new ButtonGroupe(this, 1)));
		panGroupeA.add(buttonWithLabelFond(pathFondBGroupe, "2", Color.WHITE, 55, 40, new ButtonGroupe(this, 2)));
		panGroupeA.add(buttonWithLabelFond(pathFondBGroupe, "3", Color.WHITE, 55, 40, new ButtonGroupe(this, 3)));	
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
	
	private JButton buttonWithLabelFond(String pathFond, String text, Color c, int w, int h, ActionListener act) {
	
		JButton b = new ButtonImageFond(pathFond, w, h, act);
		b.setLayout(new BorderLayout());
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(label.CENTER);
		label.setForeground(c);
		b.add(label, BorderLayout.CENTER);
		
		return b;
	}
	
	private JButton buttonWithLabelFondImage(String pathFond, String pathImage, String text, Color c, int w, int h, ActionListener act) {
		
		JButton b = new ButtonImageFond(pathFond, pathImage, w, h, act);
		b.setLayout(new BorderLayout());
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(label.CENTER);
		label.setForeground(c);
		b.add(label, BorderLayout.CENTER);
		
		return b;
	}
	
	
	private class ButtonImageFond extends JButton {

		private static final long serialVersionUID = 1L;
		private Image fond;
		private Image image;
		
		public ButtonImageFond (String pathFond, String pathImage, int w, int h, ActionListener act) {
			setPreferredSize(new Dimension(w, h));
			addActionListener(act);
			fond = new ImageIcon(getClass().getResource(pathFond)).getImage();
			image = new ImageIcon(getClass().getResource(pathImage)).getImage();
		}
		
		public ButtonImageFond (String pathFond, int w, int h, ActionListener act) {
			setPreferredSize(new Dimension(w, h));
			addActionListener(act);
			fond = new ImageIcon(getClass().getResource(pathFond)).getImage();
		}
		
		public void paintComponent(Graphics g){
	        if(fond != null){
	            Graphics2D g2d = (Graphics2D)g;
	            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	            g2d.drawImage(fond, 0, 0, getWidth(), getHeight(), null);
	        }
	        if(image != null){
	        	int offSetY = 0;
	        	int offSetX = (int)((getWidth() - getHeight()) / 2f);
	        	int largeur = getHeight();
	            g.drawImage(image, offSetX, offSetY, largeur, largeur, null);
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
		pan.add(buttonWithLabelFondImage(pathFondBUnite, pathImageD1, ""+joueurP.getUniteXmlLoader().getCout(TypeDefense.DEFI), Color.ORANGE, 65, 45, new ButtonDef(this, TypeDefense.DEFI)));
		pan.add(buttonWithLabelFondImage(pathFondBUnite, pathImageD2, ""+joueurP.getUniteXmlLoader().getCout(TypeDefense.DEFII), Color.ORANGE, 65, 45, new ButtonDef(this, TypeDefense.DEFII)));
		pan.add(buttonWithLabelFondImage(pathFondBUnite, pathImageD3, ""+joueurP.getUniteXmlLoader().getCout(TypeDefense.DEFIII), Color.ORANGE, 65, 45, new ButtonDef(this, TypeDefense.DEFIII)));
		
		pan.add(new ButtonImageFond(pathFondMenu3, 60, 45, new ButtonSellDef(this)));
		
		
		
		pan.add(new ButtonImageFond(pathFondBRetour, 35, 35, new ButtonBack(this)));
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
		case DEFENSES:
			panDefenses.setVisible(false);
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
		case DEFENSES :
			panBot.add(panDefenses, BorderLayout.SOUTH);
			panDefenses.setVisible(true);
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
			switchMenu(Menu.DEFENSES);
			break;
		case DEF2:
			switchMenu(Menu.DEFENSES);
			break;
		case DEF3:
			switchMenu(Menu.DEFENSES);
			break;
		case DEFENSES:
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
		private TypeDefense typeD;
		
		ButtonDef (InterfacePartie i, TypeDefense type) {
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



