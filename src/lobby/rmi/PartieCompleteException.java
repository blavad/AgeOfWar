package lobby.rmi;

import java.io.Serializable;

public class PartieCompleteException extends Exception {

	public PartieCompleteException(){
		super("Plus de place dans la partie.. Dommage !");
	}
}
