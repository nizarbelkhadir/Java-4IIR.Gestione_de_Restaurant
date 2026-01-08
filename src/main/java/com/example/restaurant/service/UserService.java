package com.example.restaurant.service;

import com.example.restaurant.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService {
    private List<User> users;

    public UserService() { 
        users = new ArrayList<>(); 
    }

    public synchronized void load() {
        // Not used anymore - authentication system handles persistence
    }

    public synchronized void save() {
        // Not used anymore - authentication system handles persistence
    }

    // CRUD operations (Admin functionality)
    public synchronized void addUser(User u) { 
        users.add(u); 
    }
    
    public synchronized List<User> list() { 
        // Using stream to create a copy
        return users.stream().collect(Collectors.toList()); 
    }
    
    public synchronized Optional<User> findById(String id) { 
        // Using stream with lambda to filter
        return users.stream()
                    .filter(u -> u.getId().equals(id))
                    .findFirst(); 
    }
    
    public synchronized void deleteUser(String id) {
        // Using stream to remove
        users.removeIf(u -> u.getId().equals(id));
    }
}
