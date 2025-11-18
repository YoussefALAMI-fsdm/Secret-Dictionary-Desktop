package com.secret.dictionary.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static DataBase instance;
    private Connection connection;

    // ✅ Lire depuis les variables d'environnement ou valeurs par défaut
    private static final String URL = System.getenv().getOrDefault(
            "DB_URL",
            "jdbc:postgresql://localhost:5432/dictionary"
    );
    private static final String USER = System.getenv().getOrDefault("DB_USER", "FSDM");
    private static final String PASSWD = System.getenv().getOrDefault("DB_PASSWORD", "IA");

    private DataBase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWD);
            System.out.println("✅ Connexion à la base de données réussie !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données !");
            e.printStackTrace();
            throw new RuntimeException("Impossible de se connecter à la base de données", e);
        }
    }

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getURL() {
        return URL;
    }

    public String getPASSWD() {
        return PASSWD;
    }

    public String getUSER() {
        return USER;
    }
}