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

    // Injection du service
    private MotServiceImp motService;

    // Sous-contrôleurs
    //Les contrôleurs sont des attributs de classe
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
    //Méthode héritée de l'interface Initializable
    //Cette méthode doit être appelée après que JavaFX ait injecté tous les éléments du fichier FXML dans le contrôleur
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
            //récupérer l’instance du contrôleur définie dans le fichier FXML
            menuController = menuLoader.getController();
            //Le contrôleur du menu a donc besoin du service principal : injection de dépendance
            menuController.setMotService(motService);
            //donner au contrôleur du menu une référence vers le contrôleur principal
            menuController.setMainController(this);  // RE IMPORTANT : Initialise les dialogues synonyme/antonyme
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

            // Les dialogues synonyme/antonyme sont initialisés dans MenuController
            // via setMainController() appelé ci-dessus

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
     * Affiche la vue détails dans le centre sans cacher les conteneurs gauche/droite
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