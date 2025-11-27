package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Contrôleur pour l'affichage des détails d'un mot
 */
public class WordDetailsController {

    @FXML private VBox vboxCenter;
    @FXML private Label wordTitle;
    @FXML private Label definitionText;
    @FXML private Label synonymsText;

    private MotServiceImp motService;

    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
    }

    @FXML
    public void initialize() {
        // Initialisation si nécessaire
    }

    // ========================================
    // AFFICHER LES DÉTAILS D'UN MOT
    // ========================================
    public void afficherDetailsMot(String mot) {
        if (motService == null) return;

        MotDTO dto = new MotDTO(mot, null);
        MotDTO resultat = motService.getInfoMot(dto);

        if (resultat != null) {
            wordTitle.setText(resultat.getMot());
            definitionText.setText(resultat.getDefinition() != null ?
                    resultat.getDefinition() : "Pas de définition disponible");
            synonymsText.setText("À venir...");
        } else {
            afficherErreur("Mot introuvable", "Le mot '" + mot + "' n'existe pas dans le dictionnaire.");
        }
    }

    // ========================================
    // DIALOGUE D'ERREUR
    // ========================================
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