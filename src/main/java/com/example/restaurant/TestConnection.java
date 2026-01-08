package com.example.restaurant;

import com.example.restaurant.storage.DatabaseStorage;
import com.example.restaurant.service.UserCredential;
import java.util.List;

/**
 * Programme de test pour vérifier la connexion MySQL
 */
public class TestConnection {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Test de Connexion MySQL - XAMPP");
        System.out.println("========================================");
        System.out.println();
        
        // Test 1: Connexion
        System.out.print("1. Test de connexion à MySQL... ");
        if (DatabaseStorage.testConnection()) {
            System.out.println("✅ OK");
        } else {
            System.out.println("❌ ECHEC");
            System.out.println();
            System.out.println("VERIFICATIONS:");
            System.out.println("  - XAMPP MySQL est-il démarré ?");
            System.out.println("  - La base restaurant_db existe-t-elle ?");
            System.out.println("  - Le driver MySQL JDBC est-il dans lib/ ?");
            return;
        }
        
        // Test 2: Tables
        System.out.print("2. Vérification des tables... ");
        DatabaseStorage.initializeTables();
        System.out.println("✅ OK");
        
        // Test 3: Lecture des utilisateurs
        System.out.print("3. Lecture des utilisateurs... ");
        List<UserCredential> users = DatabaseStorage.readUserCredentials();
        System.out.println("✅ OK (" + users.size() + " utilisateurs trouvés)");
        
        // Affichage des utilisateurs
        if (!users.isEmpty()) {
            System.out.println();
            System.out.println("Utilisateurs dans la base de données:");
            System.out.println("-------------------------------------");
            for (UserCredential user : users) {
                System.out.println("  - " + user.getUsername() + 
                                 " (" + user.getUserType() + ") - " + 
                                 user.getDisplayName());
            }
        }
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("  Tous les tests sont passés! ✅");
        System.out.println("========================================");
        System.out.println();
        System.out.println("Vous pouvez maintenant exécuter:");
        System.out.println("  run-db.bat");
    }
}
