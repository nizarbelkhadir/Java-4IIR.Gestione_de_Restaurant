# 📋 PROMPT PROFESSIONNEL - DIAGRAMME USE CASE SYSTÈME DE GESTION DE RESTAURANT

---

## 🎯 CONTEXTE DU PROJET

Vous êtes un expert en modélisation UML. Je vous demande de créer un **diagramme Use Case professionnel** pour un **système de gestion de restaurant interactif en Java**.

Ce système permet à 4 types d'utilisateurs différents d'interagir avec une application de restauration moderne.

---

## 👥 ACTEURS PRINCIPAUX (4 utilisateurs)

### 1️⃣ **ADMIN (Administrateur du système)**
- **Rôle**: Gère l'accès et les utilisateurs du système
- **Authentification**: Username + Password sécurisés
- **Credentials par défaut**: username=`admin`, password=`admin123`
- **Accès base de données**: `users.json` (stockage persistent des credentials)

### 2️⃣ **CLIENT (Consommateur)**
- **Rôle**: Commande des repas au restaurant
- **Authentification**: Aucune (juste saisie du nom lors de chaque visite)
- **Type de prestiges**: Prestige name-based (juste pour garder l'historique de nom)

### 3️⃣ **SERVEUR (Waiter/Staff)**
- **Rôle**: Prend les commandes des clients à table
- **Authentification**: Username + Password
- **Droit**: Création via Admin uniquement
- **Base de données**: Persistance dans `users.json`

### 4️⃣ **CUISINIER (Kitchen Staff)**
- **Rôle**: Prépare les plats commandés
- **Authentification**: Username + Password
- **Droit**: Création via Admin uniquement
- **Base de données**: Persistance dans `users.json`

---

## 🔧 FONCTIONNALITÉS PAR ACTEUR

### 📌 ADMIN - Cas d'usage:
1. **Se connecter** au système avec credentials
2. **Créer un nouvel utilisateur** (Serveur ou Cuisinier)
   - Saisir: username, password, type de rôle, nom complet
   - Vérifier l'unicité du username
3. **Lister tous les utilisateurs** du système
   - Afficher groupés par type (ADMIN, SERVEUR, CUISINIER)
   - Afficher: username, nom complet, ID unique, type de rôle
4. **Supprimer un utilisateur** (sauf l'admin principal)
   - Vérifier que ce n'est pas le dernier admin
5. **Voir les statistiques** du restaurant
   - Nombre total de commandes
   - Commandes prêtes, en préparation, reçues
   - Nombre de serveurs occupés/libres
   - Nombre d'utilisateurs dans le système
6. **Se déconnecter** du système

---

### 👤 CLIENT - Cas d'usage:
1. **Entrer dans le système** en saisissant son nom (aucune authentification)
2. **Consulter le menu** du restaurant
   - Afficher tous les plats disponibles
   - Afficher prix et description
3. **Passer une commande en SELF-SERVICE**
   - Sélectionner les plats directement
   - Envoyer la commande directement à la cuisine
   - Voir le total du prix
   - Commande traitée sans serveur
4. **Passer une commande via SERVEUR** (nouveau flux)
   - Appeler un serveur disponible
   - Le client **voit juste le menu** (pas de commande directe)
   - Le client **attend que le serveur arrive**
   - Le serveur prendra la commande à la table
5. **Voir mes commandes**
   - Afficher l'historique des commandes du client
   - Afficher statut de chaque commande (reçue, en préparation, prête)
   - Afficher les plats commandés
6. **Se déconnecter**

---

### 👨‍🍳 SERVEUR - Cas d'usage:
1. **Se connecter** avec username et password (créé par Admin)
2. **Voir les notifications** (nouveau)
   - Afficher liste des demandes de clients
   - Chaque notification: "Venez à la table de [NomClient]"
   - Marquer les notifications comme lues
   - Sélectionner une notification pour aller prendre la commande
3. **Prendre la commande du client** (nouveau)
   - Serveur reçoit la notification
   - Serveur accède à la table du client
   - Menu s'affiche au serveur
   - **Serveur saisit la commande POUR le client** (pas le client lui-même)
   - Serveur valide et envoie à la cuisine
   - Serveur devient libre immédiatement
4. **Consulter l'état des serveurs**
   - Afficher liste de tous les serveurs
   - Afficher statut de chaque serveur (libre/occupé)
5. **Voir toutes les commandes** du restaurant
   - Afficher liste complète des commandes
   - Afficher statut de chaque commande
   - Afficher client et plats correspondants
6. **Se déconnecter**

---

### 🍳 CUISINIER - Cas d'usage:
1. **Se connecter** avec username et password (créé par Admin)
2. **Recevoir les commandes** depuis serveurs et self-service
   - Commandes arrivent dans une file d'attente (BlockingQueue)
   - Commande passe au statut "EN_PRÉPARATION"
3. **Consulter les commandes en préparation**
   - Afficher uniquement les commandes en cours de préparation
   - Afficher plats à préparer
4. **Voir toutes les commandes** du restaurant
   - Afficher tous les historiques (reçues, en préparation, prêtes)
5. **Traiter les commandes**
   - Chaque commande prend 1-3 secondes de traitement (simulé)
   - Pas d'action manuelle, traitement automatique
   - Commande passe au statut "PRÊTE"
6. **Voir statistiques cuisine**
   - Total de commandes traitées
   - Nombre en préparation vs prêtes
   - Nombre reçues
7. **Se déconnecter**

---

## � SYSTÈME DE NOTIFICATIONS (Nouveau)

### Classe: `Notification.java`
- **Création automatique** quand un client appelle un serveur
- **Message standardisé**: `"Venez à la table de [NomClient]"`
- **Statuts**: Lu / Non lu
- **Stockage**: En mémoire (Map dans ServerManager)
- **Affichage serveur**: Dans le menu "Voir les notifications"

### Flux de Notification:
```
CLIENT appelle serveur
    ↓
NOTIFICATION créée automatiquement
    ↓
SERVEUR voit notification dans son menu
    ↓
SERVEUR sélectionne notification
    ↓
SERVEUR reçoit la commande du client
    ↓
SERVEUR tape la commande (pour le client)
    ↓
COMMANDE envoyée à CUISINE
    ↓
SERVEUR devient FREE
```

---

### 📁 **users.json** - Persistance des credentials
```
Format: Liste JSON des utilisateurs
{
  "username": "...",
  "password": "...",
  "userType": "ADMIN|SERVEUR|CUISINIER",
  "displayName": "...",
  "userId": "..."
}
```
- Persistance des credentials Admin, Serveurs, Cuisiniers
- Clients: PAS de persistance (juste nom saisi chaque fois)

### 📁 **orders.json** - Historique des commandes
```
Format: Liste JSON des commandes
{
  "orderId": "ORD-...",
  "clientName": "...",
  "items": "...",
  "status": "RECEIVED|IN_PREPARATION|READY",
  "timestamp": "..."
}
```
- Persistance optionnelle des commandes

---

## 🔐 AUTHENTIFICATION ET SÉCURITÉ

- **Système de login obligatoire** pour Admin, Serveur, Cuisinier
- **Pas de login pour Client** (prestige-based, juste le nom)
- **Credentials stockés en JSON** (users.json)
- **Validation des droits** par type d'utilisateur
- **Seul Admin peut créer/supprimer utilisateurs**

---

## ⚙️ TECHNOLOGIES ET PATTERNS UTILISÉS

- **Java Threads**: Gestion multi-threading pour cuisine et serveurs
- **BlockingQueue**: File d'attente thread-safe pour commandes
- **ConcurrentHashMap**: Stockage thread-safe des notifications
- **Semaphore**: Contrôle du nombre de serveurs disponibles (pool limité)
- **ExecutorService**: Thread pool pour traiter commandes en parallèle
- **Streams & Lambda**: Filtrage, groupage des données
- **JSON Storage**: Persistance simple en fichiers JSON
- **Pattern Observer implicite**: Cuisine écoute les commandes
- **Notification System**: Messages temps réel pour serveurs

---

## 📊 DIAGRAMME USE CASE - INFORMATIONS À INCLURE

**Acteurs**:
- Admin
- Client  
- Serveur
- Cuisinier

**Acteurs secondaires**:
- Système de stockage (users.json, orders.json)
- Système de Notifications

**Relations UML - Cas d'usage CLIENT**:
- Include: Appeler un serveur → Voir le menu (pas de sélection)
- Include: Self-service → Voir le menu + Sélectionner plats

**Relations UML - Cas d'usage SERVEUR**:
- Include: Recevoir notification → Aller à table → Taper commande
- Include: Notification → Soumettre commande à cuisine
- Extend: Voir notification (optionnel)

**Points de priorité**:
1. Créer des cas d'usage distincts et clairs
2. Montrer les 4 acteurs avec leurs interactions
3. Inclure la gestion d'authentification
4. **Afficher le système de notifications** (nouveau)
5. Montrer le flux CLIENT appel SERVEUR (pas de commande directe)
6. Montrer le flux SERVEUR tape commande (pas le CLIENT)
7. Afficher les relations de dépendance
8. Structurer par fonctionnalités principales

---

## ✅ RÉSUMÉ DES EXIGENCES

Vous devez créer un **diagramme Use Case UML professionnel** qui:

1. ✔️ Montre les **4 acteurs** clairement
2. ✔️ Détaille les **fonctionnalités de chaque acteur**
3. ✔️ Affiche les **interactions** entre acteurs et système
4. ✔️ Inclut les **cas d'authentification**
5. ✔️ Montre les **flux de gestion de commandes**
6. ✔️ Est **lisible, professionnel et complet**
7. ✔️ Utilise les **conventions UML** standards
8. ✔️ Peut être généré via PlantUML, Lucidchart ou draw.io

---

**Format de réponse attendu**: Diagramme visuel en UML ou code PlantUML à importer

