package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
import com.secret.dictionary.util.EmojiUtils;  // ✅ IMPORT AJOUTÉ
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WordDetailsController {

    @FXML private VBox vboxCenter;
    @FXML private Label wordTitle;
    @FXML private Label emojieLabel;
    @FXML private Label categorieLabel;
    @FXML private Label definitionText;
    @FXML private Label synonymsText;
    @FXML private Button btnModifier;

    private MotServiceImp motService;
    private MainController mainController;
    private UpdateWordDialogController updateWordDialogController;
    private MotDTO motActuel;

    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        this.updateWordDialogController = new UpdateWordDialogController(motService, mainController);
    }

    @FXML
    public void initialize() {
        // ✅ Configurer le label emoji pour affichage coloré
        if (emojieLabel != null) {
            EmojiUtils.configureEmojiLabel(emojieLabel, 48);
        }

        if (btnModifier != null) {
            btnModifier.setVisible(false);
            btnModifier.setManaged(false);
        }
    }

    public void afficherDetailsMot(String mot) {
        if (motService == null) return;

        MotDTO dto = new MotDTO(mot, null, null, null);
        MotDTO resultat = motService.getInfoMot(dto);

        if (resultat != null) {
            this.motActuel = resultat;
            wordTitle.setText(resultat.mot());

            // ✅ Afficher l'émoji avec couleurs natives
            if (resultat.emojie() != null && !resultat.emojie().trim().isEmpty()) {
                emojieLabel.setText(resultat.emojie());
                emojieLabel.setVisible(true);
                emojieLabel.setManaged(true);

                // ✅ Reconfigurer pour s'assurer que les couleurs s'affichent
                EmojiUtils.configureEmojiLabel(emojieLabel, 48);
            } else {
                emojieLabel.setVisible(false);
                emojieLabel.setManaged(false);
            }

            if (resultat.categorie() != null && !resultat.categorie().trim().isEmpty()) {
                categorieLabel.setText("🏷️ " + resultat.categorie());
                categorieLabel.setVisible(true);
                categorieLabel.setManaged(true);
            } else {
                categorieLabel.setText("🏷️ General");
                categorieLabel.setVisible(true);
                categorieLabel.setManaged(true);
            }

            definitionText.setText(resultat.definition() != null ?
                    resultat.definition() : "Pas de définition disponible");

            synonymsText.setText("À venir...");

            if (btnModifier != null) {
                btnModifier.setVisible(true);
                btnModifier.setManaged(true);
            }

            System.out.println("=== Détails du mot ===");
            System.out.println("Mot: " + resultat.mot());
            System.out.println("Définition: " + resultat.definition());
            System.out.println("Catégorie: " + resultat.categorie());
            System.out.println("Émoji: " + resultat.emojie());
            System.out.println("======================");

        } else {
            afficherErreur("Mot introuvable", "Le mot '" + mot + "' n'existe pas dans le dictionnaire.");
        }
    }

    @FXML
    private void onModifierClick() {
        if (motActuel != null && updateWordDialogController != null) {
            updateWordDialogController.show(motActuel);
        } else {
            afficherErreur("Erreur", "Aucun mot sélectionné pour la modification.");
        }
    }

    public void masquerDetails() {
        if (btnModifier != null) {
            btnModifier.setVisible(false);
            btnModifier.setManaged(false);
        }
        this.motActuel = null;
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