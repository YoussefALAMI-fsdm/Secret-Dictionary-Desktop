package com.secret.dictionary.controller;

import com.secret.dictionary.service.MotServiceImp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.function.Consumer;

/**
 * Contrôleur de la liste des mots (panneau droit)
 * Responsabilités :
 * - Affichage de la liste des mots
 * - Gestion de la sélection
 * - Rechargement de la liste
 */
public class WordListController {

    @FXML private ListView<String> wordList;

    private MotServiceImp motService;
    private Consumer<String> onWordSelected;

    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
        chargerTousLesMots();
    }

    /**
     * Définit le callback appelé lors de la sélection d'un mot
     */
    public void setOnWordSelected(Consumer<String> callback) {
        this.onWordSelected = callback;
    }

    @FXML
    private void initialize() {
        // Écoute de la sélection
        wordList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && onWordSelected != null) {
                        onWordSelected.accept(newValue);
                    }
                }
        );
    }

    /**
     * Charge tous les mots depuis le service
     */
    private void chargerTousLesMots() {
        if (motService == null) {
            return;
        }

        List<String> mots = motService.getAllMots();
        ObservableList<String> observableMots = FXCollections.observableArrayList(mots);
        wordList.setItems(observableMots);

        System.out.println("✅ " + mots.size() + " mots chargés dans la liste");
    }

    /**
     * Recharge la liste (après ajout par exemple)
     */
    public void rechargerListe() {
        chargerTousLesMots();
    }

    /**
     * Filtre la liste selon une catégorie
     */
    public void filtrerParCategorie(String categorie) {
        // TODO: Implémenter le filtrage par catégorie
        System.out.println("Filtrage par catégorie : " + categorie);
    }

    /**
     * Recherche dans la liste
     */
    public void rechercherDansListe(String query) {
        if (motService == null || query == null || query.trim().isEmpty()) {
            chargerTousLesMots();
            return;
        }

        List<String> motsFiltrés = motService.getListMot(query);
        if (motsFiltrés != null && !motsFiltrés.isEmpty()) {
            ObservableList<String> observableMots = FXCollections.observableArrayList(motsFiltrés);
            wordList.setItems(observableMots);
        }
    }

    /**
     * Sélectionne un mot dans la liste
     */
    public void selectionnerMot(String mot) {
        wordList.getSelectionModel().select(mot);
    }

    /**
     * Efface la sélection
     */
    public void clearSelection() {
        wordList.getSelectionModel().clearSelection();
    }
}