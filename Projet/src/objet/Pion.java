package objet;

import javafx.animation.Timeline;
import javafx.scene.shape.Circle;

public class Pion extends Circle {
    public int ligne, colonne;
    public int equipe;
    private static final int espacement = 30;
    public int chemin = 0; // CheminVert = 1, CheminJaune = 2 et 3 si il est sur les 2
    Timeline animation;

    public Pion(int i, int j){
        ligne = i;
        colonne = j;
    }

    public void updatePos(){
        //System.out.println("Pos avant : X = " + this.centerXProperty() + ", Y = " + this.centerYProperty());
        int posX = 250, posY = 250 + espacement;
        this.setCenterX(posX + this.colonne * 100);
        this.setCenterY(posY + this.ligne * 100);
        //System.out.println("Pos après : X = " + this.centerXProperty() + ", Y = " + this.centerYProperty());
    }

    public Timeline getAnimation()
    {
        return animation;
    }

    public void setAnimation(Timeline animation)
    {
        this.animation = animation;
    }

    public String ToString(){
        return "Pion["+ligne+","+colonne+",equipe="+equipe+",chemin="+chemin+"]";
    }
}
