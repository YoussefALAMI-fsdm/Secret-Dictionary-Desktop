-- Activer l'extension citext (si non existante) ( test valide )
CREATE EXTENSION IF NOT EXISTS citext;

-- Changer le type de la colonne mot pour qu'elle soit insensible à la casse
ALTER TABLE mot
    ALTER COLUMN mot TYPE citext;