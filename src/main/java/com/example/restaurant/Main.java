package com.example.restaurant;

import com.example.restaurant.model.*;
import com.example.restaurant.service.*;

import java.util.Arrays;
import java.util.List;

/**
 * Démo complète du système de gestion de restaurant.
 * 
 * Démonstrations:
 * 1. Self-service (client commande directement)
 * 2. Appel serveur (serveur prend commande et l'envoie à la cuisine)
 * 3. Gestion automatique des serveurs occupés (Semaphore + streams)
 * 4. Préparation parallèle en cuisine (thread pool)
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("════════════════════════════════════════");
        System.out.println("   RESTAURANT MANAGEMENT SYSTEM DEMO");
        System.out.println("════════════════════════════════════════\n");
        
        // ===== INITIALISATION =====
        System.out.println("--- Initialisation des services ---");
        
        // Service cuisine (avec thread pool pour cuisiniers)
        KitchenService kitchen = new KitchenService();
        
        // Service commandes
        OrderService orderService = new OrderService(kitchen);
        
        // Création des serveurs
        ServerStaff alice = new ServerStaff("s1", "Alice");
        ServerStaff bob = new ServerStaff("s2", "Bob");
        List<ServerStaff> serverList = Arrays.asList(alice, bob);
        System.out.println("✓ Serveurs créés: Alice, Bob");
        
        // Gestionnaire de serveurs (avec Semaphore et streams)
        ServerManager serverManager = new ServerManager(serverList, orderService);
        
        // Menu du restaurant
        MenuItem pasta = new MenuItem("m1", "Pasta Carbonara", 12.5);
        MenuItem salad = new MenuItem("m2", "Caesar Salad", 8.0);
        MenuItem pizza = new MenuItem("m3", "Pizza Margherita", 11.0);
        System.out.println("✓ Menu initialisé\n");
        
        // Démo Admin: créer des users (CRUD)
        UserService userService = new UserService();
        Admin admin = new Admin("a1", "Admin-Pierre");
        userService.addUser(admin);
        userService.addUser(alice);
        userService.addUser(bob);
        System.out.println("✓ Admin created users: " + userService.list().size() + " users in system\n");
        
        // ===== SCÉNARIOS =====
        System.out.println("════════════════════════════════════════");
        System.out.println("   SCÉNARIO 1: SELF-SERVICE");
        System.out.println("════════════════════════════════════════");
        
        Client client1 = new Client("c1", "Marie");
        serverManager.requestService(
            new ServerManager.ClientRequest(
                client1.getName(), 
                Arrays.asList(pasta, salad), 
                true  // SELF-SERVICE
            )
        );
        
        Thread.sleep(500);
        
        System.out.println("\n════════════════════════════════════════");
        System.out.println("   SCÉNARIO 2: APPEL SERVEUR");
        System.out.println("════════════════════════════════════════");
        
        Client client2 = new Client("c2", "Jean");
        serverManager.requestService(
            new ServerManager.ClientRequest(
                client2.getName(), 
                Arrays.asList(pizza), 
                false  // DEMANDE SERVEUR
            )
        );
        
        Thread.sleep(1000);
        
        System.out.println("\n════════════════════════════════════════");
        System.out.println("   SCÉNARIO 3: TOUS SERVEURS OCCUPÉS");
        System.out.println("════════════════════════════════════════");
        System.out.println("Envoi de 5 demandes simultanées (2 serveurs seulement)");
        System.out.println("→ Les 2 premiers sont servis immédiatement");
        System.out.println("→ Les autres attendent le premier serveur libre\n");
        
        for (int i = 3; i <= 7; i++) {
            Client c = new Client("c" + i, "Client-" + i);
            boolean selfServ = (i % 3 == 0); // Certains en self-service
            serverManager.requestService(
                new ServerManager.ClientRequest(
                    c.getName(), 
                    Arrays.asList(pasta), 
                    selfServ
                )
            );
            Thread.sleep(100); // Petit délai entre les demandes
        }
        
        // Laisse le système traiter toutes les commandes
        System.out.println("\n--- Traitement des commandes en cours... ---\n");
        Thread.sleep(8000);
        
        System.out.println("\n════════════════════════════════════════");
        System.out.println("   RÉSUMÉ FINAL");
        System.out.println("════════════════════════════════════════");
        List<Order> allOrders = orderService.list();
        System.out.println("Total commandes créées: " + allOrders.size());
        
        // Utilisation de STREAM + LAMBDA pour compter les commandes prêtes
        long readyCount = allOrders.stream()
            .filter(o -> o.getStatus() == Order.Status.READY)
            .count();
        
        System.out.println("Commandes prêtes: " + readyCount);
        System.out.println("\n✅ DEMO TERMINÉE\n");
        
        System.exit(0);
    }
}
