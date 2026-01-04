package com.example.restaurant;

import com.example.restaurant.model.*;
import com.example.restaurant.service.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Application interactive de gestion de restaurant avec authentification
 */
public class InteractiveMain {
    private static Scanner scanner = new Scanner(System.in);
    private static KitchenService kitchen;
    private static OrderService orderService;
    private static ServerManager serverManager;
    private static UserService userService;
    private static AuthenticationService authService;
    private static List<ServerStaff> serverList;
    private static List<MenuItem> menu;
    private static UserCredential currentUser = null;

    public static void main(String[] args) {
        initializeSystem();
        showWelcome();
        
        while (true) {
            if (currentUser == null) {
                loginMenu();
            } else {
                switch (currentUser.getUserType()) {
                    case "ADMIN":
                        adminMenu();
                        break;
                    case "CLIENT":
                        clientMenu();
                        break;
                    case "SERVEUR":
                        serverMenu();
                        break;
                    case "CUISINIER":
                        kitchenMenu();
                        break;
                    default:
                        authService.logout();
                        currentUser = null;
                }
            }
        }
    }

    private static void initializeSystem() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  🍽️  SYSTÈME DE GESTION DE RESTAURANT");
        System.out.println("═══════════════════════════════════════════════════\n");
        System.out.println("Initialisation...");
        
        kitchen = new KitchenService();
        orderService = new OrderService(kitchen);
        userService = new UserService();
        authService = new AuthenticationService();
        
        // Initialiser liste vide de serveurs
        serverList = new ArrayList<>();
        
        serverManager = new ServerManager(serverList, orderService);
        
        // Menu du restaurant
        menu = new ArrayList<>();
        menu.add(new MenuItem("m1", "Pasta Carbonara", 12.5));
        menu.add(new MenuItem("m2", "Pizza Margherita", 11.0));
        menu.add(new MenuItem("m3", "Caesar Salad", 8.0));
        menu.add(new MenuItem("m4", "Lasagna", 13.5));
        menu.add(new MenuItem("m5", "Tiramisu", 6.5));
        
