package com.secret.dictionary.util;

import org.flywaydb.core.Flyway;

/*
  Flyway est une bibliothèque qui permet de gérer l'exécution des scripts
  sur une base de données de manière intelligente : seuls les scripts non encore
  exécutés seront appliqués.
 */

public class DataBaseInit {

    private final DataBase db ;

    public DataBaseInit ( DataBase db ) { // Injecter la DB a initialiser
        this.db = db ;
    }

public  void init () {

    Flyway flayway = Flyway.configure()
            .locations("classpath:com/secret/dictionary/scripts") // Permet de localiser les scripts dans ressources
            .dataSource(db.getURL(),db.getUSER(),db.getPASSWD()) // Determier la DB source ( au quel appliquer les scripts )
            .load(); // Charge la configuration Flyway

    flayway.migrate() ; // Applique toutes les migrations qui n'ont pas encore été exécutées dans la DB dictionary
}
}
