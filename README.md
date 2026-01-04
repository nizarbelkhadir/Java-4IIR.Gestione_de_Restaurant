# Mini Projet Java - Gestion de Restaurant

## 🎯 Objectif
Système de gestion de restaurant démontrant l'utilisation de **streams**, **threads** et **lambdas** en Java.

## 🚀 Compilation et Exécution

### Option 1: Scripts Windows (recommandé)
```bash
compile.bat    # Compile le projet
run.bat        # Exécute la démo
```

### Option 2: Ligne de commande manuelle
```bash
# Compilation
javac -d bin src/main/java/com/example/restaurant/**/*.java

# Exécution
java -cp bin com.example.restaurant.Main
```

## 📁 Structure du Projet
```
src/main/java/com/example/restaurant/
├── model/           # Modèles de données
│   ├── User.java
│   ├── Client.java
│   ├── ServerStaff.java
│   ├── KitchenStaff.java
│   ├── Admin.java
│   ├── MenuItem.java
│   └── Order.java
├── service/         # Logique métier
│   ├── UserService.java
│   ├── OrderService.java
│   ├── ServerManager.java
│   └── KitchenService.java
├── storage/         # Persistance
│   └── JsonStorage.java
└── Main.java        # Point d'entrée
```

## 👥 Rôles Utilisateurs

### 1. **Client**
- Self-service: commande directement
- Ou appelle un serveur pour prendre sa commande

### 2. **Serveur (ServerStaff)**
- Reçoit les appels des clients
- Va à la table, prend la commande
- Envoie la commande à la cuisine
- **Devient libre immédiatement après envoi**

### 3. **Cuisine (KitchenStaff)**
- Reçoit les commandes (self-service + serveurs)
- Prépare les plats en parallèle
- Confirme quand les commandes sont prêtes

### 4. **Admin**
- Crée et gère les comptes (CRUD)
- Gère serveurs et cuisiniers

## 🔧 Utilisation des Concepts Java Avancés

### 1️⃣ **STREAMS** 
**Où**: `ServerManager.java` (ligne ~66), `UserService.java`, `Main.java`

**Pourquoi**: 
- Trouver le premier serveur disponible de manière déclarative et lisible
- Filtrer et transformer des collections
- Code plus concis que les boucles traditionnelles

**Exemple**:
```java
ServerStaff server = servers.stream()
    .filter(s -> !s.isBusy())  // Lambda: filtre les serveurs libres
    .findFirst()               // Prend le premier disponible
    .orElse(null);
```

### 2️⃣ **THREADS**
**Où**: 
- `ServerManager.java`: Thread dispatcher + ExecutorService
- `KitchenService.java`: Thread dispatcher + ThreadPool de cuisiniers

**Pourquoi**:
- **Asynchrone**: Les clients n'attendent pas que leur commande soit prête
- **Parallélisme**: Plusieurs serveurs et cuisiniers travaillent en même temps
- **Réaliste**: Simule un vrai restaurant avec événements concurrents

**Composants utilisés**:
- `Thread`: Dispatcher qui écoute continuellement les demandes
- `ExecutorService`: Pool de threads pour gérer plusieurs serveurs/cuisiniers
- `BlockingQueue`: Communication thread-safe entre producteurs et consommateurs
- `Semaphore`: Limite le nombre de serveurs disponibles

### 3️⃣ **LAMBDAS**
**Où**: Partout dans les services

**Pourquoi**:
- **Plus court**: Remplace les classes anonymes
- **Lisible**: Code plus clair et expressif
- **Moderne**: Style Java moderne (Java 8+)

**Exemples**:
```java
// Lambda pour créer un Runnable
Runnable dispatcher = () -> {
    // code...
};

// Lambda dans les streams
.filter(u -> u.getId().equals(id))

// Lambda pour soumettre une tâche
executor.submit(() -> process(order));
```

## 🎬 Scénarios de Démo

### Scénario 1: Self-service
Client commande directement → Cuisine reçoit → Prépare

### Scénario 2: Avec serveur
Client appelle → Serveur va à la table → Prend commande → Envoie à cuisine → Serveur libre

### Scénario 3: Tous serveurs occupés
5 clients appellent en même temps, 2 serveurs seulement:
- Les 2 premiers sont servis immédiatement
- Les autres **attendent en queue**
- Le **premier serveur qui se libère** prend le prochain client

## 🧵 Gestion de la Concurrence

### Semaphore
```java
Semaphore available = new Semaphore(servers.size());
```
- **Problème résolu**: Garantir qu'on n'assigne pas plus de clients que de serveurs disponibles
- **Fonctionnement**: 
  - `acquire()`: Prend un permit (bloque si aucun disponible)
  - `release()`: Libère un permit (dans le `finally` pour garantir la libération)

### BlockingQueue
```java
BlockingQueue<ClientRequest> requests = new LinkedBlockingQueue<>();
```
- **Problème résolu**: Communication thread-safe entre clients et dispatcher
- **Fonctionnement**:
  - `offer()`: Ajoute une demande
  - `take()`: Retire une demande (bloque si vide)

## 📊 Diagramme de Flux

```
Client → requestService()
    ↓
Self-service? 
├─ OUI → OrderService → Kitchen
└─ NON → Queue → Semaphore (attend serveur libre)
              ↓
         ServerManager (stream pour trouver serveur)
              ↓
         Server va à table
              ↓
         OrderService → Kitchen → Prépare
              ↓
         Server devient LIBRE (release semaphore)
```

## 📝 Notes pour le Prof

### Questions possibles et réponses:

**Q: Pourquoi utiliser Semaphore?**
R: Pour implémenter "le premier serveur qui devient libre sert le prochain client". Le Semaphore bloque les demandes quand tous les serveurs sont occupés et les libère automatiquement dès qu'un serveur termine.

**Q: Pourquoi BlockingQueue?**
R: Pour la communication thread-safe entre le thread principal (qui reçoit les demandes) et le dispatcher (qui assigne les serveurs). Évite les race conditions.

**Q: Pourquoi streams au lieu de boucle for?**
R: Plus lisible, déclaratif (on dit "quoi faire" pas "comment le faire"), et plus maintenable. Exemple: `filter().findFirst()` est plus clair que `for + if + break`.

**Q: Pourquoi lambdas?**
R: Réduit le boilerplate des classes anonymes. `() -> {...}` est plus court et clair que `new Runnable() { public void run() {...} }`.

**Q: Stockage JSON?**
R: Utilise `JsonStorage.java` avec interface prête. En attente de votre méthode spécifique pour implémenter la sérialisation complète.

## 🔜 Améliorations Futures
- Persistance JSON complète avec Gson/Jackson
- Interface utilisateur (CLI ou GUI)
- Gestion des tables
- Calcul du total des commandes
- Historique des serveurs

---
**Date**: Décembre 2025  
**Concepts**: Streams, Threads, Lambdas, Concurrency (Semaphore, BlockingQueue, ExecutorService)
