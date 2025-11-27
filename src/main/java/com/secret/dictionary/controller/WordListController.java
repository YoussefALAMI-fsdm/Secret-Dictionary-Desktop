package com.secret.dictionary.controller;

import com.secret.dictionary.service.MotServiceImp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Contrôleur pour la liste des mots (panneau droit)
 */
public class WordListController {

    @FXML private VBox vboxRight;
    @FXML private Label rightTitle;
    @FXML private ListView<String> wordList;

    private MotServiceImp motService;
    private MainController mainController;

    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        // Écouter les sélections dans la liste
        wordList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && mainController != null) {
                        mainController.afficherDetailsMot(newValue);
                    }
                }
        );
    }

    // ========================================
    // CHARGER TOUS LES MOTS
    // ========================================
    public void chargerTousLesMots() {
        if (motService == null) return;

        List<String> mots = motService.getAllMots();
        ObservableList<String> observableMots = FXCollections.observableArrayList(mots);
        wordList.setItems(observableMots);

        System.out.println("✅ " + mots.size() + " mots chargés dans la liste");
    }
}