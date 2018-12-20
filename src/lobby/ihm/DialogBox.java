package lobby.ihm;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.sun.xml.internal.ws.api.Component;

public class DialogBox {
	
	public DialogBox(){
		
	}
	
	public static String infoPlayer(JFrame parent, String message){
		JOptionPane jop = new JOptionPane();
		String res = jop.showInputDialog(parent, message, "AgeOfWar", JOptionPane.QUESTION_MESSAGE);
   		if (!res.equals("")) 
   			return res;
   		else {
   			return infoPlayer(parent, "Incorrect, reesseyez : ");
   		}
	}
	
	public static void info(JFrame parent, String message, String img_url){
		ImageIcon img = new ImageIcon("res/"+img_url);
		JOptionPane info = new JOptionPane();
		if (img_url!=null)
			info.showMessageDialog(parent, message, "", JOptionPane.INFORMATION_MESSAGE, img); 
		else 
			info.showMessageDialog(parent, message, "", JOptionPane.INFORMATION_MESSAGE);
			
	}
	
	public static void error(JFrame parent, String message){
		JOptionPane error_dialog = new JOptionPane();
		error_dialog.showMessageDialog(parent, "Error : "+ message, "Erreur detectee" , JOptionPane.ERROR_MESSAGE);
	}

}
