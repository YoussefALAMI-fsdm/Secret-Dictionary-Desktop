-- ======================================================
-- SCRIPT : Table temporaire simulant une materialized view
-- ======================================================

-- Supprimer la table si elle existe
DROP TABLE IF EXISTS mv_mots_count_par_categorie;

-- Créer une table pour simuler la materialized view
CREATE TABLE mv_mots_count_par_categorie (
    categorie VARCHAR(255) PRIMARY KEY,
    compteur INT
);

-- Remplir la table avec un comptage initial
INSERT INTO mv_mots_count_par_categorie (categorie, compteur)
SELECT categorie, COUNT(*)
FROM mots
GROUP BY categorie;

-- Index déjà couvert par la PK, donc pas nécessaire en plus
