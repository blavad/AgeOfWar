package lobby.rmi;

/** Classe d'exception pour eviter les doubles pseudo
 * 
 * @author DHT
 * @version 1.0
 *
 */
public class PseudoExistantException extends Exception {

	/** Constructeur de l'exception
	 * 
	 * @param n le nom du joueur
	 */
	public PseudoExistantException(String n){
		super("Ce pseudo est deja  utilise.");
	}
}
