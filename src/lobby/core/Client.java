package lobby.core;

import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import lobby.ihm.FenetreClient;
import lobby.rmi.ServeurParties;

public class Client implements Serializable {

	/**
	 * Le pseudo du joueur
	 */
	private String pseudo;
	private String ip;
	/**
	 * La partie du joueur
	 */
	private Partie maPartie;
	
	/**
	 * La fenetre du joueur
	 */
	public transient FenetreClient fenetreConnexion;
	
	public Client(String pseudo){
		this.pseudo = pseudo;
		this.ip = trouverIp();
	}
	
	public Client(ServeurParties serveur, String pseudo){
		this.pseudo = pseudo;
		this.fenetreConnexion = new FenetreClient(this, serveur);
		this.ip = trouverIp();
	}

	/**
	 * Trouve l'ip du client
	 * @return String
	 */
	
	private String trouverIp() {
		String ip = "127.0.0.1";
		try(final DatagramSocket socket = new DatagramSocket()){
			  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			  ip = socket.getLocalAddress().getHostAddress();
			} catch (SocketException | UnknownHostException e) {
				e.printStackTrace();
			}
		return ip;
	}
	/**
	 * Getter du pseudo du joueur
	 * @return String
	 */
	public String getPseudo(){
		return this.pseudo;
	}
	
	/**
	 * Getter de la partie du joueur
	 * @return Partie la partie du joueur
	 */
	public Partie getPartie(){
		return this.maPartie;
	}
	
	/**
	 * Getter de la fenetre du joueur
	 * @return String
	 */
	public FenetreClient getFenetre(){
		return this.fenetreConnexion;
	}
	/**
	 * Getter de l'ip du client
	 * @return String
	 */
	public String getIp() {
		return this.ip;
	}
	/**
	 * Setter de la partie du joueur
	 * @param p la partie du joueur
	 */
	public void setPartie(Partie p){
		this.maPartie = p;
	}

	@Override
	public boolean equals(Object o){
		if (o == null) return false;
		else {
			if (o.getClass() == Client.class)
				return ((Client)o).getPseudo().equals(this.pseudo);
			else return false;
		}
	}

	@Override
	public String toString(){
		return this.pseudo;
	}
}
