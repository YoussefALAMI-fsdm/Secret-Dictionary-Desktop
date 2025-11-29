package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour le dialogue d'ajout de synonyme
 */
public class AddSynonymeDialogController {

    private final MotServiceImp motService;
    private final MainController mainController;

    public AddSynonymeDialogController(MotServiceImp motService, MainController mainController) {
        this.motService = motService;
        this.mainController = mainController;
    }

    public void show() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("🔗 Ajouter un synonyme");
        dialog.initModality(Modality.APPLICATION_MODAL);

        // ✅ CHARGER LE CSS
        String cssURL = getClass().getResource("/com/secret/dictionary/styles/dialogs.css").toExternalForm();
        dialog.getDialogPane().getStylesheets().add(cssURL);

        ButtonType btnAjouter = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAjouter, btnAnnuler);

        // Conteneur principal
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("dialog-content");

        Label titre = new Label("Créer une relation de synonymie");
        titre.getStyleClass().add("dialog-title");

        // ========== PREMIER MOT ==========
        VBox mot1Box = new VBox(5);
        Label lblMot1 = new Label("📝 Premier mot *");
        lblMot1.getStyleClass().add("field-label");

        TextField txtMot1 = new TextField();
        txtMot1.setPromptText("Tapez pour rechercher...");
        txtMot1.getStyleClass().add("text-field");
        txtMot1.setPrefWidth(400);

        ListView<String> suggestionList1 = new ListView<>();
        suggestionList1.setVisible(false);
        suggestionList1.setManaged(false);
        suggestionList1.setMaxHeight(150);
        suggestionList1.getStyleClass().add("suggestion-list");

        VBox autocompleteBox1 = new VBox(0);
        autocompleteBox1.getChildren().addAll(txtMot1, suggestionList1);
        mot1Box.getChildren().addAll(lblMot1, autocompleteBox1);

        // ========== DEUXIÈME MOT ==========
        VBox mot2Box = new VBox(5);
        Label lblMot2 = new Label("📝 Deuxième mot (synonyme) *");
        lblMot2.getStyleClass().add("field-label");

        TextField txtMot2 = new TextField();
        txtMot2.setPromptText("Tapez pour rechercher...");
        txtMot2.getStyleClass().add("text-field");
        txtMot2.setPrefWidth(400);

        ListView<String> suggestionList2 = new ListView<>();
        suggestionList2.setVisible(false);
        suggestionList2.setManaged(false);
        suggestionList2.setMaxHeight(150);
        suggestionList2.getStyleClass().add("suggestion-list");

        VBox autocompleteBox2 = new VBox(0);
        autocompleteBox2.getChildren().addAll(txtMot2, suggestionList2);
        mot2Box.getChildren().addAll(lblMot2, autocompleteBox2);

        Label info = new Label("💡 Les deux mots doivent exister dans le dictionnaire");
        info.getStyleClass().add("info-label");

        content.getChildren().addAll(titre, mot1Box, mot2Box, info);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getStyleClass().add("custom-dialog");

        // ========================================
        // AUTOCOMPLÉTION POUR MOT 1
        // ========================================
        configureAutocompletion(txtMot1, suggestionList1, motService);

        // ========================================
        // AUTOCOMPLÉTION POUR MOT 2
        // ========================================
        configureAutocompletion(txtMot2, suggestionList2, motService);

        Button btnAjouterNode = (Button) dialog.getDialogPane().lookupButton(btnAjouter);
        btnAjouterNode.setDisable(true);

        // Activer le bouton seulement si les deux champs sont remplis
        txtMot1.textProperty().addListener((obs, old, newVal) -> {
            btnAjouterNode.setDisable(txtMot1.getText().trim().isEmpty() || txtMot2.getText().trim().isEmpty());
        });

        txtMot2.textProperty().addListener((obs, old, newVal) -> {
            btnAjouterNode.setDisable(txtMot1.getText().trim().isEmpty() || txtMot2.getText().trim().isEmpty());
        });

        txtMot1.requestFocus();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAjouter) {
                String mot1 = txtMot1.getText().trim();
                String mot2 = txtMot2.getText().trim();

                if (!mot1.isEmpty() && !mot2.isEmpty()) {
                    return ajouterSynonyme(mot1, mot2);
                }
            }
            return false;
        });

        dialog.showAndWait();
    }

    /**
     * Configure l'autocomplétion pour un TextField
     */
    private void configureAutocompletion(TextField textField, ListView<String> suggestionList, MotServiceImp motService) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String query = newValue.trim();

            if (query.isEmpty()) {
                suggestionList.setVisible(false);
                suggestionList.setManaged(false);
            } else {
                List<String> suggestions = motService.getListMot(query);

                if (suggestions.isEmpty()) {
                    suggestionList.setVisible(false);
                    suggestionList.setManaged(false);
                } else {
                    ObservableList<String> items = FXCollections.observableArrayList(suggestions);
                    suggestionList.setItems(items);

                    int itemCount = Math.min(suggestions.size(), 3);
                    double itemHeight = 35;
                    double calculatedHeight = itemCount * itemHeight + 2;
                    suggestionList.setPrefHeight(calculatedHeight);
                    suggestionList.setMinHeight(calculatedHeight);

                    suggestionList.setVisible(true);
                    suggestionList.setManaged(true);
                }
            }
        });

        // Clic sur suggestion
        suggestionList.setOnMouseClicked(event -> {
            String selected = suggestionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                textField.setText(selected);
                suggestionList.setVisible(false);
                suggestionList.setManaged(false);
            }
        });

        // Navigation clavier
        textField.setOnKeyPressed(event -> {
            if (suggestionList.isVisible()) {
                if (event.getCode() == KeyCode.DOWN) {
                    suggestionList.requestFocus();
                    suggestionList.getSelectionModel().selectFirst();
                    event.consume();
                } else if (event.getCode() == KeyCode.ENTER) {
                    if (!suggestionList.getItems().isEmpty()) {
                        String firstItem = suggestionList.getItems().get(0);
                        textField.setText(firstItem);
                        suggestionList.setVisible(false);
                        suggestionList.setManaged(false);
                    }
                    event.consume();
                }
            }
        });

        suggestionList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String selected = suggestionList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    textField.setText(selected);
                    suggestionList.setVisible(false);
                    suggestionList.setManaged(false);
                    textField.requestFocus();
                }
                event.consume();
            }
        });
    }

    /**
     * Ajoute la relation de synonymie entre deux mots
     */
    private boolean ajouterSynonyme(String mot1, String mot2) {
        if (mot1.equalsIgnoreCase(mot2)) {
            afficherErreur("❌ Erreur", "Un mot ne peut pas être son propre synonyme !");
            return false;
        }

        MotDTO dto1 = new MotDTO(mot1, null, null, null);
        MotDTO dto2 = new MotDTO(mot2, null, null, null);

        int resultat = motService.addSynonyme(dto1, dto2);

        switch (resultat) {
            case 1:
                afficherSucces("✅ Synonyme ajouté",
                        "La relation de synonymie entre '" + mot1 + "' et '" + mot2 + "' a été créée !");
                return true;
            case 0:
                afficherErreur("⚠️ Mot introuvable",
                        "L'un des mots n'existe pas dans le dictionnaire.\nVeuillez d'abord les ajouter.");
                return false;
            case -1:
                afficherErreur("❌ Erreur",
                        "Une erreur est survenue. La relation existe peut-être déjà.");
                return false;
            default:
                return false;
        }
    }

    private void afficherSucces(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        String cssURL = getClass().getResource("/com/secret/dictionary/styles/dialogs.css").toExternalForm();
        alert.getDialogPane().getStylesheets().add(cssURL);
        alert.getDialogPane().getStyleClass().add("alert-success");

        alert.showAndWait();
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        String cssURL = getClass().getResource("/com/secret/dictionary/styles/dialogs.css").toExternalForm();
        alert.getDialogPane().getStylesheets().add(cssURL);
        alert.getDialogPane().getStyleClass().add("alert-error");

        alert.showAndWait();
    }
}