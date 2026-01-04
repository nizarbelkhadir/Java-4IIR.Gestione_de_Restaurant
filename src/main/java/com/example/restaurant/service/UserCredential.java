package com.example.restaurant.service;

/**
 * Classe pour stocker les credentials des utilisateurs
 */
public class UserCredential {
    private String username;
    private String password;
    private String userType; // ADMIN, SERVEUR, CUISINIER, CLIENT
    private String displayName;
    private String userId;
    
    public UserCredential() {}
    
    public UserCredential(String username, String password, String userType, 
                         String displayName, String userId) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.displayName = displayName;
        this.userId = userId;
    }
    
    // Getters et Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    @Override
    public String toString() {
        return userType + ": " + displayName + " (@" + username + ")";
    }
}
