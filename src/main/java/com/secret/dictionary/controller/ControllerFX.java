package com.secret.dictionary.controller;

import com.secret.dictionary.service.MotServiceImp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ControllerFX { // Controlleur UI ( javaFX )

    private MotServiceImp motService ;

    public void setMotService(MotServiceImp motService) {
        this.motService = motService;
    }

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}