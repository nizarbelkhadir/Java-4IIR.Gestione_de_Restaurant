package com.example.restaurant.model;

import java.util.List;

public class Order {
    public enum Status { RECEIVED, IN_PREPARATION, READY }

    private String id;
    private String clientName;
    private List<MenuItem> items;
    private Status status = Status.RECEIVED;

    public Order() {}
    public Order(String id, String clientName, List<MenuItem> items) {
        this.id = id; 
        this.clientName = clientName; 
        this.items = items;
    }

    public String getId() { return id; }
    public String getClientName() { return clientName; }
    public List<MenuItem> getItems() { return items; }
    public Status getStatus() { return status; }
    public void setStatus(Status s) { this.status = s; }
}
