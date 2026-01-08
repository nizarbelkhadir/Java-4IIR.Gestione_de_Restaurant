package com.example.restaurant.service;

import com.example.restaurant.model.MenuItem;
import com.example.restaurant.model.ServerStaff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * ServerManager gère l'assignation des serveurs aux tables et les notifications.
 * Utilise:
 * - Semaphore: limite le nombre de serveurs disponibles et gère l'attente si tous sont occupés
 * - BlockingQueue: queue des demandes clients
 * - Streams + Lambda: pour trouver le premier serveur disponible
 * - Thread pool: pour gérer plusieurs serveurs en parallèle
 * - Map: pour stocker les notifications par serveur
 */
public class ServerManager {
    private final List<ServerStaff> servers = new ArrayList<>();
    private final BlockingQueue<ClientRequest> requests = new LinkedBlockingQueue<>();
    private final Semaphore available; // Contrôle le nombre de serveurs disponibles
    private final OrderService orderService;
    private final ExecutorService serverExecutor = Executors.newCachedThreadPool();
    
    // Système de notifications pour les serveurs
    private final Map<String, List<Notification>> serverNotifications = new ConcurrentHashMap<>();
    private final Map<String, ClientRequest> serverCurrentRequest = new ConcurrentHashMap<>();

    public static class ClientRequest {
        public final String clientName;
        public final List<MenuItem> items;
        public final boolean selfService;
        
        public ClientRequest(String clientName, List<MenuItem> items, boolean selfService) {
            this.clientName = clientName;
            this.items = items;
            this.selfService = selfService;
        }
    }

    public ServerManager(List<ServerStaff> serverList, OrderService orderService) {
        this.servers.addAll(serverList);
        this.available = new Semaphore(servers.size()); // Un permit par serveur
        this.orderService = orderService;
        
        // Initialiser les notifications pour chaque serveur
        servers.forEach(server -> serverNotifications.put(server.getName(), new ArrayList<>()));
        
        // Thread dispatcher avec lambda
        Thread dispatcher = new Thread(this::dispatch, "Server-Dispatcher");
        dispatcher.setDaemon(true);
        dispatcher.start();
    }

    public void requestService(ClientRequest req) {
        if (req.selfService) {
            // Self-service: le client passe sa commande directement
            orderService.createOrder(req.clientName, req.items);
            System.out.println("📱 Client (self-service) " + req.clientName + " placed order directly.");
        } else {
            // Client appelle un serveur
            
            // Vérifier s'il y a des serveurs dans le système
            if (servers.isEmpty()) {
                System.out.println("❌ Aucun serveur n'est disponible dans le système.");
                System.out.println("   Veuillez passer votre commande en SELF-SERVICE ou contacter l'admin.");
                return;
            }
            
            requests.offer(req);
            
            // Vérifier s'il y a des serveurs disponibles
            long availableCount = servers.stream().filter(s -> !s.isBusy()).count();
            
            if (availableCount > 0) {
                System.out.println("🔔 Client " + req.clientName + " a demandé un serveur.");
                System.out.println("✅ Serveur disponible! Un serveur va venir prendre votre commande...");
            } else {
                System.out.println("🔔 Client " + req.clientName + " a demandé un serveur.");
                System.out.println("⏳ Tous les " + servers.size() + " serveurs sont occupés. Vous êtes en file d'attente (position " + requests.size() + ")");
            }
        }
    }

    private void dispatch() {
        while (true) {
            try {
                ClientRequest req = requests.take(); // Attend une demande
                
                // Attend qu'un serveur soit disponible
                // Si tous sont occupés, bloque jusqu'à libération
                available.acquire();
                
                // STREAM + LAMBDA: trouve le premier serveur non occupé
                ServerStaff server = servers.stream()
                    .filter(s -> !s.isBusy())  // Lambda: filtre les serveurs libres
                    .findFirst()               // Prend le premier disponible
                    .orElse(null);
                
                if (server == null) {
                    // Ne devrait pas arriver (semaphore garantit disponibilité)
                    available.release();
                    requests.offer(req); // Re-queue
                    continue;
                }
                
                server.setBusy(true);
                
                // Créer une notification pour le serveur
                Notification notif = new Notification(server.getName(), req.clientName);
                serverNotifications.get(server.getName()).add(notif);
                serverCurrentRequest.put(server.getName(), req);
                
                System.out.println("� NOTIFICATION envoyée à " + server.getName() + ": " + notif.getMessage());
                
                // Lambda pour créer le Runnable
                serverExecutor.submit(() -> handleWithServer(server, req));
                
            } catch (InterruptedException e) { 
                Thread.currentThread().interrupt(); 
                break; 
            }
        }
    }

    private void handleWithServer(ServerStaff server, ClientRequest req) {
        try {
            // Simule: aller à la table, prendre la commande
            Thread.sleep(500 + (long)(Math.random() * 800));
            
        } catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
        } finally {
            // LE SERVEUR DEVIENT LIBRE dès qu'il envoie la commande (comme demandé)
            server.setBusy(false);
            available.release(); // Libère un permit pour le prochain client
        }
    }
    
    /**
     * Récupérer les notifications non lues d'un serveur
     */
    public List<Notification> getNotifications(String serverName) {
        return serverNotifications.getOrDefault(serverName, new ArrayList<>());
    }
    
    /**
     * Marquer une notification comme lue
     */
    public void markNotificationAsRead(String serverName, int index) {
        List<Notification> notifs = serverNotifications.get(serverName);
        if (notifs != null && index >= 0 && index < notifs.size()) {
            notifs.get(index).setRead(true);
        }
    }
    
    /**
     * Obtenir la demande actuelle d'un serveur
     */
    public ClientRequest getCurrentRequest(String serverName) {
        return serverCurrentRequest.get(serverName);
    }
    
    /**
     * Le serveur soumet la commande
     */
    public void submitOrderFromServer(String serverName, List<MenuItem> items) {
        ClientRequest req = serverCurrentRequest.get(serverName);
        if (req != null) {
            orderService.createOrder(req.clientName, items);
            System.out.println("📋 Server " + serverName + " submitted order for " + req.clientName);
            serverCurrentRequest.remove(serverName);
        }
    }
}

