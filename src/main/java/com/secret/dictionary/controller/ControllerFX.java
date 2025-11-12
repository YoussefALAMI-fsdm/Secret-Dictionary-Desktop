package com.secret.dictionary.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ControllerFX {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}