package com.secret.dictionary.controller;

import com.secret.dictionary.service.MotServiceImp;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Contrôleur pour la vue des statistiques par catégorie
 */
public class StatisticsViewController {

    @FXML private VBox statsContainer;
    @FXML private Label titleLabel;
    @FXML private Label totalCountLabel;
    @FXML private VBox categoriesBox;

    private MotServiceImp motService;

    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
        chargerStatistiques();
    }

    @FXML
    //Elle sert à initialiser l'interface graphique
    public void initialize() {
        // Configuration initiale si nécessaire
    }

    /**
     * Charge et affiche les statistiques depuis le service
     */
    public void chargerStatistiques() {
        if (motService == null) return;


        Map<String, Integer> stats = motService.getMotCountParCategorie();

        if (stats == null || stats.isEmpty()) {
            afficherMessageVide();
            return;
        }

        // Calculer le total
        int total = stats.values().stream().mapToInt(Integer::intValue).sum();
        //Convertit chaque Integer en int
        //Parce que sum() n’existe que sur les IntStream, pas sur Stream<Integer>.
        totalCountLabel.setText(total + " mots au total");

        // Vider la liste actuelle
        categoriesBox.getChildren().clear();

        // Mapper les emojis par catégorie
        Map<String, String> categoryEmojis = Map.of(
                "General", "📚",
                "Verbe", "⚡",
                "Adjectif", "🎨",
                "Nom", "📝",
                "Adverbe", "🔄",
                "Expression", "💬"
        );

        // Ajouter chaque catégorie
        //applique une action à chaque entrée de la Map
        stats.forEach((categorie, count) -> {
            String emoji = categoryEmojis.getOrDefault(categorie, "📌");
            VBox categoryCard = creerCarteCategorie(emoji, categorie, count, total);
            categoriesBox.getChildren().add(categoryCard);
        });
    }

    /**
     * Crée une carte visuelle pour une catégorie
     */
    private VBox creerCarteCategorie(String emoji, String categorie, int count, int total) {
        VBox card = new VBox(8);
        card.getStyleClass().add("category-card");
        card.setAlignment(Pos.CENTER_LEFT);

        // En-tête avec emoji et nom
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label emojiLabel = new Label(emoji);
        emojiLabel.getStyleClass().add("category-emoji");

        Label nameLabel = new Label(categorie);
        nameLabel.getStyleClass().add("category-name");

        header.getChildren().addAll(emojiLabel, nameLabel);

        // Barre de progression et compteur
        HBox progressBar = new HBox(10);
        progressBar.setAlignment(Pos.CENTER_LEFT);
        //setHgrow est une méthode statique de HBox , indique la priorité de croissance horizontal
        HBox.setHgrow(progressBar, Priority.ALWAYS);

        // Calculer le pourcentage
        double percentage = total > 0 ? (count * 100.0 / total) : 0;

        // Barre de progression visuelle
        HBox bar = new HBox();
        bar.getStyleClass().add("progress-bar-background");
        bar.setPrefHeight(12);
        bar.setMaxHeight(12);
        HBox.setHgrow(bar, Priority.ALWAYS);

        HBox fill = new HBox();
        fill.getStyleClass().add("progress-bar-fill");
        fill.setPrefHeight(12);
        fill.setMaxHeight(12);
        fill.setPrefWidth(percentage * 3); // Largeur proportionnelle (max 300px pour 100%)

        bar.getChildren().add(fill);

        // Label du compteur
        Label countLabel = new Label(count + " (" + String.format("%.1f", percentage) + "%)");
        countLabel.getStyleClass().add("category-count");
        countLabel.setMinWidth(100);
        countLabel.setAlignment(Pos.CENTER_RIGHT);

        progressBar.getChildren().addAll(bar, countLabel);

        card.getChildren().addAll(header, progressBar);

        return card;
    }

    /**
     * Affiche un message quand il n'y a pas de données
     */
    private void afficherMessageVide() {
        categoriesBox.getChildren().clear();

        VBox emptyBox = new VBox(15);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.getStyleClass().add("empty-state");

        Label emptyEmoji = new Label("📭");
        emptyEmoji.setStyle("-fx-font-size: 64px;");

        Label emptyText = new Label("Aucune statistique disponible");
        emptyText.getStyleClass().add("empty-text");

        Label emptySubtext = new Label("Ajoutez des mots pour voir les statistiques");
        emptySubtext.getStyleClass().add("empty-subtext");

        emptyBox.getChildren().addAll(emptyEmoji, emptyText, emptySubtext);
        categoriesBox.getChildren().add(emptyBox);

        totalCountLabel.setText("0 mots au total");
    }

    /**
     * Rafraîchit les statistiques (appelé depuis MainController)
     */
    public void rafraichirStatistiques() {
        chargerStatistiques();
    }
}