# 📊 RAPPORT DU PROJET - Gestion de Restaurant

## 🎯 Qu'est-ce que le projet ?

C'est un système qui **simule un restaurant** où:
- Des **clients** passent des commandes (en self-service ou via un serveur)
- Des **serveurs** prennent les commandes des clients
- La **cuisine** prépare les plats
- Un **admin** gère les utilisateurs

---

## 📁 Les Classes Principales

### **Modèle (model/)**
Ce dossier contient les **classes qui représentent les données**:

1. **`User.java`** 
   - Classe abstraite (parent) pour tous les utilisateurs
   - Contient: id, name

2. **`Client.java`** 
   - Représente un **client du restaurant**
   - Hérite de User
   - Peut commander des plats

3. **`ServerStaff.java`** 
   - Représente un **serveur**
   - Hérite de User
   - Contient un `AtomicBoolean` pour suivre s'il est occupé ou non
   - 🔑 **Utilise un thread-safe boolean** car plusieurs threads peuvent accéder à sa disponibilité

4. **`KitchenStaff.java`** 
   - Représente un **cuisinier**
   - Hérite de User

5. **`Admin.java`** 
   - Représente un **administrateur**
   - Hérite de User
   - Gère les utilisateurs

6. **`MenuItem.java`** 
   - Représente un **plat du menu**
   - Contient: id, nom du plat, prix

7. **`Order.java`** 
   - Représente une **commande** 
   - Contient: id, nom du client, liste de plats, statut (RECEIVED → IN_PREPARATION → READY)

---

### **Services (service/)**
Ce dossier contient la **logique métier** (les actions du restaurant):

1. **`UserService.java`** 
   - Gère les utilisateurs (CRUD: Create, Read, Update, Delete)
   - **UTILISE STREAMS ET LAMBDAS** ⭐
   
2. **`OrderService.java`** 
   - Gère les **commandes** (création, listes)
   - Transmet les commandes à la cuisine

3. **`KitchenService.java`** 
   - Gère la **préparation des commandes**
   - **UTILISE THREADS** ⭐

4. **`ServerManager.java`** 
   - Assigne les **serveurs aux clients**
   - Gère la queue des demandes
   - **UTILISE THREADS ET STREAMS** ⭐

5. **`AuthenticationService.java`** 
   - Authentifie les utilisateurs (login/logout)
   - Sauvegarde les utilisateurs dans JSON

---

### **Stockage (storage/)**

1. **`JsonStorage.java`** 
   - Sauvegarde et charge les données en **JSON**
   - Utilise les **Generics** (`<T>`) pour fonctionner avec n'importe quel type

---

### **Interface Utilisateur**

1. **`InteractiveMain.java`** 
   - Application interactive avec **menu en console**
   - Permet à l'utilisateur de se connecter et d'accéder à différentes fonctionnalités

2. **`Main.java`** 
   - Point d'entrée simple pour démo

---

## 📄 Les Fichiers JSON

1. **`users.json`** - Stocke les identifiants des utilisateurs
2. **`orders.json`** - Stocke l'historique des commandes

---

## 🔑 Les 3 CONCEPTS PRINCIPAUX (ce que le prof va tester)

---

## ⚡ 1. LES STREAMS (Java 8+)

### Qu'est-ce que c'est ?
Les **streams** permettent de traiter les collections (listes) de manière **fonctionnelle et déclarative**. 
C'est comme une "chaîne de montage" pour traiter des données.

### Où ça s'utilise dans le projet ?

#### **Exemple 1: Dans `UserService.java`**
```java
public synchronized List<User> list() { 
    // Stream: crée une chaîne de traitement
    return users.stream()           // Convertit la liste en stream
                .collect(Collectors.toList());  // Remet en liste
}

public synchronized Optional<User> findById(String id) { 
    // Stream + Lambda pour FILTRER
    return users.stream()
                .filter(u -> u.getId().equals(id))  // Lambda: filtre les utilisateurs
                .findFirst();                       // Prend le premier trouvé
}

public synchronized void deleteUser(String id) {
    // removeIf utilise aussi une lambda pour supprimer
    users.removeIf(u -> u.getId().equals(id));
}
```

**Pourquoi les streams ?**
- ✅ Code plus lisible et déclaratif (dit QUOI faire, pas COMMENT)
- ✅ Plus court et plus élégant qu'une boucle for
- ✅ Possible pour paralléliser si besoin
- ✅ Manipulation de données en chaîne (filter → map → collect)

