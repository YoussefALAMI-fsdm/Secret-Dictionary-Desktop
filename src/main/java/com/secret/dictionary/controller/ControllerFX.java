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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur principal avec dialogues personnalisés
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
    // 🎨 DIALOGUE RECHERCHE PERSONNALISÉ
    // ========================================
    @FXML
    private void onRechercheClick() {
        // Créer un dialogue personnalisé
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("🔍 Rechercher un mot");
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Boutons personnalisés
        ButtonType btnRechercher = new ButtonType("Rechercher", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnRechercher, btnAnnuler);

        // ✨ CONTENU PERSONNALISÉ
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-background-color: #1a0b2e; -fx-background-radius: 10;");

        Label titre = new Label("Entrez le mot à rechercher");
        titre.setStyle("-fx-font-size: 16px; -fx-text-fill: #c77dff; -fx-font-weight: bold;");

        TextField txtRecherche = new TextField();
        txtRecherche.setPromptText("Exemple : Bonjour");
        txtRecherche.setStyle(
                "-fx-background-color: #16213e; " +
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: #888; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10; " +
                        "-fx-background-radius: 5;"
        );
        txtRecherche.setPrefWidth(300);

        Label info = new Label("💡 La recherche est insensible à la casse");
        info.setStyle("-fx-font-size: 12px; -fx-text-fill: #b185db;");

        content.getChildren().addAll(titre, txtRecherche, info);
        dialog.getDialogPane().setContent(content);

        // ✨ STYLE DU DIALOGUE
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #0f0e17; " +
                        "-fx-border-color: #7209b7; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;"
        );

        // Focus automatique sur le champ
        txtRecherche.requestFocus();

        // Conversion du résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnRechercher) {
                return txtRecherche.getText().trim();
            }
            return null;
        });

        // Afficher et traiter
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(mot -> {
            if (!mot.isEmpty()) {
                afficherDetailsMot(mot);
            }
        });
    }

    // ========================================
    // 🎨 DIALOGUE NOUVEAU MOT PERSONNALISÉ
    // ========================================
    @FXML
    private void onNouveauClick() {
        Dialog<MotDTO> dialog = new Dialog<>();
        dialog.setTitle("➕ Ajouter un nouveau mot");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType btnAjouter = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAjouter, btnAnnuler);

        // ✨ CONTENU PERSONNALISÉ
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a0b2e; -fx-background-radius: 10;");

        Label titre = new Label("Créer une nouvelle entrée");
        titre.setStyle("-fx-font-size: 18px; -fx-text-fill: #c77dff; -fx-font-weight: bold;");

        // Champ MOT
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

        // Champ DÉFINITION
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

        // ✨ STYLE DU DIALOGUE
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #0f0e17; " +
                        "-fx-border-color: #7209b7; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;"
        );

        // Focus automatique
        txtMot.requestFocus();

        // Validation : désactiver le bouton si le mot est vide
        Button btnAjouterNode = (Button) dialog.getDialogPane().lookupButton(btnAjouter);
        btnAjouterNode.setDisable(true);
        txtMot.textProperty().addListener((observable, oldValue, newValue) -> {
            btnAjouterNode.setDisable(newValue.trim().isEmpty());
        });

        // Conversion du résultat
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

        // Afficher et traiter
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
    // DIALOGUES D'INFORMATION PERSONNALISÉS
    // ========================================

    /**
     * Dialogue de succès personnalisé
     */
    private void afficherSucces(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style personnalisé
        alert.getDialogPane().setStyle(
                "-fx-background-color: #1a0b2e; " +
                        "-fx-border-color: #52b788; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;"
        );

        // Style du texte
        alert.getDialogPane().lookup(".content.label").setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 14px;"
        );

        alert.showAndWait();
    }

    /**
     * Dialogue d'erreur personnalisé
     */
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style personnalisé
        alert.getDialogPane().setStyle(
                "-fx-background-color: #1a0b2e; " +
                        "-fx-border-color: #e85d04; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;"
        );

        // Style du texte
        alert.getDialogPane().lookup(".content.label").setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 14px;"
        );

        alert.showAndWait();
    }
}