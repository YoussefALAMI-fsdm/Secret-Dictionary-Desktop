package com.secret.dictionary.util;

// On va utiliser le patterns Singleton qui permet de garantir l'existance d'une seul instance de connexion
// partagé partout => Cela garantie une seule configuration, une seule connexion, un seul objet partout dans le projet.

public class DataBase { // class util qui permet de creer la DB et gerer la connexion

    private static DataBase instance ;

   private DataBase() {

   }

   public static DataBase getInstance() {

       if ( instance == null )
           instance = new DataBase() ; // Creation unique si aucune instance n'existe !

       return instance ;
   }

}