#### **Exemple 2: Dans `ServerManager.java`**
```java
// Trouver le premier serveur DISPONIBLE (pas occupé)
ServerStaff server = servers.stream()
    .filter(s -> !s.isBusy())      // Lambda: filtre les serveurs libres
    .findFirst()                    // Prend le premier
    .orElse(null);                  // Ou null s'il n'y en a pas
```

**Pourquoi ici ?**
- ✅ Recherche fonctionnelle et sécurisée (retourne Optional)
- ✅ Combiné avec lambda pour une logique complexe
- ✅ Plus concis qu'une boucle for classique

---

## 🔄 2. LES THREADS (Concurrence)

### Qu'est-ce que c'est ?
Les **threads** permettent d'exécuter **plusieurs choses en même temps**.
Par exemple: pendant que la cuisine prépare un plat, un serveur peut prendre une autre commande.

### Où ça s'utilise dans le projet ?

#### **Exemple 1: Dans `KitchenService.java`**

```java
// Thread dispatcher avec LAMBDA
Runnable dispatcher = () -> {  // Ceci est une lambda!
    while (!Thread.currentThread().isInterrupted()) {
        try {
            Order o = queue.take();  // Attend une commande
            // Lambda: soumettre au thread pool
            cooks.submit(() -> process(o));  // Lance la préparation
        } catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
        }
    }
};

Thread t = new Thread(dispatcher, "Kitchen-Dispatcher");
t.setDaemon(true);  // Thread daemon (s'arrête si l'app s'arrête)
t.start();          // Lance le thread
```

**Ce que ça fait:**
1. Un thread dispatcher **écoute continuellement** la queue des commandes
2. Quand une commande arrive, il la soumet à un **thread pool de 2 cuisiniers**
3. Les 2 cuisiniers travaillent **en parallèle** (2 plats à la fois)
4. Pendant ce temps, l'application peut accepter d'autres commandes

**Pourquoi les threads ?**
- ✅ La cuisine peut travailler simultanément sur plusieurs commandes
- ✅ L'application reste réactive (pas bloquée)
- ✅ Simule la réalité d'un vrai restaurant

#### **Exemple 2: Dans `ServerManager.java`**

```java
// Semaphore: limite le nombre de serveurs disponibles
private final Semaphore available;  // Si 2 serveurs, max 2 demandes en parallèle

public ServerManager(List<ServerStaff> serverList, OrderService orderService) {
    this.available = new Semaphore(servers.size());  // Un permit par serveur
    
    // Thread dispatcher avec lambda
    Thread dispatcher = new Thread(this::dispatch, "Server-Dispatcher");
    dispatcher.setDaemon(true);
    dispatcher.start();
}

private void dispatch() {
    while (true) {
        try {
            ClientRequest req = requests.take();  // Attend une demande client
            
            // BLOQUE si tous les serveurs sont occupés
            available.acquire();  // Attend qu'un serveur se libère
            
            // Traiter la demande du serveur...
            
            // Quand fini, relâcher le serveur
            available.release();
        } catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
        }
    }
}
```

**Pourquoi les threads ET Semaphore ?**
- ✅ Plusieurs serveurs peuvent servir des clients **en parallèle**
- ✅ Si tous les serveurs sont occupés, les clients **attendent** dans la queue
- ✅ Quand un serveur finit, il traite le client suivant
- ✅ Simule un restaurant réaliste avec nombre limité de serveurs

#### **Problème que les threads résolvent:**

**SANS threads (synchrone):**
```
⏱️  Client 1 appelle serveur → Serveur prend 2 min
⏱️  Client 2 doit attendre 2 min
⏱️  Pendant ce temps, la cuisine ne fait rien!
```

**AVEC threads (asynchrone):**
```
⏱️  Client 1 appelle serveur
⏱️  Client 2 appelle serveur EN MÊME TEMPS (serveur 2)
⏱️  Pendant ce temps, cuisine prépare les plats (thread pool)
⏱️  Application beaucoup plus rapide!
```

---

## 🎯 3. LES LAMBDAS (Syntaxe moderne)

### Qu'est-ce que c'est ?
Les **lambdas** sont des **fonctions anonymes courtes** introduites en Java 8.
Elles permettent de passer des **comportements** en paramètre, comme en JavaScript.

### Syntaxe

```java
// Format général:
(paramètres) -> { corps de la fonction }

// Exemples:
x -> x * 2                           // Multiplie par 2
(x, y) -> x + y                      // Additionne deux nombres
user -> user.getId().equals("123")   // Filtre les utilisateurs
() -> System.out.println("Hello")    // Pas de paramètres
```

### Où ça s'utilise dans le projet ?

