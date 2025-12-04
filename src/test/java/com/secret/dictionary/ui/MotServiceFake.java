package com.secret.dictionary.ui;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.service.MotService;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Implémentation Fake de MotService pour les tests fonctionnels TestFX
 * Simule le comportement du service réel avec des données en mémoire
 */
public class MotServiceFake implements MotService {

    // Stockage en mémoire des mots
    private final Map<String, MotDTO> mots = new HashMap<>();

    // Stockage des relations synonymes (bidirectionnel)
    private final Map<String, Set<String>> synonymes = new HashMap<>();

    // Stockage des relations antonymes (bidirectionnel)
    private final Map<String, Set<String>> antonymes = new HashMap<>();

    // Flag pour simuler une erreur DB
    private boolean simulateDbError = false;

    public MotServiceFake() {
        // Initialisation avec quelques données par défaut pour les tests
        initDefaultData();
    }

    /**
     * Permet de simuler une erreur de base de données pour les tests
     */
    public void setSimulateDbError(boolean simulate) {
        this.simulateDbError = simulate;
    }

    /**
     * Réinitialise toutes les données (utile entre les tests)
     */
    public void reset() {
        mots.clear();
        synonymes.clear();
        antonymes.clear();
        simulateDbError = false;
        initDefaultData();
    }

    /**
     * Initialise quelques données par défaut
     */
    private void initDefaultData() {
        addMot(new MotDTO("chat", "Petit félin domestique", "Nom", "🐱"));
        addMot(new MotDTO("chien", "Meilleur ami de l'homme", "Nom", "🐕"));
        addMot(new MotDTO("heureux", "Qui ressent de la joie", "Adjectif", "😊"));
        addMot(new MotDTO("triste", "Qui ressent de la peine", "Adjectif", "😢"));
        addMot(new MotDTO("courir", "Se déplacer rapidement", "Verbe", "🏃"));
    }

    @Override
    public List<String> getAllMots() {
        return new LinkedList<>(mots.keySet());
    }

    @Override
    public int addMot(MotDTO dto) {
        if (simulateDbError) {
            return -1;
        }

        if (dto == null || dto.mot() == null || dto.mot().trim().isEmpty()) {
            return -1;
        }

        String motKey = dto.mot().toLowerCase();

        if (mots.containsKey(motKey)) {
            return 0; // Mot déjà existant
        }

        mots.put(motKey, dto);
        return 1; // Succès
    }

    @Override
    public MotDTO getInfoMot(MotDTO dto) {
        if (dto == null || dto.mot() == null) {
            return null;
        }

        String motKey = dto.mot().toLowerCase();
        return mots.get(motKey);
    }

