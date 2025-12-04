-- ================================================================
-- SCRIPT V11 : Insertion de données de test complètes (VERSION H2)
-- 30 mots avec définitions, catégories, emojis, synonymes et antonymes
-- ================================================================

-- ========================================
-- INSERTION DES MOTS (H2 : MERGE au lieu de INSERT ... ON CONFLICT)
-- ========================================

MERGE INTO mots (mot, def, categorie, emojie) KEY(mot) VALUES
-- Verbes (10 mots)
('Apprendre', 'Acquérir des connaissances par l''étude ou l''expérience', 'Verbe', '📚'),
('Enseigner', 'Transmettre des connaissances à quelqu''un', 'Verbe', '👨‍🏫'),
('Oublier', 'Ne plus se souvenir de quelque chose', 'Verbe', '🤔'),
('Créer', 'Faire exister quelque chose de nouveau', 'Verbe', '✨'),
('Détruire', 'Faire disparaître complètement, anéantir', 'Verbe', '💥'),
('Construire', 'Bâtir, édifier quelque chose', 'Verbe', '🏗️'),
('Démolir', 'Abattre, détruire une construction', 'Verbe', '🔨'),
('Augmenter', 'Rendre plus grand, accroître', 'Verbe', '📈'),
('Diminuer', 'Rendre plus petit, réduire', 'Verbe', '📉'),
('Commencer', 'Faire la première partie de quelque chose', 'Verbe', '🚀'),

-- Adjectifs (10 mots)
('Magnifique', 'D''une très grande beauté', 'Adjectif', '🌟'),
('Splendide', 'D''un éclat remarquable, somptueux', 'Adjectif', '✨'),
('Horrible', 'Qui inspire l''horreur, très laid', 'Adjectif', '😱'),
('Rapide', 'Qui se déplace à grande vitesse', 'Adjectif', '⚡'),
('Lent', 'Qui manque de rapidité', 'Adjectif', '🐌'),
('Grand', 'De dimensions importantes', 'Adjectif', '📏'),
('Petit', 'De dimensions réduites', 'Adjectif', '🔬'),
('Heureux', 'Qui éprouve du bonheur', 'Adjectif', '😊'),
('Triste', 'Qui éprouve de la tristesse', 'Adjectif', '😢'),
('Intelligent', 'Doué d''intelligence, de capacités mentales', 'Adjectif', '🧠'),

-- Noms (7 mots)
('Connaissance', 'Ensemble des choses connues, savoir', 'Nom', '💡'),
('Ignorance', 'Manque de connaissance, d''instruction', 'Nom', '❓'),
('Joie', 'Sentiment de bonheur intense', 'Nom', '🎉'),
('Tristesse', 'État affectif pénible et durable', 'Nom', '💔'),
('Courage', 'Force morale face au danger', 'Nom', '💪'),
('Peur', 'Émotion d''angoisse face à un danger', 'Nom', '😨'),
('Amour', 'Sentiment d''affection profonde', 'Nom', '❤️'),

-- Expressions (3 mots)
('Bonne chance', 'Souhait de réussite', 'Expression', '🍀'),
('Mauvaise chance', 'Souhait négatif ou malchance', 'Expression', '🌧️'),
('Au revoir', 'Formule de salutation pour se séparer', 'Expression', '👋');

-- ========================================
-- AJOUT DE MOTS SUPPLÉMENTAIRES
-- ========================================

MERGE INTO mots (mot, def, categorie, emojie) KEY(mot) VALUES
-- Verbes supplémentaires pour synonymes
('Accroître', 'Augmenter en quantité ou en intensité', 'Verbe', '📊'),
('Réduire', 'Diminuer en quantité ou en intensité', 'Verbe', '⬇️'),
('Terminer', 'Mener à son terme, finir', 'Verbe', '🏁'),

-- Adjectifs supplémentaires
('Stupide', 'Qui manque d''intelligence', 'Adjectif', '🤪'),
('Affreux', 'Extrêmement laid ou désagréable', 'Adjectif', '👹'),

-- Noms supplémentaires
('Haine', 'Sentiment violent d''aversion', 'Nom', '😡'),
('Lâcheté', 'Manque de courage', 'Nom', '🏃‍♂️');

-- ========================================
-- RELATIONS DE SYNONYMIE
-- ========================================

-- H2 : Utiliser INSERT simple (les PRIMARY KEY géreront les doublons)
INSERT INTO mots_synonymes (mot_id, synonyme_id)
SELECT m1.id, m2.id
FROM mots m1
JOIN mots m2 ON m2.mot = 'Splendide'
WHERE m1.mot = 'Magnifique'
AND NOT EXISTS (
    SELECT 1 FROM mots_synonymes WHERE mot_id = m1.id AND synonyme_id = m2.id
);

INSERT INTO mots_synonymes (mot_id, synonyme_id)
SELECT m1.id, m2.id
FROM mots m1
JOIN mots m2 ON m2.mot = 'Accroître'
WHERE m1.mot = 'Augmenter'
AND NOT EXISTS (
    SELECT 1 FROM mots_synonymes WHERE mot_id = m1.id AND synonyme_id = m2.id
);

INSERT INTO mots_synonymes (mot_id, synonyme_id)
SELECT m1.id, m2.id
FROM mots m1
JOIN mots m2 ON m2.mot = 'Réduire'
WHERE m1.mot = 'Diminuer'
AND NOT EXISTS (
    SELECT 1 FROM mots_synonymes WHERE mot_id = m1.id AND synonyme_id = m2.id
);

-- ========================================
-- RELATIONS D'ANTONYMIE
-- ========================================

-- Antonymes de verbes (une relation à la fois)
INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Oublier'
WHERE m1.mot = 'Apprendre'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Détruire'
WHERE m1.mot = 'Créer'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Démolir'
WHERE m1.mot = 'Construire'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Diminuer'
WHERE m1.mot = 'Augmenter'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Terminer'
WHERE m1.mot = 'Commencer'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

-- Antonymes d'adjectifs
INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Horrible'
WHERE m1.mot = 'Magnifique'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Lent'
WHERE m1.mot = 'Rapide'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Petit'
WHERE m1.mot = 'Grand'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Triste'
WHERE m1.mot = 'Heureux'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

-- Antonymes de noms
INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Ignorance'
WHERE m1.mot = 'Connaissance'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Tristesse'
WHERE m1.mot = 'Joie'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Peur'
WHERE m1.mot = 'Courage'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

-- Antonymes d'expressions
INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Mauvaise chance'
WHERE m1.mot = 'Bonne chance'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

-- ========================================
-- RELATIONS D'ANTONYMIE SUPPLÉMENTAIRES
-- ========================================

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Stupide'
WHERE m1.mot = 'Intelligent'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Affreux'
WHERE m1.mot = 'Splendide'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Haine'
WHERE m1.mot = 'Amour'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);

INSERT INTO mots_antonymes (mot_id, antonyme_id)
SELECT m1.id, m2.id FROM mots m1 JOIN mots m2 ON m2.mot = 'Lâcheté'
WHERE m1.mot = 'Courage'
AND NOT EXISTS (SELECT 1 FROM mots_antonymes WHERE mot_id = m1.id AND antonyme_id = m2.id);