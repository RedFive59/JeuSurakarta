package appPackage;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
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

import java.util.*;

import static java.lang.Math.abs;

/**
 * Cette classe nous permet de créer de l'application de jeu, dans celle-ci on gèrera les actions à réaliser,
 * les animations de déplacement ainsi que les prises, on y gère aussi la scène pour l'affichage.
 */
public class ApplicationSurakarta extends javafx.application.Application implements EventHandler<MouseEvent> {
    private Group troupe; // Group qui va permettre d'afficher la totalité des objets sur la scène
    private List<EmplacementPion> emplacements; // Liste des emplacements
    public List<Pion> pions; // Liste des pions du plateau
    private static final Color equipe1 = Color.RED, equipe2 = Color.BLUE; // Couleur des équipes
    private static final Color couleur1 = Color.GREEN, couleur2 = Color.YELLOW; // Couleur 1 = Chemin 1 & Couleur 2 = Chemin 2
    public Pion pionSelected = null; // Pion qui est sélectionné
    public EmplacementPion emplacementPionSelected = null; // EmplacementPion qui est sélectionné
    private static final int espacement = 30; // Espacement pour écrire un texte en haut
    private List<EmplacementPion> cheminVert; // Chemin 1
    private List<EmplacementPion> cheminJaune; // Chemin 2
    private EmplacementPion anciennePosition = null; // Mémoire de l'ancienne position d'un pion
    private double tempsAnimation = 500; // Vitesse de déplacement d'un pion d'une case vers l'autre
    private GestionPartie gp;
    private Stage primaryStage;
    public IA ia;
    public String modeJeu = null;
    private Group troupe2; // Group qui va permettre d'afficher la totalité des objets sur la scène
    private Stage newWindow;

    @Override
    public void start(Stage primaryStage) throws Exception{
        initVar();
        creationPlateau();

        //Définition de la scène
        Scene scene = new Scene(troupe, 1000, 1000, Color.BLACK);
        primaryStage.setTitle("Jeu du Surakarta");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        this.primaryStage = primaryStage;

        //Scène du menu
        troupe2 = new Group();
        choixModeJeu();

        //Définition de la scène
        Scene scene2 = new Scene(troupe2, 1000, 300, Color.BLACK);
        Stage newWindow = new Stage();
        newWindow.setTitle("Menu");
        newWindow.setScene(scene2);
        newWindow.show();
        this.newWindow = newWindow;

        gp.texte.setFill(Color.GREY);
        gp.texte.setText("Choisissez un mode de jeu");
    }

    /**
     * Méthode qui permet à l'utilisateur de choisir son mode de jeu
     * @return
     * Le mode de jeu (JvJ ou JvO)
     */
    private void choixModeJeu() {
        Rectangle rect1 = new Rectangle();
        rect1.setX(50);
        rect1.setY(100);
        rect1.setWidth(400);
        rect1.setHeight(100);
        rect1.setFill(Color.WHITE);
        rect1.setId("JvJ");
        rect1.setOnMouseClicked(this::handle);
        troupe2.getChildren().add(rect1);

        Text texte1 = new Text();
        texte1 = new Text(50, 155, "Joueur VS Joueur");
        texte1.setFont(new Font(20));
        texte1.setTextAlignment(TextAlignment.CENTER);
        texte1.setWrappingWidth(400);
        troupe2.getChildren().add(texte1);

        Rectangle rect2 = new Rectangle();
        rect2.setX(550);
        rect2.setY(100);
        rect2.setWidth(400);
        rect2.setHeight(100);
        rect2.setFill(Color.WHITE);
        rect2.setId("JvO");
        rect2.setOnMouseClicked(this::handle);
        troupe2.getChildren().add(rect2);

        Text texte2 = new Text();
        texte2 = new Text(550, 155, "Joueur VS Ordinateur");
        texte2.setFont(new Font(20));
        texte2.setTextAlignment(TextAlignment.CENTER);
        texte2.setWrappingWidth(400);
        troupe2.getChildren().add(texte2);
    }

    /**
     * Méthode qui nous retourne l'emplacement d'un pion avec une liste d'emplacements donné
     * @param p
     * Pion concerné
     * @param lEmplacements
     * Liste où l'on cherche le pion ainsi que son emplacement
     * @return
     * Emplacement recherché
     */
    private EmplacementPion emplacementDepuisPion(Pion p, List<EmplacementPion> lEmplacements) {
        if(p == null){
            //System.out.println("Pion inexistant");
            return null;
        }
        if(lEmplacements == null){
            //System.out.println("Liste vide");
            return null;
        }
        for(EmplacementPion ep : lEmplacements){
            if(ep.p == p){
                return ep;
            }
        }
        return null;
    }

