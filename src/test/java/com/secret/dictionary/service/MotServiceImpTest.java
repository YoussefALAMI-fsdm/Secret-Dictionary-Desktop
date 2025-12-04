package com.secret.dictionary.service;

import com.secret.dictionary.dao.DAOExeption;
import com.secret.dictionary.dao.MotDAOImp;
import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.model.Mot;
import com.secret.dictionary.utils.SimpleLogger;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour MotServiceImp avec Mockito
 * Teste la logique métier et les conversions DTO <-> Entity
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class MotServiceImpTest {

    private static final String LOG_FILE_PATH = "logs/LogMotServiceTest.log";
    private static SimpleLogger logger;

    @Mock
    private MotDAOImp daoMock;

    private MotServiceImp service;
    private AutoCloseable closeable;

    // ========================================
    // INITIALISATION DU LOGGER
    // ========================================

    @BeforeAll
    public static void initLogger() {
        logger = new SimpleLogger(LOG_FILE_PATH, true);
        logger.log("╔════════════════════════════════════════════════════════════╗");
        logger.log("║       DÉBUT DE LA SESSION DE TESTS - MotService           ║");
        logger.log("╚════════════════════════════════════════════════════════════╝");
        logger.log("");
    }

    @AfterAll
    public static void closeLogger() {
        logger.log("");
        logger.log("╔════════════════════════════════════════════════════════════╗");
        logger.log("║        FIN DE LA SESSION DE TESTS - MotService            ║");
        logger.log("╚════════════════════════════════════════════════════════════╝");
    }

    // ========================================
    // CONFIGURATION MOCKITO
    // ========================================

    @BeforeEach
    public void setUp() {
        logger.log("────────────────────────────────────────────────────────────");
        logger.log("🔧 Initialisation des mocks...");

        closeable = MockitoAnnotations.openMocks(this);
        service = new MotServiceImp(daoMock);

        logger.log("✅ Service initialisé avec DAO mocké");
        logger.log("");
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
        logger.log("🔒 Mocks fermés");
        logger.log("");
    }

    // ========================================
    // TESTS - getAllMots()
    // ========================================

    @Test
    @DisplayName("Test getAllMots() - Liste de mots retournée")
    public void testGetAllMots_Success() throws DAOExeption {
        logger.log("🧪 TEST : getAllMots() - Success");

        List<String> motsDAO = Arrays.asList("Chat", "Chien", "Oiseau");
        when(daoMock.findAllMot()).thenReturn(motsDAO);

        List<String> result = service.getAllMots();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("Chat"));

        verify(daoMock, times(1)).findAllMot();

        logger.log("📊 Nombre de mots retournés : " + result.size());
        logger.log("✅ Test réussi");
    }

    @Test
    @DisplayName("Test getAllMots() - Liste vide si DAO retourne null")
    public void testGetAllMots_NullFromDAO() throws DAOExeption {
        logger.log("🧪 TEST : getAllMots() - DAO retourne null");

        when(daoMock.findAllMot()).thenReturn(null);

        List<String> result = service.getAllMots();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        logger.log("✅ Liste vide retournée correctement");
    }

    @Test
    @DisplayName("Test getAllMots() - Exception DAO gérée")
    public void testGetAllMots_DAOException() throws DAOExeption {
        logger.log("🧪 TEST : getAllMots() - Exception DAO");

        when(daoMock.findAllMot()).thenThrow(new DAOExeption("Erreur DB", null));

        List<String> result = service.getAllMots();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        logger.log("✅ Exception gérée, liste vide retournée");
    }

    // ========================================
    // TESTS - addMot()
    // ========================================

    @Test
    @DisplayName("Test addMot() - Ajout réussi")
    public void testAddMot_Success() throws DAOExeption {
        logger.log("🧪 TEST : addMot() - Ajout réussi");

        MotDTO dto = new MotDTO("Nouveau", "Définition", "Nom", "📝");

        when(daoMock.saveMot(any(Mot.class))).thenReturn(true);
        doNothing().when(daoMock).rafraichirMaterializedView();

        int result = service.addMot(dto);

        assertEquals(1, result);
        verify(daoMock, times(1)).saveMot(any(Mot.class));
        verify(daoMock, times(1)).rafraichirMaterializedView();

        logger.log("✅ Mot ajouté avec succès");
    }

    @Test
    @DisplayName("Test addMot() - Mot déjà existant")
    public void testAddMot_AlreadyExists() throws DAOExeption {
        logger.log("🧪 TEST : addMot() - Mot existant");

        MotDTO dto = new MotDTO("Existant", "Def", "Nom", null);

        when(daoMock.saveMot(any(Mot.class)))
                .thenThrow(new DAOExeption("Duplicate", null));

        Mot motExistant = new Mot(1, "Existant", "Def", "Nom", null);
        when(daoMock.findByMot(any(Mot.class))).thenReturn(motExistant);

        int result = service.addMot(dto);

        assertEquals(0, result);
        verify(daoMock, never()).rafraichirMaterializedView();

        logger.log("✅ Mot existant détecté correctement");
    }

    @Test
    @DisplayName("Test addMot() - Erreur DB")
    public void testAddMot_DBError() throws DAOExeption {
        logger.log("🧪 TEST : addMot() - Erreur DB");

        MotDTO dto = new MotDTO("Test", "Def", "Nom", null);

        when(daoMock.saveMot(any(Mot.class))).thenReturn(false);

        int result = service.addMot(dto);

        assertEquals(-1, result);
        verify(daoMock, never()).rafraichirMaterializedView();

        logger.log("✅ Erreur DB gérée correctement");
    }

    // ========================================
    // TESTS - getInfoMot()
    // ========================================

    @Test
    @DisplayName("Test getInfoMot() - Mot trouvé")
    public void testGetInfoMot_Found() throws DAOExeption {
        logger.log("🧪 TEST : getInfoMot() - Mot trouvé");

        MotDTO dtoRecherche = new MotDTO("Chat", null, null, null);
        Mot motTrouve = new Mot(1, "Chat", "Félin domestique", "Nom", "🐱");

        when(daoMock.findByMot(any(Mot.class))).thenReturn(motTrouve);

        MotDTO result = service.getInfoMot(dtoRecherche);

        assertNotNull(result);
        assertEquals("Chat", result.mot());
        assertEquals("Félin domestique", result.definition());
        assertEquals("🐱", result.emojie());

        logger.log("📝 Mot trouvé : " + result.mot());
        logger.log("✅ Test réussi");
    }

    @Test
    @DisplayName("Test getInfoMot() - Mot non trouvé")
    public void testGetInfoMot_NotFound() throws DAOExeption {
        logger.log("🧪 TEST : getInfoMot() - Mot non trouvé");

        MotDTO dtoRecherche = new MotDTO("Inexistant", null, null, null);

        when(daoMock.findByMot(any(Mot.class))).thenReturn(null);

        MotDTO result = service.getInfoMot(dtoRecherche);

        assertNull(result);

        logger.log("✅ Null retourné correctement");
    }

    // ========================================
    // TESTS - updateMot()
    // ========================================

    @Test
    @DisplayName("Test updateMot() - Modification réussie")
    public void testUpdateMot_Success() throws DAOExeption {
        logger.log("🧪 TEST : updateMot() - Modification réussie");

        MotDTO ancien = new MotDTO("Ancien", "Def1", "Nom", "📝");
        MotDTO nouveau = new MotDTO("Ancien", "Def2", "Verbe", "✨");

        when(daoMock.updateMot(any(Mot.class), any(Mot.class))).thenReturn(true);
        doNothing().when(daoMock).rafraichirMaterializedView();

        boolean result = service.updateMot(ancien, nouveau);

        assertTrue(result);
        verify(daoMock, times(1)).updateMot(any(Mot.class), any(Mot.class));
        verify(daoMock, times(1)).rafraichirMaterializedView();

        logger.log("✅ Modification réussie");
    }

    @Test
    @DisplayName("Test updateMot() - Échec de modification")
    public void testUpdateMot_Failure() throws DAOExeption {
        logger.log("🧪 TEST : updateMot() - Échec");

        MotDTO ancien = new MotDTO("Test", "Def1", "Nom", null);
        MotDTO nouveau = new MotDTO("Test", "Def2", "Verbe", null);

        when(daoMock.updateMot(any(Mot.class), any(Mot.class))).thenReturn(false);

        boolean result = service.updateMot(ancien, nouveau);

        assertFalse(result);
        verify(daoMock, never()).rafraichirMaterializedView();

        logger.log("✅ Échec géré correctement");
    }

    // ========================================
    // TESTS - addSynonyme()
    // ========================================

    @Test
    @DisplayName("Test addSynonyme() - Ajout réussi")
    public void testAddSynonyme_Success() throws DAOExeption {
        logger.log("🧪 TEST : addSynonyme() - Ajout réussi");

        MotDTO mot1 = new MotDTO("Heureux", null, null, null);
        MotDTO mot2 = new MotDTO("Joyeux", null, null, null);

        when(daoMock.getIDByMot("Heureux")).thenReturn(1);
        when(daoMock.getIDByMot("Joyeux")).thenReturn(2);
        when(daoMock.addSynonyme(any(Mot.class), any(Mot.class))).thenReturn(true);

        int result = service.addSynonyme(mot1, mot2);

        assertEquals(1, result);
        verify(daoMock, times(1)).addSynonyme(any(Mot.class), any(Mot.class));

        logger.log("✅ Synonyme ajouté");
    }

    @Test
    @DisplayName("Test addSynonyme() - Mot inexistant")
    public void testAddSynonyme_MotNotFound() throws DAOExeption {
        logger.log("🧪 TEST : addSynonyme() - Mot inexistant");

        MotDTO mot1 = new MotDTO("Existant", null, null, null);
        MotDTO mot2 = new MotDTO("Inexistant", null, null, null);

        when(daoMock.getIDByMot("Existant")).thenReturn(1);
        when(daoMock.getIDByMot("Inexistant")).thenReturn(-1);

        int result = service.addSynonyme(mot1, mot2);

        assertEquals(0, result);
        verify(daoMock, never()).addSynonyme(any(Mot.class), any(Mot.class));

        logger.log("✅ Mot inexistant détecté");
    }

    // ========================================
    // TESTS - getListSynonymes()
    // ========================================

    @Test
    @DisplayName("Test getListSynonymes() - Synonymes trouvés")
    public void testGetListSynonymes_Found() throws DAOExeption {
        logger.log("🧪 TEST : getListSynonymes() - Synonymes trouvés");

        MotDTO mot = new MotDTO("Heureux", null, null, null);
        List<String> synonymes = Arrays.asList("Joyeux", "Content");

        when(daoMock.getIDByMot("Heureux")).thenReturn(1);
        when(daoMock.getSynonymes(any(Mot.class))).thenReturn(synonymes);

        List<String> result = service.getListSynonymes(mot);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Joyeux"));

        logger.log("📊 Synonymes trouvés : " + result.size());
        logger.log("✅ Test réussi");
    }

    @Test
    @DisplayName("Test getListSynonymes() - Mot inexistant")
    public void testGetListSynonymes_MotNotFound() throws DAOExeption {
        logger.log("🧪 TEST : getListSynonymes() - Mot inexistant");

        MotDTO mot = new MotDTO("Inexistant", null, null, null);

        when(daoMock.getIDByMot("Inexistant")).thenReturn(-1);

        List<String> result = service.getListSynonymes(mot);

        assertNull(result);

        logger.log("✅ Null retourné pour mot inexistant");
    }

    @Test
    @DisplayName("Test getListSynonymes() - Aucun synonyme")
    public void testGetListSynonymes_Empty() throws DAOExeption {
        logger.log("🧪 TEST : getListSynonymes() - Aucun synonyme");

        MotDTO mot = new MotDTO("Seul", null, null, null);

        when(daoMock.getIDByMot("Seul")).thenReturn(1);
        when(daoMock.getSynonymes(any(Mot.class))).thenReturn(null);

        List<String> result = service.getListSynonymes(mot);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        logger.log("✅ Liste vide retournée");
    }

    // ========================================
    // TESTS - getMotCountParCategorie()
    // ========================================

    @Test
    @DisplayName("Test getMotCountParCategorie() - Statistiques OK")
    public void testGetMotCountParCategorie_Success() throws DAOExeption {
        logger.log("🧪 TEST : getMotCountParCategorie() - Success");

        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("Verbe", 10);
        stats.put("Nom", 15);
        stats.put("Adjectif", 8);

        when(daoMock.getMotCountParCategorie()).thenReturn(stats);

        Map<String, Integer> result = service.getMotCountParCategorie();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(10, result.get("Verbe"));

        logger.log("📊 Statistiques retournées : " + result.size() + " catégories");
        logger.log("✅ Test réussi");
    }

    @Test
    @DisplayName("Test getMotCountParCategorie() - Map vide si null")
    public void testGetMotCountParCategorie_Null() throws DAOExeption {
        logger.log("🧪 TEST : getMotCountParCategorie() - DAO retourne null");

        when(daoMock.getMotCountParCategorie()).thenReturn(null);

        Map<String, Integer> result = service.getMotCountParCategorie();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        logger.log("✅ Map vide retournée");
    }

    // ========================================
    // TESTS - Conversions DTO <-> Entity
    // ========================================

    @Test
    @DisplayName("Test dtoToEntity() - Conversion DTO vers Entity")
    public void testDtoToEntity() {
        logger.log("🧪 TEST : dtoToEntity() - Conversion");

        MotDTO dto = new MotDTO("Test", "Définition", "Nom", "📝");

        Mot entity = service.dtoToEntity(dto);

        assertNotNull(entity);
        assertEquals("Test", entity.getMot());
        assertEquals("Définition", entity.getDefinition());
        assertEquals("Nom", entity.getCategorie());
        assertEquals("📝", entity.getEmojie());
        assertEquals(-1, entity.getId());

        logger.log("✅ Conversion DTO → Entity réussie");
    }

    @Test
    @DisplayName("Test entityToDTO() - Conversion Entity vers DTO")
    public void testEntityToDTO() {
        logger.log("🧪 TEST : entityToDTO() - Conversion");

        Mot entity = new Mot(5, "Test", "Définition", "Verbe", "✨");

        MotDTO dto = service.entityToDTO(entity);

        assertNotNull(dto);
        assertEquals("Test", dto.mot());
        assertEquals("Définition", dto.definition());
        assertEquals("Verbe", dto.categorie());
        assertEquals("✨", dto.emojie());

        logger.log("✅ Conversion Entity → DTO réussie");
    }
}