package objet;

import javafx.scene.shape.Circle;

/**
 * Classe Emplacement qui hérite de la classe Circle afin de pouvoir dessiner cette objet sur notre plateau
 * On y retrouve sa position réelle sur le plateau ([ligne,colonne]), elle est composé également d'un pion qui peut être null.
 * On définit si elle est lié à un arc ou pas pour savoir si la prise est possible.
 */
public class EmplacementPion extends Circle {
    public int ligne, colonne; // Place sur le plateau [ligne, colonne]
    public Pion p = null; // Pion sur l'emplacement
    public boolean liaisonArc = false; // Booléen qui détermine si l'emplacement est lié à un arc ou pas

    public EmplacementPion(int i, int j){
        super();
        ligne = i;
        colonne = j;
        if(j == 0){
            if(i == 1 || i == 2 || i == 3 || i == 4) liaisonArc = true;
        }
        if(j == 1){
            if(i == 0 || i == 5) liaisonArc = true;
        }
        if(j == 2){
            if(i == 0 || i == 5) liaisonArc = true;
        }
        if(j == 3){
            if(i == 0 || i == 5) liaisonArc = true;
        }
        if(j == 4){
            if(i == 0 || i == 5) liaisonArc = true;
        }
        if(j == 5){
            if(i == 1 || i == 2 || i == 3 || i == 4) liaisonArc = true;
        }
    }

    /**
     * Méthode toString() pour l'emplacement
     * @return
     * String contenant les infos de l'emplacement (ligne, colonne, liaisonArc et info Pion si il existe).
     */
    public String toString(){
        if(p != null) return "EmplacementPion["+ligne+","+colonne+","+liaisonArc+"] contenant " + p.toString();
        else return "EmplacementPion["+ligne+","+colonne+","+liaisonArc+"]";
    }
}
