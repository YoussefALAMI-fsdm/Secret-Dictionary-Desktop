DE

-- Activer l'extension citext (si non existante)
CREATE EXTENSION IF NOT EXISTS citext;

-- Changer le type de la colonne
ALTER TABLE mot
    ALTER COLUMN mot TYPE citext;

-- Réappliquer la contrainte NOT NULL sur mots(mot)
ALTER TABLE mot
    ALTER COLUMN mot SET NOT NULL;