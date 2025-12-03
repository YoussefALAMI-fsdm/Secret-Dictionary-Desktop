/**
 * On va utiliser H2 pour simuler une vrai BD PostgreSQL , la BD est lancé directement en mémoire
 * => Meuilleur performance
 * H2 ne supporte pas tt les fonctionnalité natif de PostgreSQL ( comme pg_trgm , MATERIALIZED VIEW )
 * Alors pour cela on essaie de simuler un peu ce comportement en utilisons des fonctionnalité supporter par H2
 *
 */

package com.secret.dictionary.dao;

import com.secret.dictionary.util.DataBase;
import com.secret.dictionary.utils.SimpleLogger;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_METHOD) // Chaque méthode de test (@Test)
                                                // reçoit une nouvelle instance de la classe de test.
                                               // pour garantie le test indepandant
// car si un test insère, supprime ou modifie des données, il ne doit pas affecter les autres tests

public class MotDAOImpTest {

    private Connection connexionH2; // Ne peut pas etre final car il est rénistailiser avant chaque test ( @BeforeEach )
    private MotDAOImp dao ;

    private static final String LOG_FILE_PATH = "logs/LogMotDAOTest.log";
    private static SimpleLogger logger ;

    @BeforeAll // static car pas besion etre appartient a chaque instance de test séparer (@Test)
    public static void initLogger() {
        logger = new SimpleLogger(LOG_FILE_PATH, true); // true = reset fichier au début
        logger.log("\n============= Début de la session de tests =============\n");
    }

    @AfterAll
    public static void closeLogger() {
        logger.log("\n============= Fin de la session de tests =============\n");
    }

    @BeforeEach
    public void initDB () throws Exception { // on a Junit de gerer les Exeption : si lancé => Test echoue

        logger.log("Initialisation de la base H2 pour le test.");

        connexionH2 = DriverManager.getConnection (
                "jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_UPPER=FALSE;DB_CLOSE_DELAY=-1;", // mem:testdb => Creer la DB en mémoire
                "sa", ""           // DATABASE_TO_UPPER=FALSE => Sensible a la casse pour respecté les regles de PostgreSQL
                ); // DB_CLOSE_DELAY=-1 => Permet a la base de rester ouvert en mémoire pour executer les scripts avec flyway

        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_UPPER=FALSE;DB_CLOSE_DELAY=-1;",
                        "sa",
                        "") // sa => user par default de H2 , "" => pas de mot de passe
                .locations("classpath:db/migration")
                .load();

        logger.log("Migration Flyway exécutée.");
        flyway.migrate();

        DataBase dbMock = mock(DataBase.class); // Puiseque notre DAO prend une BD en arguement alors on le mock
        when(dbMock.getConnection()).thenReturn(connexionH2);  // Quand mon DAO va utiliser .getConnection on va jamais l'appelé
        dao = new MotDAOImp(dbMock); // mais on va plutot passé directement le connexionH2 => DB creer réelement mais avec H2

        logger.log("DAO initialisé avec la connexion H2.");
    }

    @AfterEach
    public void fermerConnexion () throws Exception {
        if ( connexionH2 != null && !connexionH2.isClosed() )
            connexionH2.close();

        logger.log("Connexion H2 fermée après le test.");
    }



}
