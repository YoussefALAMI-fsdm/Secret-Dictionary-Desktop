package com.secret.dictionary.controller;

import com.secret.dictionary.service.MotServiceImp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * Contrôleur principal - Orchestration des sous-composants
 */
public class MainController {

    @FXML private BorderPane rootPane;
    @FXML private HBox topBar;
    @FXML private HBox leftContainer;
    @FXML private HBox centerContainer;
    @FXML private HBox rightContainer;

    // Injection du service
    private MotServiceImp motService;

    // Sous-contrôleurs
    private MenuController menuController;
    private WordDetailsController wordDetailsController;
    private WordListController wordListController;
    private SearchDialogController searchDialogController;
    private AddWordDialogController addWordDialogController;

    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
        initializeSubControllers();
    }

    @FXML
    public void initialize() {
        // L'initialisation complète se fait après injection du service
    }

    /**
     * Initialise tous les sous-contrôleurs avec le service
     */
    private void initializeSubControllers() {
        try {
            // Charger le menu latéral
            FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/com/secret/dictionary/fxml/side-menu.fxml"));
            VBox menuView = menuLoader.load();
            menuController = menuLoader.getController();
            menuController.setMotService(motService);
            menuController.setMainController(this);
            leftContainer.getChildren().add(menuView);

            // Charger la zone de détails
            FXMLLoader detailsLoader = new FXMLLoader(getClass().getResource("/com/secret/dictionary/fxml/word-details.fxml"));
            ScrollPane detailsView = detailsLoader.load();
            wordDetailsController = detailsLoader.getController();
            wordDetailsController.setMotService(motService);
            centerContainer.getChildren().add(detailsView);

            // Charger la liste des mots
            FXMLLoader listLoader = new FXMLLoader(getClass().getResource("/com/secret/dictionary/fxml/word-list.fxml"));
            VBox listView = listLoader.load();
            wordListController = listLoader.getController();
            wordListController.setMotService(motService);
            wordListController.setMainController(this);
            rightContainer.getChildren().add(listView);

            // Initialiser les contrôleurs de dialogues
            searchDialogController = new SearchDialogController(motService, this);
            addWordDialogController = new AddWordDialogController(motService, this);

            // Charger tous les mots au démarrage
            wordListController.chargerTousLesMots();

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des composants FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========================================
    // ACTIONS TOOLBAR
    // ========================================
    @FXML
    private void onRechercheClick() {
        searchDialogController.show();
    }

    @FXML
    public void onNouveauClick() {
        addWordDialogController.show();
    }

    // ========================================
    // MÉTHODES PUBLIQUES POUR NAVIGATION
    // ========================================
    public void afficherDetailsMot(String mot) {
        wordDetailsController.afficherDetailsMot(mot);
        showDetailsView();
    }

    public void afficherTousLesMots() {
        wordListController.chargerTousLesMots();
        hideDetailsView();
    }

    public void rafraichirListeMots() {
        wordListController.chargerTousLesMots();
    }

    // ========================================
    // GESTION VISIBILITÉ DES VUES
    // ========================================
    /**
     * Affiche la vue détails dans le centre sans cacher les conteneurs gauche/droite
     */
    private void showDetailsView() {
        centerContainer.setVisible(true);
        centerContainer.setManaged(true);
    }

    /**
     * Cache la vue détails
     */
    private void hideDetailsView() {
        centerContainer.setVisible(false);
        centerContainer.setManaged(false);
    }
}
