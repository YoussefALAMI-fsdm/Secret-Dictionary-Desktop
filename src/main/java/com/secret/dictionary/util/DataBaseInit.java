package com.secret.dictionary.util;

import org.flywaydb.core.Flyway;

/*
  Flyway est une bibliothèque qui permet de gérer l'exécution des scripts
  sur une base de données de manière intelligente : seuls les scripts non encore
  exécutés seront appliqués.
 */

public class DataBaseInit {

public void init () {

    Flyway flayway = Flyway.configure()
            .locations("classpath:scripts") // Permet de localiser les scripts dans ressources
            .dataSource("jdbc:postgresql://localhost:5432/dictionary","FSDM","IA") // Determier la DB source ( au quel appliquer les scripts )
            .load(); // Charge la configuration Flyway

    flayway.migrate() ; // Applique toutes les migrations qui n'ont pas encore été exécutées dans la DB dictionary
}
}