        System.out.println("✓ Système initialisé");
        System.out.println("✓ Admin par défaut: username=admin, password=admin123\n");
    }

    private static void showWelcome() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║        BIENVENUE AU RESTAURANT LA BELLA          ║");
        System.out.println("╚═══════════════════════════════════════════════════╝\n");
    }

    private static void loginMenu() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("          CONNEXION");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("1. Se connecter comme ADMIN");
        System.out.println("2. Se connecter comme SERVEUR");
        System.out.println("3. Se connecter comme CUISINIER");
        System.out.println("4. Entrer comme CLIENT");
        System.out.println("5. Voir les statistiques");
        System.out.println("0. Quitter");
        System.out.println("═══════════════════════════════════════════════════");
        
        System.out.print("\nVotre choix: ");
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                loginAsStaff("ADMIN");
                break;
            case "2":
                loginAsStaff("SERVEUR");
                break;
            case "3":
                loginAsStaff("CUISINIER");
                break;
            case "4":
                loginAsClient();
                break;
            case "5":
                showStatistics();
                break;
            case "0":
                System.out.println("\n👋 Au revoir!");
                System.exit(0);
                break;
            default:
                System.out.println("\n❌ Choix invalide");
        }
    }
    
    private static void loginAsStaff(String expectedType) {
        System.out.println("\n--- Connexion " + expectedType + " ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        currentUser = authService.login(username, password);
        
        if (currentUser == null) {
            System.out.println("❌ Identifiants incorrects!");
            return;
        }
        
        if (!currentUser.getUserType().equals(expectedType)) {
            System.out.println("❌ Vous n'avez pas les droits " + expectedType);
            authService.logout();
            currentUser = null;
            return;
        }
        
        System.out.println("\n✓ Connecté comme " + currentUser.getDisplayName() + " (" + expectedType + ")");
    }
    
    private static void loginAsClient() {
        System.out.println("\n--- Bienvenue Client ---");
        System.out.print("Votre nom: ");
        String name = scanner.nextLine();
        
        if (name == null || name.trim().isEmpty()) {
            System.out.println("❌ Nom invalide");
            return;
        }
        
        currentUser = authService.loginAsClient(name);
        System.out.println("\n✓ Bienvenue " + currentUser.getDisplayName() + "!");
    }

    private static void adminMenu() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("          MENU ADMIN - " + currentUser.getDisplayName());
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("1. Créer un utilisateur (Serveur/Cuisinier)");
        System.out.println("2. Lister tous les utilisateurs");
        System.out.println("3. Supprimer un utilisateur");
        System.out.println("4. Voir statistiques");
        System.out.println("0. Se déconnecter");
        System.out.println("═══════════════════════════════════════════════════");
        
        System.out.print("\nVotre choix: ");
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                createUser();
                break;
            case "2":
                listUsers();
                break;
            case "3":
                deleteUser();
                break;
            case "4":
                showStatistics();
                break;
            case "0":
                authService.logout();
                currentUser = null;
                System.out.println("\n✓ Déconnecté");
                break;
            default:
                System.out.println("\n❌ Choix invalide");
        }
    }

    private static void clientMenu() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("          MENU CLIENT - " + currentUser.getDisplayName());
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("1. Commander en SELF-SERVICE (rapide)");
        System.out.println("2. Appeler un SERVEUR (service à table)");
        System.out.println("3. Voir le menu");
        System.out.println("4. Voir mes commandes");
        System.out.println("0. Se déconnecter");
        System.out.println("═══════════════════════════════════════════════════");
        
        System.out.print("\nVotre choix: ");
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                placeOrderSelfService();
                break;
            case "2":
                callServer();
                break;
            case "3":
                showMenu();
                break;
            case "4":
                showMyOrders();
                break;
            case "0":
                authService.logout();
                currentUser = null;
                System.out.println("\n✓ Déconnecté");
                break;
            default:
                System.out.println("\n❌ Choix invalide");
        }
    }

    private static void serverMenu() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("          MENU SERVEUR - " + currentUser.getDisplayName());
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("1. Voir les notifications");
        System.out.println("2. Voir état des serveurs");
        System.out.println("3. Voir toutes les commandes");
        System.out.println("0. Se déconnecter");
        System.out.println("═══════════════════════════════════════════════════");
        
        System.out.print("\nVotre choix: ");
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                showNotifications();
                break;
            case "2":
                showServerStatus();
                break;
            case "3":
                showAllOrders();
                break;
            case "0":
                authService.logout();
                currentUser = null;
                System.out.println("\n✓ Déconnecté");
                break;
            default:
                System.out.println("\n❌ Choix invalide");
        }
    }

    private static void kitchenMenu() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("          MENU CUISINE - " + currentUser.getDisplayName());
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("1. Voir commandes en préparation");
        System.out.println("2. Voir toutes les commandes");
        System.out.println("3. Statistiques cuisine");
        System.out.println("0. Se déconnecter");
        System.out.println("═══════════════════════════════════════════════════");
        
        System.out.print("\nVotre choix: ");
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                showOrdersInProgress();
                break;
            case "2":
                showAllOrders();
                break;
            case "3":
                showStatistics();
                break;
            case "0":
                authService.logout();
                currentUser = null;
                System.out.println("\n✓ Déconnecté");
                break;
            default:
                System.out.println("\n❌ Choix invalide");
        }
    }

    private static void createUser() {
        System.out.println("\n--- Créer un utilisateur ---");
        System.out.println("Type: 1=Serveur, 2=Cuisinier");
        System.out.print("Type: ");
        String type = scanner.nextLine();
        
        String userType;
        if (type.equals("1")) {
            userType = "SERVEUR";
        } else if (type.equals("2")) {
            userType = "CUISINIER";
        } else {
            System.out.println("❌ Type invalide");
            return;
        }
        
        System.out.print("Username (login): ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Nom complet: ");
        String displayName = scanner.nextLine();
        
        boolean success = authService.createUser(username, password, userType, displayName);
        
        if (success) {
            System.out.println("✓ Utilisateur créé: " + displayName + " (" + userType + ")");
            
            // Ajouter au serverList si c'est un serveur
            if (userType.equals("SERVEUR")) {
                ServerStaff newServer = new ServerStaff(username, displayName);
                serverList.add(newServer);
                System.out.println("  → Ajouté à la liste des serveurs actifs");
            }
        } else {
            System.out.println("❌ Erreur: username déjà existant ou type invalide");
        }
    }

    private static void listUsers() {
        System.out.println("\n--- Liste des utilisateurs ---");
        List<UserCredential> users = authService.listAllUsers();
        
        if (users.isEmpty()) {
            System.out.println("Aucun utilisateur");
            return;
        }
        
        // Grouper par type
        Map<String, List<UserCredential>> byType = users.stream()
            .collect(Collectors.groupingBy(UserCredential::getUserType));
        
        byType.forEach((type, list) -> {
            System.out.println("\n" + type + ":");
            list.forEach(u -> System.out.println("  - @" + u.getUsername() + 
                " → " + u.getDisplayName() + " [" + u.getUserId() + "]"));
        });
    }

    private static void deleteUser() {
        System.out.print("\nUsername de l'utilisateur à supprimer: ");
        String username = scanner.nextLine();
        
        boolean success = authService.deleteUser(username);
        
        if (success) {
            System.out.println("✓ Utilisateur supprimé");
            
            // Retirer de serverList si nécessaire
            serverList.removeIf(s -> s.getId().equals(username));
        } else {
            System.out.println("❌ Impossible de supprimer (n'existe pas ou est admin)");
        }
    }

    private static void showMenu() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║                   MENU                            ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.get(i);
            System.out.printf("%d. %-25s %.2f€%n", i + 1, item.getName(), item.getPrice());
        }
    }

    private static void placeOrderSelfService() {
        showMenu();
        System.out.println("\nEntrez les numéros des plats (séparés par des espaces):");
        System.out.print("Choix: ");
        String input = scanner.nextLine();
        
        List<MenuItem> items = new ArrayList<>();
        try {
            String[] numbers = input.split(" ");
            for (String num : numbers) {
                int index = Integer.parseInt(num.trim()) - 1;
                if (index >= 0 && index < menu.size()) {
                    items.add(menu.get(index));
                }
            }
            
            if (items.isEmpty()) {
                System.out.println("❌ Aucun plat sélectionné");
                return;
            }
            
            // Self-service
            serverManager.requestService(
                new ServerManager.ClientRequest(currentUser.getDisplayName(), items, true)
            );
            
            double total = items.stream().mapToDouble(MenuItem::getPrice).sum();
            System.out.println("\n✓ Commande passée en self-service!");
            System.out.printf("Total: %.2f€%n", total);
            
        } catch (Exception e) {
            System.out.println("❌ Erreur dans la sélection");
        }
    }

    private static void callServer() {
        showMenu();
        System.out.println("\n✓ Serveur appelé! Il arrive bientôt...");
        System.out.println("En attente d'un serveur pour prendre votre commande...\n");
        
        // Appel serveur sans items (le serveur prendra la commande)
        serverManager.requestService(
            new ServerManager.ClientRequest(currentUser.getDisplayName(), new ArrayList<>(), false)
        );
    }

    private static void showMyOrders() {
        System.out.println("\n--- Mes commandes ---");
        List<Order> myOrders = orderService.list().stream()
            .filter(o -> o.getClientName().equals(currentUser.getDisplayName()))
            .collect(Collectors.toList());
        
        if (myOrders.isEmpty()) {
            System.out.println("Aucune commande");
            return;
        }
        
        myOrders.forEach(o -> {
            System.out.println("\n" + o.getId() + " - " + o.getStatus());
            o.getItems().forEach(item -> System.out.println("  • " + item.getName()));
        });
    }

    private static void showAllOrders() {
        System.out.println("\n--- Toutes les commandes ---");
        List<Order> orders = orderService.list();
        
        if (orders.isEmpty()) {
            System.out.println("Aucune commande");
            return;
        }
        
        orders.forEach(o -> {
            System.out.printf("\n%s | %s | %s%n", o.getId(), o.getClientName(), o.getStatus());
            o.getItems().forEach(item -> System.out.println("  • " + item.getName()));
        });
    }

    private static void showOrdersInProgress() {
        System.out.println("\n--- Commandes en préparation ---");
        
        // Utilisation de STREAM pour filtrer
        List<Order> inProgress = orderService.list().stream()
            .filter(o -> o.getStatus() != Order.Status.READY)
            .collect(Collectors.toList());
        
        if (inProgress.isEmpty()) {
            System.out.println("Aucune commande en préparation");
            return;
        }
        
        inProgress.forEach(o -> {
            System.out.printf("\n%s | %s | %s%n", o.getId(), o.getClientName(), o.getStatus());
            o.getItems().forEach(item -> System.out.println("  • " + item.getName()));
        });
    }

    private static void showServerStatus() {
        System.out.println("\n--- État des serveurs ---");
        serverList.forEach(s -> {
            String status = s.isBusy() ? "🔴 OCCUPÉ" : "🟢 LIBRE";
            System.out.println(s.getName() + ": " + status);
        });
    }

    private static void showStatistics() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║              STATISTIQUES                         ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        
        List<Order> orders = orderService.list();
        
        // Utilisation de STREAMS pour les statistiques
        long total = orders.size();
        long ready = orders.stream().filter(o -> o.getStatus() == Order.Status.READY).count();
        long inPrep = orders.stream().filter(o -> o.getStatus() == Order.Status.IN_PREPARATION).count();
        long received = orders.stream().filter(o -> o.getStatus() == Order.Status.RECEIVED).count();
        
        System.out.println("Total commandes: " + total);
        System.out.println("  • Prêtes: " + ready);
        System.out.println("  • En préparation: " + inPrep);
        System.out.println("  • Reçues: " + received);
        
        System.out.println("\nServeurs:");
        long busy = serverList.stream().filter(ServerStaff::isBusy).count();
        System.out.println("  • Occupés: " + busy + "/" + serverList.size());
        
        System.out.println("\nUtilisateurs: " + userService.list().size());
    }
    
    private static void showNotifications() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("          NOTIFICATIONS");
        System.out.println("═══════════════════════════════════════════════════");
        
        List<Notification> notifs = serverManager.getNotifications(currentUser.getDisplayName());
        
        if (notifs.isEmpty()) {
            System.out.println("Aucune notification");
            return;
        }
        
        System.out.println("\nVous avez " + notifs.size() + " notification(s):\n");
        for (int i = 0; i < notifs.size(); i++) {
            Notification notif = notifs.get(i);
            String status = notif.isRead() ? "✓ Lu" : "● Non lu";
            System.out.println((i + 1) + ". " + notif.getMessage() + " [" + status + "]");
        }
        
        System.out.print("\nSélectionner une notification pour prendre la commande (0 pour retour): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            
            if (choice == 0) {
                return;
            }
            
            if (choice < 1 || choice > notifs.size()) {
                System.out.println("❌ Choix invalide");
                return;
            }
            
            Notification selectedNotif = notifs.get(choice - 1);
            serverManager.markNotificationAsRead(currentUser.getDisplayName(), choice - 1);
            
            ServerManager.ClientRequest req = serverManager.getCurrentRequest(currentUser.getDisplayName());
            if (req != null) {
                takeOrderFromClient(selectedNotif.getClientName());
            } else {
                System.out.println("❌ Pas de demande associée");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrée invalide");
        }
    }
    
    private static void takeOrderFromClient(String clientName) {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("          PRENDRE LA COMMANDE DE " + clientName);
        System.out.println("═══════════════════════════════════════════════════\n");
        
        showMenu();
        
        System.out.println("\n--- Sélectionnez les plats pour " + clientName + " ---");
        System.out.println("Entrez les numéros des plats (séparés par des espaces):");
        System.out.print("Choix: ");
        String input = scanner.nextLine();
        
        List<MenuItem> items = new ArrayList<>();
        try {
            String[] numbers = input.split(" ");
            for (String num : numbers) {
                int index = Integer.parseInt(num.trim()) - 1;
                if (index >= 0 && index < menu.size()) {
                    items.add(menu.get(index));
                }
            }
            
            if (items.isEmpty()) {
                System.out.println("❌ Aucun plat sélectionné");
                return;
            }
            
            // Soumettre la commande du client
            serverManager.submitOrderFromServer(currentUser.getDisplayName(), items);
            
            double total = items.stream().mapToDouble(MenuItem::getPrice).sum();
            System.out.println("\n✓ Commande enregistrée!");
            System.out.printf("Total: %.2f€%n", total);
            
        } catch (Exception e) {
            System.out.println("❌ Erreur dans la sélection");
        }
    }}