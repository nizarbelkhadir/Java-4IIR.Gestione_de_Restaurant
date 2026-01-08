package com.example.restaurant.service;

import com.example.restaurant.model.Order;

import java.util.concurrent.*;

/**
 * KitchenService gère la préparation des commandes.
 * Utilise:
 * - BlockingQueue: pour recevoir les commandes de manière asynchrone
 * - Semaphore: pour gérer le nombre de cuisiniers disponibles
 * - ExecutorService (thread pool): pour traiter plusieurs commandes en parallèle (simule plusieurs cuisiniers)
 * - Thread daemon: pour le dispatcher qui écoute continuellement la queue
 */
public class KitchenService {
    private final BlockingQueue<Order> queue = new LinkedBlockingQueue<>();
    private final ExecutorService cooks;
    private final Semaphore availableCooks; // Sémaphore pour gérer les cuisiniers disponibles
    private final int numberOfCooks;

    public KitchenService() {
        this(2); // Par défaut 2 cuisiniers
    }
    
    public KitchenService(int numberOfCooks) {
        this.numberOfCooks = numberOfCooks;
        this.cooks = Executors.newFixedThreadPool(numberOfCooks);
        this.availableCooks = new Semaphore(numberOfCooks); // Un permit par cuisinier
        
        // Runnable avec lambda - plus concis qu'une classe anonyme
        Runnable dispatcher = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Order o = queue.take(); // bloque jusqu'à une nouvelle commande
                    
                    // Attendre qu'un cuisinier soit disponible
                    System.out.println("⏳ Order " + o.getId() + " en attente d'un cuisinier disponible...");
                    availableCooks.acquire(); // Bloque si tous les cuisiniers sont occupés
                    
                    System.out.println("👨‍🍳 Un cuisinier a pris la commande " + o.getId());
                    // Lambda pour soumettre au thread pool
                    cooks.submit(() -> process(o));
                } catch (InterruptedException e) { 
                    Thread.currentThread().interrupt(); 
                }
            }
        };
        
        Thread t = new Thread(dispatcher, "Kitchen-Dispatcher");
        t.setDaemon(true);
        t.start();
    }

    public void submitOrder(Order o) {
        o.setStatus(Order.Status.IN_PREPARATION);
        queue.offer(o);
        System.out.println("🍳 Kitchen: received order " + o.getId() + " from " + o.getClientName());
        System.out.println("   Queue position: " + queue.size() + " commande(s) en attente");
    }

    private void process(Order o) {
        try {
            System.out.println("🔥 Cuisinier commence à préparer " + o.getId());
            // Simule le temps de préparation
            Thread.sleep(2000 + (long)(Math.random() * 3000));
            o.setStatus(Order.Status.READY);
            System.out.println("✅ Kitchen: order READY " + o.getId() + " for " + o.getClientName());
        } catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
        } finally {
            // Libérer le cuisinier pour la prochaine commande
            availableCooks.release();
            System.out.println("👨‍🍳 Cuisinier disponible pour une nouvelle commande");
        }
    }
    
    /**
     * Obtenir le nombre de cuisiniers disponibles
     */
    public int getAvailableCooksCount() {
        return availableCooks.availablePermits();
    }
    
    /**
     * Obtenir le nombre total de cuisiniers
     */
    public int getTotalCooksCount() {
        return numberOfCooks;
    }
    
    /**
     * Obtenir le nombre de commandes en attente
     */
    public int getQueueSize() {
        return queue.size();
    }
}
