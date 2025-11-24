package com.secret.dictionary.controller;

import com.secret.dictionary.service.MotServiceImp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Contrôleur du dialogue de recherche avec autocomplétion
 * Responsabilités :
 * - Interface de recherche
 * - Autocomplétion en temps réel
 * - Navigation clavier
 */
public class SearchDialogController {

    @FXML private TextField txtRecherche;
    @FXML private ListView<String> suggestionList;
    @FXML private Label resultCount;
    @FXML private VBox autocompleteBox;

    private MotServiceImp motService;
    private Consumer<String> onWordSelected;
    private Dialog<String> dialog;

    /**
     * Méthode statique pour créer et afficher le dialogue
     */
    public static void show(MotServiceImp motService, Consumer<String> onWordSelected) throws IOException {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("🔍 Rechercher un mot");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType btnRechercher = new ButtonType("Rechercher", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnRechercher, btnAnnuler);

        // Chargement du FXML
        FXMLLoader loader = new FXMLLoader(
                SearchDialogController.class.getResource("/com/secret/dictionary/fxml/search-dialog.fxml")
        );
        VBox content = loader.load();

        SearchDialogController controller = loader.getController();
        controller.dialog = dialog;
        controller.motService = motService;
        controller.onWordSelected = onWordSelected;
        controller.setupDialog(btnRechercher);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getStylesheets().add(
                SearchDialogController.class.getResource("/com/secret/dictionary/styles/dialogs.css").toExternalForm()
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnRechercher) {
                return controller.txtRecherche.getText().trim();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(mot -> {
            if (!mot.isEmpty() && onWordSelected != null) {
                onWordSelected.accept(mot);
            }
        });
    }

    @FXML
    private void initialize() {
        setupAutocompletion();
        setupKeyboardNavigation();
    }

    /**
     * Configuration du dialogue
     */
    private void setupDialog(ButtonType btnRechercher) {
        Button btnRechercherNode = (Button) dialog.getDialogPane().lookupButton(btnRechercher);
        btnRechercherNode.setDisable(true);

        txtRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            btnRechercherNode.setDisable(newValue.trim().isEmpty());
        });

        txtRecherche.requestFocus();
    }

    /**
     * Configuration de l'autocomplétion
     */
    private void setupAutocompletion() {
        txtRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            String query = newValue.trim();

            if (query.isEmpty()) {
                suggestionList.setVisible(false);
                suggestionList.setManaged(false);
                resultCount.setVisible(false);
            } else {
                List<String> suggestions = motService.getListMot(query);

                if (suggestions == null || suggestions.isEmpty()) {
                    suggestionList.setVisible(false);
                    suggestionList.setManaged(false);
                    resultCount.setText("❌ Aucun mot trouvé");
                    resultCount.setStyle("-fx-text-fill: #e85d04;");
                    resultCount.setVisible(true);
                } else {
                    ObservableList<String> items = FXCollections.observableArrayList(suggestions);
                    suggestionList.setItems(items);

                    // Calcul hauteur optimale
                    int itemCount = Math.min(suggestions.size(), 5);
                    double itemHeight = 35;
                    double calculatedHeight = itemCount * itemHeight + 2;
                    suggestionList.setPrefHeight(calculatedHeight);
                    suggestionList.setMinHeight(calculatedHeight);

                    suggestionList.setVisible(true);
                    suggestionList.setManaged(true);

                    String countText = suggestions.size() == 1 ?
                            "✓ 1 mot trouvé" :
                            "✓ " + suggestions.size() + " mots trouvés";
                    resultCount.setText(countText);
                    resultCount.setStyle("-fx-text-fill: #52b788;");
                    resultCount.setVisible(true);
                }
            }
        });

        // Clic sur suggestion
        suggestionList.setOnMouseClicked(event -> {
            String selected = suggestionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtRecherche.setText(selected);
                suggestionList.setVisible(false);
                suggestionList.setManaged(false);
            }
        });
    }

    /**
     * Configuration de la navigation clavier
     */
    private void setupKeyboardNavigation() {
        txtRecherche.setOnKeyPressed(event -> {
            if (suggestionList.isVisible()) {
                if (event.getCode() == KeyCode.DOWN) {
                    suggestionList.requestFocus();
                    suggestionList.getSelectionModel().selectFirst();
                    event.consume();
                } else if (event.getCode() == KeyCode.ENTER) {
                    if (!suggestionList.getItems().isEmpty()) {
                        String firstItem = suggestionList.getItems().get(0);
                        txtRecherche.setText(firstItem);
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
                    txtRecherche.setText(selected);
                    suggestionList.setVisible(false);
                    suggestionList.setManaged(false);
                    txtRecherche.requestFocus();
                }
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                suggestionList.setVisible(false);
                suggestionList.setManaged(false);
                txtRecherche.requestFocus();
                event.consume();
            }
        });
    }
}