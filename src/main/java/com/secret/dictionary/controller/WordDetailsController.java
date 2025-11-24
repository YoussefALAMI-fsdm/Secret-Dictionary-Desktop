package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

/**
 * Contrôleur pour l'affichage des détails d'un mot
 * Responsabilités :
 * - Affichage du mot sélectionné
 * - Affichage de la définition
 * - Affichage des synonymes (à venir)
 */
public class WordDetailsController {

    @FXML private Label wordTitle;
    @FXML private Label definitionText;
    @FXML private Label synonymsText;

    private MotServiceImp motService;

    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
    }

    @FXML
    private void initialize() {
        // État initial
        wordTitle.setText("Sélectionnez un mot");
        definitionText.setText("...");
        synonymsText.setText("À venir...");
    }

    /**
     * Affiche les détails d'un mot
     */
    public void afficherMot(String mot) {
        if (motService == null || mot == null || mot.trim().isEmpty()) {
            return;
        }

        MotDTO dto = new MotDTO(mot, null);
        MotDTO resultat = motService.getInfoMot(dto);

        if (resultat != null) {
            wordTitle.setText(resultat.getMot());

            String definition = resultat.getDefinition();
            if (definition != null && !definition.trim().isEmpty()) {
                definitionText.setText(definition);
            } else {
                definitionText.setText("Pas de définition disponible");
            }

            // TODO: Implémenter les synonymes
            synonymsText.setText("À venir...");

        } else {
            afficherErreur("Mot introuvable",
                    "Le mot '" + mot + "' n'existe pas dans le dictionnaire.");
        }
    }

    /**
     * Réinitialise l'affichage
     */
    public void reset() {
        wordTitle.setText("Sélectionnez un mot");
        definitionText.setText("...");
        synonymsText.setText("À venir...");
    }

    /**
     * Affiche une alerte d'erreur
     */
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