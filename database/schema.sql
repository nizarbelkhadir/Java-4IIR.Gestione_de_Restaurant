-- Base de données pour le système de gestion de restaurant
-- À exécuter dans phpMyAdmin après avoir créé la base de données

CREATE DATABASE IF NOT EXISTS restaurant_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE restaurant_db;

-- Table des utilisateurs (credentials)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_type VARCHAR(20) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_user_type (user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table des commandes
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(50) UNIQUE NOT NULL,
    client_name VARCHAR(100) NOT NULL,
    items TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    timestamp BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_status (status),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insertion des données par défaut (utilisateurs de test)
INSERT INTO users (user_id, username, password, user_type, display_name) VALUES
('U001', 'admin', 'admin123', 'ADMIN', 'Administrateur'),
('U002', 'serveur1', 'pass123', 'SERVER', 'Serveur 1'),
('U003', 'chef1', 'pass123', 'KITCHEN', 'Chef Cuisine 1'),
('U004', 'client1', 'pass123', 'CLIENT', 'Client 1');

-- Vérification des données insérées
SELECT * FROM users;
