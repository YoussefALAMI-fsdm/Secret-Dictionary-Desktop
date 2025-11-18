/*
 * Puiseque on utilise une architecture MVC en couche, alors il y'a beaucoup de depandance entre les class
 * ( ex : MotDAO a besion de DataBase afin de recuperer la connexion avec la BD et executé les requete SQL dans cette DB
 *
 * Pour cela on utilise l'injection des depandance
 * c a d pour chaque class on lui injecte ( fournie ) une instance de la class qui a lui besion pour fonctionner
 * au lieu de creer l’objet directement dans la classe ( c a d creer new DataBase() directement dans MotDAO )
 * car cela casse l'architecture MVC dans la quel par exemple le MotDAO ne doit jamais connaitre comment DB est creer
 * il doit l'utiliser directement sans avoir les détaill
 *
 *
 *           Dans le MVC pro l'injection est de ce sens :
 *
 *             DataBase -> DataBaseInit : Car init a besion du DB a initialiser
 *             DataBase -> DAO : car le DAO a besion de recuperer la connexion avec la BD afin de executer les scripts
 *             DAO -> Service : car le service a besion du DAO afin qu'il peut appliquer la logique metier sur les données en utilisons les methode du DAO
 *             Service -> Controller FX : car le controller UI a besion du communiquer avec le Backend depuis le UI
 *
 * ⚠️ On injecte jamais des entité de données simple ( model , dto )
 * car l'injection ce fait pour des services et des utilitaire, pas juste des entité de données simple
 */

package com.secret.dictionary.app;

import com.secret.dictionary.controller.ControllerFX;
import com.secret.dictionary.dao.MotDAO;
import com.secret.dictionary.service.MotService;
import com.secret.dictionary.util.DataBase;
import com.secret.dictionary.util.DataBaseInit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        DataBase db = DataBase.getInstance() ; // Recuperer l'instance unique ( Singleton ) du BD

        DataBaseInit dbInit = new DataBaseInit(db);  // Injction du DB dans le initialisateur du BD
        dbInit.init() ; // 2. Lancer Flyway (chargement scripts V1, V2, V3... et executions des scripts nessecaire )

        MotDAO motDAO = new MotDAO(db) ; // Injecté la DB au DAO pour quel l'utilise afin de ce connecté et executé les requetes

        MotService motService = new MotService(motDAO) ; // Injecter le dao au service afin qu'il l'utilise pour manipuler les données

       // ControllerFX controlleur = new ControllerFX(motService) ; jamais utilisé !!! , car le fxml creer sont propre controller avec fx:cntroller

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/secret/dictionary/fxml/hello-view.fxml")); // le fxml/hello-view.fxml n'est pas suffisant ici car le compiler essaie de chercher dans le meme package que celui du class d'entrée ( com.secret.dictionary.app ) or .app ,n'existe pas dans ressources

        Parent parent = fxmlLoader.load() ; // On creer tout d'abord le root + Controller

        ControllerFX controlleur = fxmlLoader.getController() ; // Sans la ligne precedent le controller sera null car pas encore creer ( pas encore de load() )

        controlleur.setMotService(motService); // injecter le mot service dans le controller utiliser par le fxml afin qu'il l'utilse pour la communication UI <=> Backend

        Scene scene = new Scene(parent, 320, 240);
        stage.setTitle("Developement fonctionnalite par aya");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}