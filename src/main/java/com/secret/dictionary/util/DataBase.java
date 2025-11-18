// @ Cette class a pour but unique qui est la creation d'une instance unique ( partagé partout dans le programme ) de connexion avec la BD
// @ On ne creer jamais des methode creerTable , creerIndex pour garantie le SRP ( Single Responsibility Principle )

package com.secret.dictionary.util;

// On va utiliser le patterns Singleton qui permet de garantir l'existance d'une seul instance de connexion
// partagé partout => Cela garantie une seule configuration, une seule connexion, un seul objet partout dans le projet.

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase { // class util qui permet de creer la DB et gerer la connexion

    private static DataBase instance ;
    private Connection connection ; //Si la connexion est static → elle n’est plus liée à l’instance → Singleton cassé.

    private static final String URL = "jdbc:postgresql://localhost:5432/dictionary" ; // JDBC ce connecte au BD PostgreSQL qui tourne en local ( dans un conteneur docker )
    private static final String USER = "FSDM" ;
    private static final String PASSWD = "IA" ;

    private DataBase()  {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWD);
        } catch ( SQLException e ) {
            System.err.println("Probleme lors du creation d'instance de connexion ( Constructeur DataBase ) ! ");
            e.printStackTrace();  // Affiche toute la trace de l'erreur
        }
    }

    public static DataBase getInstance() {

        if ( instance == null )
            instance = new DataBase() ; // Creation unique si aucune instance n'existe !

        return instance ;
    }

    public Connection getConnection () { // C'est la methode utile pour recuperer la connexion au instance BD creer
        return connection ;
    }
}
