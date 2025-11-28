package com.secret.dictionary.util;

import javafx.scene.text.Font;
import javafx.scene.control.Label;

/**
 * Utilitaire pour gérer l'affichage des emojis en couleurs natives
 */
public class EmojiUtils {

    private static Font emojiFont;

    static {
        // Charger une police qui supporte les emojis colorés
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            // Windows : Segoe UI Emoji
            emojiFont = Font.font("Segoe UI Emoji", 48);
        } else if (os.contains("mac")) {
            // macOS : Apple Color Emoji
            emojiFont = Font.font("Apple Color Emoji", 48);
        } else {
            // Linux : Noto Color Emoji
            emojiFont = Font.font("Noto Color Emoji", 48);
        }

        // Fallback si la police n'est pas trouvée
        if (emojiFont == null || emojiFont.getFamily().equals("System")) {
            emojiFont = Font.font("Segoe UI Symbol", 48);
        }

        System.out.println("✅ Police emoji chargée : " + emojiFont.getFamily());
    }

    /**
     * Configure un Label pour afficher correctement les emojis en couleurs
     */
    public static void configureEmojiLabel(Label label, double fontSize) {
        if (label == null) return;

        label.setFont(Font.font(emojiFont.getFamily(), fontSize));

        // Style CSS pour forcer l'affichage coloré natif
        label.setStyle(
                "-fx-font-family: '" + emojiFont.getFamily() + "';" +
                        "-fx-font-size: " + fontSize + "px;"
        );
    }

    /**
     * Retourne la police emoji appropriée pour la taille donnée
     */
    public static Font getEmojiFont(double size) {
        return Font.font(emojiFont.getFamily(), size);
    }

    /**
     * Retourne le nom de la famille de police emoji utilisée
     */
    public static String getEmojiFontFamily() {
        return emojiFont.getFamily();
    }
}