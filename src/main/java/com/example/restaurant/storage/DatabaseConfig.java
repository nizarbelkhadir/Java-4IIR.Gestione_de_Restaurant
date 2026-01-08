package com.example.restaurant.storage;

/**
 * Configuration de la connexion à la base de données MySQL
 * Utilisé avec XAMPP/phpMyAdmin
 */
public class DatabaseConfig {
    
    // Configuration par défaut pour XAMPP
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Par défaut vide pour XAMPP
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    /**
     * Obtenir l'URL de connexion à la base de données
     */
    public static String getDbUrl() {
        return DB_URL;
    }
    
    /**
     * Obtenir le nom d'utilisateur de la base de données
     */
    public static String getDbUser() {
        return DB_USER;
    }
    
    /**
     * Obtenir le mot de passe de la base de données
     */
    public static String getDbPassword() {
        return DB_PASSWORD;
    }
    
    /**
     * Obtenir le driver JDBC
     */
    public static String getDbDriver() {
        return DB_DRIVER;
    }
    
    /**
     * Configuration personnalisée (optionnel)
     */
    public static class Builder {
        private String url = DB_URL;
        private String user = DB_USER;
        private String password = DB_PASSWORD;
        
        public Builder url(String url) {
            this.url = url;
            return this;
        }
        
        public Builder user(String user) {
            this.user = user;
            return this;
        }
        
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        
        public DatabaseConfig build() {
            return new DatabaseConfig();
        }
    }
}
