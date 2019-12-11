package appPackage;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import objet.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class ApplicationSurakarta extends javafx.application.Application implements EventHandler<MouseEvent> {
    private Group troupe; // Group qui va permettre d'afficher la totalité des objets sur la scène
    private List<EmplacementPion> emplacements;
    private List<Pion> pions;
    private static final Color equipe1 = Color.RED, equipe2 = Color.BLUE;
    private static final Color couleur1 = Color.GREEN, couleur2 = Color.YELLOW; // Couleur 1 = Chemin 1 & Couleur 2 = Chemin 2
    private Pion pionSelected = null; // Pion qui est sélectionné
    private EmplacementPion emplacementPionSelected = null; // EmplacementPion qui est sélectionné
    private static final int espacement = 30; // Espacement pour écrire un texte en haut
    private List<EmplacementPion> cheminVert; // Chemin 1
    private List<EmplacementPion> cheminJaune; // Chemin 2
    private int equipeQuiJoue = 0; // Décide quelle équipe va jouer
    private Text texte; // Texte pour donner des indications

    @Override
    public void start(Stage primaryStage) throws Exception{
        initVar();
        creationPlateau();
        changementEquipe(); // Choisi une équipe aléatoirement

        //Définition de la scène
        Scene scene = new Scene(troupe, 1000, 1000, Color.BLACK);
        primaryStage.setTitle("Jeu du Surakarta");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void initVar(){
        troupe = new Group();
        emplacements = new ArrayList<EmplacementPion>();
        creationEmplacementPion();
        pions = new ArrayList<Pion>();
        creationPion();
        creationChemins();
        updateChemins();
    }

    void creationPlateau(){
        ajoutLignes();
        ajoutArcs();
        ajoutEmplacements();
        ajoutPions();
        ajoutTexte();
        Timeline littleCycle = new Timeline(new KeyFrame(Duration.millis(100),event-> deplacementPion() ));
        littleCycle.setCycleCount(Timeline.INDEFINITE);
        littleCycle.play();
    }

    void creationChemins(){
        cheminVert = new ArrayList<EmplacementPion>();
        cheminVert.add(emplacements.get(2));
        cheminVert.add(emplacements.get(8));
        cheminVert.add(emplacements.get(14));
        cheminVert.add(emplacements.get(20));
        cheminVert.add(emplacements.get(26));
        cheminVert.add(emplacements.get(32));
        cheminVert.add(emplacements.get(18));
        cheminVert.add(emplacements.get(19));
        cheminVert.add(emplacements.get(21));
        cheminVert.add(emplacements.get(22));
        cheminVert.add(emplacements.get(23));
        cheminVert.add(emplacements.get(33));
        cheminVert.add(emplacements.get(27));
        cheminVert.add(emplacements.get(15));
        cheminVert.add(emplacements.get(9));
        cheminVert.add(emplacements.get(3));
        cheminVert.add(emplacements.get(17));
        cheminVert.add(emplacements.get(16));
        cheminVert.add(emplacements.get(13));
        cheminVert.add(emplacements.get(12));

        cheminJaune = new ArrayList<EmplacementPion>();
        cheminJaune.add(emplacements.get(1));
        cheminJaune.add(emplacements.get(7));
        cheminJaune.add(emplacements.get(13));
        cheminJaune.add(emplacements.get(19));
        cheminJaune.add(emplacements.get(25));
        cheminJaune.add(emplacements.get(31));
        cheminJaune.add(emplacements.get(24));
        cheminJaune.add(emplacements.get(26));
        cheminJaune.add(emplacements.get(27));
        cheminJaune.add(emplacements.get(28));
        cheminJaune.add(emplacements.get(29));
        cheminJaune.add(emplacements.get(34));
        cheminJaune.add(emplacements.get(22));
        cheminJaune.add(emplacements.get(16));
        cheminJaune.add(emplacements.get(10));
        cheminJaune.add(emplacements.get(4));
        cheminJaune.add(emplacements.get(11));
        cheminJaune.add(emplacements.get(9));
        cheminJaune.add(emplacements.get(8));
        cheminJaune.add(emplacements.get(6));

        //affichageChemin(cheminVert);
        //affichageChemin(cheminJaune);
    }

    void affichageChemin(List<EmplacementPion> Chemin){
        System.out.println("   Chemin =");
        for(EmplacementPion ep : Chemin) System.out.println(ep.ToString());
    }

    void creationEmplacementPion(){
        int ligne = 0, colonne = 0;
        for(int i = 1; i<=36; i++){
            EmplacementPion ep = new EmplacementPion(ligne, colonne);
            emplacements.add(ep);
            colonne++;
            if(i%6 == 0) ligne++;
            if(colonne == 6) colonne = 0;
        }
        //affichageEmplacements();
    }

    private void affichageEmplacements(){
        for(EmplacementPion ep : emplacements) System.out.println(ep.ToString());
    }

    void creationPion(){
        int ligne = 0, colonne = 0;
        int numEmplacement = 0;
        int equipe = 1;
        for(int i = 1; i<=24; i++){
            Pion p = new Pion(ligne, colonne);
            pions.add(p);
            colonne++;
            if(i%6 == 0) ligne++;
            if(ligne == 2) ligne = 4;
            if(colonne == 6) colonne = 0;
            if(i == 13){
                numEmplacement = 25;
                equipe = 2;
            } else numEmplacement++;
            p.equipe = equipe;
            emplacements.get(numEmplacement-1).p = p;
            //System.out.println(emplacements.get(numEmplacement-1).ToString());
        }
        //affichagePions();
    }

    private void affichagePions(){
        for(Pion p : pions) System.out.println(p.ToString());
    }

    void ajoutEmplacements(){
        int posX = 250, posY = 250 + espacement;
        int epaisseur = 10;
        int cpt = 25;
        for(EmplacementPion ep : emplacements){
            ep.setCenterX(posX + ep.colonne * 100);
            ep.setCenterY(posY + ep.ligne * 100);
            ep.setRadius(epaisseur);
            ep.setFill(Color.WHITE);
            ep.setOpacity(0.6);
            ep.setSmooth(true);
            ep.setOnMouseClicked(this::handle);
            troupe.getChildren().add(ep);
        }
    }

    void ajoutPions(){
        int epaisseur = 7;
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(2);
        dropShadow.setOffsetX(1);
        dropShadow.setOffsetY(2);
        for(Pion p : pions){
            p.updatePos();
            p.setRadius(epaisseur);
            if(p.equipe == 1) p.setFill(equipe1);
            else if(p.equipe == 2) p.setFill(equipe2);
            else p.setFill(Color.TRANSPARENT);
            p.setOpacity(1);
            p.setOnMouseClicked(this::handle);
            p.setEffect(dropShadow);
            p.setSmooth(true);
            troupe.getChildren().add(p);
        }
    }

    void ajoutArcs(){
        int posX = 250, posY = 250 + espacement;
        int radius1 = 100, radius2 = 200;
        for(int i = 1; i<=8; i++){
            Arc arc = new Arc();
            if(i%2 != 0){
                arc.setRadiusX(radius1);
                arc.setRadiusY(radius1);
                arc.setStroke(couleur2);
                arc.setOpacity(0.8);
            } else {
                arc.setRadiusX(radius2);
                arc.setRadiusY(radius2);
                arc.setStroke(couleur1);
            }
            if(i < 3){
                arc.setStartAngle(0);
                arc.setLength(270);
                arc.setCenterX(posX);
                arc.setCenterY(posY);
            } else {
                if(i < 5){
                    arc.setCenterX(posX + 500);
                    arc.setCenterY(posY);
                    arc.setStartAngle(-90);
                    arc.setLength(270);
                } else {
                    if(i < 7){
                        arc.setCenterX(posX + 500);
                        arc.setCenterY(posY + 500);
                        arc.setStartAngle(-180);
                        arc.setLength(270);
                    } else {
                        arc.setCenterX(posX);
                        arc.setCenterY(posY + 500);
                        arc.setStartAngle(90);
                        arc.setLength(270);
                    }
                }
            }
            arc.setStrokeWidth(3);
            arc.setFill(Color.TRANSPARENT);
            arc.setType(ArcType.OPEN);
            troupe.getChildren().add(arc);
        }
    }

    void ajoutLignes(){
        int posStartX = 250, posEndX = 750;
        int posStartY = 250 + espacement, posEndY = 750 + espacement;
        for(int i = 1; i<=8; i++){
            Line line = new Line();
            switch(i){
                case 1:
                    line.setStartX(posStartX);
                    line.setStartY(posStartY + 100);
                    line.setEndX(posEndX);
                    line.setEndY(posStartY + 100);
                    line.setStroke(couleur2);
                    line.setOpacity(0.8);
                    line.setScaleY(3);
                    break;
                case 2:
                    line.setStartX(posStartX);
                    line.setStartY(posStartY + 200);
                    line.setEndX(posEndX);
                    line.setEndY(posStartY + 200);
                    line.setStroke(couleur1);
                    line.setScaleY(3);
                    break;
                case 3:
                    line.setStartX(posStartX);
                    line.setStartY(posStartY + 300);
                    line.setEndX(posEndX);
                    line.setEndY(posStartY + 300);
                    line.setStroke(couleur1);
                    line.setScaleY(3);
                    break;
                case 4:
                    line.setStartX(posStartX);
                    line.setStartY(posStartY + 400);
                    line.setEndX(posEndX);
                    line.setEndY(posStartY + 400);
                    line.setStroke(couleur2);
                    line.setOpacity(0.8);
                    line.setScaleY(3);
                    break;
                case 5:
                    line.setStartX(posStartX + 100);
                    line.setStartY(posStartY);
                    line.setEndX(posStartX + 100);
                    line.setEndY(posEndY);
                    line.setStroke(couleur2);
                    line.setOpacity(0.8);
                    line.setScaleX(3);
                    break;
                case 6:
                    line.setStartX(posStartX + 200);
                    line.setStartY(posStartY);
                    line.setEndX(posStartX + 200);
                    line.setEndY(posEndY);
                    line.setStroke(couleur1);
                    line.setScaleX(3);
                    break;
                case 7:
                    line.setStartX(posStartX + 300);
                    line.setStartY(posStartY);
                    line.setEndX(posStartX + 300);
                    line.setEndY(posEndY);
                    line.setStroke(couleur1);
                    line.setScaleX(3);
                    break;
                case 8:
                    line.setStartX(posStartX + 400);
                    line.setStartY(posStartY);
                    line.setEndX(posStartX + 400);
                    line.setEndY(posEndY);
                    line.setStroke(couleur2);
                    line.setOpacity(0.8);
                    line.setScaleX(3);
                    break;
                default:
                    break;
            }
            troupe.getChildren().add(line);
            line.setSmooth(true);
        }
    }

    public void ajoutTexte(){
        Rectangle r = new Rectangle();
        r.setX(300);
        r.setY(10);
        r.setWidth(400);
        r.setHeight(espacement);
        r.setFill(Color.WHITE);
        r.setStroke(Color.GOLDENROD);
        r.setStrokeWidth(5);
        troupe.getChildren().add(r);

        texte = new Text(300, 30, "Tour du joueur");
        texte.setFont(new Font(20));
        texte.setTextAlignment(TextAlignment.CENTER);
        texte.setWrappingWidth(400);
        troupe.getChildren().add(texte);
    }

    public void handle(MouseEvent me) {
        Object o = me.getSource();
        if(o instanceof Pion){
            Pion pion = ((Pion) o);
            if(pionSelected != null && pion.equipe != equipeQuiJoue){
                for(EmplacementPion ep : emplacements){
                    if(ep.p == pion){
                        clicSurEmplacement(ep);
                        break;
                    }
                }
                //System.out.println("Equipe adverse {" + emplacementPionSelected.ToString() + "}");
                if(pionPrenable(pionSelected, pion)){
                    System.out.println("Pion " + pion.ToString() + " pris");
                    removePionEmplacement(pionSelected);
                    pionSelected.ligne = pion.ligne;
                    pionSelected.colonne = pion.colonne;
                    animationPionPrise();
                    emplacementPionSelected.p = pionSelected;
                    detruirePion(pion);
                    updateChemins();
                    updateCouleurPion(pionSelected);
                    pionSelected = null;
                    emplacementPionSelected = null;
                }
                else System.out.println("Pion " + pion.ToString() + " imprenable");
            } else clicSurPion((Pion)o);
        }
        if(o instanceof EmplacementPion) clicSurEmplacement((EmplacementPion)o);
    }

    private void removePionEmplacement(Pion pion) {
        for(EmplacementPion ep : emplacements){
            if(ep.p == pion){
                ep.p = null;
                break;
            }
        }
    }

    private boolean pionPrenable(Pion pionPrenant, Pion pionMenace) {
        if(pionPrenant.chemin == 3){
            if(pionMenace.chemin == 1 || pionMenace.chemin == 2){
                return pasDePionChemin(pionPrenant, pionMenace);
            }
        }
        if(pionPrenant.chemin != 0 && pionPrenant.chemin == pionMenace.chemin) return pasDePionChemin(pionPrenant, pionMenace);
        return false;
    }

    private boolean pasDePionChemin(Pion pionPrenant, Pion pionMenace) {
        List<EmplacementPion> cheminLePlusCourt = cheminLePlusCourt(pionPrenant, pionMenace);
        if(cheminLePlusCourt == null){
            System.out.println("   Pas de chemin le plus court");
            return false;
        }
        return true;
    }

    private List<EmplacementPion> cheminLePlusCourt(Pion pionPrenant, Pion pionMenace) {
        List<EmplacementPion> cheminCroissant = new ArrayList<EmplacementPion>();
        List<EmplacementPion> cheminDecroissant = new ArrayList<EmplacementPion>();
        Boolean obstacleCroissant = false, obstacleDecroissant = false;
        int init, cpt = 0, cpt2 = 0;
        switch (pionMenace.chemin){
            case 1:
                init = cheminVert.indexOf(emplacementDepuisPion(pionMenace, cheminVert));
                for(int i = 0; i < 20; i++){
                    init++;
                    if(init > 19) init = 0;
                    if(cheminVert.get(i).p != null){
                        obstacleCroissant = true;
                        break;
                    }
                    else cheminCroissant.add(cheminVert.get(i));
                    if(cheminVert.get(i).p == pionMenace) break;
                }
                init = cheminVert.indexOf(emplacementDepuisPion(pionMenace, cheminVert));
                for(int i = 0; i < 20; i++){
                    init--;
                    if(init < 0) init = 19;
                    if(cheminVert.get(i).p != null){
                        obstacleDecroissant = true;
                        break;
                    }
                    else cheminDecroissant.add(cheminVert.get(i));
                    if(cheminVert.get(i).p == pionMenace) break;
                }
                break;
            case 2:
                init = cheminJaune.indexOf(emplacementDepuisPion(pionMenace, cheminJaune));
                for(int i = 0; i < 20; i++){
                    init++;
                    if(init > 19) init = 0;
                    if(cheminJaune.get(i).p != null){
                        obstacleCroissant = true;
                        break;
                    }
                    else cheminCroissant.add(cheminJaune.get(i));
                    if(cheminJaune.get(i).p == pionMenace) break;
                }
                init = cheminJaune.indexOf(emplacementDepuisPion(pionMenace, cheminJaune));
                for(int i = 0; i < 20; i++){
                    init--;
                    if(init < 0) init = 19;
                    if(cheminJaune.get(i).p != null){
                        obstacleDecroissant = true;
                        break;
                    }
                    else cheminDecroissant.add(cheminJaune.get(i));
                    if(cheminJaune.get(i).p == pionMenace) break;
                }
                break;
            case 3:
                Pion pionMenaceChemin1 = new Pion(pionPrenant.ligne, pionPrenant.colonne);
                pionMenaceChemin1.chemin = 1;
                Pion pionMenaceChemin2 = new Pion(pionPrenant.ligne, pionPrenant.colonne);
                pionMenaceChemin2.chemin = 2;
                return plusPetiteListe(cheminLePlusCourt(pionPrenant, pionMenaceChemin1), cheminLePlusCourt(pionPrenant, pionMenaceChemin2));
            default:
                return null;
        }
        if(obstacleCroissant && obstacleDecroissant) return null;
        return plusPetiteListe(cheminCroissant, cheminDecroissant);
    }

    private List<EmplacementPion> plusPetiteListe(List<EmplacementPion> cheminCroissant, List<EmplacementPion> cheminDecroissant) {
        if(cheminCroissant == null && cheminDecroissant == null) return null;
        if(cheminCroissant != null && cheminDecroissant == null) return cheminCroissant;
        if(cheminCroissant == null && cheminDecroissant != null) return cheminDecroissant;
        if(cheminCroissant.size() > cheminDecroissant.size()) return cheminDecroissant;
        else return cheminCroissant;
    }

    private void detruirePion(Pion pion) {
        pion.equipe = 0;
        pion.ligne = -1;
        pion.colonne = -1;
        troupe.getChildren().remove(pion);
    }

    public void clicSurPion(Pion p){
        //System.out.println(p.ToString()+" pressé");
        if(pionSelected != null){
            if(pionSelected.equipe == 1) pionSelected.setFill(equipe1);
            else if(pionSelected.equipe == 2) pionSelected.setFill(equipe2);
            else pionSelected.setFill(Color.TRANSPARENT);
        }
        if(equipeQuiJoue == p.equipe){
            pionSelected = p;
            pionSelected.setFill(Color.PURPLE);
        }
    }

    public void clicSurEmplacement(EmplacementPion ep){
        //System.out.println(ep.ToString()+" pressé");
        if(pionSelected == null) return;
        emplacementPionSelected = ep;
    }

    void updateChemins(){
        for(EmplacementPion ep : emplacements){
            if(ep.p != null && cheminVert.contains(ep)) ep.p.chemin = 1;
            if(ep.p != null && cheminJaune.contains(ep)){
                if(ep.p.chemin == 1) ep.p.chemin = 3;
                else ep.p.chemin = 2;
            }
        }
    }

    Boolean deplacementPossible(Pion p, EmplacementPion ep){
        if(emplacementPionSelected.p == null) {
            int diffLigne = p.ligne - ep.ligne, diffColonne = p.colonne - ep.colonne;
            if (abs(diffLigne) > 1 || abs(diffColonne) > 1){
                //System.out.println("Déplacement impossible");
                return false;
            }
            //System.out.println("Déplacement possible");
            return true;
        }
        return false;
    }

    public void animationPion(){
        long tempo = 500;
        Timeline timeline = new Timeline();
        int xdest = 250 + emplacementPionSelected.colonne * 100;
        int ydest = 250 + espacement + emplacementPionSelected.ligne * 100;
        KeyFrame bougePion = new KeyFrame(new Duration(tempo),
                new KeyValue(pionSelected.centerXProperty(), xdest),
                new KeyValue(pionSelected.centerYProperty(), ydest));
        timeline.getKeyFrames().add(bougePion);
        timeline.play();
        pionSelected.setAnimation(timeline);
        changementEquipe();
    }

    public void animationPionPrise(){
        long tempo = 500;
        Timeline timeline = new Timeline();
        int xdest = 250 + emplacementPionSelected.ligne * 100;
        int ydest = 250 + espacement + emplacementPionSelected.colonne * 100;
        KeyFrame bougePion = new KeyFrame(new Duration(tempo),
                new KeyValue(pionSelected.centerXProperty(), xdest),
                new KeyValue(pionSelected.centerYProperty(), ydest));
        timeline.getKeyFrames().add(bougePion);
        timeline.play();
        pionSelected.setAnimation(timeline);
        changementEquipe();
    }

    private void changementEquipe() {
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
            texte.setFill(Color.BLUE);
            equipeQuiJoue = 2;
        } else
        if(equipeQuiJoue == 2){
            texte.setText("Equipe Rouge");
            texte.setFill(Color.RED);
            equipeQuiJoue = 1;
        }
    }

    public void deplacementPion(){
        if(emplacementPionSelected != null && pionSelected != null){
            if(deplacementPossible(pionSelected, emplacementPionSelected)){
                EmplacementPion ancien = emplacementDepuisPion(pionSelected, emplacements);
                if(ancien != null) ancien.p = null; // On enleve le pion de son ancien emplacement
                pionSelected.ligne = emplacementPionSelected.ligne;
                pionSelected.colonne = emplacementPionSelected.colonne;
                emplacementPionSelected.p = pionSelected;
                animationPion(); // Il bouge jusqu'à sa nouvelle position
                updateCouleurPion(pionSelected);
                pionSelected = null; // On fait en sorte de devoir rechoisir ce même pion ou un nouveau pion
            }
            emplacementPionSelected = null; // On devra rechoisir un emplacement
            updateChemins();
        }
    }

    private EmplacementPion emplacementDepuisPion(Pion p, List<EmplacementPion> lEmplacements) {
        for(EmplacementPion ep : lEmplacements){
            if(ep.p == p){
                return ep;
            }
        }
        return null;
    }

    private void updateCouleurPion(Pion pionSelected) {
        if(pionSelected.equipe == 1) pionSelected.setFill(equipe1); // On reset sa couleur de base
        else if(pionSelected.equipe == 2) pionSelected.setFill(equipe2);
        else pionSelected.setFill(Color.TRANSPARENT);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
