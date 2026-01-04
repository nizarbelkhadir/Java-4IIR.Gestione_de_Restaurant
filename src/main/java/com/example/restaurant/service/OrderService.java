package com.example.restaurant.service;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderService {
    private final List<Order> orders = new ArrayList<>();
    private final KitchenService kitchen;

    public OrderService(KitchenService kitchen) { 
        this.kitchen = kitchen; 
    }

    public synchronized Order createOrder(String clientName, List<MenuItem> items) {
        String id = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        Order o = new Order(id, clientName, items);
        orders.add(o);
        kitchen.submitOrder(o);
        return o;
    }

    public synchronized List<Order> list() { 
        return new ArrayList<>(orders); 
    }
}
