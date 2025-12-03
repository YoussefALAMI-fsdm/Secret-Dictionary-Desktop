/**
 * On va utiliser H2 pour simuler une vrai BD PostgreSQL , la BD est lancé directement en mémoire
 * => Meuilleur performance
 * H2 ne supporte pas tt les fonctionnalité natif de PostgreSQL ( comme pg_trgm , MATERIALIZED VIEW )
 * Alors pour cela on essaie de simuler un peu ce comportement en utilisons des fonctionnalité supporter par H2
 *
 */

package com.secret.dictionary.dao;

import com.secret.dictionary.model.Mot;
import com.secret.dictionary.util.DataBase;
import com.secret.dictionary.utils.SimpleLogger;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
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
        logger.log("╔════════════════════════════════════════════════════════════╗");
        logger.log("║         DÉBUT DE LA SESSION DE TESTS - MotDAOImp          ║");
        logger.log("╚════════════════════════════════════════════════════════════╝");
        logger.log("");
    }

    @AfterAll
    public static void closeLogger() {
        logger.log("");
        logger.log("╔════════════════════════════════════════════════════════════╗");
        logger.log("║          FIN DE LA SESSION DE TESTS - MotDAOImp           ║");
        logger.log("╚════════════════════════════════════════════════════════════╝");
    }

    @BeforeEach
    public void initDB () throws Exception { // on donne a Junit de gerer les Exeption : si lancé => Test echoue

        logger.log("────────────────────────────────────────────────────────────");
        logger.log("🔧 Initialisation de la base H2 en mémoire...");

        connexionH2 = DriverManager.getConnection (
                "jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_UPPER=FALSE;DB_CLOSE_DELAY=-1;",
                "sa", ""
        );

        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_UPPER=FALSE;DB_CLOSE_DELAY=-1;",
                        "sa",
                        "")
                .cleanDisabled(false)
                .locations("classpath:db/migration")
                .load();

        flyway.clean(); // Nettoie la base avant chaque test
        flyway.migrate(); // Applique toutes les migrations (dont V11 avec les relations)


        /* DataBase dbMock = mock(DataBase.class); // Puiseque notre DAO prend une BD en arguement alors on le mock
        when(dbMock.getConnection()).thenReturn(connexionH2);  // Quand mon DAO va utiliser .getConnection on va jamais l'appelé
        dao = new MotDAOImp(dbMock); // mais on va plutot passé directement le connexionH2 => DB creer réelement mais avec H2
       */ // -- Probleme de mocker des class singleton
        // # Solution : Creer constructuer avec Connection direct en DAO

        dao = new MotDAOImp(connexionH2);

        logger.log("✅ Base de données H2 initialisée avec succès");
        logger.log("✅ Migrations Flyway appliquées");
        logger.log("✅ DAO prêt pour les tests");
        logger.log("");

    }

    @AfterEach
    public void fermerConnexion () throws Exception {
        if ( connexionH2 != null && !connexionH2.isClosed() ) {
            connexionH2.close();
            logger.log("🔒 Connexion H2 fermée");
        }
        logger.log("");
    }

                                 // -- findAllMot()

    @Test
    @DisplayName("Test findAllMot() - Vérifier que les mots sont retournés")
    public void testFindAllMot_Success() throws DAOExeption {
        logger.log("🧪 TEST : findAllMot()");

        List<String> mots = dao.findAllMot();
        assertNotNull(mots, "La liste ne doit pas être null"); // Car flyway execute toujours scripts V11
        assertFalse(mots.isEmpty(), "La liste ne doit pas être vide");

        logger.log("📊 Nombre de mots trouvés : " + mots.size());
        logger.log("✅ Test findAllMot() réussi");
    }

                                        // -- SaveMot()

    @Test
    @DisplayName("Test saveMot() - Ajout d'un mot valide")
    public void testSaveMot_Success() throws DAOExeption {
        logger.log("🧪 TEST : saveMot()");

        Mot mot = new Mot(-1, "TestMot", "Définition de test", "Nom", "🧪");
        boolean resultat = dao.saveMot(mot);

        assertTrue(resultat, "L'ajout doit réussir");
        logger.log("📝 Mot ajouté : " + mot.getMot());
        logger.log("📖 Définition : " + mot.getDefinition());
        logger.log("🏷️ Catégorie : " + mot.getCategorie());
        logger.log("😊 Emoji : " + mot.getEmojie());

        Mot motRecupere = dao.findByMot(new Mot(-1, "TestMot", null, null, null));
        assertNotNull(motRecupere, "Le mot doit être récupérable");
        assertEquals("Définition de test", motRecupere.getDefinition());
        logger.log("✅ Mot retrouvé dans la base : " + motRecupere.getMot() + " → " + motRecupere.getDefinition());
    }

    @Test
    @DisplayName("Test saveMot() - Mot avec champs minimaux")
    public void testSaveMot_Minimal() throws DAOExeption {
        logger.log("🧪 TEST : saveMot() - champs minimaux");

        Mot mot = new Mot(100, "MotMinimal", null, null, null);
        boolean resultat = dao.saveMot(mot);

        assertTrue(resultat, "L'ajout doit réussir même avec champs null");
        logger.log("✅ Mot minimal ajouté : " + mot.getMot());

        Mot motRecupere = dao.findByMot(mot);
        assertNotNull(motRecupere,"Le mot doit être récupérable");
        assertNull(motRecupere.getDefinition(),"La def ne doit pas être récupérable car null au creation");
        logger.log("✅ Vérification champs null OK");
    }
                         // -- findByMot

    @Test
    @DisplayName("Test findByMot() - Mot existant")
    public void testFindByMot_Exists() throws DAOExeption {
        logger.log("🧪 TEST : findByMot() - Mot existant");

        Mot motRecherche = new Mot(-1, "Apprendre", null, null, null);
        Mot motTrouve = dao.findByMot(motRecherche);

        assertNotNull(motTrouve,"Le mot doit etre exist");
        assertEquals("Apprendre", motTrouve.getMot());
        logger.log("✅ Mot trouvé : " + motTrouve.getMot());
        logger.log("📖 Définition : " + motTrouve.getDefinition());
    }

    @Test
    @DisplayName("Test findByMot() - Mot inexistant")
    public void testFindByMot_NotExists() throws DAOExeption {
        logger.log("🧪 TEST : findByMot() - Mot inexistant");

        Mot motRecherche = new Mot(-1, "MotInexistant12345", null, null, null);
        Mot motTrouve = dao.findByMot(motRecherche);

        assertNull(motTrouve,"Mot ne doit pas etre trouvée !");
        logger.log("✅ Mot inexistant retourné null");
    }


                               // -- updateMot

    @Test
    @DisplayName("Test updateMot() - Modification réussie")
    public void testUpdateMot_Success() throws DAOExeption {
        logger.log("🧪 TEST : updateMot() - Mot existant");

        Mot motOriginal = new Mot(-1, "MotAModifier", "Ancienne définition", "Nom", "📝");
        dao.saveMot(motOriginal);
        logger.log("📝 Mot original ajouté : " + motOriginal.getMot());

        Mot motModifie = new Mot(-1, "MotAModifier", "NouvelleDef", "Verbe", "✨");
        boolean resultat = dao.updateMot(motOriginal, motModifie);

        assertTrue(resultat);
        logger.log("✏️ Modification effectuée");

        Mot recup = dao.findByMot(new Mot(-1, "MotAModifier", null, null, null));
        assertEquals("NouvelleDef", recup.getDefinition());
        logger.log("✅ Mot modifié : " + recup.getDefinition());
    }

    @Test
    @DisplayName("Test updateMot() - Mot inexistant")
    public void testUpdateMot_NotExists() throws DAOExeption {
        logger.log("🧪 TEST : updateMot() - Mot inexistant");

        Mot motInexistant = new Mot(-1, "MotInexistant999", "Def", "Nom", null);
        Mot nouveauMot = new Mot(-1, "MotInexistant999", "Nouvelle def", "Verbe", "🔥");

        boolean resultat = dao.updateMot(motInexistant, nouveauMot);
        assertFalse(resultat);
        logger.log("✅ Modification échouée comme prévu pour mot inexistant");
    }



                                     // -- getIDByMot()


    @Test
    @DisplayName("Test getIDByMot() - Récupération d'un ID valide")
    public void testGetIDByMot_Valid() throws DAOExeption {
        logger.log("🧪 TEST : getIDByMot() - Récupération d'un Mot valide");

        // "Apprendre" existe dans les données de test
        int id = dao.getIDByMot("Apprendre");

        assertTrue(id > 0, "L'ID doit être positif");
        logger.log("✅ Test réussi : ID valide récupéré");
    }

    @Test
    @DisplayName("Test getIDByMot() - Mot inexistant")
    public void testGetIDByMot_NotFound() throws DAOExeption {
        logger.log("🧪 TEST : getIDByMot() - Mot inexistant");

        int id = dao.getIDByMot("MotTotalementInexistant999");

        assertEquals(-1, id, "L'ID doit être -1 pour un mot inexistant");
        logger.log("✅ Test réussi : -1 retourné pour un mot inexistant");
    }

                   // -- Synonymes

    @Test
    @DisplayName("Test addSynonyme() - Ajout d'une relation de synonymie valide")
    public void testAddSynonyme_Success() throws DAOExeption {
        logger.log("🧪 TEST : addSynonyme() - Ajout d'une relation de synonymie");

        // ✅ Utiliser deux mots qui ne sont PAS déjà liés dans V11
        // Vérifions les données de test : "Rapide" et "Lent" sont antonymes, pas synonymes
        // Utilisons "Rapide" et "Heureux" qui ne sont pas encore liés
        int id1 = dao.getIDByMot("Rapide");
        int id2 = dao.getIDByMot("Heureux");

        Mot mot1 = new Mot(id1, "Rapide", null, null, null);
        Mot mot2 = new Mot(id2, "Heureux", null, null, null);

        boolean resultat = dao.addSynonyme(mot1, mot2);

        assertTrue(resultat, "L'ajout de synonyme doit réussir");
        logger.log("🔗 Synonymes liés : Rapide ↔ Heureux");
        logger.log("✅ Test réussi : Relation de synonymie créée");
    }

    @Test
    @DisplayName("Test getSynonymes() - Récupération des synonymes")
    public void testGetSynonymes() throws DAOExeption {
        logger.log("🧪 TEST : getSynonymes() - Récupération des synonymes");

        // "Magnifique" et "Splendide" sont synonymes dans les données de test
        int id = dao.getIDByMot("Magnifique");
        Mot mot = new Mot(id, "Magnifique", null, null, null);

        List<String> synonymes = dao.getSynonymes(mot);

        assertNotNull(synonymes, "La liste ne doit pas être null");
        assertFalse(synonymes.isEmpty(), "Des synonymes doivent être trouvés");
        assertTrue(synonymes.contains("Splendide"), "Splendide doit être un synonyme");

        logger.log("✅ Test réussi : Synonymes récupérés correctement");
    }

    @Test
    @DisplayName("Test getSynonymes() - Mot sans synonymes")
    public void testGetSynonymes_Empty() throws DAOExeption {
        logger.log("🧪 TEST : getSynonymes() - Mot sans synonymes");

        int id = dao.getIDByMot("Joie");
        Mot mot = new Mot(id, "Joie", null, null, null);

        List<String> synonymes = dao.getSynonymes(mot);

        assertTrue(synonymes == null || synonymes.isEmpty(), "Aucun synonyme ne doit être trouvé");
        logger.log("✅ Test réussi : Liste vide ou null pour un mot sans synonymes");
    }


                             // -- Antonymes


    @Test
    @DisplayName("Test addAntonyme() - Ajout d'une relation d'antonymie")
    public void testAddAntonyme_Success() throws DAOExeption {
        logger.log("🧪 TEST : addAntonyme() - Ajout d'une relation d'antonymie");

        // ✅ Utiliser deux mots qui ne sont PAS déjà liés dans V11
        // "Grand" et "Intelligent" ne sont pas liés
        int id1 = dao.getIDByMot("Grand");
        int id2 = dao.getIDByMot("Intelligent");

        Mot mot1 = new Mot(id1, "Grand", null, null, null);
        Mot mot2 = new Mot(id2, "Intelligent", null, null, null);

        boolean resultat = dao.addAntonyme(mot1, mot2);

        assertTrue(resultat, "L'ajout d'antonyme doit réussir");
        logger.log("⚡ Antonymes liés : Grand ↔ Intelligent");
        logger.log("✅ Test réussi : Relation d'antonymie créée");
    }

    @Test
    @DisplayName("Test getAntonymes() - Récupération des antonymes")
    public void testGetAntonymes() throws DAOExeption {
        logger.log("🧪 TEST : getAntonymes() - Récupération des antonymes");

        int id = dao.getIDByMot("Heureux");
        Mot mot = new Mot(id, "Heureux", null, null, null);

        List<String> antonymes = dao.getAntonymes(mot);

        assertNotNull(antonymes, "La liste ne doit pas être null");
        assertFalse(antonymes.isEmpty(), "Des antonymes doivent être trouvés");
        assertTrue(antonymes.contains("Triste"), "Triste doit être un antonyme");

        logger.log("📝 Antonymes de 'Heureux' : " + antonymes);
        logger.log("✅ Test réussi : Antonymes récupérés correctement");
    }


                         // -- getMotCountParCategorie()

    @Test
    @DisplayName("Test getMotCountParCategorie() - Comptage par catégorie")
    public void testGetMotCountParCategorie() throws DAOExeption {
        logger.log("🧪 TEST : getMotCountParCategorie() - Comptage par catégorie");

        Map<String, Integer> stats = dao.getMotCountParCategorie();

        assertNotNull(stats, "La map ne doit pas être null");
        assertFalse(stats.isEmpty(), "Des catégories doivent exister");

        logger.log("📊 Statistiques par catégorie :");
        logger.log("✅ Test réussi : Statistiques calculées correctement");
    }
}