    @Override
    public List<String> getListMot(String mot) {
        if (mot == null || mot.trim().isEmpty()) {
            return new LinkedList<>();
        }

        String searchTerm = mot.toLowerCase();

        return mots.keySet().stream()
                .filter(m -> m.contains(searchTerm))
                .sorted()
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public boolean updateMot(MotDTO ancien, MotDTO nouveau) {
        if (simulateDbError) {
            return false;
        }

        if (ancien == null || nouveau == null ||
                ancien.mot() == null || nouveau.mot() == null) {
            return false;
        }

        String ancienKey = ancien.mot().toLowerCase();
        String nouveauKey = nouveau.mot().toLowerCase();

        // Vérifier que l'ancien mot existe
        if (!mots.containsKey(ancienKey)) {
            return false;
        }

        // Si le mot change de nom
        if (!ancienKey.equals(nouveauKey)) {
            // Vérifier que le nouveau nom n'existe pas déjà
            if (mots.containsKey(nouveauKey)) {
                return false;
            }

            // Mettre à jour les relations
            updateRelationsForRename(ancienKey, nouveauKey);

            // Supprimer l'ancien et ajouter le nouveau
            mots.remove(ancienKey);
        }

        mots.put(nouveauKey, nouveau);
        return true;
    }

    private void updateRelationsForRename(String oldKey, String newKey) {
        // Mettre à jour les synonymes
        if (synonymes.containsKey(oldKey)) {
            Set<String> syns = synonymes.remove(oldKey);
            synonymes.put(newKey, syns);

            // Mettre à jour les références inverses
            for (String syn : syns) {
                Set<String> inverseSyns = synonymes.get(syn);
                if (inverseSyns != null) {
                    inverseSyns.remove(oldKey);
                    inverseSyns.add(newKey);
                }
            }
        }

        // Mettre à jour les antonymes
        if (antonymes.containsKey(oldKey)) {
            Set<String> ants = antonymes.remove(oldKey);
            antonymes.put(newKey, ants);

            // Mettre à jour les références inverses
            for (String ant : ants) {
                Set<String> inverseAnts = antonymes.get(ant);
                if (inverseAnts != null) {
                    inverseAnts.remove(oldKey);
                    inverseAnts.add(newKey);
                }
            }
        }
    }

    @Override
    public int addSynonyme(MotDTO mot1, MotDTO mot2) {
        if (simulateDbError) {
            return -1;
        }

        if (mot1 == null || mot2 == null ||
                mot1.mot() == null || mot2.mot() == null) {
            return -1;
        }

        String key1 = mot1.mot().toLowerCase();
        String key2 = mot2.mot().toLowerCase();

        // Vérifier que les deux mots existent
        if (!mots.containsKey(key1) || !mots.containsKey(key2)) {
            return 0;
        }

        // Ajouter la relation bidirectionnelle
        synonymes.computeIfAbsent(key1, k -> new HashSet<>()).add(key2);
        synonymes.computeIfAbsent(key2, k -> new HashSet<>()).add(key1);

        return 1;
    }

    @Override
    public int addAntonyme(MotDTO mot1, MotDTO mot2) {
        if (simulateDbError) {
            return -1;
        }

        if (mot1 == null || mot2 == null ||
                mot1.mot() == null || mot2.mot() == null) {
            return -1;
        }

        String key1 = mot1.mot().toLowerCase();
        String key2 = mot2.mot().toLowerCase();

        // Vérifier que les deux mots existent
        if (!mots.containsKey(key1) || !mots.containsKey(key2)) {
            return 0;
        }

        // Ajouter la relation bidirectionnelle
        antonymes.computeIfAbsent(key1, k -> new HashSet<>()).add(key2);
        antonymes.computeIfAbsent(key2, k -> new HashSet<>()).add(key1);

        return 1;
    }

    @Override
    public List<String> getListSynonymes(MotDTO mot) {
        if (mot == null || mot.mot() == null) {
            return null;
        }

        String motKey = mot.mot().toLowerCase();

        // Vérifier que le mot existe
        if (!mots.containsKey(motKey)) {
            return null; // Mot inexistant
        }

        // Retourner la liste des synonymes (vide si aucun)
        Set<String> syns = synonymes.get(motKey);
        if (syns == null || syns.isEmpty()) {
            return new LinkedList<>();
        }

        return new LinkedList<>(syns);
    }

    @Override
    public List<String> getListAntonymes(MotDTO mot) {
        if (mot == null || mot.mot() == null) {
            return null;
        }

        String motKey = mot.mot().toLowerCase();

        // Vérifier que le mot existe
        if (!mots.containsKey(motKey)) {
            return null; // Mot inexistant
        }

        // Retourner la liste des antonymes (vide si aucun)
        Set<String> ants = antonymes.get(motKey);
        if (ants == null || ants.isEmpty()) {
            return new LinkedList<>();
        }

        return new LinkedList<>(ants);
    }

    @Override
    public Map<String, Integer> getMotCountParCategorie() {
        Map<String, Integer> counts = new HashMap<>();

        for (MotDTO dto : mots.values()) {
            if (dto.categorie() != null) {
                counts.merge(dto.categorie(), 1, Integer::sum);
            }
        }

        // Trier par ordre alphabétique des catégories
        return counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}