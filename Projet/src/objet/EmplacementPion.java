package objet;

import javafx.scene.shape.Circle;

public class EmplacementPion extends Circle {
    public int ligne, colonne;
    public Pion p = null;
    public boolean liaisonArc = false;

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

    public String ToString(){
        if(p != null) return "EmplacementPion["+ligne+","+colonne+","+liaisonArc+"] contenant " + p.ToString();
        else return "EmplacementPion["+ligne+","+colonne+","+liaisonArc+"]";
    }
}
