package com.secret.dictionary.controller;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotServiceImp;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur principal de l'interface JavaFX
 * Gère toutes les interactions utilisateur et la communication avec le backend
 */
public class ControllerFX {

    // ========================================
    // INJECTION DU SERVICE (depuis Main.java)
    // ========================================
    private MotServiceImp motService;

    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
        chargerTousLesMots(); // Charger les mots au démarrage
    }

    // ========================================
    // COMPOSANTS FXML (liés automatiquement)
    // ========================================

    // Zone gauche (sidebar)
    @FXML private VBox vboxLeft;
    @FXML private Button btnCatego;
    @FXML private VBox menuCatego;
    @FXML private VBox btnFavorisGroup;
    @FXML private VBox btnNouveauGroup;
    @FXML private Button btnRecherche;
    @FXML private Button btnNouveau;

    // Zone centrale (détails du mot)
    @FXML private VBox vboxCenter;
    @FXML private Label wordTitle;
    @FXML private Label definitionText;
    @FXML private Label synonymsText;

    // Zone droite (liste des mots)
    @FXML private VBox vboxRight;
    @FXML private Label rightTitle;
    @FXML private ListView<String> wordList;

    // ========================================
    // ÉTAT DU MENU (ouvert/fermé)
    // ========================================
    private boolean isMenuVisible = false;

    // ========================================
    // MÉTHODE D'INITIALISATION AUTOMATIQUE
    // ========================================
    @FXML
    public void initialize() {
        // Configuration du ListView pour détecter la sélection
        wordList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        afficherDetailsMot(newValue);
                    }
                }
        );
    }

    // ========================================
    // GESTION DU MENU DÉROULANT (Catégories)
    // ========================================
    /**
     * MODIFICATION 1 : Toggle du sous-menu Catégories
     * - Affiche/Cache le menu avec animation
     * - Déplace les boutons Favoris et Nouveau vers le bas quand ouvert
     */
    @FXML
    private void toggleMenu() {
        // Inverser l'état du menu
        isMenuVisible = !isMenuVisible;
        menuCatego.setVisible(isMenuVisible);
        menuCatego.setManaged(isMenuVisible); // CRUCIAL pour l'animation

        // Animation de déplacement vers le bas
        TranslateTransition ttFavoris = new TranslateTransition(Duration.millis(300), btnFavorisGroup);
        TranslateTransition ttNouveau = new TranslateTransition(Duration.millis(300), btnNouveauGroup);

        if (isMenuVisible) {
            // Calculer la hauteur du menu pour déplacer les boutons
            double menuHeight = 5; // Ajuster selon le nombre de boutons
            ttFavoris.setToY(menuHeight);
            ttNouveau.setToY(menuHeight);
        } else {
            // Remettre en place
            ttFavoris.setToY(0);
            ttNouveau.setToY(0);
        }

        ttFavoris.play();
        ttNouveau.play();
    }

    // ========================================
    // CHARGER TOUS LES MOTS (au démarrage)
    // ========================================
    /**
     * MODIFICATION 2 : Chargement initial des mots
     * - Appelle le service pour récupérer tous les mots
     * - Remplit le ListView à droite
     */
    private void chargerTousLesMots() {
        if (motService == null) return;

        List<String> mots = motService.getAllMots();
        ObservableList<String> observableMots = FXCollections.observableArrayList(mots);
        wordList.setItems(observableMots);

        // Afficher le nombre de mots trouvés
        System.out.println("✅ " + mots.size() + " mots chargés dans la liste");
    }

    // ========================================
    // AFFICHER LES DÉTAILS D'UN MOT
    // ========================================
    /**
     * MODIFICATION 3 : Affichage des détails au centre
     * - Appelée quand on clique sur un mot dans la liste
     * - Récupère les infos depuis le backend
     * - Affiche définition et synonymes au centre
     */
    private void afficherDetailsMot(String mot) {
        if (motService == null) return;

        // Créer un DTO pour la recherche
        MotDTO dto = new MotDTO(mot, null);
        MotDTO resultat = motService.getInfoMot(dto);

        if (resultat != null) {
            // Afficher les infos au centre
            wordTitle.setText(resultat.getMot());
            definitionText.setText(resultat.getDefinition() != null ?
                    resultat.getDefinition() : "Pas de définition disponible");
            synonymsText.setText("À venir..."); // À implémenter plus tard

            // Cacher la liste et le menu gauche
            vboxRight.setVisible(false);
            vboxLeft.setVisible(false);
            vboxCenter.setVisible(true);
        } else {
            afficherErreur("Mot introuvable", "Le mot '" + mot + "' n'existe pas dans le dictionnaire.");
        }
    }

    // ========================================
    // BOUTON "TOUS LES MOTS" (retour accueil)
    // ========================================
    /**
     * MODIFICATION 4 : Retour à la vue initiale
     * - Réaffiche le menu gauche et la liste droite
     * - Cache les détails du centre
     */
    @FXML
    private void onTousLesMotsClick() {
        vboxLeft.setVisible(true);
        vboxRight.setVisible(true);
        vboxCenter.setVisible(false);

        // Recharger les mots
        chargerTousLesMots();

        // Fermer le menu Catégories
        if (isMenuVisible) {
            toggleMenu();
        }
    }

    // ========================================
    // BOUTON "RECHERCHE"
    // ========================================
    /**
     * MODIFICATION 5 : Dialogue de recherche
     * - Ouvre une fenêtre pour saisir un mot
     * - Affiche les détails si trouvé
     */
    @FXML
    private void onRechercheClick() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Recherche de mot");
        dialog.setHeaderText("Rechercher un mot dans le dictionnaire");
        dialog.setContentText("Mot :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(mot -> {
            if (!mot.trim().isEmpty()) {
                afficherDetailsMot(mot.trim());
            }
        });
    }

    // ========================================
    // BOUTON "NOUVEAU"
    // ========================================
    /**
     * MODIFICATION 6 : Dialogue d'ajout de mot
     * - Ouvre une fenêtre pour saisir mot + définition
     * - Ajoute le mot dans la base de données
     */
    @FXML
    private void onNouveauClick() {
        // Créer un dialogue personnalisé avec 2 champs
        Dialog<MotDTO> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un nouveau mot");
        dialog.setHeaderText("Saisir les informations du mot");

        // Boutons OK et Annuler
        ButtonType btnOk = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);

        // Créer les champs de saisie
        TextField txtMot = new TextField();
        txtMot.setPromptText("Mot");
        TextArea txtDef = new TextArea();
        txtDef.setPromptText("Définition");
        txtDef.setPrefRowCount(4);

        // Layout du dialogue
        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Mot :"), txtMot,
                new Label("Définition :"), txtDef
        );
        dialog.getDialogPane().setContent(content);

        // Conversion du résultat en MotDTO
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnOk) {
                String mot = txtMot.getText().trim();
                String def = txtDef.getText().trim();
                if (!mot.isEmpty()) {
                    return new MotDTO(mot, def.isEmpty() ? null : def);
                }
            }
            return null;
        });

        // Afficher le dialogue et traiter le résultat
        Optional<MotDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            int resultat = motService.addMot(dto);

            switch (resultat) {
                case 1:
                    afficherInfo("Succès", "Le mot '" + dto.getMot() + "' a été ajouté avec succès !");
                    chargerTousLesMots(); // Recharger la liste
                    break;
                case 0:
                    afficherErreur("Mot existant", "Le mot '" + dto.getMot() + "' existe déjà dans le dictionnaire.");
                    break;
                case -1:
                    afficherErreur("Erreur", "Une erreur est survenue lors de l'ajout du mot.");
                    break;
            }
        });
    }

    // ========================================
    // MÉTHODES UTILITAIRES (Dialogues)
    // ========================================

    /**
     * Afficher un message d'information
     */
    private void afficherInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Afficher un message d'erreur
     */
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}