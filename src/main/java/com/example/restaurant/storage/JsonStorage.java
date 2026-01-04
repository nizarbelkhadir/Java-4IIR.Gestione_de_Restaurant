package com.example.restaurant.storage;

import com.example.restaurant.service.UserCredential;
import com.example.restaurant.model.Order;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage {
    
    /**
     * Lire les credentials depuis le fichier JSON
     */
    public static List<UserCredential> readUserCredentials(File file) {
        List<UserCredential> list = new ArrayList<>();
        if (!file.exists()) {
            return list;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().equals("[") || line.trim().equals("]")) {
                    continue;
                }
                
                // Parse simple JSON manually
                UserCredential cred = parseUserCredential(line);
                if (cred != null) {
                    list.add(cred);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lecture users.json: " + e.getMessage());
        }
        
        return list;
    }
    
    /**
     * Écrire les credentials dans le fichier JSON
     */
    public static void writeUserCredentials(File file, List<UserCredential> credentials) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("[");
            for (int i = 0; i < credentials.size(); i++) {
                UserCredential cred = credentials.get(i);
                writer.print("  {");
                writer.print("\"username\":\"" + escapeJson(cred.getUsername()) + "\",");
                writer.print("\"password\":\"" + escapeJson(cred.getPassword()) + "\",");
                writer.print("\"userType\":\"" + escapeJson(cred.getUserType()) + "\",");
                writer.print("\"displayName\":\"" + escapeJson(cred.getDisplayName()) + "\",");
                writer.print("\"userId\":\"" + escapeJson(cred.getUserId()) + "\"");
                writer.print("}");
                if (i < credentials.size() - 1) {
                    writer.print(",");
                }
                writer.println();
            }
            writer.println("]");
        } catch (IOException e) {
            System.err.println("Erreur écriture users.json: " + e.getMessage());
        }
    }
    
    /**
     * Lire les commandes depuis le fichier JSON
     */
    public static List<OrderData> readOrders(File file) {
        List<OrderData> list = new ArrayList<>();
        if (!file.exists()) {
            return list;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().equals("[") || line.trim().equals("]")) {
                    continue;
                }
                
                OrderData order = parseOrder(line);
                if (order != null) {
                    list.add(order);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lecture orders.json: " + e.getMessage());
        }
        
        return list;
    }
    
    /**
     * Écrire les commandes dans le fichier JSON
     */
    public static void writeOrders(File file, List<OrderData> orders) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("[");
            for (int i = 0; i < orders.size(); i++) {
                OrderData order = orders.get(i);
                writer.print("  {");
                writer.print("\"orderId\":\"" + escapeJson(order.orderId) + "\",");
                writer.print("\"clientName\":\"" + escapeJson(order.clientName) + "\",");
                writer.print("\"items\":\"" + escapeJson(order.items) + "\",");
                writer.print("\"status\":\"" + escapeJson(order.status) + "\",");
                writer.print("\"timestamp\":\"" + order.timestamp + "\"");
                writer.print("}");
                if (i < orders.size() - 1) {
                    writer.print(",");
                }
                writer.println();
            }
            writer.println("]");
        } catch (IOException e) {
            System.err.println("Erreur écriture orders.json: " + e.getMessage());
        }
    }
    
    // Méthodes utilitaires de parsing
    
    private static UserCredential parseUserCredential(String json) {
        try {
            UserCredential cred = new UserCredential();
            
            String username = extractValue(json, "username");
            String password = extractValue(json, "password");
            String userType = extractValue(json, "userType");
            String displayName = extractValue(json, "displayName");
            String userId = extractValue(json, "userId");
            
            if (username != null && userType != null) {
                cred.setUsername(username);
                cred.setPassword(password != null ? password : "");
                cred.setUserType(userType);
                cred.setDisplayName(displayName != null ? displayName : username);
                cred.setUserId(userId != null ? userId : "");
                return cred;
            }
        } catch (Exception e) {
            System.err.println("Erreur parsing credential: " + e.getMessage());
        }
        return null;
    }
    
    private static OrderData parseOrder(String json) {
        try {
            OrderData order = new OrderData();
            
            order.orderId = extractValue(json, "orderId");
            order.clientName = extractValue(json, "clientName");
            order.items = extractValue(json, "items");
            order.status = extractValue(json, "status");
            String timestamp = extractValue(json, "timestamp");
            order.timestamp = timestamp != null ? Long.parseLong(timestamp) : 0;
            
            return order;
        } catch (Exception e) {
            System.err.println("Erreur parsing order: " + e.getMessage());
        }
        return null;
    }
    
    private static String extractValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        
        start += search.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        
        return json.substring(start, end);
    }
    
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
    
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
}
