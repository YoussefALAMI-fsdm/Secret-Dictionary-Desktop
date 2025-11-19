-- Créer un index unique sur mots.mot (plus performant)
DROP INDEX IF EXISTS idx_mot;  -- Supprime l'ancien index si existant
CREATE UNIQUE INDEX idx_mot_unique ON mots(mot);