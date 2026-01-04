package com.example.restaurant.service;

/**
 * Classe pour gérer les notifications des serveurs
 */
public class Notification {
    private String id;
    private String serverUsername;
    private String clientName;
    private String message;
    private long timestamp;
    private boolean read;
    
    public Notification(String serverUsername, String clientName) {
        this.id = "NOTIF-" + System.currentTimeMillis();
        this.serverUsername = serverUsername;
        this.clientName = clientName;
        this.message = "Venez à la table de " + clientName;
        this.timestamp = System.currentTimeMillis();
        this.read = false;
    }
    
    // Getters et Setters
    public String getId() { return id; }
    public String getServerUsername() { return serverUsername; }
    public String getClientName() { return clientName; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    
    @Override
    public String toString() {
        return "🔔 " + message;
    }
}
