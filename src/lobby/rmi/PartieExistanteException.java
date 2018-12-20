package lobby.rmi;

/** Classe d'exception pour eviter les doubles parties
 * 
 * @author DHT
 * @version 1.0
 *
 */
public class PartieExistanteException extends Exception {

	/** Constructeur de l'exception
	 * 
	 * @param n le nom de la partie
	 */
	public PartieExistanteException(String n){
		super(n +" est un super nom de partie.\n Malheuresement il est deja  utilise.");
	}
}
