package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
// ✅ SUPPRIMER l'import de EmojiUtils
// import com.secret.dictionary.util.EmojiUtils;  // ❌ NE PAS IMPORTER
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

                    // ✅ Émoji en BLANC (sans EmojiUtils)
                    if (dto.emojie() != null && !dto.emojie().isEmpty()) {
                        Label emojieLabel = new Label(dto.emojie());

                        // ✅ Style blanc simple (sans police emoji colorée)
                        emojieLabel.setStyle(
                                "-fx-font-size: 18px; " +
                                        "-fx-text-fill: white;"
                        );

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

        wordList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && mainController != null) {
                        mainController.afficherDetailsMot(newValue.mot());
                    }
                }
        );
    }

    public void chargerTousLesMots() {
        if (motService == null) return;

        List<String> mots = motService.getAllMots();
        ObservableList<MotDTO> observableMots = FXCollections.observableArrayList();

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