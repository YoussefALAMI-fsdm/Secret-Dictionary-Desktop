-- Création de la materialized view
CREATE MATERIALIZED VIEW mv_mots_count_par_categorie AS
SELECT categorie, COUNT(*) as compteur
FROM mots
GROUP BY categorie;

-- Index pour accélérer l'accès
CREATE UNIQUE INDEX idx_mv_categorie ON mv_mots_count_par_categorie(categorie);
