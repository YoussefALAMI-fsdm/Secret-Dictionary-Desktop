package com.secret.dictionary.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/secret/dictionary/fxml/hello-view.fxml")); // le fxml/hello-view.fxml n'est pas suffisant ici car le compiler essaie de chercher dans le meme package que celui du class d'entrée ( com.secret.dictionary.app ) or .app ,n'existe pas dans ressources
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Developement fonctionnalite par aya");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}