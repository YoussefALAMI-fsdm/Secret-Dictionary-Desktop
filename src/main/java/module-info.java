module com.secret.dictionary.secretdictionarydesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Car la class DataBase est dans le meme projet
    requires flyway.core; // Permet de gerer les migrations de la base de données (versionner et appliquer automatiquement les scripts SQL)

    // Ouvre les packages contenant les controllers pour FXMLLoader
    opens com.secret.dictionary.controller to javafx.fxml;
    opens com.secret.dictionary.app to javafx.fxml;

    // Exports uniquement les packages que tu veux rendre visibles à d'autres modules (optionnel ici)
    exports com.secret.dictionary.controller;
    exports com.secret.dictionary.app;

    // Si tu as un package model et que tu veux l'utiliser depuis d'autres modules
    exports com.secret.dictionary.model;
}