#### **Exemple 1: Filter + Lambda dans `UserService.java`**
```java
return users.stream()
    .filter(u -> u.getId().equals(id))  // Lambda: condition de filtrage
    .findFirst();
```
La lambda `u -> u.getId().equals(id)` dit: **"garde les utilisateurs dont l'id égale celui recherché"**

#### **Exemple 2: removeIf + Lambda dans `UserService.java`**
```java
users.removeIf(u -> u.getId().equals(id));  // Lambda: condition de suppression
```
La lambda dit: **"supprime les utilisateurs dont l'id égale celui recherché"**

#### **Exemple 3: Runnable + Lambda dans `KitchenService.java`**
```java
// SANS lambda (classe anonyme - verbeux):
Runnable dispatcher = new Runnable() {
    public void run() {
        // ... 20 lignes de code
    }
};

// AVEC lambda (concis):
Runnable dispatcher = () -> {
    while (!Thread.currentThread().isInterrupted()) {
        Order o = queue.take();
        cooks.submit(() -> process(o));
    }
};
```

#### **Exemple 4: forEach + Lambda dans `ServerManager.java`**
```java
servers.forEach(server -> serverNotifications.put(server.getName(), new ArrayList<>()));
```
La lambda dit: **"pour chaque serveur, ajoute ses notifications"**

#### **Exemple 5: submit avec Lambda dans `KitchenService.java`**
```java
cooks.submit(() -> process(o));  // Lambda: exécute process(o) dans un thread
```
La lambda dit: **"exécute process(o) en parallèle"**

### Pourquoi les lambdas ?

- ✅ **Code plus concis** - Moins de boilerplate
- ✅ **Lisible** - Exprime clairement l'intention
- ✅ **Fonctionnel** - Traite le code comme une fonction, pas un objet
- ✅ **Avec Streams** - Permet filter(), map(), forEach()
- ✅ **Avec Threads** - Permet créer Runnable facilement

---

## 🎓 Résumé: Comment ces 3 concepts se COMBINENT

### Cas réel du projet:

```java
// STREAMS + LAMBDA pour trouver un serveur disponible
ServerStaff server = servers.stream()          // STREAM
    .filter(s -> !s.isBusy())                  // LAMBDA + STREAM
    .findFirst()
    .orElse(null);

// THREAD + LAMBDA pour traiter la commande
cooks.submit(() -> process(o));                // THREAD + LAMBDA

// THREADS + SEMAPHORE pour limiter la concurrence
available.acquire();                           // Attend qu'un serveur se libère
// ... traiter commande
available.release();                           // Libère un serveur
```

### Pourquoi c'est important ?

1. **STREAMS** = Comment traiter les données (filtrer, transformer, chercher)
2. **THREADS** = Comment exécuter les choses en parallèle (multiple cuisiniers, serveurs)
3. **LAMBDAS** = Comment écrire le code de façon concise et moderne

**Sans ces concepts:** Code verbeux, lent, pas moderne
**Avec ces concepts:** Code lisible, rapide, moderne et Java 8+

---

## 📊 Tableau Récapitulatif

| Concept | Où utilisé | Pourquoi | Exemple |
|---------|-----------|---------|---------|
| **STREAMS** | `UserService.java`, `ServerManager.java` | Filtrer/chercher dans des collections de manière fonctionnelle | `users.stream().filter(u -> u.getId().equals(id)).findFirst()` |
| **THREADS** | `KitchenService.java`, `ServerManager.java` | Exécuter plusieurs tâches en parallèle | Cuisiniers qui préparent en parallèle avec `ExecutorService` |
| **LAMBDAS** | Partout: `UserService`, `KitchenService`, `ServerManager` | Écrire du code concis pour les conditions, boucles, runnable | `() -> process(o)`, `s -> !s.isBusy()` |

---

## 🚀 Comment exécuter le projet

```powershell
# Compilation
.\compile.bat

# Exécution
.\run.bat
```

L'application vous propose un menu interactif pour:
- 👨‍💼 **Connectez-vous comme ADMIN** (username: admin, password: admin123)
- 👨‍🍳 **Connectez-vous comme CUISINIER**
- 🍽️ **Connectez-vous comme SERVEUR**
- 👤 **Entrez comme CLIENT**

---

## ✨ Conclusion

Ce projet démontre les **3 piliers de Java moderne**:
- ✅ **STREAMS** - Manipulation élégante des données
- ✅ **THREADS** - Concurrence et parallélisation
- ✅ **LAMBDAS** - Syntaxe moderne et concise

Tous ensemble pour créer une **simulation de restaurant réaliste et performante** ! 🍕🍝🍳
