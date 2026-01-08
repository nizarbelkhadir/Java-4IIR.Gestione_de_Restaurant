package com.example.restaurant.storage;

import com.example.restaurant.service.UserCredential;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour gérer le stockage des données dans MySQL via JDBC
 * Remplace JsonStorage pour utiliser une vraie base de données
 */
public class DatabaseStorage {
    
    /**
     * Obtenir une connexion à la base de données
     */
    private static Connection getConnection() throws SQLException {
        try {
            Class.forName(DatabaseConfig.getDbDriver());
            return DriverManager.getConnection(
                DatabaseConfig.getDbUrl(),
                DatabaseConfig.getDbUser(),
                DatabaseConfig.getDbPassword()
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL JDBC introuvable. Assurez-vous que mysql-connector-j est dans le classpath.", e);
        }
    }
    
    /**
     * Tester la connexion à la base de données
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            return false;
        }
    }
    
    // ========== GESTION DES UTILISATEURS ==========
    
    /**
     * Lire tous les utilisateurs depuis la base de données
     */
    public static List<UserCredential> readUserCredentials() {
        List<UserCredential> credentials = new ArrayList<>();
        String sql = "SELECT user_id, username, password, user_type, display_name FROM users";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                UserCredential cred = new UserCredential();
                cred.setUserId(rs.getString("user_id"));
                cred.setUsername(rs.getString("username"));
                cred.setPassword(rs.getString("password"));
                cred.setUserType(rs.getString("user_type"));
                cred.setDisplayName(rs.getString("display_name"));
                credentials.add(cred);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lecture users depuis MySQL: " + e.getMessage());
            e.printStackTrace();
        }
        
        return credentials;
    }
    
    /**
     * Ajouter un nouvel utilisateur
     */
    public static boolean addUserCredential(UserCredential credential) {
        String sql = "INSERT INTO users (user_id, username, password, user_type, display_name) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, credential.getUserId());
            pstmt.setString(2, credential.getUsername());
            pstmt.setString(3, credential.getPassword());
            pstmt.setString(4, credential.getUserType());
            pstmt.setString(5, credential.getDisplayName());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur ajout user dans MySQL: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Mettre à jour un utilisateur existant
     */
    public static boolean updateUserCredential(UserCredential credential) {
        String sql = "UPDATE users SET password = ?, user_type = ?, display_name = ? WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, credential.getPassword());
            pstmt.setString(2, credential.getUserType());
            pstmt.setString(3, credential.getDisplayName());
            pstmt.setString(4, credential.getUsername());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour user dans MySQL: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprimer un utilisateur
     */
    public static boolean deleteUserCredential(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur suppression user dans MySQL: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Trouver un utilisateur par nom d'utilisateur
     */
    public static UserCredential findUserByUsername(String username) {
        String sql = "SELECT user_id, username, password, user_type, display_name FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                UserCredential cred = new UserCredential();
                cred.setUserId(rs.getString("user_id"));
                cred.setUsername(rs.getString("username"));
                cred.setPassword(rs.getString("password"));
                cred.setUserType(rs.getString("user_type"));
                cred.setDisplayName(rs.getString("display_name"));
                return cred;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur recherche user dans MySQL: " + e.getMessage());
        }
        
        return null;
    }
    
    // ========== GESTION DES COMMANDES ==========
    
    /**
     * Classe pour stocker les données de commande
     */
    public static class OrderData {
        public String orderId;
        public String clientName;
        public String items;
        public String status;
        public long timestamp;
        
        public OrderData() {}
    }
    
    /**
     * Lire toutes les commandes depuis la base de données
     */
    public static List<OrderData> readOrders() {
        List<OrderData> orders = new ArrayList<>();
        String sql = "SELECT order_id, client_name, items, status, timestamp FROM orders ORDER BY timestamp DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                OrderData order = new OrderData();
                order.orderId = rs.getString("order_id");
                order.clientName = rs.getString("client_name");
                order.items = rs.getString("items");
                order.status = rs.getString("status");
                order.timestamp = rs.getLong("timestamp");
                orders.add(order);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lecture orders depuis MySQL: " + e.getMessage());
        }
        
        return orders;
    }
    
    /**
     * Ajouter une nouvelle commande
     */
    public static boolean addOrder(OrderData order) {
        String sql = "INSERT INTO orders (order_id, client_name, items, status, timestamp) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, order.orderId);
            pstmt.setString(2, order.clientName);
            pstmt.setString(3, order.items);
            pstmt.setString(4, order.status);
            pstmt.setLong(5, order.timestamp);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur ajout order dans MySQL: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Mettre à jour le statut d'une commande
     */
    public static boolean updateOrderStatus(String orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setString(2, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour order status dans MySQL: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprimer une commande
     */
    public static boolean deleteOrder(String orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, orderId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur suppression order dans MySQL: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Initialiser les tables de la base de données (si nécessaire)
     */
    public static void initializeTables() {
        // Cette méthode peut être appelée au démarrage pour vérifier que les tables existent
        try (Connection conn = getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            
            // Vérifier si la table users existe
            ResultSet rsUsers = meta.getTables(null, null, "users", new String[]{"TABLE"});
            if (!rsUsers.next()) {
                System.err.println("ATTENTION: La table 'users' n'existe pas. Veuillez exécuter schema.sql dans phpMyAdmin.");
            }
            
            // Vérifier si la table orders existe
            ResultSet rsOrders = meta.getTables(null, null, "orders", new String[]{"TABLE"});
            if (!rsOrders.next()) {
                System.err.println("ATTENTION: La table 'orders' n'existe pas. Veuillez exécuter schema.sql dans phpMyAdmin.");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur vérification tables: " + e.getMessage());
        }
    }
}
