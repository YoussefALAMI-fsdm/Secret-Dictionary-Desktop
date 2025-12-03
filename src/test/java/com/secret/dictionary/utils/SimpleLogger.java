package com.secret.dictionary.utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleLogger { // On creer un petit loger pour enregister les logs des test d'une facon permanante

    private final String filePath ; // chemin pour ecris les logs

    public SimpleLogger(String filePath , boolean reset ) {
        this.filePath = filePath;

        // Vérifie si le dossier existe, sinon le créer
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs(); // crée tous les dossiers nécessaires
            if (!created) {
                System.err.println("Impossible de créer le dossier : " + parentDir.getAbsolutePath());
            }
        }

        // Si reset = true, supprime le fichier existant pour démarrer propre
        if (reset && file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                System.err.println("Impossible de supprimer l'ancien fichier de log : " + filePath);
            }
        }
    }

    public void log(String message) {

        try ( BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) { // try with ressources
            // true => mode append, ajoute à la fin du fichier
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // Creer un LocalDateTime pour enregister les dates exact
            writer.write(timestamp + " - " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace(); // Au moins on sait si l'écriture a échoué
        }
    }

}
