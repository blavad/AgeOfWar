package lobby.ihm;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.sun.xml.internal.ws.api.Component;

public class DialogBox {
	
	public DialogBox(){
		
	}
	
	public String infoPlayer(String message){
		JOptionPane jop = new JOptionPane();
		String pseudo = jop.showInputDialog(null, message, "AgeOfWar", JOptionPane.QUESTION_MESSAGE);
   		if (pseudo != null) 
   			return pseudo;
   		else {
   			return infoPlayer("Pseudo : ");
   		}
	}
	
	public static void alert(JFrame parent, String message){
		JOptionPane alert_dialog = new JOptionPane();
		alert_dialog.showInputDialog(parent, message, "Attention", JOptionPane.WARNING_MESSAGE);
	}
	
	public static void error(JFrame parent, String message){
		JOptionPane error_dialog = new JOptionPane();
		error_dialog.showMessageDialog(parent, "Error : "+ message, "Erreur détectée" , JOptionPane.ERROR_MESSAGE);
	}

}
