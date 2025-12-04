DROP INDEX IF EXISTS idx_mot_unique;
CREATE UNIQUE INDEX idx_mot_covering ON mots(mot) INCLUDE (id);