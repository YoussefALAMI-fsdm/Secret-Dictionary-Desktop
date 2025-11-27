package com.secret.dictionary.controller;

import com.secret.dictionary.service.MotServiceImp;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Contrôleur du menu latéral gauche
 */
public class MenuController {

    @FXML private VBox vboxLeft;
    @FXML private Button btnCatego;
    @FXML private VBox menuCatego;
    @FXML private VBox btnFavorisGroup;
    @FXML private VBox btnNouveauGroup;

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
    public void initialize() {
        // Initialisation si nécessaire
    }

    // ========================================
    // GESTION DU MENU DÉROULANT
    // ========================================
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

    // ========================================
    // ACTIONS DES BOUTONS
    // ========================================
    @FXML
    private void onTousLesMotsClick() {
        if (mainController != null) {
            mainController.afficherTousLesMots();
        }

        if (isMenuVisible) {
            toggleMenu();
        }
    }

    @FXML
    private void onNouveauClick() {
        if (mainController != null) {
            mainController.onNouveauClick();
        }
    }
}