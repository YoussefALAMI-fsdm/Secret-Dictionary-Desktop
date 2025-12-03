-- H2 ne supporte pas toujours IF NOT EXISTS pour CREATE INDEX
DROP INDEX IF EXISTS idx_mot;  -- supprimer l'ancien index si existant
CREATE UNIQUE INDEX idx_mot ON mots(mot);
