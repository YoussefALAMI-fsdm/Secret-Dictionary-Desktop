package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.Optional;

/**
 * Contrôleur pour le dialogue d'ajout de nouveau mot
 * Version mise à jour avec categorie et emojie
 */
public class AddWordDialogController {

    private final MotServiceImp motService;
    private final MainController mainController;

    // Catégories disponibles
    private static final String[] CATEGORIES = {
            "General", "Verbe", "Adjectif", "Nom", "Adverbe", "Expression"
    };

    public AddWordDialogController(MotServiceImp motService, MainController mainController) {
        this.motService = motService;
        this.mainController = mainController;
    }

    // ========================================
    // AFFICHER LE DIALOGUE
    // ========================================
    public void show() {
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

        // ========== CHAMP MOT ==========
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

        // ========== CHAMP DÉFINITION ==========
        VBox defBox = new VBox(5);
        Label lblDef = new Label("📖 Définition");
        lblDef.setStyle("-fx-font-size: 14px; -fx-text-fill: #b185db; -fx-font-weight: bold;");

        TextArea txtDef = new TextArea();
        txtDef.setPromptText("Entrez la définition du mot...");
        txtDef.setStyle(
                "-fx-background-color: #b185db; " +
                        "-fx-control-inner-background: #16213e; " +
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: #888; " +
                        "-fx-font-size: 13px; " +
                        "-fx-background-radius: 5;"
        );
        txtDef.setPrefRowCount(4);
        txtDef.setPrefWidth(400);
        txtDef.setWrapText(true);
        defBox.getChildren().addAll(lblDef, txtDef);

        // ========== CHAMP CATÉGORIE ==========
        VBox categorieBox = new VBox(5);
        Label lblCategorie = new Label("🏷️ Catégorie");
        lblCategorie.setStyle("-fx-font-size: 14px; -fx-text-fill: #b185db; -fx-font-weight: bold;");

        ComboBox<String> cmbCategorie = new ComboBox<>();
        cmbCategorie.getItems().addAll(CATEGORIES);
        cmbCategorie.setValue("General");
        cmbCategorie.setStyle(
                "-fx-background-color: #16213e; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-radius: 5;"
        );
        cmbCategorie.setPrefWidth(400);
        categorieBox.getChildren().addAll(lblCategorie, cmbCategorie);

        // ========== CHAMP ÉMOJI ==========
        VBox emojieBox = new VBox(5);
        Label lblEmojie = new Label("😊 Émoji (optionnel)");
        lblEmojie.setStyle("-fx-font-size: 14px; -fx-text-fill: #b185db; -fx-font-weight: bold;");

        TextField txtEmojie = new TextField();
        txtEmojie.setPromptText("Ex: 🎉 ✨ 💡");
        txtEmojie.setStyle(
                "-fx-background-color: #16213e; " +
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: #888; " +
                        "-fx-font-size: 18px; " +
                        "-fx-padding: 10; " +
                        "-fx-background-radius: 5;"
        );
        txtEmojie.setPrefWidth(400);
        emojieBox.getChildren().addAll(lblEmojie, txtEmojie);

        Label info = new Label("* Champ obligatoire");
        info.setStyle("-fx-font-size: 11px; -fx-text-fill: #888; -fx-font-style: italic;");

        content.getChildren().addAll(titre, motBox, defBox, categorieBox, emojieBox, info);
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
                String categorie = cmbCategorie.getValue();
                String emojie = txtEmojie.getText().trim();

                if (!mot.isEmpty()) {
                    return new MotDTO(
                            mot,
                            def.isEmpty() ? null : def,
                            categorie,
                            emojie.isEmpty() ? null : emojie
                    );
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
                            "Le mot '" + dto.mot() + "' a été ajouté avec succès !");
                    mainController.rafraichirListeMots();
                    break;
                case 0:
                    afficherErreur("⚠️ Mot existant",
                            "Le mot '" + dto.mot() + "' existe déjà dans le dictionnaire.");
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