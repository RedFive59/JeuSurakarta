package objet;

import javafx.animation.Timeline;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Classe Pion qui hérite de la classe Circle afin de pouvoir dessiner cette objet sur notre plateau
 * On y retrouve sa position réelle sur le plateau ([ligne,colonne]), il est aussi composé d'une timeline pour être animé.
 */
public class Pion extends Circle {
    public int ligne, colonne; // Place sur le plateau [ligne, colonne]
    public int equipe; // Numéro d'équipe du pion
    private static final int espacement = 30; // Espacement entre les pions
    public int chemin = 0; // CheminVert = 1, CheminJaune = 2 et 3 si il est sur les 2
    public List<EmplacementPion> cheminASuivre; // Liste des déplacements que le pion va devoir faire
    Timeline animation; // Pour animer le pion

    public Pion(int i, int j){
        ligne = i;
        colonne = j;
        cheminASuivre = new ArrayList<EmplacementPion>();
    }

    /**
     * Permet de mettre à jour sa position sur le plateau selon sa position réelle
     */
    public void updatePos(){
        //System.out.println("Pos avant : X = " + this.centerXProperty() + ", Y = " + this.centerYProperty());
        int posX = 250, posY = 250 + espacement;
        this.setCenterX(posX + this.colonne * 100);
        this.setCenterY(posY + this.ligne * 100);
        //System.out.println("Pos après : X = " + this.centerXProperty() + ", Y = " + this.centerYProperty());
    }

    /**
     * Méthode qui donne les emplacements libres autour d'elle (sans pion dessus)
     * @param lep
     * Liste des emplacements
     * @return
     * Liste des emplacements disponibles
     */
    public List<EmplacementPion> emplacementsAutour(List<EmplacementPion> lep){
        List<EmplacementPion> lres = new ArrayList<EmplacementPion>();
        for(EmplacementPion ep : lep){
            int diffLigne = this.ligne - ep.ligne, diffColonne = this.colonne - ep.colonne;
            if (abs(diffLigne) < 2 && abs(diffColonne) < 2){
                if(diffLigne != 0 && diffColonne != 0){
                    if(ep.p == null){
                        lres.add(ep);
                    }
                }
            }
        }
        return lres;
    }

    /**
     * Méthode nécessaire pour les animations
     */
    public Timeline getAnimation()
    {
        return animation;
    }

    /**
     * Méthode nécessaire pour les animations
     */
    public void setAnimation(Timeline animation)
    {
        this.animation = animation;
    }

    /**
     * Méthode toString() pour le pion
     * @return
     * String contenant les infos du pion (ligne, colonne, equipe et chemin).
     */
    public String toString(){
        return "Pion["+ligne+","+colonne+",equipe="+equipe+",chemin="+chemin+"]";
    }
}
