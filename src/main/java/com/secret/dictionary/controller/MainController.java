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
 * MIS À JOUR : Ajout de la vue des statistiques par défaut
 */
public class MainController {
    @FXML private BorderPane rootPane;
    @FXML private HBox topBar;
    @FXML private HBox leftContainer;
    @FXML private HBox centerContainer;
    @FXML private HBox rightContainer;

    private MotServiceImp motService;

    // Sous-contrôleurs
    private MenuController menuController;
    private WordDetailsController wordDetailsController;
    private WordListController wordListController;
    private SearchDialogController searchDialogController;
    private AddWordDialogController addWordDialogController;
    private StatisticsViewController statisticsViewController;

    // Vues centrales
    private ScrollPane detailsView;
    private ScrollPane statisticsView;

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
            // ========================================
            // CHARGER LE MENU LATÉRAL
            // ========================================
            FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/com/secret/dictionary/fxml/side-menu.fxml"));
            VBox menuView = menuLoader.load();
            menuController = menuLoader.getController();
            menuController.setMotService(motService);
            menuController.setMainController(this);
            leftContainer.getChildren().add(menuView);

            // ========================================
            // CHARGER LA ZONE DE DÉTAILS
            // ========================================
            FXMLLoader detailsLoader = new FXMLLoader(getClass().getResource("/com/secret/dictionary/fxml/word-details.fxml"));
            detailsView = detailsLoader.load();
            wordDetailsController = detailsLoader.getController();
            wordDetailsController.setMotService(motService);
            wordDetailsController.setMainController(this);

            // ========================================
            // CHARGER LA VUE DES STATISTIQUES
            // ========================================
            FXMLLoader statsLoader = new FXMLLoader(getClass().getResource("/com/secret/dictionary/fxml/statistics-view.fxml"));
            statisticsView = statsLoader.load();
            statisticsViewController = statsLoader.getController();
            statisticsViewController.setMotService(motService);

            // ✅ AFFICHER LES STATISTIQUES PAR DÉFAUT
            centerContainer.getChildren().add(statisticsView);

            // ========================================
            // CHARGER LA LISTE DES MOTS
            // ========================================
            FXMLLoader listLoader = new FXMLLoader(getClass().getResource("/com/secret/dictionary/fxml/word-list.fxml"));
            VBox listView = listLoader.load();
            wordListController = listLoader.getController();
            wordListController.setMotService(motService);
            wordListController.setMainController(this);
            rightContainer.getChildren().add(listView);

            // ========================================
            // INITIALISER LES CONTRÔLEURS DE DIALOGUES
            // ========================================
            searchDialogController = new SearchDialogController(motService, this);
            addWordDialogController = new AddWordDialogController(motService, this);

            // ========================================
            // CHARGER TOUS LES MOTS AU DÉMARRAGE
            // ========================================
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

    @FXML
    private void onStatistiquesClick() {
        showStatisticsView();
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
        showStatisticsView(); // Retour aux statistiques
    }

    public void rafraichirListeMots() {
        wordListController.chargerTousLesMots();
        // Rafraîchir aussi les statistiques
        if (statisticsViewController != null) {
            statisticsViewController.rafraichirStatistiques();
        }
    }

    // ========================================
    // GESTION VISIBILITÉ DES VUES
    // ========================================

    /**
     * Affiche la vue détails dans le centre
     */
    private void showDetailsView() {
        centerContainer.getChildren().clear();
        centerContainer.getChildren().add(detailsView);
        centerContainer.setVisible(true);
        centerContainer.setManaged(true);
    }

    /**
     * Affiche la vue statistiques dans le centre
     */
    private void showStatisticsView() {
        // Masquer d'abord les détails
        if (wordDetailsController != null) {
            wordDetailsController.masquerDetails();
        }

        centerContainer.getChildren().clear();
        centerContainer.getChildren().add(statisticsView);
        centerContainer.setVisible(true);
        centerContainer.setManaged(true);

        // Rafraîchir les statistiques
        if (statisticsViewController != null) {
            statisticsViewController.rafraichirStatistiques();
        }
    }

    /**
     * Cache la vue centrale
     */
    private void hideDetailsView() {
        if (wordDetailsController != null) {
            wordDetailsController.masquerDetails();
        }
        centerContainer.setVisible(false);
        centerContainer.setManaged(false);
    }
}