package lobby.rmi;

/** Classe d'exception pour eviter les doubles pseudo
 * 
 * @author DHT
 * @version 1.0
 *
 */
public class SuppressionPartieException extends Exception {

	/** Constructeur de l'exception
	 * 
	 * @param n le nom du joueur
	 */
	public SuppressionPartieException(String n){
		super("Impossible de supprimer la partie.\nCause : "+n );
	}
}
