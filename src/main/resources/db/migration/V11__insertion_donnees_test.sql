-- ================================================================
-- SCRIPT V11 : Insertion de données de test complètes
-- 30 mots avec définitions, catégories, emojis, synonymes et antonymes
-- ================================================================

-- ========================================
-- INSERTION DES MOTS
-- ========================================

INSERT INTO mots (mot, def, categorie, emojie) VALUES
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
('Au revoir', 'Formule de salutation pour se séparer', 'Expression', '👋')
ON CONFLICT (mot) DO NOTHING;

-- ========================================
-- AJOUT DE MOTS SUPPLÉMENTAIRES (AVANT LES RELATIONS)
-- ========================================

INSERT INTO mots (mot, def, categorie, emojie) VALUES
-- Verbes supplémentaires pour synonymes
('Accroître', 'Augmenter en quantité ou en intensité', 'Verbe', '📊'),
('Réduire', 'Diminuer en quantité ou en intensité', 'Verbe', '⬇️'),
('Terminer', 'Mener à son terme, finir', 'Verbe', '🏁'),

-- Adjectifs supplémentaires
('Stupide', 'Qui manque d''intelligence', 'Adjectif', '🤪'),
('Affreux', 'Extrêmement laid ou désagréable', 'Adjectif', '👹'),

-- Noms supplémentaires
('Haine', 'Sentiment violent d''aversion', 'Nom', '😡'),
('Lâcheté', 'Manque de courage', 'Nom', '🏃‍♂️')
ON CONFLICT (mot) DO NOTHING;

-- ========================================
-- RELATIONS DE SYNONYMIE
-- ========================================

INSERT INTO mots_synonymes (mot_id, synonyme_id) VALUES
((SELECT id FROM mots WHERE mot = 'Magnifique'), (SELECT id FROM mots WHERE mot = 'Splendide')),
((SELECT id FROM mots WHERE mot = 'Augmenter'), (SELECT id FROM mots WHERE mot = 'Accroître')),
((SELECT id FROM mots WHERE mot = 'Diminuer'), (SELECT id FROM mots WHERE mot = 'Réduire'))
ON CONFLICT DO NOTHING;

-- ========================================
-- RELATIONS D'ANTONYMIE
-- ========================================

-- Antonymes de verbes
INSERT INTO mots_antonymes (mot_id, antonyme_id) VALUES
((SELECT id FROM mots WHERE mot = 'Apprendre'), (SELECT id FROM mots WHERE mot = 'Oublier')),
((SELECT id FROM mots WHERE mot = 'Créer'), (SELECT id FROM mots WHERE mot = 'Détruire')),
((SELECT id FROM mots WHERE mot = 'Construire'), (SELECT id FROM mots WHERE mot = 'Démolir')),
((SELECT id FROM mots WHERE mot = 'Augmenter'), (SELECT id FROM mots WHERE mot = 'Diminuer')),
((SELECT id FROM mots WHERE mot = 'Commencer'), (SELECT id FROM mots WHERE mot = 'Terminer')),

-- Antonymes d'adjectifs
((SELECT id FROM mots WHERE mot = 'Magnifique'), (SELECT id FROM mots WHERE mot = 'Horrible')),
((SELECT id FROM mots WHERE mot = 'Rapide'), (SELECT id FROM mots WHERE mot = 'Lent')),
((SELECT id FROM mots WHERE mot = 'Grand'), (SELECT id FROM mots WHERE mot = 'Petit')),
((SELECT id FROM mots WHERE mot = 'Heureux'), (SELECT id FROM mots WHERE mot = 'Triste')),

-- Antonymes de noms
((SELECT id FROM mots WHERE mot = 'Connaissance'), (SELECT id FROM mots WHERE mot = 'Ignorance')),
((SELECT id FROM mots WHERE mot = 'Joie'), (SELECT id FROM mots WHERE mot = 'Tristesse')),
((SELECT id FROM mots WHERE mot = 'Courage'), (SELECT id FROM mots WHERE mot = 'Peur')),

-- Antonymes d'expressions
((SELECT id FROM mots WHERE mot = 'Bonne chance'), (SELECT id FROM mots WHERE mot = 'Mauvaise chance'))
ON CONFLICT DO NOTHING;

-- ========================================
-- RELATIONS D'ANTONYMIE SUPPLÉMENTAIRES
-- ========================================

-- Antonymes supplémentaires
INSERT INTO mots_antonymes (mot_id, antonyme_id) VALUES
((SELECT id FROM mots WHERE mot = 'Intelligent'), (SELECT id FROM mots WHERE mot = 'Stupide')),
((SELECT id FROM mots WHERE mot = 'Splendide'), (SELECT id FROM mots WHERE mot = 'Affreux')),
((SELECT id FROM mots WHERE mot = 'Amour'), (SELECT id FROM mots WHERE mot = 'Haine')),
((SELECT id FROM mots WHERE mot = 'Courage'), (SELECT id FROM mots WHERE mot = 'Lâcheté'))
ON CONFLICT DO NOTHING;

-- ========================================
-- VÉRIFICATION DES DONNÉES INSÉRÉES
-- ========================================

-- Afficher le nombre total de mots
DO $$
DECLARE
    total_mots INT;
    total_synonymes INT;
    total_antonymes INT;
BEGIN
    SELECT COUNT(*) INTO total_mots FROM mots;
    SELECT COUNT(*) INTO total_synonymes FROM mots_synonymes;
    SELECT COUNT(*) INTO total_antonymes FROM mots_antonymes;

    RAISE NOTICE '====================================';
    RAISE NOTICE 'RÉSUMÉ DE L''INSERTION';
    RAISE NOTICE '====================================';
    RAISE NOTICE 'Total de mots insérés: %', total_mots;
    RAISE NOTICE 'Total de relations de synonymes: %', total_synonymes;
    RAISE NOTICE 'Total de relations d''antonymes: %', total_antonymes;
    RAISE NOTICE '====================================';
END $$;