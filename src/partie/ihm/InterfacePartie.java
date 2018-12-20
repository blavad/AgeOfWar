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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.print.DocFlavor.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import partie.core.Outils;
import partie.core.TypeUnite;
import partie.core.VarPartie;
import partie.rmi.JoueurPartieImpl;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class InterfacePartie  extends JFrame {
	
	private JoueurPartieImpl joueurP;
	private Menu currentMenu;
	private Color color;
	private Clip clip;

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
	private JPanel panCenter;
	
	
	//----------------------------------------------------------------
	// Constantes
	//----------------------------------------------------------------
	// Chemin musique
	private String pathMusique = "/SpaceOfWar.wav";
	// Chemins images
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
	private String pathFondBRetour = "/BoutonRetour.png";
	private String pathFondBUnite = "/BoutonUnite.png";
	
	// Largeur de la fenetre
	private int prefWidth = 350;
	// Hauteur du plateau
	private int prefHeightPanCenter = 350;
	// Hauteur de la barre de menu
	private int prefHeightPanMenu = 80;
	// Hauteur du label argent
	private int prefHeightLabelArgent = 30;
	// Hauteur du panel qui affiche le camp du joueur
	private int prefHeightPanCamp = 70;
	// Dimension des boutons du menu general
	private int prefWidthBMGeneral = 90;
	private int prefHeightBMGeneral = 45;
	// Dimension des boutons des unites
	private int prefWidthBMUnite = 65;
	private int prefHeightBMUnite = 45;
	// Dimension des boutons retour
	private int prefWidthBMBack = 35;
	private int prefHeightBMBack = 35;
	// Dimension des boutons du menu groupe
	private int prefWidthBGroupe = 50;
	private int prefHeightBGroupe = 40;

	private Font font8 = new Font("Arial",Font.BOLD,8);
	private Font font10 = new Font("Arial",Font.BOLD,10);
	private Font font12 = new Font("Arial",Font.BOLD,12);
	
	/**
	 * Constructeur de l'interface graphique<li>
	 *  Initialisation des differents JPanel et boutons
	 * @param j JoueurPartieImpl : joueur auquel l'interface est liee
	 */
	public InterfacePartie(JoueurPartieImpl j) {
		this.joueurP = j;
		this.color = Outils.defineColor(joueurP.getCamp());
		this.setIconImage(new ImageIcon(getClass().getResource(pathIcon)).getImage());
		
		
		
		java.net.URL url = getClass().getResource(pathMusique);
		
		try {
			clip = AudioSystem.getClip();
			try(AudioInputStream audioIn = AudioSystem.getAudioInputStream(url)) {
				clip.open(audioIn);
			} catch (IOException | UnsupportedAudioFileException e) {
				e.printStackTrace();
			}

			clip.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
		
		this.setTitle("Space Of War");
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
		JPanel panTop2 = new JPanel();
		panTop2.setLayout(new BorderLayout());
		labArgent = new JLabel(String.format("Argent : %d", joueurP.getArgent()));
		labArgent.setForeground(Color.BLACK);
		labArgent.setHorizontalAlignment(labArgent.CENTER);
		panTop2.setPreferredSize(new Dimension(prefWidth, prefHeightLabelArgent));
		panTop2.add(labArgent, BorderLayout.CENTER);
		panTop2.add(buttonWithLabelFond(pathFondBUnite, "Musique", Color.WHITE, 90, 20, new ButtonMusique(this)), BorderLayout.WEST);
		panTop.add(panTop2, BorderLayout.CENTER);
		
		//Panel affichant le camp du joueur
		JPanel panCamp = new PanelImageFond(pathFondMenu3);
		panCamp.setLayout(new BorderLayout());
		panCamp.setPreferredSize(new Dimension(prefWidth, prefHeightPanCamp));
		JLabel label = new JLabel("CAMP " + joueurP.getCamp());
		label.setHorizontalAlignment(label.CENTER);
		label.setForeground(color);
		panCamp.add(label, BorderLayout.CENTER);
		panTop.add(panCamp, BorderLayout.NORTH);
		
		

		//----------------------------------------------------------------
		//Construction du Panel bas (Menu)
		//----------------------------------------------------------------
		panBot = new JPanel();
		panBot.setLayout(new BorderLayout());
		panBot.setBackground(color);
		this.currentMenu = Menu.GENERAL;

		// Construction du Menu General
		panMenu = createPanelMenu(Menu.GENERAL);
		
		JButton bUnites   = buttonWithLabelFond(pathFondMenu3, "Unites",   color, prefWidthBMGeneral, prefHeightBMGeneral, new ButtonMenu(this, Menu.UNITES));
		JButton bDefences = buttonWithLabelFond(pathFondMenu3, "Defenses", color, prefWidthBMGeneral, prefHeightBMGeneral, new ButtonMenu(this, Menu.DEFENSES));
		
		JPanel panM = new PanelImageFond(pathFondMenu1);
		panM.add(bUnites);
		panM.add(bDefences);
		panMenu.add(panM, BorderLayout.CENTER);
		
		//Construction du Menu des Unites
		panUnites = createPanelMenu(Menu.UNITES);
		
		JButton bUnite1 = buttonWithLabelFondImage(pathFondBUnite, pathImageV1, ""+joueurP.getUniteXmlLoader().getCout(TypeUnite.CAC),     Color.ORANGE, prefWidthBMUnite, prefHeightBMUnite, new ButtonUnite(this, TypeUnite.CAC));
		JButton bUnite2 = buttonWithLabelFondImage(pathFondBUnite, pathImageV2, ""+joueurP.getUniteXmlLoader().getCout(TypeUnite.DISTANT), Color.ORANGE, prefWidthBMUnite, prefHeightBMUnite, new ButtonUnite(this, TypeUnite.DISTANT));
		JButton bUnite3 = buttonWithLabelFondImage(pathFondBUnite, pathImageV3, ""+joueurP.getUniteXmlLoader().getCout(TypeUnite.TANK),    Color.ORANGE, prefWidthBMUnite, prefHeightBMUnite, new ButtonUnite(this, TypeUnite.TANK));
		
		JButton bBackU = new ButtonImageFond(pathFondBRetour, prefWidthBMBack, prefHeightBMBack, new ButtonBack(this));

		JPanel panU = new PanelImageFond(pathFondMenu1);
		panU.setBackground(Color.WHITE);
		panU.add(bUnite1);
		panU.add(bUnite2);
		panU.add(bUnite3);
		panU.add(bBackU);
		panUnites.add(panU, BorderLayout.CENTER);
		
		//Construction du Menu des defenses
		panDefenses = createPanelMenu(Menu.DEFENSES);
		
		JButton bDef1 = buttonWithLabelFond(pathFondBUnite, "Def1", color, prefWidthBMUnite, prefHeightBMUnite, new ButtonMenu(this, Menu.DEF1));
		JButton bDef2 = buttonWithLabelFond(pathFondBUnite, "Def2", color, prefWidthBMUnite, prefHeightBMUnite, new ButtonMenu(this, Menu.DEF2));
		JButton bDef3 = buttonWithLabelFond(pathFondBUnite, "Def3", color, prefWidthBMUnite, prefHeightBMUnite, new ButtonMenu(this, Menu.DEF3));
		
		JButton bBackD = new ButtonImageFond(pathFondBRetour, prefWidthBMBack, prefHeightBMBack, new ButtonBack(this));

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
		labGroupe.setText(String.format("Groupe selectionne : %d", joueurP.getGroupeSelect()));
		labGroupe.setHorizontalAlignment(labGroupe.CENTER);
		labGroupe.setForeground(Color.WHITE);
		pan2.add(labGroupe);
		panGroupe.add(pan2, BorderLayout.NORTH);
		//JPanel panGroupeA = new PanelImageFond(pathFondMenu1);

		JPanel panGroupeA = new JPanel();
		panGroupeA.setBackground(Color.BLACK);
		panGroupeA.add(buttonWithLabelFond(pathFondBGroupe, "1", Color.WHITE, prefWidthBGroupe, prefHeightBGroupe, new ButtonGroupe(this, 1)));
		panGroupeA.add(buttonWithLabelFond(pathFondBGroupe, "2", Color.WHITE, prefWidthBGroupe, prefHeightBGroupe, new ButtonGroupe(this, 2)));
		panGroupeA.add(buttonWithLabelFond(pathFondBGroupe, "3", Color.WHITE, prefWidthBGroupe, prefHeightBGroupe, new ButtonGroupe(this, 3)));	
		
		panGroupe.add(panGroupeA, BorderLayout.CENTER);
		
		panBot.add(panGroupe, BorderLayout.NORTH);
		
		

		//----------------------------------------------------------------
		//Construction du Panel central (Champ de bataille)
		//----------------------------------------------------------------
		panCenter = new JPanel();
		panCenter.setPreferredSize(new Dimension(prefWidth, prefHeightPanCenter));
		panCenter.setBackground(Color.BLACK);
		panCenter.addMouseListener(new PointeurPlateau(this));
		
		
		panGeneral.add(panTop, BorderLayout.NORTH);
		panGeneral.add(panBot, BorderLayout.SOUTH);
		panGeneral.add(panCenter, BorderLayout.CENTER);

		this.pack();
		System.out.println("Constrution interface");
	}
	
	/**
	 * Raccourci pour creer un bouton avec<li>
	 *  Une image en fond<li>
	 *  Un label fixe en fond
	 * @param pathFond String : Chemin de l'image de fond
	 * @param text String : Texte du label de fond
	 * @param c Color : couleur du texte
	 * @param w int : Largeur du bouton
	 * @param h int : Hauteur du bouton
	 * @param act ActionListener actionListener lie au bouton
	 * @return JButton personnalise
	 */
	private JButton buttonWithLabelFond(String pathFond, String text, Color c, int w, int h, ActionListener act) {
	
		JButton b = new ButtonImageFond(pathFond, w, h, act);
		b.setLayout(new BorderLayout());
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(label.CENTER);
		label.setForeground(c);
		b.add(label, BorderLayout.CENTER);
		
		return b;
	}
	/**
	 * Raccourci pour creer un bouton avec<li>
	 *  Une image en fond<li>
	 *  Une image en premier plan<li>
	 *  Un label fixe en fond
	 * @param pathFond String : Chemin de l'image de fond
	 * @param pathImage String : Chemin de l'image au premier plan
	 * @param text String : Texte du label de fond
	 * @param c Color : couleur du texte
	 * @param w int : Largeur du bouton
	 * @param h int : Hauteur du bouton
	 * @param act ActionListener actionListener lie au bouton
	 * @return JButton personnalise
	 */
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
	 * Creer un JPanel de menu
	 * @param m Menu : Onglet du menu associe
	 * @return un JPanel au bon format du Menu avec le nom de l'onglet associe
	 */
	private JPanel createPanelMenu(Menu m) {
		JPanel pan = new JPanel();
		pan.setPreferredSize(new Dimension(prefWidth, prefHeightPanMenu));
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
	 * Creer un JPanel de menu (defence)
	 * @param m Menu : Onglet du menu associe
	 * @return un JPanel au bon format du Menu avec les boutons de defense associes
	 */
	private JPanel createPanDef(Menu m) {
		JPanel panP = new JPanel();
		panP.setPreferredSize(new Dimension(prefWidth, prefHeightPanMenu));
		panP.setLayout(new BorderLayout());
		
		JPanel pan2 = new PanelImageFond(pathFondMenu2);
		JLabel lab = new JLabel(m.toString());
		lab.setHorizontalAlignment(lab.CENTER);
		lab.setForeground(Color.WHITE);
		pan2.add(lab);
		panP.add(pan2, BorderLayout.NORTH);
		
		JPanel pan = new PanelImageFond(pathFondMenu1);
		pan.setBackground(Color.WHITE);
		pan.add(buttonWithLabelFondImage(pathFondBUnite, pathImageD1, ""+joueurP.getUniteXmlLoader().getCout(TypeUnite.DEFI),   Color.ORANGE, prefWidthBMUnite, prefHeightBMUnite, new ButtonUnite(this, TypeUnite.DEFI)));
		pan.add(buttonWithLabelFondImage(pathFondBUnite, pathImageD2, ""+joueurP.getUniteXmlLoader().getCout(TypeUnite.DEFII),  Color.ORANGE, prefWidthBMUnite, prefHeightBMUnite, new ButtonUnite(this, TypeUnite.DEFII)));
		pan.add(buttonWithLabelFondImage(pathFondBUnite, pathImageD3, ""+joueurP.getUniteXmlLoader().getCout(TypeUnite.DEFIII), Color.ORANGE, prefWidthBMUnite, prefHeightBMUnite, new ButtonUnite(this, TypeUnite.DEFIII)));
		
		
		JButton sellButton = new ButtonImageFond(pathFondBUnite, prefWidthBMUnite, prefHeightBMUnite, new ButtonSellDef(this));
		sellButton.setLayout(new BorderLayout());
		JLabel venteDef0 = new JLabel(String.format("Vendre"));
		venteDef0.setForeground(Color.WHITE);
		venteDef0.setHorizontalAlignment(labArgent.CENTER);
		venteDef0.setFont(font8);
		sellButton.add(venteDef0, BorderLayout.NORTH);
		switch (m) {
		case DEF1:
			venteDef1 = new JLabel(String.format("0"));
			venteDef1.setForeground(Color.WHITE);
			venteDef1.setHorizontalAlignment(labArgent.CENTER);
			sellButton.add(venteDef1, BorderLayout.CENTER);
			break;
		case DEF2:
			venteDef2 = new JLabel(String.format("0"));
			venteDef2.setForeground(Color.WHITE);
			venteDef2.setHorizontalAlignment(labArgent.CENTER);
			sellButton.add(venteDef2, BorderLayout.CENTER);
			break;
		case DEF3:
			venteDef3 = new JLabel(String.format("0"));
			venteDef3.setForeground(Color.WHITE);
			venteDef3.setHorizontalAlignment(labArgent.CENTER);
			sellButton.add(venteDef3, BorderLayout.CENTER);
			break;
		default:
			break;
		}
		pan.add(sellButton);
		
		pan.add(new ButtonImageFond(pathFondBRetour, prefWidthBMBack, prefHeightBMBack, new ButtonBack(this)));
		panP.add(pan, BorderLayout.CENTER);
		
		return panP;
	}
	
	
	
	public JPanel getCenterPan() { return this.panCenter; }
	
	
	
	/**
	 * Affiche le menu selectionne 
	 * @param menu Menu : Nouveau menu selectionne
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
		// Affiche le menu selectionne
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
	 * Revient au menu precedent
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
	 * Met a jour le Label qui affiche l'argent
	 */
	public void changementArgent() {
		labArgent.setText(String.format("Argent : %d", joueurP.getArgent()));
	}
	/**
	 * Met à jour le Label qui affiche le groupe selectionne
	 */
	public void changementGroupe() {
		labGroupe.setText(String.format("Groupe selectionne : %d", joueurP.getGroupeSelect()));
	}
	/**
	 * Met à jour les Label qui affichent le prix de vente de chaque defense
	 */
	public void changementDefense(Menu m, int cout) {
		switch (m) {
		case DEF1:
			venteDef1.setText(String.format("+%d", (int)Math.floor(cout * VarPartie.REMBOURSEMENT_DEFENSE)));
			break;
		case DEF2:
			venteDef2.setText(String.format("+%d", (int)Math.floor(cout * VarPartie.REMBOURSEMENT_DEFENSE)));
			break;
		case DEF3:
			venteDef3.setText(String.format("+%d", (int)Math.floor(cout * VarPartie.REMBOURSEMENT_DEFENSE)));
			break;
		default:
			break;
		}
	}

	public Menu getCurrentMenu() { return this.currentMenu; }
	public JoueurPartieImpl getJoueurP() { return this.joueurP; }
	public void muteMusique() {
		if (clip.isRunning()) clip.stop();
		else clip.start();
	}
	
	
	//----------------------------------------------------------------
	// ActionListeners
	//----------------------------------------------------------------
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
			interfaceP.getJoueurP().creerUnite(interfaceP.currentMenu, typeU);
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
	private class PointeurPlateau implements MouseListener  {

		private InterfacePartie interfaceP;
		
		PointeurPlateau (InterfacePartie i) {
			this.interfaceP = i;
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			this.interfaceP.joueurP.changeObjectifGroupe(arg0.getX(), arg0.getY());
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		
	}
	
	private class ButtonMusique implements ActionListener {
		
		private InterfacePartie interfaceP;
		
		ButtonMusique (InterfacePartie i) {
			this.interfaceP = i;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			interfaceP.muteMusique();
		}
	}

}



