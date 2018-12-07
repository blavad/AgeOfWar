package partie.core;

public class CacUnite extends Unite {

	public CacUnite(Vect2 pos, int camp) {
		super(pos, VarPartie.VIE_CACU, camp);
		rayonUnite = VarPartie.RAYON_CACU;
		cout = VarPartie.COUT_CACU;
		degatA = VarPartie.DEGAT_ATTAQUE_CACU;
		vitesseA = VarPartie.VITESSE_ATTAQUE_CACU;
		porteeA = VarPartie.PORTEE_ATTAQUE_CACU;
		vitesseDeDeplacement = VarPartie.VITESSE_DEPLACEMENT_CACU;
		etat = EtatUnite.INACTIF;
	}

}
