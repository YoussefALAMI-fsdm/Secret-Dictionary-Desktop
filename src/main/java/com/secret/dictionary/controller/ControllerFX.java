package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur principal avec dialogues personnalisés et autocomplétion
 */
public class ControllerFX {

    // ========================================
    // INJECTION DU SERVICE
    // ========================================
    private MotServiceImp motService;

    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
        chargerTousLesMots();
    }

    // ========================================
    // COMPOSANTS FXML
    // ========================================
    @FXML private VBox vboxLeft;
    @FXML private Button btnCatego;
    @FXML private VBox menuCatego;
    @FXML private VBox btnFavorisGroup;
    @FXML private VBox btnNouveauGroup;
    @FXML private Button btnRecherche;
    @FXML private Button btnNouveau;
    @FXML private VBox vboxCenter;
    @FXML private Label wordTitle;
    @FXML private Label definitionText;
    @FXML private Label synonymsText;
    @FXML private VBox vboxRight;
    @FXML private Label rightTitle;
    @FXML private ListView<String> wordList;

    private boolean isMenuVisible = false;

    // ========================================
    // INITIALISATION
    // ========================================
    @FXML
    public void initialize() {
        wordList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        afficherDetailsMot(newValue);
                    }
                }
        );
    }

    // ========================================
    // GESTION DU MENU
    // ========================================
    @FXML
    private void toggleMenu() {
        isMenuVisible = !isMenuVisible;
        menuCatego.setVisible(isMenuVisible);
        menuCatego.setManaged(isMenuVisible);

        TranslateTransition ttFavoris = new TranslateTransition(Duration.millis(300), btnFavorisGroup);
        TranslateTransition ttNouveau = new TranslateTransition(Duration.millis(300), btnNouveauGroup);

        if (isMenuVisible) {
            double menuHeight = 5;
            ttFavoris.setToY(menuHeight);
            ttNouveau.setToY(menuHeight);
        } else {
            ttFavoris.setToY(0);
            ttNouveau.setToY(0);
        }

        ttFavoris.play();
        ttNouveau.play();
    }

    // ========================================
    // CHARGER TOUS LES MOTS
    // ========================================
    private void chargerTousLesMots() {
        if (motService == null) return;

        List<String> mots = motService.getAllMots();
        ObservableList<String> observableMots = FXCollections.observableArrayList(mots);
        wordList.setItems(observableMots);

        System.out.println("✅ " + mots.size() + " mots chargés dans la liste");
    }

    // ========================================
    // AFFICHER LES DÉTAILS D'UN MOT
    // ========================================
    private void afficherDetailsMot(String mot) {
        if (motService == null) return;

        MotDTO dto = new MotDTO(mot, null);
        MotDTO resultat = motService.getInfoMot(dto);

        if (resultat != null) {
            wordTitle.setText(resultat.getMot());
            definitionText.setText(resultat.getDefinition() != null ?
                    resultat.getDefinition() : "Pas de définition disponible");
            synonymsText.setText("À venir...");

            vboxRight.setVisible(false);
            vboxLeft.setVisible(false);
            vboxCenter.setVisible(true);
        } else {
            afficherErreur("Mot introuvable", "Le mot '" + mot + "' n'existe pas dans le dictionnaire.");
        }
    }

    // ========================================
    // BOUTON "TOUS LES MOTS"
    // ========================================
    @FXML
    private void onTousLesMotsClick() {
        vboxLeft.setVisible(true);
        vboxRight.setVisible(true);
        vboxCenter.setVisible(false);

        chargerTousLesMots();

        if (isMenuVisible) {
            toggleMenu();
        }
    }

    // ========================================
    // 🎨 DIALOGUE RECHERCHE AVEC AUTOCOMPLÉTION
    // ========================================
    @FXML
    private void onRechercheClick() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("🔍 Rechercher un mot");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType btnRechercher = new ButtonType("Rechercher", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnRechercher, btnAnnuler);

        // ✨ CONTENEUR PRINCIPAL
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-background-color: #1a0b2e; -fx-background-radius: 10;");
        content.setMinHeight(Region.USE_PREF_SIZE);

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
        autocompleteBox.setMinHeight(Region.USE_PREF_SIZE);

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
                    int itemCount = Math.min(suggestions.size(), 5);
                    double itemHeight = 35;
                    double calculatedHeight = itemCount * itemHeight + 2;
                    suggestionList.setPrefHeight(calculatedHeight);
                    suggestionList.setMinHeight(calculatedHeight);

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
                afficherDetailsMot(mot);
            }
        });
    }

    // ========================================
    // 🎨 DIALOGUE NOUVEAU MOT
    // ========================================
    @FXML
    private void onNouveauClick() {
        Dialog<MotDTO> dialog = new Dialog<>();
        dialog.setTitle("➕ Ajouter un nouveau mot");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType btnAjouter = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAjouter, btnAnnuler);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a0b2e; -fx-background-radius: 10;");

        Label titre = new Label("Créer une nouvelle entrée");
        titre.setStyle("-fx-font-size: 18px; -fx-text-fill: #c77dff; -fx-font-weight: bold;");

        VBox motBox = new VBox(5);
        Label lblMot = new Label("📝 Mot *");
        lblMot.setStyle("-fx-font-size: 14px; -fx-text-fill: #b185db; -fx-font-weight: bold;");

        TextField txtMot = new TextField();
        txtMot.setPromptText("Exemple : Époustouflant");
        txtMot.setStyle(
                "-fx-background-color: #16213e; " +
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: #888; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10; " +
                        "-fx-background-radius: 5;"
        );
        txtMot.setPrefWidth(400);
        motBox.getChildren().addAll(lblMot, txtMot);

        VBox defBox = new VBox(5);
        Label lblDef = new Label("📖 Définition");
        lblDef.setStyle("-fx-font-size: 14px; -fx-text-fill: #b185db; -fx-font-weight: bold;");

        TextArea txtDef = new TextArea();
        txtDef.setPromptText("Entrez la définition du mot...");
        txtDef.setStyle(
                "-fx-background-color: #b185db;  " +
                        "-fx-control-inner-background: #16213e; "+
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: #888; " +
                        "-fx-font-size: 13px; " +
                        "-fx-background-radius: 5;"

        );
        txtDef.setPrefRowCount(4);
        txtDef.setPrefWidth(400);
        txtDef.setWrapText(true);
        defBox.getChildren().addAll(lblDef, txtDef);

        Label info = new Label("* Champ obligatoire");
        info.setStyle("-fx-font-size: 11px; -fx-text-fill: #888; -fx-font-style: italic;");

        content.getChildren().addAll(titre, motBox, defBox, info);
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().setStyle(
                "-fx-background-color: #0f0e17; " +
                        "-fx-border-color: #7209b7; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;"
        );

        txtMot.requestFocus();

        Button btnAjouterNode = (Button) dialog.getDialogPane().lookupButton(btnAjouter);
        btnAjouterNode.setDisable(true);
        txtMot.textProperty().addListener((observable, oldValue, newValue) -> {
            btnAjouterNode.setDisable(newValue.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAjouter) {
                String mot = txtMot.getText().trim();
                String def = txtDef.getText().trim();
                if (!mot.isEmpty()) {
                    return new MotDTO(mot, def.isEmpty() ? null : def);
                }
            }
            return null;
        });

        Optional<MotDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            int resultat = motService.addMot(dto);

            switch (resultat) {
                case 1:
                    afficherSucces("✅ Mot ajouté",
                            "Le mot '" + dto.getMot() + "' a été ajouté avec succès !");
                    chargerTousLesMots();
                    break;
                case 0:
                    afficherErreur("⚠️ Mot existant",
                            "Le mot '" + dto.getMot() + "' existe déjà dans le dictionnaire.");
                    break;
                case -1:
                    afficherErreur("❌ Erreur",
                            "Une erreur est survenue lors de l'ajout du mot.");
                    break;
            }
        });
    }

    // ========================================
    // DIALOGUES D'INFORMATION
    // ========================================
    private void afficherSucces(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().setStyle(
                "-fx-background-color: #1a0b2e; " +
                        "-fx-border-color: #52b788; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;"
        );

        alert.getDialogPane().lookup(".content.label").setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 14px;"
        );

        alert.showAndWait();
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().setStyle(
                "-fx-background-color: #1a0b2e; " +
                        "-fx-border-color: #e85d04; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;"
        );

        alert.getDialogPane().lookup(".content.label").setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 14px;"
        );

        alert.showAndWait();
    }
}