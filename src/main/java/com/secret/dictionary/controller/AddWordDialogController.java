package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Contrôleur du dialogue d'ajout de mot
 * Responsabilités :
 * - Interface d'ajout de mot
 * - Validation des données
 * - Communication avec le service
 */
public class AddWordDialogController {

    @FXML private TextField txtMot;
    @FXML private TextArea txtDef;

    private MotServiceImp motService;
    private Dialog<MotDTO> dialog;

    /**
     * Méthode statique pour créer et afficher le dialogue
     */
    public static void show(MotServiceImp motService, Consumer<Boolean> onComplete) throws IOException {
        Dialog<MotDTO> dialog = new Dialog<>();
        dialog.setTitle("➕ Ajouter un nouveau mot");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType btnAjouter = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAjouter, btnAnnuler);

        // Chargement du FXML
        FXMLLoader loader = new FXMLLoader(
                AddWordDialogController.class.getResource("/com/secret/dictionary/fxml/add-word-dialog.fxml")
        );
        VBox content = loader.load();

        AddWordDialogController controller = loader.getController();
        controller.dialog = dialog;
        controller.motService = motService;
        controller.setupDialog(btnAjouter);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getStylesheets().add(
                AddWordDialogController.class.getResource("/com/secret/dictionary/styles/dialogs.css").toExternalForm()
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAjouter) {
                String mot = controller.txtMot.getText().trim();
                String def = controller.txtDef.getText().trim();
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
                    if (onComplete != null) onComplete.accept(true);
                    break;
                case 0:
                    afficherErreur("⚠️ Mot existant",
                            "Le mot '" + dto.getMot() + "' existe déjà dans le dictionnaire.");
                    if (onComplete != null) onComplete.accept(false);
                    break;
                case -1:
                    afficherErreur("❌ Erreur",
                            "Une erreur est survenue lors de l'ajout du mot.");
                    if (onComplete != null) onComplete.accept(false);
                    break;
            }
        });
    }

    @FXML
    private void initialize() {
        // Configuration initiale
        txtDef.setWrapText(true);
    }

    /**
     * Configuration du dialogue
     */
    private void setupDialog(ButtonType btnAjouter) {
        Button btnAjouterNode = (Button) dialog.getDialogPane().lookupButton(btnAjouter);
        btnAjouterNode.setDisable(true);

        txtMot.textProperty().addListener((observable, oldValue, newValue) -> {
            btnAjouterNode.setDisable(newValue.trim().isEmpty());
        });

        txtMot.requestFocus();
    }

    /**
     * Affiche une alerte de succès
     */
    private static void afficherSucces(String titre, String message) {
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

    /**
     * Affiche une alerte d'erreur
     */
    private static void afficherErreur(String titre, String message) {
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