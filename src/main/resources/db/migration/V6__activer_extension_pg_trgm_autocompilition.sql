-- Active l'extension pg_trgm si elle n'est pas déjà installée
-- pg_trgm fournit des fonctions et opérateurs pour la recherche floue (trigrammes)
CREATE EXTENSION IF NOT EXISTS pg_trgm;


-- Crée un index pour accélérer la recherche de mots proches dans la colonne 'mot'
-- Utilise les trigrammes (pg_trgm) pour permettre les recherches floues
CREATE INDEX idx_mots_trgm ON mots USING gin(mot gin_trgm_ops);