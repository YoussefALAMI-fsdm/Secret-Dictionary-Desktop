package com.secret.dictionary.ui;

import com.secret.dictionary.app.Main;
import com.secret.dictionary.controller.MainController;
import com.secret.dictionary.utils.SimpleLogger;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Tests fonctionnels de l'interface principale avec TestFX
 * Teste la navigation, les interactions utilisateur et l'intégration des composants
 */
//Chaque test commence avec un nouvel objet test
//JUnit crée une nouvelle instance de MainViewTest pour chaque méthode de test
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class MainViewTest extends ApplicationTest {

    //permet de dire où est stocké un fichier
    private static final String LOG_FILE_PATH = "logs/LogMainViewTest.log";
    private static SimpleLogger logger;

    private MainController mainController;
    private MotServiceFake motServiceFake;

    // ========================================
    // INITIALISATION DU LOGGER
    // ========================================

    @BeforeAll
    public static void initLogger() {
        logger = new SimpleLogger(LOG_FILE_PATH, true);
        logger.log("╔════════════════════════════════════════════════════════════╗");
        logger.log("║       DÉBUT DE LA SESSION DE TESTS - MainView UI          ║");
        logger.log("╚════════════════════════════════════════════════════════════╝");
        logger.log("");
    }

    @AfterAll
    public static void closeLogger() {
        logger.log("");
        logger.log("╔════════════════════════════════════════════════════════════╗");
        logger.log("║        FIN DE LA SESSION DE TESTS - MainView UI           ║");
        logger.log("╚════════════════════════════════════════════════════════════╝");
    }

    // ========================================
    // CONFIGURATION TESTFX
    // ========================================

    @Override
    public void start(Stage stage) throws Exception {
        logger.log("────────────────────────────────────────────────────────────");
        logger.log("🔧 Initialisation de l'application JavaFX pour test...");

        // Créer un service fake pour les tests
        motServiceFake = new MotServiceFake();

        // Charger l'application (similaire à Main.start())
        javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(
                Main.class.getResource("/com/secret/dictionary/fxml/main-view.fxml")
        );

        javafx.scene.Parent parent = fxmlLoader.load();
        mainController = fxmlLoader.getController();

        // Injecter le service fake
        mainController.setMotService(motServiceFake);

        javafx.scene.Scene scene = new javafx.scene.Scene(parent, 800, 600);
        scene.getStylesheets().add(
                Main.class.getResource("/com/secret/dictionary/styles/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.show();
        stage.toFront();

        logger.log("✅ Application JavaFX initialisée avec succès");
        logger.log("");
    }

    @BeforeEach
    public void setupTest() {
        logger.log("────────────────────────────────────────────────────────────");
        logger.log("🧪 Préparation d'un nouveau test...");
    }

    @AfterEach
    public void tearDown() throws TimeoutException {
        logger.log("🧹 Nettoyage après le test");
        logger.log("");

        // Réinitialiser le service fake
        if (motServiceFake != null) {
            motServiceFake.reset();
        }

        // Fermer les fenêtres ouvertes
        FxToolkit.cleanupStages();
    }

    // ========================================
    // TESTS DE L'INTERFACE PRINCIPALE
    // ========================================

    @Test
    //Annotation JUnit 5
    @DisplayName("Test UI - Vérifier que l'interface principale s'affiche")
    public void testMainViewLoads() {
        logger.log("🧪 TEST : Chargement de l'interface principale");

        verifyThat("📊 Statistiques", LabeledMatchers.hasText("📊 Statistiques"));
        verifyThat("🔍 Recherche", LabeledMatchers.hasText("🔍 Recherche"));

        logger.log("✅ Interface principale chargée correctement");
        logger.log("✅ Boutons Statistiques et Recherche présents");
    }

    @Test
    @DisplayName("Test UI - Affichage de la liste des mots")
    public void testWordListDisplays() {
        logger.log("🧪 TEST : Affichage de la liste des mots");

        //wildcard
        ListView<?> wordList = lookup("#wordList").query();
        //JUnit
        assertNotNull(wordList, "La liste des mots doit être présente");
        assertFalse(wordList.getItems().isEmpty(), "La liste ne doit pas être vide");

        int count = wordList.getItems().size();
        logger.log("📊 Nombre de mots dans la liste : " + count);
        logger.log("✅ Liste des mots affichée correctement");
    }

    @Test
    @DisplayName("Test UI - Clic sur le bouton Statistiques")
    public void testStatisticsButtonClick() {
        logger.log("🧪 TEST : Clic sur le bouton Statistiques");

        clickOn("📊 Statistiques");

        Label titleLabel = lookup("#titleLabel").query();
        assertNotNull(titleLabel, "Le titre des statistiques doit être présent");
        assertEquals("Statistiques du Dictionnaire", titleLabel.getText());

        logger.log("✅ Bouton Statistiques cliqué");
        logger.log("✅ Vue statistiques affichée");
    }

    @Test
    @DisplayName("Test UI - Ouvrir le dialogue de recherche")
    public void testOpenSearchDialog() {
        logger.log("🧪 TEST : Ouverture du dialogue de recherche");

        clickOn("🔍 Recherche");
        sleep(300);

        Node dialog = lookup(".dialog-pane").query();
        assertNotNull(dialog, "Le dialogue de recherche doit être ouvert");

        logger.log("✅ Dialogue de recherche ouvert");

        press(KeyCode.ESCAPE).release(KeyCode.ESCAPE);
        sleep(300);

        logger.log("✅ Dialogue fermé avec succès");
    }

    @Test
    @DisplayName("Test UI - Sélection d'un mot dans la liste")
    public void testWordSelection() {
        logger.log("🧪 TEST : Sélection d'un mot dans la liste");

        //recuperer l'objet reel
        ListView<?> wordList = lookup("#wordList").query();
        assertFalse(wordList.getItems().isEmpty(), "La liste doit contenir des mots");

        clickOn(wordList);
        sleep(300);

        Label wordTitle = lookup("#wordTitle").query();
        assertNotNull(wordTitle, "Le titre du mot doit être affiché");
        assertNotEquals("Sélectionnez un mot", wordTitle.getText());

        logger.log("📝 Mot sélectionné : " + wordTitle.getText());
        logger.log("✅ Détails du mot affichés correctement");
    }

    /*
    @Test
    @DisplayName("Test UI - Navigation vers tous les mots depuis le menu")
    public void testNavigateToAllWords() {
        logger.log("🧪 TEST : Navigation vers tous les mots");

        clickOn("📁 Recherche avancée");
        sleep(300);
        clickOn("• Emojis");
        sleep(300);

        Label titleLabel = lookup("#titleLabel").query();
        assertNotNull(titleLabel, "La vue statistiques doit être affichée");

        logger.log("✅ Navigation vers tous les mots réussie");
    }
*/
    @Test
    @DisplayName("Test UI - Vérifier les styles CSS appliqués")
    public void testStylesApplied() {
        logger.log("🧪 TEST : Vérification des styles CSS");

        Button statsButton = lookup("📊 Statistiques").query();
        assertNotNull(statsButton, "Le bouton Statistiques doit exister");

        String style = statsButton.getStyle();
        assertNotNull(style, "Le bouton doit avoir un style");

        logger.log("✅ Styles CSS appliqués correctement");
    }

    /*@Test
    @DisplayName("Test UI - Fermeture et réouverture du menu déroulant")
    public void testMenuToggle() {
        logger.log("🧪 TEST : Toggle du menu déroulant");

        clickOn("📁 Recherche avancée");
        sleep(300);

        Node menuCatego = lookup("#menuCatego").query();
        assertNotNull(menuCatego, "Le menu doit être présent");
        assertTrue(menuCatego.isVisible(), "Le menu doit être visible");

        logger.log("✅ Menu ouvert");

        clickOn("📁 Recherche avancée");
        sleep(300);

        assertFalse(menuCatego.isVisible(), "Le menu doit être caché");

        logger.log("✅ Menu fermé");
        logger.log("✅ Toggle du menu fonctionne correctement");
    }*/

    @Test
    @DisplayName("Test UI - Vérifier la présence du bouton Modifier")
    public void testModifierButtonVisibility() {
        logger.log("🧪 TEST : Visibilité du bouton Modifier");

        // ✅ ÉTAPE 1 : Sélectionner un mot pour charger word-details.fxml
        logger.log("📝 Sélection d'un mot dans la liste...");
        ListView<?> wordList = lookup("#wordList").query();
        assertNotNull(wordList, "La liste des mots doit être présente");
        assertFalse(wordList.getItems().isEmpty(), "La liste doit contenir des mots");

        clickOn(wordList);
        sleep(500); // attendre que word-details.fxml soit chargé

        // ✅ ÉTAPE 2 : Vérifier le bouton Modifier
        logger.log("🔍 Recherche du bouton Modifier...");
        Button btnModifier = lookup("#btnModifier").query();
        assertNotNull(btnModifier, "Le bouton Modifier doit exister après sélection");

        assertTrue(btnModifier.isVisible(), "Le bouton doit être visible après sélection d'un mot");
        assertTrue(btnModifier.isManaged(), "Le bouton doit être géré (managed)");

        logger.log("✅ Bouton Modifier visible après sélection d'un mot");
    }

    @Test
    @DisplayName("Test UI - Vérifier le compteur total dans les statistiques")
    public void testStatisticsTotalCount() {
        logger.log("🧪 TEST : Compteur total dans les statistiques");

        clickOn("📊 Statistiques");
        sleep(300);

        Label totalCountLabel = lookup("#totalCountLabel").query();
        assertNotNull(totalCountLabel, "Le label du total doit être présent");

        String totalText = totalCountLabel.getText();
        //contains : sous chaine
        assertTrue(totalText.contains("mots au total"), "Le texte doit contenir 'mots au total'");

        logger.log("📊 " + totalText);
        logger.log("✅ Compteur total affiché correctement");
    }

    // ========================================
    // MÉTHODES UTILITAIRES
    // ========================================

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clickOnSafe(String query) {
        try {
            clickOn(query);
            logger.log("✅ Clic sur : " + query);
        } catch (Exception e) {
            logger.log("❌ Échec du clic sur : " + query);
            throw e;
        }
    }

    private void assertVisible(String query, String description) {
        Node node = lookup(query).query();
        assertNotNull(node, description + " doit exister");
        assertTrue(node.isVisible(), description + " doit être visible");
        logger.log("✅ " + description + " visible");
    }
}