    /**
     * Méthode qui initialise nos variables
     * Elle crée les pions, chemins et emplacements
     */
    void initVar(){
        ia = new IA(this);
        troupe = new Group();
        emplacements = new ArrayList<EmplacementPion>();
        creationEmplacementPion();
        pions = new ArrayList<Pion>();
        creationPion();
        creationChemins();
        updateChemins();
        gp = new GestionPartie(modeJeu, this);
    }

    /**
     * Méthode où l'on ajoute au plateau les différents objets crée auparavant
     */
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

    /**
     * Méthode qui définit les 2 chemins selon la position des cases
     * Les chemins permettront de vérifier si la prise est possible
     */
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
        cheminVert.add(emplacements.get(20));
        cheminVert.add(emplacements.get(21));
        cheminVert.add(emplacements.get(22));
        cheminVert.add(emplacements.get(23));
        cheminVert.add(emplacements.get(33));
        cheminVert.add(emplacements.get(27));
        cheminVert.add(emplacements.get(21));
        cheminVert.add(emplacements.get(15));
        cheminVert.add(emplacements.get(9));
        cheminVert.add(emplacements.get(3));
        cheminVert.add(emplacements.get(17));
        cheminVert.add(emplacements.get(16));
        cheminVert.add(emplacements.get(15));
        cheminVert.add(emplacements.get(14));
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
        cheminJaune.add(emplacements.get(25));
        cheminJaune.add(emplacements.get(26));
        cheminJaune.add(emplacements.get(27));
        cheminJaune.add(emplacements.get(28));
        cheminJaune.add(emplacements.get(29));
        cheminJaune.add(emplacements.get(34));
        cheminJaune.add(emplacements.get(28));
        cheminJaune.add(emplacements.get(22));
        cheminJaune.add(emplacements.get(16));
        cheminJaune.add(emplacements.get(10));
        cheminJaune.add(emplacements.get(4));
        cheminJaune.add(emplacements.get(11));
        cheminJaune.add(emplacements.get(10));
        cheminJaune.add(emplacements.get(9));
        cheminJaune.add(emplacements.get(8));
        cheminJaune.add(emplacements.get(7));
        cheminJaune.add(emplacements.get(6));

