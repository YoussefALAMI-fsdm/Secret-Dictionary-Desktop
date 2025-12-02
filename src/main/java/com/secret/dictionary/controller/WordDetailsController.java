package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
import com.secret.dictionary.util.EmojiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class WordDetailsController {

    @FXML private VBox vboxCenter;
    @FXML private Label wordTitle;
    @FXML private Label emojieLabel;
    @FXML private Label categorieLabel;
    @FXML private Label definitionText;
    @FXML private FlowPane synonymsFlow;
    @FXML private FlowPane antonymesFlow;
    @FXML private VBox synonymsSection;
    @FXML private VBox antonymesSection;
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
        // Configurer le label emoji pour affichage coloré
        if (emojieLabel != null) {
            EmojiUtils.configureEmojiLabel(emojieLabel, 48);
        }

        if (btnModifier != null) {
            btnModifier.setVisible(false);
            btnModifier.setManaged(false);
        }

        // Cacher les sections synonymes/antonymes par défaut
        if (synonymsSection != null) {
            synonymsSection.setVisible(false);
            synonymsSection.setManaged(false);
        }
        if (antonymesSection != null) {
            antonymesSection.setVisible(false);
            antonymesSection.setManaged(false);
        }
    }

    public void afficherDetailsMot(String mot) {
        if (motService == null) return;

        MotDTO dto = new MotDTO(mot, null, null, null);
        MotDTO resultat = motService.getInfoMot(dto);

        if (resultat != null) {
            this.motActuel = resultat;
            wordTitle.setText(resultat.mot());

            // Afficher l'émoji
            if (resultat.emojie() != null && !resultat.emojie().trim().isEmpty()) {
                emojieLabel.setText(resultat.emojie());
                emojieLabel.setVisible(true);
                emojieLabel.setManaged(true);
                EmojiUtils.configureEmojiLabel(emojieLabel, 48);
            } else {
                emojieLabel.setVisible(false);
                emojieLabel.setManaged(false);
            }

            // Afficher la catégorie
            if (resultat.categorie() != null && !resultat.categorie().trim().isEmpty()) {
                categorieLabel.setText("🏷️ " + resultat.categorie());
                categorieLabel.setVisible(true);
                categorieLabel.setManaged(true);
            } else {
                categorieLabel.setText("🏷️ General");
                categorieLabel.setVisible(true);
                categorieLabel.setManaged(true);
            }

            // Afficher la définition
            definitionText.setText(resultat.definition() != null ?
                    resultat.definition() : "Pas de définition disponible");

            // ✅ AFFICHER LES SYNONYMES
            afficherSynonymes(resultat);

            // ✅ AFFICHER LES ANTONYMES
            afficherAntonymes(resultat);

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

    // ========================================
    // AFFICHER LES SYNONYMES
    // ========================================
    private void afficherSynonymes(MotDTO mot) {
        synonymsFlow.getChildren().clear();

        List<String> synonymes = motService.getListSynonymes(mot);

        if (synonymes == null) {
            // Mot n'existe pas
            synonymsSection.setVisible(false);
            synonymsSection.setManaged(false);
            return;
        }

        if (synonymes.isEmpty()) {
            // Aucun synonyme trouvé
            Label aucunLabel = new Label("Aucun synonyme");
            aucunLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 13px; -fx-font-style: italic;");
            synonymsFlow.getChildren().add(aucunLabel);
        } else {
            // Ajouter chaque synonyme comme un badge cliquable
            for (String synonyme : synonymes) {
                Label badge = creerBadgeMot(synonyme, "#2196F3");
                synonymsFlow.getChildren().add(badge);
            }
        }

        synonymsSection.setVisible(true);
        synonymsSection.setManaged(true);
    }

    // ========================================
    // AFFICHER LES ANTONYMES
    // ========================================
    private void afficherAntonymes(MotDTO mot) {
        antonymesFlow.getChildren().clear();

        List<String> antonymes = motService.getListAntonymes(mot);

        if (antonymes == null) {
            // Mot n'existe pas
            antonymesSection.setVisible(false);
            antonymesSection.setManaged(false);
            return;
        }

        if (antonymes.isEmpty()) {
            // Aucun antonyme trouvé
            Label aucunLabel = new Label("Aucun antonyme");
            aucunLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 13px; -fx-font-style: italic;");
            antonymesFlow.getChildren().add(aucunLabel);
        } else {
            // Ajouter chaque antonyme comme un badge cliquable
            for (String antonyme : antonymes) {
                Label badge = creerBadgeMot(antonyme, "#e85d04");
                antonymesFlow.getChildren().add(badge);
            }
        }

        antonymesSection.setVisible(true);
        antonymesSection.setManaged(true);
    }

    // ========================================
    // CRÉER UN BADGE CLIQUABLE
    // ========================================
    private Label creerBadgeMot(String mot, String couleur) {
        Label badge = new Label(mot);
        badge.setStyle(
                "-fx-background-color: " + couleur + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 6 12; " +
                        "-fx-background-radius: 15; " +
                        "-fx-font-size: 13px; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 2);"
        );

        // Effet hover
        badge.setOnMouseEntered(e -> {
            badge.setStyle(
                    "-fx-background-color: derive(" + couleur + ", 20%); " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 6 12; " +
                            "-fx-background-radius: 15; " +
                            "-fx-font-size: 13px; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 8, 0, 0, 3);"
            );
        });

        badge.setOnMouseExited(e -> {
            badge.setStyle(
                    "-fx-background-color: " + couleur + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 6 12; " +
                            "-fx-background-radius: 15; " +
                            "-fx-font-size: 13px; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 2);"
            );
        });

        // Clic pour afficher les détails du mot
        badge.setOnMouseClicked(e -> {
            if (mainController != null) {
                mainController.afficherDetailsMot(mot);
            }
        });

        return badge;
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
        if (synonymsSection != null) {
            synonymsSection.setVisible(false);
            synonymsSection.setManaged(false);
        }
        if (antonymesSection != null) {
            antonymesSection.setVisible(false);
            antonymesSection.setManaged(false);
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