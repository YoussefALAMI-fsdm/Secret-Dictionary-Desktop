CREATE TABLE mots_antonymes (
    mot_id INT NOT NULL,
    antonyme_id INT NOT NULL,
    PRIMARY KEY (mot_id, antonyme_id),
    CONSTRAINT fk_mot_ant FOREIGN KEY (mot_id) REFERENCES mots(id) ON DELETE CASCADE,
    CONSTRAINT fk_ant FOREIGN KEY (antonyme_id) REFERENCES mots(id) ON DELETE CASCADE,
    CONSTRAINT chk_not_self_ant CHECK (mot_id <> antonyme_id)
);