package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Contrôleur pour la liste des mots (panneau droit)
 * Version mise à jour avec affichage des émojis dans la liste
 */
public class WordListController {

    @FXML private VBox vboxRight;
    @FXML private Label rightTitle;
    @FXML private ListView<MotDTO> wordList;

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
        // Configuration du rendu personnalisé des cellules
        wordList.setCellFactory(param -> new ListCell<MotDTO>() {
            @Override
            protected void updateItem(MotDTO dto, boolean empty) {
                super.updateItem(dto, empty);

                if (empty || dto == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    // Émoji (si présent)
                    if (dto.emojie() != null && !dto.emojie().isEmpty()) {
                        Label emojieLabel = new Label(dto.emojie());
                        emojieLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;"); //.
                        hbox.getChildren().add(emojieLabel);
                    }

                    // Mot
                    Label motLabel = new Label(dto.mot());
                    motLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                    HBox.setHgrow(motLabel, Priority.ALWAYS);
                    hbox.getChildren().add(motLabel);

                    setGraphic(hbox);
                    setText(null);
                }
            }
        });

        // Écouter les sélections dans la liste
        wordList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && mainController != null) {
                        mainController.afficherDetailsMot(newValue.mot());
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
        ObservableList<MotDTO> observableMots = FXCollections.observableArrayList();

        // Récupérer les infos complètes pour chaque mot
        for (String mot : mots) {
            MotDTO dto = motService.getInfoMot(new MotDTO(mot, null, null, null));
            if (dto != null) {
                observableMots.add(dto);
            }
        }

        wordList.setItems(observableMots);
        System.out.println("✅ " + mots.size() + " mots chargés dans la liste");
    }
}