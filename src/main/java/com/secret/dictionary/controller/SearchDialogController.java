package com.secret.dictionary.controller;

import com.secret.dictionary.service.MotServiceImp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour le dialogue de recherche avec autocomplétion
 */
public class SearchDialogController {

    private final MotServiceImp motService;
    private final MainController mainController;

    public SearchDialogController(MotServiceImp motService, MainController mainController) {
        this.motService = motService;
        this.mainController = mainController;
    }

    // ========================================
    // AFFICHER LE DIALOGUE
    // ========================================
    public void show() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("🔍 Rechercher un mot");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType btnRechercher = new ButtonType("Rechercher", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnRechercher, btnAnnuler);

        // ✨ CONTENEUR PRINCIPAL
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER_LEFT);//class, enumeration
        content.setStyle("-fx-background-color: #1a0b2e; -fx-background-radius: 10;");
        content.setMinHeight(Region.USE_PREF_SIZE);//calculée automatiquement par JavaFX, class abstraite,constante statique de la classe Region

        Label titre = new Label("Entrez le mot à rechercher");
        titre.setStyle("-fx-font-size: 16px; -fx-text-fill: #c77dff; -fx-font-weight: bold;");

        // Champ de texte
        TextField txtRecherche = new TextField();
        txtRecherche.setPromptText("Tapez pour rechercher...");
        txtRecherche.setStyle(
                "-fx-background-color: #16213e; " +
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: #888; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10; " +
                        "-fx-background-radius: 5 5 0 0;"
        );
        txtRecherche.setPrefWidth(400);

        // Liste de suggestions
        ListView<String> suggestionList = new ListView<>();
        suggestionList.setVisible(false);
        suggestionList.setManaged(false);
        suggestionList.setMaxHeight(180);
        suggestionList.setStyle(
                "-fx-background-color: #16213e; " +
                        "-fx-border-color: #7209b7; " +
                        "-fx-border-width: 0 2 2 2; " +
                        "-fx-border-radius: 0 0 5 5; " +
                        "-fx-background-radius: 0 0 5 5; " +
                        "-fx-background-insets: 0; " +
                        "-fx-padding: 0; " +
                        "-fx-fixed-cell-size: 35;"
        );
        //"-fx-fixed-cell-size: " + ITEM_HEIGHT + ";"

        // Style des cellules
        suggestionList.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText("  " + item);
                    setStyle(
                            "-fx-text-fill: white; " +
                                    "-fx-padding: 6 10; " +
                                    "-fx-font-size: 13px; " +
                                    "-fx-background-color: transparent; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-background-insets: 0;"
                    );

                    setOnMouseEntered(e -> {
                        if (!isEmpty()) {
                            setStyle(
                                    "-fx-text-fill: white; " +
                                            "-fx-padding: 6 10; " +
                                            "-fx-font-size: 13px; " +
                                            "-fx-background-color: #7209b7; " +
                                            "-fx-cursor: hand; " +
                                            "-fx-background-insets: 0;"
                            );
                        }
                    });

                    setOnMouseExited(e -> {
                        if (!isEmpty() && !isSelected()) {
                            setStyle(
                                    "-fx-text-fill: white; " +
                                            "-fx-padding: 6 10; " +
                                            "-fx-font-size: 13px; " +
                                            "-fx-background-color: transparent; " +
                                            "-fx-cursor: hand; " +
                                            "-fx-background-insets: 0;"
                            );
                        }
                    });
                }
            }
        });

        // Conteneur pour TextField + ListView
        VBox autocompleteBox = new VBox(0);
        autocompleteBox.getChildren().addAll(txtRecherche, suggestionList);
        autocompleteBox.setMaxWidth(400);
        autocompleteBox.setPrefWidth(400);
        autocompleteBox.setMinHeight(150);

        // Labels d'information
        Label info = new Label("💡 La recherche floue trouve les mots similaires");
        info.setStyle("-fx-font-size: 12px; -fx-text-fill: #b185db;");

        Label resultCount = new Label("");
        resultCount.setStyle("-fx-font-size: 11px; -fx-text-fill: #888; -fx-font-style: italic;");
        resultCount.setVisible(false);

        content.getChildren().addAll(titre, autocompleteBox, info, resultCount);
        dialog.getDialogPane().setContent(content);

        // Style du dialogue
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #0f0e17; " +
                        "-fx-border-color: #7209b7; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;"
        );

        // ========================================
        // 🔥 LOGIQUE D'AUTOCOMPLÉTION EN TEMPS RÉEL
        // ========================================
        Button btnRechercherNode = (Button) dialog.getDialogPane().lookupButton(btnRechercher);

        txtRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            String query = newValue.trim();

            if (query.isEmpty()) {
                suggestionList.setVisible(false);
                suggestionList.setManaged(false);
                resultCount.setVisible(false);
                btnRechercherNode.setDisable(true);
                txtRecherche.setStyle(
                        "-fx-background-color: #16213e; " +
                                "-fx-text-fill: white; " +
                                "-fx-prompt-text-fill: #888; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 10; " +
                                "-fx-background-radius: 5 5 0 0;"
                );
            } else {
                List<String> suggestions = motService.getListMot(query);

                if (suggestions.isEmpty()) {
                    suggestionList.setVisible(false);
                    suggestionList.setManaged(false);
                    resultCount.setText("❌ Aucun mot trouvé");
                    resultCount.setStyle("-fx-font-size: 11px; -fx-text-fill: #e85d04; -fx-font-style: italic;");
                    resultCount.setVisible(true);
                    btnRechercherNode.setDisable(true);
                } else {
                    ObservableList<String> items = FXCollections.observableArrayList(suggestions);
                    suggestionList.setItems(items);

                    // Calcul hauteur optimale
                    int itemCount = Math.min(suggestions.size(), 3);
                    double itemHeight = 35;
                    double calculatedHeight = itemCount * itemHeight + 2;
                    suggestionList.setPrefHeight(calculatedHeight);
                    suggestionList.setMinHeight(calculatedHeight);

                    // ✅ Hauteur fixe pour 3 éléments + bordures
                    //double fixedHeight = VISIBLE_ITEMS * ITEM_HEIGHT + 4;
                    //suggestionList.setPrefHeight(fixedHeight);
                    //suggestionList.setMaxHeight(fixedHeight);
                    //suggestionList.setMinHeight(fixedHeight);

                    suggestionList.setVisible(true);
                    suggestionList.setManaged(true);

                    // Forcer rafraîchissement
                    suggestionList.layout();
                    autocompleteBox.layout();
                    content.layout();

                    String countText = suggestions.size() == 1 ?
                            "✓ 1 mot trouvé" :
                            "✓ " + suggestions.size() + " mots trouvés";
                    resultCount.setText(countText);
                    resultCount.setStyle("-fx-font-size: 11px; -fx-text-fill: #52b788; -fx-font-style: italic;");
                    resultCount.setVisible(true);

                    btnRechercherNode.setDisable(false);

                    txtRecherche.setStyle(
                            "-fx-background-color: #16213e; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-prompt-text-fill: #888; " +
                                    "-fx-font-size: 14px; " +
                                    "-fx-padding: 10; " +
                                    "-fx-background-radius: 5 5 0 0; " +
                                    "-fx-border-color: #52b788; " +
                                    "-fx-border-width: 0 0 0 0;"
                    );
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

        // Navigation clavier
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

        txtRecherche.requestFocus();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnRechercher) {
                return txtRecherche.getText().trim();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(mot -> {
            if (!mot.isEmpty()) {
                mainController.afficherDetailsMot(mot);
            }
        });
    }
}