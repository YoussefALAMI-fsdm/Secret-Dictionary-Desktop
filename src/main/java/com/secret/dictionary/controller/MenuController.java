package com.secret.dictionary.controller;

import com.secret.dictionary.service.MotServiceImp;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Contrôleur du menu latéral gauche
 * Responsabilités :
 * - Gestion de l'animation du menu catégories
 * - Navigation entre les différentes sections
 */
public class MenuController {

    @FXML private Button btnCatego;
    @FXML private VBox menuCatego;
    @FXML private VBox btnFavorisGroup;
    @FXML private VBox btnNouveauGroup;
    @FXML private Button btnNouveau;

    private MotServiceImp motService;
    private MainController mainController;
    private boolean isMenuVisible = false;

    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        // Configuration initiale si nécessaire
    }

    /**
     * Toggle du menu catégories avec animation
     */
    @FXML
    private void toggleMenu() {
        isMenuVisible = !isMenuVisible;
        menuCatego.setVisible(isMenuVisible);
        menuCatego.setManaged(isMenuVisible);

        TranslateTransition ttFavoris = new TranslateTransition(Duration.millis(300), btnFavorisGroup);
        TranslateTransition ttNouveau = new TranslateTransition(Duration.millis(300), btnNouveauGroup);

        if (isMenuVisible) {
            double menuHeight = 5;
            ttFavoris.setToY(menuHeight);
            ttNouveau.setToY(menuHeight);
        } else {
            ttFavoris.setToY(0);
            ttNouveau.setToY(0);
        }

        ttFavoris.play();
        ttNouveau.play();
    }

    /**
     * Afficher tous les mots
     */
    @FXML
    private void onTousLesMotsClick() {
        if (mainController != null) {
            mainController.showListView();
            if (isMenuVisible) {
                toggleMenu();
            }
        }
    }

    /**
     * Ouvrir le dialogue d'ajout de mot
     */
    @FXML
    private void onNouveauClick() {
        if (mainController != null) {
            mainController.openAddWordDialog();
        }
    }

    // Méthodes pour les autres catégories (à implémenter)
    @FXML
    private void onVerbsClick() {
        // TODO: Filtrer par verbes
    }

    @FXML
    private void onAdjectifsClick() {
        // TODO: Filtrer par adjectifs
    }

    @FXML
    private void onNomsClick() {
        // TODO: Filtrer par noms
    }

    @FXML
    private void onAdverbesClick() {
        // TODO: Filtrer par adverbes
    }

    @FXML
    private void onExpressionsClick() {
        // TODO: Filtrer par expressions
    }

    @FXML
    private void onFavorisClick() {
        // TODO: Afficher les favoris
    }

    @FXML
    private void onRevisionClick() {
        // TODO: Mode révision
    }

    @FXML
    private void onEtiquettesClick() {
        // TODO: Gestion des étiquettes
    }
}