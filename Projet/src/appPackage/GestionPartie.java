package appPackage;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Random;

/**
 * Classe qui gère toutes les informations de la partie
 */
public class GestionPartie {
    public int nbPionEquipe1 = 12; // Nombre de pions de l'équipe 1
    public int nbPionEquipe2 = 12; // Nombre de pions de l'équipe 2
    public int equipeQuiJoue = 0; // Décide quelle équipe va jouer
    private static final Color equipe1 = Color.RED, equipe2 = Color.BLUE; // Couleur des équipes
    public Text texte; // Texte pour donner des indications
    public Rectangle cadre; // Cadre autour du texte
    public boolean partieFini; // Booléen qui détermine si la partie est fini ou pas
    public ApplicationSurakarta app; // Référence à l'application pour récupérer des variables

    public GestionPartie(String modeJeu, ApplicationSurakarta app){
        super();
        partieFini = false;
        this.app = app;
    }

    /**
     * Méthode qui change l'équipe qui joue
     * Elle change le texte en haut de l'écran selon le résultat
     * Permet également de définir de façon aléatoire quelle équipe commencera
     */
    public void changementEquipe() {
        if(equipeQuiJoue == 0){
            Random rnd = new Random();
            int random;
            do {
                random = rnd.nextInt() % 2;
            } while(random < 0);
            equipeQuiJoue =  random + 1;
            //System.out.println("Equipe " + equipeQuiJoue + " commmence !");
        }
        if(equipeQuiJoue == 1){
            texte.setText("Equipe Bleue");
            texte.setFill(equipe2);
            cadre.setStroke(equipe2);
            equipeQuiJoue = 2;
        } else
        if(equipeQuiJoue == 2){
            texte.setText("Equipe Rouge");
            texte.setFill(equipe1);
            cadre.setStroke(equipe1);
            equipeQuiJoue = 1;
        }
        if(partieFini()) finDePartie();
        if(app.modeJeu == "JvO" && equipeQuiJoue == app.ia.numEquipe) app.ia.jouer();
    }

    /**
     * Enlève un pion à l'équipe affecté
     */
    public void prisePion(){
        if(equipeQuiJoue == 1) nbPionEquipe1 -= 1;
        if(equipeQuiJoue == 2) nbPionEquipe2 -= 1;
    }

    /**
     * Regarde si la partie est fini
     * @return
     * Booléen de décision
     */
    private boolean partieFini(){
        return nbPionEquipe1 == 0 || nbPionEquipe2 == 0;
    }

    /**
     * Méthode qui change le texte pour dire que la partie est terminée
     * Lorsque l'on cliquera sur un emplacement ou un pion le jeu se fermera
     */
    private void finDePartie(){
        if(nbPionEquipe1 == 0) texte.setText("L'équipe rouge à gagné, cliquez sur un point");
        if(nbPionEquipe2 == 0) texte.setText("L'équipe bleue à gagné, cliquez sur un point");
        texte.setFill(Color.GOLDENROD);
        cadre.setStroke(Color.GOLDENROD);
        partieFini = true;
    }
}
