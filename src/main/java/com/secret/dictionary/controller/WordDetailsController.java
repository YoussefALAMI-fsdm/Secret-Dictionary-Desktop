package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Contrôleur pour l'affichage des détails d'un mot
 * Version mise à jour avec categorie et emojie
 */
public class WordDetailsController {

    @FXML private VBox vboxCenter;
    @FXML private Label wordTitle;
    @FXML private Label emojieLabel;
    @FXML private Label categorieLabel;
    @FXML private Label definitionText;
    @FXML private Label synonymsText;
    @FXML private Button btnModifier;  // ✅ Ajout du bouton

    private MotServiceImp motService;
    private MainController mainController;
    private UpdateWordDialogController updateWordDialogController;  // ✅ Ajout
    private MotDTO motActuel;  // ✅ Pour stocker le mot affiché

    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        // ✅ Initialiser le contrôleur de dialogue de modification
        this.updateWordDialogController = new UpdateWordDialogController(motService, mainController);
    }

    @FXML
    public void initialize() {
        // ✅ Masquer le bouton Modifier par défaut
        if (btnModifier != null) {
            btnModifier.setVisible(false);
            btnModifier.setManaged(false);
        }
    }

    // ========================================
    // AFFICHER LES DÉTAILS D'UN MOT
    // ========================================
    public void afficherDetailsMot(String mot) {
        if (motService == null) return;

        MotDTO dto = new MotDTO(mot, null, null, null);
        MotDTO resultat = motService.getInfoMot(dto);

        if (resultat != null) {
            // ✅ Stocker le mot actuel
            this.motActuel = resultat;

            // Afficher le mot
            wordTitle.setText(resultat.mot());

            // Afficher l'émoji s'il existe
            if (resultat.emojie() != null && !resultat.emojie().trim().isEmpty()) {
                emojieLabel.setText(resultat.emojie());
                emojieLabel.setVisible(true);
                emojieLabel.setManaged(true);
            } else {
                emojieLabel.setVisible(false);
                emojieLabel.setManaged(false);
            }

            // Afficher la catégorie
            if (resultat.categorie() != null && !resultat.categorie().trim().isEmpty()) {
                categorieLabel.setText("🏷 " + resultat.categorie());
                categorieLabel.setVisible(true);
                categorieLabel.setManaged(true);
            } else {
                categorieLabel.setText("🏷 General");
                categorieLabel.setVisible(true);
                categorieLabel.setManaged(true);
            }

            // Afficher la définition
            definitionText.setText(resultat.definition() != null ?
                    resultat.definition() : "Pas de définition disponible");

            // Synonymes (à implémenter plus tard)
            synonymsText.setText("À venir...");

            // ✅ AFFICHER LE BOUTON MODIFIER
            if (btnModifier != null) {
                btnModifier.setVisible(true);
                btnModifier.setManaged(true);
            }

            // DEBUG
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

    // ========================================
    // ACTION DU BOUTON MODIFIER
    // ========================================
    @FXML
    private void onModifierClick() {
        if (motActuel != null && updateWordDialogController != null) {
            updateWordDialogController.show(motActuel);
        } else {
            afficherErreur("Erreur", "Aucun mot sélectionné pour la modification.");
        }
    }

    // ========================================
    // MASQUER LES DÉTAILS
    // ========================================
    public void masquerDetails() {
        // ✅ Masquer le bouton quand on cache les détails
        if (btnModifier != null) {
            btnModifier.setVisible(false);
            btnModifier.setManaged(false);
        }
        this.motActuel = null;
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