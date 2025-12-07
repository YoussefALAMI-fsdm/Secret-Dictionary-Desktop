package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.Optional;

/**
 * Contrôleur pour le dialogue de modification d'un mot existant
 */
public class UpdateWordDialogController {

    private final MotServiceImp motService;
    private final MainController mainController;

    // Catégories disponibles
    private static final String[] CATEGORIES = {
            "General", "Verbe", "Adjectif", "Nom", "Adverbe", "Expression"
    };

    public UpdateWordDialogController(MotServiceImp motService, MainController mainController) {
        this.motService = motService;
        this.mainController = mainController;
    }

    // ========================================
    // AFFICHER LE DIALOGUE DE MODIFICATION
    // ========================================
    public void show(MotDTO motActuel) {
        if (motActuel == null) {
            afficherErreur("❌ Erreur", "Aucun mot sélectionné pour la modification.");
            return;
        }

        Dialog<MotDTO> dialog = new Dialog<>();
        dialog.setTitle("✏️ Modifier le mot : " + motActuel.mot());
        //blocage d’une fenêtre
        dialog.initModality(Modality.APPLICATION_MODAL);//enum,constante

        String cssURL = getClass().getResource("/com/secret/dictionary/styles/dialogs.css").toExternalForm();
        dialog.getDialogPane().getStylesheets().add(cssURL);

        ButtonType btnEnregistrer = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnEnregistrer, btnAnnuler);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("dialog-content");

        Label titre = new Label("Modifier les informations du mot");
        titre.getStyleClass().add("dialog-title");

        // ========== CHAMP MOT (MODIFIABLE) ==========
        VBox motBox = new VBox(5);
        Label lblMot = new Label("📝 Mot");
        lblMot.getStyleClass().add("field-label");

        TextField txtMot = new TextField(motActuel.mot());
        txtMot.setEditable(true);
        txtMot.setPrefWidth(400);
        txtMot.getStyleClass().add("text-field"); // style normal
        motBox.getChildren().addAll(lblMot, txtMot);

        // ========== CHAMP DÉFINITION ==========
        VBox defBox = new VBox(5);
        Label lblDef = new Label("📖 Définition");
        lblDef.getStyleClass().add("field-label");

        TextArea txtDef = new TextArea(motActuel.definition() != null ? motActuel.definition() : "");
        txtDef.setPromptText("Entrez la définition du mot...");
        txtDef.getStyleClass().add("text-area");
        txtDef.setPrefRowCount(4);
        txtDef.setPrefWidth(400);
        txtDef.setWrapText(true);
        defBox.getChildren().addAll(lblDef, txtDef);

        // ========== CHAMP CATÉGORIE ==========
        VBox categorieBox = new VBox(5);
        Label lblCategorie = new Label("🏷️ Catégorie");
        lblCategorie.getStyleClass().add("field-label");

        ComboBox<String> cmbCategorie = new ComboBox<>();
        cmbCategorie.getItems().addAll(CATEGORIES);
        cmbCategorie.setValue(motActuel.categorie() != null ? motActuel.categorie() : "General");
        cmbCategorie.getStyleClass().add("combo-box");
        cmbCategorie.setPrefWidth(400);
        categorieBox.getChildren().addAll(lblCategorie, cmbCategorie);

        // ========== CHAMP ÉMOJI ==========
        VBox emojieBox = new VBox(5);
        Label lblEmojie = new Label("😊 Émoji (optionnel)");
        lblEmojie.getStyleClass().add("field-label");

        TextField txtEmojie = new TextField(motActuel.emojie() != null ? motActuel.emojie() : "");
        txtEmojie.setPromptText("Ex: 🎉 ✨ 💡");
        txtEmojie.getStyleClass().add("text-field-emoji");
        txtEmojie.setPrefWidth(400);
        emojieBox.getChildren().addAll(lblEmojie, txtEmojie);

        Label info = new Label("💡 Vous pouvez modifier le mot, sa définition, sa catégorie et son émoji.");
        info.getStyleClass().add("info-label");

        content.getChildren().addAll(titre, motBox, defBox, categorieBox, emojieBox, info);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getStyleClass().add("custom-dialog");

        txtMot.requestFocus();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnEnregistrer) {
                String mot = txtMot.getText().trim();
                String def = txtDef.getText().trim();
                String categorie = cmbCategorie.getValue();
                String emojie = txtEmojie.getText().trim();

                return new MotDTO(
                        mot.isEmpty() ? motActuel.mot() : mot,
                        def.isEmpty() ? null : def,
                        categorie,
                        emojie.isEmpty() ? null : emojie
                );
            }
            return null;
        });

        Optional<MotDTO> result = dialog.showAndWait();
        result.ifPresent(nouveauDTO -> {
            boolean resultat = motService.updateMot(motActuel, nouveauDTO);

            if (resultat) {
                afficherSucces("✅ Modification réussie",
                        "Le mot '" + nouveauDTO.mot() + "' a été modifié avec succès !");
                mainController.rafraichirListeMots();
                mainController.afficherDetailsMot(nouveauDTO.mot());
            } else {
                //Mot déjà existant
                //Erreur base de données
                afficherErreur("❌ Erreur",
                        "Une erreur est survenue lors de la modification du mot.");
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

        alert.getDialogPane().getStyleClass().add("alert-success");
        alert.showAndWait();
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().getStyleClass().add("alert-error");
        alert.showAndWait();
    }
}