        //affichageChemin(cheminVert);
        //affichageChemin(cheminJaune);
    }

    /**
     * Méthode toString() pour les chemins
     * @param Chemin
     * Chemin à afficher
     */
    void affichageChemin(List<EmplacementPion> Chemin){
        System.out.println("   Chemin =");
        for(EmplacementPion ep : Chemin) System.out.println(ep.toString());
    }

    /**
     * Méthode de création des 36 emplacements de pions
     */
    void creationEmplacementPion(){
        int ligne = 0, colonne = 0;
        for(int i = 1; i<=36; i++){
            EmplacementPion ep = new EmplacementPion(ligne, colonne);
            emplacements.add(ep);
            ia.ajoutEmplacement(ep);
            colonne++;
            if(i%6 == 0) ligne++;
            if(colonne == 6) colonne = 0;
        }
        //affichageEmplacements();
    }

    /**
     * Méthode toString() pour les emplacements
     */
    private void affichageEmplacements(){
        for(EmplacementPion ep : emplacements) System.out.println(ep.toString());
    }

    /**
     * Méthode de création des 24 pions
     */
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

    /**
     * Méthode toString() pour les pions
     */
    private void affichagePions(){
        for(Pion p : pions) System.out.println(p.toString());
    }

    /**
     * Méthode d'ajout des emplacements sur la scène
     */
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

    /**
     * Méthode d'ajout des pions sur la scène
     */
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

    /**
     * Méthode d'ajout des courbures sur la scène
     */
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

    /**
     * Méthode d'ajout des lignes sur la scène
     */
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

    /**
     * Méthode d'ajout du texte en haut de l'écran sur la scène
     * Ce texte permet d'afficher quel équipe doit jouer
     */
    public void ajoutTexte(){
        gp.cadre = new Rectangle();
        gp.cadre.setX(300);
        gp.cadre.setY(10);
        gp.cadre.setWidth(400);
        gp.cadre.setHeight(espacement);
        gp.cadre.setFill(Color.WHITE);
        gp.cadre.setStroke(Color.GOLDENROD);
        gp.cadre.setStrokeWidth(5);
        troupe.getChildren().add(gp.cadre);

        gp.texte = new Text(300, 30, "Tour du joueur");
        gp.texte.setFont(new Font(20));
        gp.texte.setTextAlignment(TextAlignment.CENTER);
        gp.texte.setWrappingWidth(400);
        troupe.getChildren().add(gp.texte);
    }

    /**
     * Méthode liée à l'implémentation de l'eventHandler sur la classe ApplicationSurakarta
     * Elle permet de définir les actions à réaliser selon le type d'objet sur lequel on a cliqué
     * @param me
     * Event qui est évoqué au moment du clic
     */
    public void handle(MouseEvent me) {
        Object o = me.getSource();
        if(modeJeu != null) {
            if(o instanceof Pion){
                Pion pion = ((Pion) o);
                // Vérification qu'un pion est sélectionné et que le nouveau pion sélectionné appartient à l'équipe adverse
                if(pionSelected != null && pion.equipe != gp.equipeQuiJoue){
                    clicSurEmplacement(emplacementDepuisPion(pion, emplacements));
                    if(pionPrenable(pionSelected, pion)){
                        anciennePosition = emplacementDepuisPion(pionSelected, emplacements);
                        updateCouleurPion(pionSelected);
                        detruirePion(pion);
                        animationPionPrise();
                        updateChemins();
                        //pionSelected.cheminASuivre = cheminLePlusCourt(pionSelected, pion);
                        pionSelected = null;
                        emplacementPionSelected = null;
                    }
                } else clicSurPion((Pion)o);
            }
            if(o instanceof EmplacementPion) clicSurEmplacement((EmplacementPion)o);
        } else
        if(o instanceof Rectangle){
            Rectangle r = ((Rectangle) o);
            this.modeJeu = r.getId();
            newWindow.close();
            gp.changementEquipe();
            if(modeJeu == "JvO") initIA();
        }
    }

    /**
     * Méthode d'initialisation de l'IA
     * Elle ajoute les pions et donne son numéro d'équipe
     */
    private void initIA() {
        if(gp.equipeQuiJoue == 1) ia.numEquipe = 2;
        if(gp.equipeQuiJoue == 2) ia.numEquipe = 1;
        //System.out.println("Equipe qui commence = " + gp.equipeQuiJoue);
        //System.out.println("Equipe de l'IA = " + ia.numEquipe);
        ia.ajoutPions();
    }

    /**
     * Méthode qui retire un pion de son emplacement
     * @param pion
     * Pion recherché
     */
    private void removePionEmplacement(Pion pion) {
        for(EmplacementPion ep : emplacements){
            if(ep.p == pion){
                ep.p = null;
                break;
            }
        }
    }

    /**
     * Méthode qui regarde sur quel chemin se trouve le pion prenant et vérifie ainsi grâce à la méthode pasDePionChemin si la prise est possible
     * @param pionPrenant
     * Pion qui cherche à prendre un autre pion
     * @param pionMenace
     * Pion qui est menacé d'être pris
     * @return
     * Booléen de décision
     */
    private boolean pionPrenable(Pion pionPrenant, Pion pionMenace) {
        if(pionPrenant.chemin != 0 && pionPrenant.chemin == pionMenace.chemin) return pasDePionChemin(pionPrenant, pionMenace);
        if(pionPrenant.chemin == 3){
            if(pionMenace.chemin == 1 || pionMenace.chemin == 2){
                return pasDePionChemin(pionPrenant, pionMenace);
            }
        }
        if(pionMenace.chemin == 3) return pasDePionChemin(pionPrenant, pionMenace);
        //System.out.println("Les 2 pions n'ont aucun chemin en commun /!\\");
        return false;
    }

    /**
     * Méthode qui vérifie si il y a déjà un pion sur son chemin le plus court
     * Elle vérifie également si les 2 pions ne sont pas sur la même ligne (et les 2 pions en bout de plateau) car cheminLePlusCourt ne le prend pas en compte
     * @param pionPrenant
     * Pion qui cherche à prendre un autre pion
     * @param pionMenace
     * Pion qui est menacé d'être pris
     * @return
     * Booléen de décision
     */
    private boolean pasDePionChemin(Pion pionPrenant, Pion pionMenace) {
        List<EmplacementPion> cheminLePlusCourt = cheminLePlusCourt(pionPrenant, pionMenace);
        if(cheminLePlusCourt == null) return false;
        if(cheminLePlusCourt.size() > 1 && (cheminMemeLigne(cheminLePlusCourt) || cheminMemeColonne(cheminLePlusCourt))) return false;

        //Affichage du chemin le plus court
        System.out.print("cheminLePlusCourt - [");
        for(EmplacementPion ep : cheminLePlusCourt){
            System.out.print(" " + ep.toString() + "\n");
        }
        System.out.println("]");

        pionPrenant.cheminASuivre = cheminLePlusCourt;
        return true;
    }

    /**
     * Méthode qui vérifie si 2 pions en extrémité sont sur la même ligne
     * @param cheminLePlusCourt
     * Chemin à vérifier
     * @return
     * Booléen de décision
     */
    private boolean cheminMemeLigne(List<EmplacementPion> cheminLePlusCourt) {
        int cpt = 1;
        EmplacementPion epPrec = emplacementDepuisPion(pionSelected, emplacements);
        for(int i = 0; i<cheminLePlusCourt.size(); i++){
            EmplacementPion epActuel = cheminLePlusCourt.get(i);
            if(epPrec.ligne == epActuel.ligne) cpt++;
            epPrec = epActuel;
        }
        if(cpt == cheminLePlusCourt.size()+1){
            //System.out.println("Problème d'alignement ligne");
            return true;
        }
        return false;
    }

    /**
     * Méthode qui vérifie si 2 pions en extrémité sont sur la même colonne
     * @param cheminLePlusCourt
     * Chemin à vérifier
     * @return
     * Booléen de décision
     */
    private boolean cheminMemeColonne(List<EmplacementPion> cheminLePlusCourt) {
        int cpt = 1;
        EmplacementPion epPrec = emplacementDepuisPion(pionSelected, emplacements);
        for(int i = 0; i<cheminLePlusCourt.size(); i++){
            EmplacementPion epActuel = cheminLePlusCourt.get(i);
            if(epPrec.colonne == epActuel.colonne) cpt++;
            epPrec = epActuel;
        }
        if(cpt == cheminLePlusCourt.size()+1){
            //System.out.println("Problème d'alignement colonne");
            return true;
        }
        return false;
    }

    /**
     * Méthode qui va tout d'abord définir quel chemin de la classe ApplicationSurakarta elle doit vérifier
     *
     * @param pionPrenant
     * Pion qui cherche à prendre un autre pion
     * @param pionMenace
     * Pion qui est menacé d'être pris
     * @return
     * Booléen de décision
     */
    private List<EmplacementPion> cheminLePlusCourt(Pion pionPrenant, Pion pionMenace) {
        List<EmplacementPion> cheminCroissant = new ArrayList<EmplacementPion>();
        List<EmplacementPion> cheminDecroissant = new ArrayList<EmplacementPion>();
        List<EmplacementPion> chemin = null;
        Boolean obstacleCroissant = false, obstacleDecroissant = false, passeParArc1 = false, passeParArc2 = false;
        int init = 0, cpt = 0, cpt2 = 0, max;
        switch (pionMenace.chemin){
            case 1:
                //System.out.println("\tChemin vert -");
                chemin = cheminVert;
                break;
            case 2:
                //System.out.println("\tChemin jaune -");
                chemin = cheminJaune;
                break;
            case 3:
                pionMenace.chemin = 1;
                List<EmplacementPion> chemin1 = cheminLePlusCourt(pionPrenant, pionMenace);
                pionMenace.chemin = 2;
                List<EmplacementPion> chemin2 = cheminLePlusCourt(pionPrenant, pionMenace);
                pionMenace.chemin = 3;
                return plusPetiteListe(chemin1, chemin2);
            default:
                return null;
        }
        max = chemin.size();
        //Vérification que le pion prenant se trouve bien sur un emplacement du chemin sélectionné
        if(emplacementDepuisPion(pionPrenant, chemin) == null) return null;

        //Tests du chemin sélectionné dans les 2 sens
            //Sens croissant
        init = chemin.indexOf(emplacementDepuisPion(pionPrenant, chemin));
        if(emplacementDepuisPion(pionPrenant, chemin).liaisonArc && chemin.get(0).liaisonArc) passeParArc1 = true;
        for(int i = 0; i < max; i++){
            init++;
            if(init > max-1) init = 0;
            EmplacementPion ep = chemin.get(init);
            if(ep.p == pionMenace){
                cheminCroissant.add(ep);
                break;
            }
            if(ep.p != null && ep.p != pionPrenant){
                if(ep.liaisonArc) passeParArc1 = true;
                obstacleCroissant = true;
                //System.out.println("   Sens croissant / Obstacle détecté : " + ep.p.ToString());
                break;
            }
            if(ep.liaisonArc) passeParArc1 = true;
            cheminCroissant.add(ep);
        }
            //Sens décroissant
        init = chemin.indexOf(emplacementDepuisPion(pionPrenant, chemin));
        if(emplacementDepuisPion(pionPrenant, chemin).liaisonArc && chemin.get(0).liaisonArc) passeParArc2 = true;
        for(int i = 0; i < max; i++){
            init--;
            if(init < 0) init = max-1;
            EmplacementPion ep = chemin.get(init);
            if(ep.p == pionMenace){
                cheminDecroissant.add(ep);
                break;
            }
            if(ep.p != null && ep.p != pionPrenant){
                if(ep.liaisonArc) passeParArc2 = true;
                obstacleDecroissant = true;
                //System.out.println("   Sens décroissant / Obstacle détecté : " + ep.p.ToString());
                break;
            }
            if(ep.liaisonArc) passeParArc2 = true;
            cheminDecroissant.add(ep);
        }
        // Vérification qu'il n'y a pas déjà un chemin de valide
        if((obstacleCroissant && obstacleDecroissant) || (!obstacleCroissant && !passeParArc1) || (!obstacleDecroissant && !passeParArc2)){
            // On vérifie maintenant l'autre chemin si le pion est sur un croisement de 2 chemins
            if(chemin.indexOf(emplacementDepuisPion(pionPrenant, chemin)) != chemin.lastIndexOf(emplacementDepuisPion(pionPrenant, chemin))){
                //System.out.println("Croisement");
                obstacleCroissant = false;
                obstacleDecroissant = false;
                passeParArc1 = false;
                passeParArc2 = false;
                init = chemin.lastIndexOf(emplacementDepuisPion(pionPrenant, chemin));
                if(emplacementDepuisPion(pionPrenant, chemin).liaisonArc && chemin.get(0).liaisonArc) passeParArc1 = true;
                for(int i = 0; i < max; i++){
                    init++;
                    if(init > max-1) init = 0;
                    EmplacementPion ep = chemin.get(init);
                    if(ep.p == pionMenace){
                        cheminCroissant.add(ep);
                        break;
                    }
                    if(ep.p != null && ep.p != pionPrenant){
                        if(ep.liaisonArc) passeParArc1 = true;
                        obstacleCroissant = true;
                        //System.out.println("   Sens croissant / Obstacle détecté : " + ep.p.ToString());
                        break;
                    }
                    if(ep.liaisonArc) passeParArc1 = true;
                    cheminCroissant.add(ep);
                }
                init = chemin.lastIndexOf(emplacementDepuisPion(pionPrenant, chemin));
                if(emplacementDepuisPion(pionPrenant, chemin).liaisonArc && chemin.get(0).liaisonArc) passeParArc2 = true;
                for(int i = 0; i < max; i++){
                    init--;
                    if(init < 0) init = max-1;
                    EmplacementPion ep = chemin.get(init);
                    if(ep.p == pionMenace){
                        cheminDecroissant.add(ep);
                        break;
                    }
                    if(ep.p != null && ep.p != pionPrenant){
                        if(ep.liaisonArc) passeParArc2 = true;
                        obstacleDecroissant = true;
                        //System.out.println("   Sens décroissant / Obstacle détecté : " + chemin.get(init).p.ToString());
                        break;
                    }
                    if(ep.liaisonArc) passeParArc2 = true;
                    cheminDecroissant.add(ep);
                }
            }
        }

        //System.out qui débug les tests réalisés
        //System.out.println("Test Chemin " + pionMenace.chemin + " fait avec comme résultat : obstacleCroissant(" + obstacleCroissant + ") et passeParArc1("+passeParArc1+"), obstacleDecroissant(" + obstacleDecroissant + ") et passeParArc2("+passeParArc2+")");

        if(obstacleCroissant && obstacleDecroissant) return null;
        if(!obstacleCroissant && passeParArc1){
            if(!obstacleDecroissant && passeParArc2){
                if(cheminMemeLigne(cheminCroissant) || cheminMemeColonne(cheminCroissant)) return cheminDecroissant;
                if(cheminMemeLigne(cheminDecroissant) || cheminMemeColonne(cheminDecroissant)) return cheminCroissant;
                return plusPetiteListe(cheminCroissant, cheminDecroissant);
            }
            else {
                if(cheminCroissant.get(0).p == pionMenace && !cheminCroissant.get(0).liaisonArc) return null;
                return cheminCroissant;
            }
        }
        if(!obstacleDecroissant && passeParArc2){
            if(cheminDecroissant.get(0).p == pionMenace && !cheminDecroissant.get(0).liaisonArc) return null;
            else return cheminDecroissant;
        }
        return null;
    }

    /**
     * Méthode qui retourne la plus petite liste des 2 listes en entrée
     * @param cheminCroissant
     * Liste 1
     * @param cheminDecroissant
     * Liste 2
     * @return
     * Liste 1 ou 2 selon la taille de celle-ci
     */
    private List<EmplacementPion> plusPetiteListe(List<EmplacementPion> cheminCroissant, List<EmplacementPion> cheminDecroissant) {
        if(cheminCroissant == null && cheminDecroissant == null) return null;
        if(cheminCroissant != null && cheminDecroissant == null) return cheminCroissant;
        if(cheminCroissant == null && cheminDecroissant != null) return cheminDecroissant;
        if(cheminCroissant.size() > cheminDecroissant.size()) return cheminDecroissant;
        else return cheminCroissant;
    }

    /**
     * Méthode qui va enlever un pion du plateau
     * @param pion
     * Pion à supprimer
     */
    private void detruirePion(Pion pion) {
        pion.equipe = 0;
        pion.ligne = -1;
        pion.colonne = -1;
        troupe.getChildren().remove(pion);
        if(modeJeu == "JvO" && gp.equipeQuiJoue != ia.numEquipe){
            ia.retirerPion(pion);
        }
    }

    /**
     * Méthode d'handling du clic sur un pion
     * Elle change la couleur du pion de l'ancien pion sélectionné puis
     * défini le nouveau pion sélectionné tout en le mettant de couleur violette
     * @param p
     * Pion selectionné
     */
    public void clicSurPion(Pion p){
        if(gp.partieFini) primaryStage.close();
        //System.out.println(p.ToString()+" pressé");
        if(pionSelected != null){
            if(pionSelected.equipe == 1) pionSelected.setFill(equipe1);
            else if(pionSelected.equipe == 2) pionSelected.setFill(equipe2);
            else pionSelected.setFill(Color.TRANSPARENT);
        }
        if(gp.equipeQuiJoue == p.equipe){
            pionSelected = p;
            pionSelected.setFill(Color.PURPLE);
        }
    }

    /**
     * Méthode d'handling du clic sur emplacement
     * @param ep
     * Emplacement selectionné
     */
    public void clicSurEmplacement(EmplacementPion ep){
        if(gp.partieFini) primaryStage.close();
        //System.out.println(ep.ToString()+" pressé");
        if(pionSelected == null) return;
        emplacementPionSelected = ep;
    }

    /**
     * Méthode qui va mettre à jour les chemins de tous les pions se trouvant dans la liste emplacements
     */
    void updateChemins(){
        for(EmplacementPion ep : emplacements){
            if(ep.p != null && cheminVert.contains(ep)) ep.p.chemin = 1;
            if(ep.p != null && cheminJaune.contains(ep)){
                if(ep.p.chemin == 1) ep.p.chemin = 3;
                else ep.p.chemin = 2;
            }
        }
    }

    /**
     * Méthode qui regarde si un déplacement simple (de 1 autour du pion) est possible depuis un pion vers un emplacement
     * @param p
     * Pion à déplacer
     * @param ep
     * Emplacement visé
     * @return
     * Booléen de décision
     */
    public Boolean deplacementPossible(Pion p, EmplacementPion ep){
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

    /**
     * Méthode qui vérifie si un déplacement est possible et gère l'appel à l'animation ainsi que la mise à jour de la couleur du pion
     * En fin d'appel elle remet à jour les chemins des pions car un pion à peut-être changer de chemin
     */
    public void deplacementPion(){
        if(emplacementPionSelected != null && pionSelected != null){
            if(deplacementPossible(pionSelected, emplacementPionSelected)){
                EmplacementPion ancien = emplacementDepuisPion(pionSelected, emplacements);
                if(ancien != null) ancien.p = null; // On enleve le pion de son ancien emplacement
                pionSelected.ligne = emplacementPionSelected.ligne;
                pionSelected.colonne = emplacementPionSelected.colonne;
                emplacementPionSelected.p = pionSelected;
                updateCouleurPion(pionSelected);
                animationPion(); // Il bouge jusqu'à sa nouvelle position
                pionSelected = null; // On fait en sorte de devoir rechoisir ce même pion ou un nouveau pion
            }
            emplacementPionSelected = null; // On devra rechoisir un emplacement
            updateChemins();
        }
    }

    /**
     * Méthode simple qui met à jour la couleur d'un pion
     * @param pionSelected
     * Pion qui doit être mis à jour
     */
    private void updateCouleurPion(Pion pionSelected) {
        if(pionSelected.equipe == 1) pionSelected.setFill(equipe1); // On reset sa couleur de base
        else if(pionSelected.equipe == 2) pionSelected.setFill(equipe2);
        else pionSelected.setFill(Color.TRANSPARENT);
    }

    /**
     * Méthode qui permet de réaliser l'animation de déplacement du pion
     * Celle-ci ne sert que pour les déplacements simples (déplacement de 1)
     */
    public void animationPion(){
        double tempo = tempsAnimation;
        Timeline timeline = new Timeline();
        int xdest = 250 + emplacementPionSelected.colonne * 100;
        int ydest = 250 + espacement + emplacementPionSelected.ligne * 100;
        KeyFrame bougePion = new KeyFrame(new Duration(tempo),
                new KeyValue(pionSelected.centerXProperty(), xdest),
                new KeyValue(pionSelected.centerYProperty(), ydest));
        timeline.getKeyFrames().add(bougePion);
        timeline.play();
        pionSelected.setAnimation(timeline);
        gp.changementEquipe();
    }

    /**
     * Méthode qui permet de réaliser l'animation de déplacement du pion
     * Celle-ci sert lorsqu'un pion en prend un autre
     */
    public void animationPionPrise(){
        final Path path = generatePath();
        final PathTransition transition = generatePathTransition(path);
        transition.play();
        removePionEmplacement(pionSelected);
        pionSelected.ligne = emplacementPionSelected.ligne;
        pionSelected.colonne = emplacementPionSelected.colonne;
        emplacementPionSelected.p = pionSelected;
        pionSelected.updatePos();
        gp.prisePion();
        gp.changementEquipe();
    }

    /**
     * Méthode liée à la méthode animationPionPrise qui va générer un Path selon le chemin qu'un pion a à suivre
     * Elle permet de stocker toutes les LineTo et ArcTo que notre pion va devoir suivre
     * @return
     * Path pour le pion
     */
    private Path generatePath(){
        int x = 250 + pionSelected.colonne * 100;
        int y = 250 + espacement + pionSelected.ligne * 100;
        int numChemin = numChemin(pionSelected.cheminASuivre);
        float rotation1 = 200, rotation2 = 100;
        final Path path = new Path();
        path.getElements().add(new MoveTo(x,y));
        int tailleChemin = pionSelected.cheminASuivre.size();
        for(int i = 0; i < tailleChemin; i++){
            //System.out.println(" Mouvement " + i);
            EmplacementPion ep = pionSelected.cheminASuivre.get(i);
            x = 250 + ep.colonne * 100;
            y = 250 + espacement + ep.ligne * 100;
            if(ep.liaisonArc && (anciennePosition.liaisonArc || tailleChemin == 1)){
                // Cas où l'on doit faire une animation d'arc
                ArcTo arcTo = new ArcTo();
                arcTo.setX(x);
                arcTo.setY(y);
                //System.out.println("Arc vers ["+x+", "+y+"]");
                if(numChemin == 1){
                    arcTo.setRadiusX(rotation1);
                    arcTo.setRadiusY(rotation1);
                } else
                if(numChemin == 2){
                    arcTo.setRadiusX(rotation2);
                    arcTo.setRadiusY(rotation2);
                }
                arcTo.setLargeArcFlag(true);
                definitionSensRotation(arcTo, ep);
                path.getElements().add(arcTo);
                anciennePosition = ep;
            } else {
                //Cas où l'on passe d'un point à l'autre
                //System.out.println("Ligne vers ["+x+", "+y+"]");
                LineTo lineTo = new LineTo();
                lineTo.setX(x);
                lineTo.setY(y);
                path.getElements().add(lineTo);
                anciennePosition = ep;
            }
        }
        anciennePosition = null;
        return path;
    }

    /**
     * Méthode liée à la méthode animationPionPrise qui va générer un PathTransition selon le chemin qu'un pion a à suivre$
     * Elle définit que la durée que le pion va mettre à faire ce chemin ainsi que le chemin à suivre
     * On y spécifie l'objet affecté, le nombre de cycle qui est toujours de 1 ainsi que le fait qu'il ne doit pas faire demi-tour
     * @param path
     * Path déjà rempli
     * @return
     * PathTransition pour le pion
     */
    private PathTransition generatePathTransition(final Path path){
        double tempo;
        if(pionSelected.cheminASuivre.size() > 0) tempo = tempsAnimation * pionSelected.cheminASuivre.size();
        else tempo = tempsAnimation + 200;
        //System.out.println("Size = " + pionSelected.cheminASuivre.size());
        final PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(tempo));
        pathTransition.setPath(path);
        pathTransition.setNode(pionSelected);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);
        return pathTransition;
    }

    /**
     * Méthode permettant de définir dans quel sens la rotation doit se faire lorsqu'un pion passe par un arc
     * Chaque déplacement autour de l'arc dispose d'un sens bien défini donc il a suffit de spécifier pour chacun le quel prendre
     * @param arcTo
     * ArcTo qui recevra le sens de rotation
     * @param ep
     * Emplacement vers lequel il se dirige
     */
    private void definitionSensRotation(ArcTo arcTo, EmplacementPion ep) {
        if(ep.ligne == 0){
            if(ep.colonne == 1 || ep.colonne == 2){
                // Rotation gauche haut
                arcTo.setSweepFlag(true);
                return;
            }
            if(ep.colonne == 3 || ep.colonne == 4){
                // Rotation droite haut
                arcTo.setSweepFlag(false);
                return;
            }
        }
        if(ep.ligne == 5){
            if(ep.colonne == 1 || ep.colonne == 2){
                // Rotation gauche bas
                arcTo.setSweepFlag(false);
                return;
            }
            if(ep.colonne == 3 || ep.colonne == 4){
                // Rotation droite bas
                arcTo.setSweepFlag(true);
                return;
            }
        }
        if(ep.colonne == 0){
            if(ep.ligne == 1 || ep.ligne == 2){
                // Rotation haut gauche
                arcTo.setSweepFlag(false);
                return;
            }
            if(ep.ligne == 3 || ep.ligne == 4){
                // Rotation bas gauche
                arcTo.setSweepFlag(true);
                return;
            }
        }
        if(ep.colonne == 5){
            if(ep.ligne == 1 || ep.ligne == 2){
                // Rotation haut droite
                arcTo.setSweepFlag(true);
                return;
            }
            if(ep.ligne == 3 || ep.ligne == 4){
                // Rotation bas droite
                arcTo.setSweepFlag(false);
                return;
            }
        }
    }

    /**
     * Méthode qui définit quel chemin le pion va-t-il suivre
     * Pour cela on regarde le nombre de cases par lequel passe le plus le cheminASuivre
     * @param cheminASuivre
     * Chemin que le pion doit suivre
     * @return
     * Numéro du chemin
     */
    private int numChemin(List<EmplacementPion> cheminASuivre) {
        int cptCheminVert = 0, cptCheminJaune = 0;
        for(EmplacementPion ep : cheminASuivre){
            if(ep.ligne == 0){
                if(ep.colonne == 1 || ep.colonne == 4) cptCheminJaune++;
                if(ep.colonne == 2 || ep.colonne == 3) cptCheminVert++;
            }
            if(ep.ligne == 1){
                if(ep.colonne == 0 || ep.colonne == 5) cptCheminJaune++;
            }
            if(ep.ligne == 2){
                if(ep.colonne == 0 || ep.colonne == 5) cptCheminVert++;
            }
            if(ep.ligne == 3){
                if(ep.colonne == 0 || ep.colonne == 5) cptCheminVert++;
            }
            if(ep.ligne == 4){
                if(ep.colonne == 0 || ep.colonne == 5) cptCheminJaune++;
            }
            if(ep.ligne == 5){
                if(ep.colonne == 1 || ep.colonne == 4) cptCheminJaune++;
                if(ep.colonne == 2 || ep.colonne == 3) cptCheminVert++;
            }
        }
        if(cptCheminJaune > cptCheminVert) return 2;
        else return 1;
    }

    /**
     * Main qui lance l'application après compilation
     * @param args
     * Args par défaut
     */
    public static void main(String[] args) {
        launch(args);
    }
}
