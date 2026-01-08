package com.example.restaurant.service;

import com.example.restaurant.storage.DatabaseStorage;

import java.util.*;

/**
 * Service d'authentification et gestion des credentials
 * MISE À JOUR: Utilise MySQL via JDBC au lieu de fichiers JSON
 */
public class AuthenticationService {
    private Map<String, UserCredential> credentials;
    private UserCredential currentUser;
    
    public AuthenticationService() {
        credentials = new HashMap<>();
        // Tester la connexion à la base de données
        if (!DatabaseStorage.testConnection()) {
            System.err.println("ERREUR: Impossible de se connecter à la base de données MySQL!");
            System.err.println("Assurez-vous que XAMPP est démarré et que la base 'restaurant_db' existe.");
        }
        // Initialiser les tables et charger les credentials
        DatabaseStorage.initializeTables();
        loadCredentials();
    }
    
    /**
     * Charge les credentials depuis la base de données MySQL
     */
    private void loadCredentials() {
        List<UserCredential> list = DatabaseStorage.readUserCredentials();
        credentials.clear();
        for (UserCredential cred : list) {
            credentials.put(cred.getUsername(), cred);
        }
        
        if (credentials.isEmpty()) {
            System.out.println("Aucun utilisateur trouvé. Les données par défaut devraient être dans la base de données.");
        }
    }
    
    /**
     * Rafraîchir les credentials depuis la base de données
     */
    public void refreshCredentials() {
        loadCredentials();
    }
    
    /**
     * Authentifie un utilisateur (serveur/cuisinier/admin)
     * @return UserCredential si succès, null sinon
     */
    public UserCredential login(String username, String password) {
        UserCredential cred = credentials.get(username);
        if (cred != null && cred.getPassword().equals(password)) {
            currentUser = cred;
            return cred;
        }
        return null;
    }
    
    /**
     * Crée un client (juste avec nom, sans password)
     * @return UserCredential pour le client
     */
    public UserCredential loginAsClient(String clientName) {
        if (clientName == null || clientName.trim().isEmpty()) {
            return null;
        }
        
        // Créer credential temporaire pour le client (sans sauvegarder)
        String clientId = "CLI-" + UUID.randomUUID().toString().substring(0, 8);
        currentUser = new UserCredential(
            clientName.toLowerCase(),
            "", // Pas de password pour les clients
            "CLIENT",
            clientName,
            clientId
        );
        return currentUser;
    }
    
    /**
     * Créer un nouvel utilisateur (ADMIN seulement)
     * @return true si succès
     */
    public boolean createUser(String username, String password, String userType, String displayName) {
        if (currentUser == null || !currentUser.getUserType().equals("ADMIN")) {
            return false; // Seul admin peut créer des utilisateurs
        }
        
        if (credentials.containsKey(username)) {
            return false; // Username déjà existe
        }
        
        if (!userType.equals("SERVEUR") && !userType.equals("CUISINIER")) {
            return false; // Type invalide
        }
        
        String userId;
        if (userType.equals("SERVEUR")) {
            userId = "SRV-" + UUID.randomUUID().toString().substring(0, 8);
        } else {
            userId = "KIT-" + UUID.randomUUID().toString().substring(0, 8);
        }
        
        UserCredential newUser = new UserCredential(
            username,
            password,
            userType,
            displayName,
            userId
        );
        
        // Sauvegarder dans la base de données
        if (DatabaseStorage.addUserCredential(newUser)) {
            credentials.put(username, newUser);
            return true;
        }
        return false;
    }
    
    /**
     * Supprimer un utilisateur (ADMIN seulement)
     */
    public boolean deleteUser(String username) {
        if (currentUser == null || !currentUser.getUserType().equals("ADMIN")) {
            return false;
        }
        
        if (username.equals("admin")) {
            return false; // Ne peut pas supprimer l'admin
        }
        
        // Supprimer de la base de données
        if (DatabaseStorage.deleteUserCredential(username)) {
            credentials.remove(username);
            return true;
        }
        return false;
    }
    
    /**
     * Liste tous les utilisateurs (ADMIN seulement)
     */
    public List<UserCredential> listAllUsers() {
        if (currentUser == null || !currentUser.getUserType().equals("ADMIN")) {
            return new ArrayList<>();
        }
        return new ArrayList<>(credentials.values());
    }
    
    /**
     * Déconnexion
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Obtenir l'utilisateur connecté
     */
    public UserCredential getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Vérifier si connecté
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }
}
