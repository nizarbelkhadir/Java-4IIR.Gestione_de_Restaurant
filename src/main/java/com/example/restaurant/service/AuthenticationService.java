package com.example.restaurant.service;

import com.example.restaurant.model.*;
import com.example.restaurant.storage.JsonStorage;

import java.io.File;
import java.util.*;

/**
 * Service d'authentification et gestion des credentials
 */
public class AuthenticationService {
    private static final String USERS_FILE = "users.json";
    private Map<String, UserCredential> credentials;
    private UserCredential currentUser;
    
    public AuthenticationService() {
        credentials = new HashMap<>();
        loadCredentials();
        initializeDefaultAdmin();
    }
    
    /**
     * Charge les credentials depuis le fichier JSON
     */
    private void loadCredentials() {
        File file = new File(USERS_FILE);
        if (file.exists()) {
            List<UserCredential> list = JsonStorage.readUserCredentials(file);
            for (UserCredential cred : list) {
                credentials.put(cred.getUsername(), cred);
            }
        }
    }
    
    /**
     * Sauvegarde les credentials dans le fichier JSON
     */
    private void saveCredentials() {
        File file = new File(USERS_FILE);
        List<UserCredential> list = new ArrayList<>(credentials.values());
        JsonStorage.writeUserCredentials(file, list);
    }
    
    /**
     * Initialise l'admin par défaut si n'existe pas
     */
    private void initializeDefaultAdmin() {
        if (!credentials.containsKey("admin")) {
            UserCredential admin = new UserCredential(
                "admin",
                "admin123",
                "ADMIN",
                "Admin",
                "ADM-001"
            );
            credentials.put("admin", admin);
            saveCredentials();
        }
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
        
        credentials.put(username, newUser);
        saveCredentials();
        return true;
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
        
        credentials.remove(username);
        saveCredentials();
        return true;
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
