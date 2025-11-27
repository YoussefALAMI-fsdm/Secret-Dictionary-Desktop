/*
 * Puiseque on utilise une architecture MVC en couche, alors il y'a beaucoup de depandance entre les class
 * ( ex : MotDAO a besion de DataBase afin de recuperer la connexion avec la BD et executé les requete SQL dans cette DB
 *
 * Pour cela on utilise l'injection des depandance
 * c a d pour chaque class on lui injecte ( fournie ) une instance de la class qui a lui besion pour fonctionner
 * au lieu de creer l'objet directement dans la classe ( c a d creer new DataBase() directement dans MotDAO )
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
 * ⚠ On injecte jamais des entité de données simple ( model , dto )
 * car l'injection ce fait pour des services et des utilitaire, pas juste des entité de données simple
 */

package com.secret.dictionary.app;

import com.secret.dictionary.controller.MainController;
import com.secret.dictionary.dao.MotDAOImp;
import com.secret.dictionary.service.MotServiceImp;
import com.secret.dictionary.util.DataBase;
import com.secret.dictionary.util.DataBaseInit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;


/*                        @Test Backend ( temporaire )
public class Main {

    public static void main () {
        DataBase db = DataBase.getInstance() ; // Recuperer l'instance unique ( Singleton ) du BD

        DataBaseInit dbInit = new DataBaseInit(db);  // Injction du DB dans le initialisateur du BD
        dbInit.init() ; // 2. Lancer Flyway (chargement scripts V1, V2, V3... et executions des scripts nessecaire )

        MotDAOImp motDAO = new MotDAOImp(db) ; // Injecté la DB au DAO pour quel l'utilise afin de ce connecté et executé les requetes

        MotServiceImp motService = new MotServiceImp(motDAO) ; // Injecter le dao au service afin qu'il l'utilise pour manipuler les données

        List<String> l = motService.getAllMots() ;

        if ( l.isEmpty() ) {
            System.out.println("Aucun mot n'est trouvée dans la DB !");
        }
        else {
            Iterator<String> it = l.iterator() ;
            while (it.hasNext()) {
                System.out.println(it.next());
            }
        }
    }
}
*/
public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        DataBase db = DataBase.getInstance() ; // Recuperer l'instance unique ( Singleton ) du BD

        DataBaseInit dbInit = new DataBaseInit(db);  // Injction du DB dans le initialisateur du BD
        dbInit.init() ; // 2. Lancer Flyway (chargement scripts V1, V2, V3... et executions des scripts nessecaire )

        MotDAOImp motDAO = new MotDAOImp(db) ; // Injecté la DB au DAO pour quel l'utilise afin de ce connecté et executé les requetes

        MotServiceImp motService = new MotServiceImp(motDAO) ; // Injecter le dao au service afin qu'il l'utilise pour manipuler les données

        // ControllerFX controlleur = new ControllerFX(motService) ; jamais utilisé !!! , car le fxml creer sont propre controller avec fx:cntroller

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/secret/dictionary/fxml/main-view.fxml")); // le fxml/main-view.fxml n'est pas suffisant ici car le compiler essaie de chercher dans le meme package que celui du class d'entrée ( com.secret.dictionary.app ) or .app ,n'existe pas dans ressources

        Parent parent = fxmlLoader.load() ; // On creer tout d'abord le root + Controller

        MainController controlleur = fxmlLoader.getController() ; // Sans la ligne precedent le controller sera null car pas encore creer ( pas encore de load() )

        controlleur.setMotService(motService); // injecter le mot service dans le controller utiliser par le fxml afin qu'il l'utilse pour la communication UI <=> Backend

        Scene scene = new Scene(parent, 800, 600); // Taille adaptée

        scene.getStylesheets().add(  // Liason entre css et fxml
                Main.class.getResource("/com/secret/dictionary/styles/style.css").toExternalForm()
        );

        stage.setTitle("Secret Dictionary - Gestion de Vocabulaire");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}