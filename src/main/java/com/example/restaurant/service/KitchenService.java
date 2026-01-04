package com.example.restaurant.service;

import com.example.restaurant.model.Order;

import java.util.concurrent.*;

/**
 * KitchenService gère la préparation des commandes.
 * Utilise:
 * - BlockingQueue: pour recevoir les commandes de manière asynchrone
 * - ExecutorService (thread pool): pour traiter plusieurs commandes en parallèle (simule plusieurs cuisiniers)
 * - Thread daemon: pour le dispatcher qui écoute continuellement la queue
 */
public class KitchenService {
    private final BlockingQueue<Order> queue = new LinkedBlockingQueue<>();
    private final ExecutorService cooks = Executors.newFixedThreadPool(2);

    public KitchenService() {
        // Runnable avec lambda - plus concis qu'une classe anonyme
        Runnable dispatcher = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Order o = queue.take(); // bloque jusqu'à une nouvelle commande
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
    }

    private void process(Order o) {
        try {
            // Simule le temps de préparation
            Thread.sleep(1000 + (long)(Math.random() * 2000));
            o.setStatus(Order.Status.READY);
            System.out.println("✅ Kitchen: order READY " + o.getId() + " for " + o.getClientName());
        } catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
        }
    }
}
