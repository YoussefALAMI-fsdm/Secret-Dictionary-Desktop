CREATE TABLE IF NOT EXISTS mots_synonymes (
    mot_id INT NOT NULL,
    synonyme_id INT NOT NULL,
    PRIMARY KEY (mot_id, synonyme_id),
    CONSTRAINT fk_mot FOREIGN KEY (mot_id) REFERENCES mots(id) ON DELETE CASCADE, -- toutes les lignes de la table enfant qui font référence
    CONSTRAINT fk_synonyme FOREIGN KEY (synonyme_id) REFERENCES mots(id) ON DELETE CASCADE, -- à la ligne supprimée dans la table parent seront automatiquement supprimées.
    CONSTRAINT chk_not_self CHECK (mot_id <> synonyme_id)
);