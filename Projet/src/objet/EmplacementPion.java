package objet;

import javafx.scene.shape.Circle;

public class EmplacementPion extends Circle {
    public int ligne, colonne;
    public Pion p = null;

    public EmplacementPion(int i, int j){
        super();
        ligne = i;
        colonne = j;
    }

    public String ToString(){
        if(p != null) return "EmplacementPion["+ligne+","+colonne+"] : " + p.ToString();
        else return "EmplacementPion["+ligne+","+colonne+"]";
    }
}
