package objet;

import appPackage.ApplicationSurakarta;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class IA {
    private List<Pion> pions; // Liste des pions du plateau
    private List<EmplacementPion> emplacements; // Liste des emplacements
    public int numEquipe = 0;
    public ApplicationSurakarta app;
    private int numCoup = 0;

    /**
     * Constructeur de base pour l'IA
     * On y créé les listes de pions et d'emplacements
     * @param app
     * Référence à l'application de jeu pour éviter de redéfinir des méthodes en double
     */
    public IA(ApplicationSurakarta app){
        super();
        pions = new ArrayList<Pion>();
        emplacements = new ArrayList<EmplacementPion>();
        this.app = app;
    }

    /**
     * Ajout des pions selon l'équipe de l'IA
     */
    public void ajoutPions(){
        for(int i = 0; i < 24; i++){
            Pion p = app.pions.get(i);
            if(numEquipe == 1){
                if(i < 12) ajoutPion(p);
            }
            if(numEquipe == 2){
                if(i >= 12) ajoutPion(p);
            }
        }
        /*
        for(Pion p : pions){
            System.out.println(p + "\n\t-----");
        }
        */
    }

    /**
     * Ajout pion à la liste de pions
     * @param p
     * Pion à ajouter
     */
    public void ajoutPion(Pion p){
        pions.add(p);
    }

    /**
     * Retrait pion de la liste de pions
     * @param p
     * Pion à retirer
     */
    public void retirerPion(Pion p){
        if(pions.contains(p)) pions.remove(p);
    }

    /**
     * Ajout d'emplactement à sa liste
     * @param ep
     * Emplacement à ajouter
     */
    public void ajoutEmplacement(EmplacementPion ep){
        emplacements.add(ep);
    }

    /**
     * Méthode qui permet à l'IA de jouer
     * Elle va choisir un pion qui lui appartient, voir si il y a des emplacements autour de lui qui sont libres
     * Si oui il joue (se déplace seulement) sinon il recherche un autre pion
     */
    public void jouer(){
        if(pions.size() == 0) return;
        numCoup++;
        Random r = new Random();
        int numPion;
        Pion p;
        EmplacementPion ep;
        List<EmplacementPion> lep;
        do {
            do {
                numPion = r.nextInt() % pions.size();
            } while (numPion < 0);
            p = pions.get(numPion);
            lep = p.emplacementsAutour(emplacements);
        } while(lep.size() < 1 && p.ligne != -1 && p.colonne != -1);
        int nbEmplacement;
        do{
            nbEmplacement = r.nextInt()%(lep.size());
        } while(nbEmplacement < 0);
        ep = lep.get(nbEmplacement);

        //System.out.println("L'ordinateur joue son coup n*" + numCoup + "\n  Deplacement de " + p + " vers " + ep + "\n-----------------------");

        app.emplacementPionSelected = ep;
        app.pionSelected = p;
        app.deplacementPion();
    }
}
