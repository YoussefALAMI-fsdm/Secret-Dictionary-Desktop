package com.secret.dictionary.controller;

import com.secret.dictionary.service.MotServiceImp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Contrôleur principal qui orchestre les différents composants de l'interface
 * Responsabilités :
 * - Injection du service dans les sous-contrôleurs
 * - Chargement des vues FXML
 * - Coordination entre les différents panneaux
 */
public class MainController {

    @FXML private BorderPane rootPane;
    @FXML private VBox leftPanel;
    @FXML private VBox centerPanel;
    @FXML private VBox rightPanel;

    private MotServiceImp motService;

    // Références aux sous-contrôleurs
    private MenuController menuController;
    private WordDetailsController wordDetailsController;
    private WordListController wordListController;

    /**
     * Injection du service métier
     */
    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
        initializeSubControllers();
    }

    /**
     * Initialisation après injection du service
     */
    private void initializeSubControllers() {
        try {
            loadMenuPanel();
            loadWordDetailsPanel();
            loadWordListPanel();

            rootPane.applyCss();
            rootPane.layout();

            // Configuration de la communication entre contrôleurs
            wordListController.setOnWordSelected(this::handleWordSelection);
            menuController.setMainController(this);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des panneaux : " + e.getMessage());
        }
    }

    /**
     * Charge le panneau menu (gauche)
     */
    private void loadMenuPanel() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/secret/dictionary/fxml/menu-panel.fxml")
        );
        Parent menuView = loader.load();

        menuController = loader.getController();
        menuController.setMotService(motService);

        leftPanel.getChildren().setAll(menuView);
    }

    /**
     * Charge le panneau détails du mot (centre)
     */
    private void loadWordDetailsPanel() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/secret/dictionary/fxml/word-details.fxml")
        );
        Parent detailsView = loader.load();

        wordDetailsController = loader.getController();
        wordDetailsController.setMotService(motService);

        centerPanel.getChildren().setAll(detailsView);
    }

    /**
     * Charge le panneau liste des mots (droite)
     */
    private void loadWordListPanel() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/secret/dictionary/fxml/word-list.fxml")
        );
        Parent listView = loader.load();

        wordListController = loader.getController();
        wordListController.setMotService(motService);

        rightPanel.getChildren().setAll(listView);
    }

    /**
     * Gestion de la sélection d'un mot dans la liste
     */
    private void handleWordSelection(String mot) {
        wordDetailsController.afficherMot(mot);
        showDetailsView();
    }

    /**
     * Affiche la vue des détails (cache la liste)
     */
    public void showDetailsView() {
        leftPanel.setVisible(false);
        rightPanel.setVisible(false);
        centerPanel.setVisible(true);
    }

    /**
     * Affiche la vue liste complète
     */
    public void showListView() {
        leftPanel.setVisible(true);
        rightPanel.setVisible(true);
        centerPanel.setVisible(false);
        wordListController.rechargerListe();
    }

    /**
     * Ouvre le dialogue de recherche
     */
    public void openSearchDialog() {
        try {
            SearchDialogController.show(motService, this::handleWordSelection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ouvre le dialogue d'ajout de mot
     */
    public void openAddWordDialog() {
        try {
            AddWordDialogController.show(motService, success -> {
                if (success) {
                    wordListController.rechargerListe();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode appelée par le bouton recherche dans la barre supérieure
     */
    @FXML
    private void onRechercheClick() {
        openSearchDialog();
    }
}