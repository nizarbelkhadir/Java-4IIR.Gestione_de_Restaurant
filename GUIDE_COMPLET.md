# 🎓 GUIDE COMPLET - Projet Gestion Restaurant

## ⚠️ PRÉREQUIS

### Installation de Java (OBLIGATOIRE)
Vous devez installer Java JDK avant d'exécuter le projet:

1. **Télécharger Java JDK** (version 11 ou supérieure):
   - Site officiel: https://www.oracle.com/java/technologies/downloads/
   - Ou OpenJDK: https://adoptium.net/

2. **Installer Java** et cocher "Add to PATH" pendant l'installation

3. **Vérifier l'installation**:
   ```powershell
   java -version
   javac -version
   ```

## 🚀 EXÉCUTION DU PROJET

### Étape 1: Ouvrir PowerShell
- Clic droit sur le dossier du projet
- "Ouvrir dans le Terminal" ou "Open PowerShell here"

### Étape 2: Compiler
```powershell
.\compile.bat
```

### Étape 3: Exécuter
```powershell
.\run.bat
```

## 📚 EXPLICATION COMPLÈTE DU CODE

### 🎯 Vue d'ensemble
Le projet simule un restaurant avec:
- **Clients**: commandent en self-service ou via serveur
- **Serveurs**: prennent les commandes et les transmettent
- **Cuisine**: prépare les plats
- **Admin**: gère les utilisateurs

---

## 🧩 PARTIE 1: MODÈLES DE DONNÉES

### 1.1 User.java (Classe abstraite de base)
```java
public abstract class User {
    protected String id;
    protected String name;
}
```
**Explication**: Classe parent pour tous les utilisateurs. Utilise l'héritage pour partager les attributs communs.

### 1.2 Client.java, ServerStaff.java, KitchenStaff.java, Admin.java
Ces classes **héritent** de User et représentent les différents rôles.

**ServerStaff** a un attribut spécial:
```java
private final AtomicBoolean busy = new AtomicBoolean(false);
```
**Pourquoi AtomicBoolean?** 
- Thread-safe: plusieurs threads peuvent lire/écrire sans corruption
- Évite les race conditions quand plusieurs threads vérifient si un serveur est libre

### 1.3 MenuItem.java
Représente un plat du menu avec id, nom, prix.

### 1.4 Order.java
Représente une commande avec:
- Liste de MenuItem
- Statut: RECEIVED → IN_PREPARATION → READY
- Nom du client

---

## 🧩 PARTIE 2: STOCKAGE (JsonStorage.java)

```java
public class JsonStorage {
    public static <T> List<T> readList(File file, Class<T> clazz)
    public static <T> void writeList(File file, List<T> list)
}
```

**Explication**:
- Utilise les **generics** (`<T>`) pour fonctionner avec n'importe quel type
- Version simplifiée pour démo (pas de vraie sérialisation JSON)
- Dans un vrai projet, utiliserait Gson ou Jackson

---

## 🧩 PARTIE 3: SERVICES

### 3.1 UserService.java (CRUD pour Admin)

#### UTILISATION DE STREAMS #1
```java
public synchronized Optional<User> findById(String id) { 
    return users.stream()
                .filter(u -> u.getId().equals(id))  // ← LAMBDA
                .findFirst(); 
}
```

**Explication prof**:
- **Stream**: transforme la liste en pipeline de données
- **Lambda** `u -> u.getId().equals(id)`: fonction anonyme qui teste chaque user
- **filter()**: garde seulement les users qui correspondent
- **findFirst()**: retourne le premier trouvé (ou vide)

**Pourquoi?** Plus lisible et expressif qu'une boucle for avec break.

#### UTILISATION DE STREAMS #2
```java
public synchronized void deleteUser(String id) {
    users.removeIf(u -> u.getId().equals(id));  // ← LAMBDA
}
```

**Explication**: `removeIf` utilise un **lambda** pour définir la condition de suppression.

---

### 3.2 KitchenService.java (Cuisine avec threads)

#### UTILISATION DE THREADS #1
```java
private final BlockingQueue<Order> queue = new LinkedBlockingQueue<>();
private final ExecutorService cooks = Executors.newFixedThreadPool(2);
```

**Explication**:
- **BlockingQueue**: file d'attente thread-safe pour les commandes
- **ExecutorService**: pool de 2 threads (= 2 cuisiniers qui travaillent en parallèle)

#### UTILISATION DE LAMBDAS #1
```java
Runnable dispatcher = () -> {
    while (!Thread.currentThread().isInterrupted()) {
        Order o = queue.take();
        cooks.submit(() -> process(o));  // ← LAMBDA dans lambda!
    }
};
```

**Explication**:
- Premier lambda `() -> {...}`: crée un Runnable sans classe anonyme
- Deuxième lambda `() -> process(o)`: soumet la tâche au pool de threads

**Pourquoi threads?** 
- La cuisine prépare plusieurs plats **en même temps** (parallélisme réel)
- Le dispatcher **attend continuellement** de nouvelles commandes (asynchrone)

---

### 3.3 ServerManager.java (Cœur du système)

#### UTILISATION DE SEMAPHORE (Concept clé!)
```java
private final Semaphore available;

public ServerManager(List<ServerStaff> serverList, OrderService orderService) {
    this.available = new Semaphore(servers.size()); // 2 permits si 2 serveurs
}
```

**Explication pour le prof**:
- **Semaphore**: mécanisme de synchronisation qui limite l'accès à une ressource
- Ici: limite = nombre de serveurs disponibles
- Si 2 serveurs → 2 "permits"

**Fonctionnement**:
1. Client appelle → `available.acquire()` (prend 1 permit)
2. Si permits = 0 → **bloque et attend** (file d'attente automatique)
3. Serveur finit → `available.release()` (rend 1 permit)
4. Premier client en attente est **automatiquement débloqué**

**Pourquoi?** Implémente exactement la règle: "si tous les serveurs sont occupés, le premier qui se libère prend le prochain client".

#### UTILISATION DE STREAMS #3
```java
ServerStaff server = servers.stream()
    .filter(s -> !s.isBusy())  // ← LAMBDA: filtre serveurs libres
    .findFirst()               // Prend le premier
    .orElse(null);
```

**Explication pour le prof**:
- **Alternative au foreach**: évite de boucler manuellement
- **Déclaratif**: on dit "trouve le premier serveur non occupé"
- **Lambda**: `s -> !s.isBusy()` est une fonction qui teste chaque serveur

**Sans stream** (version impérative):
```java
ServerStaff server = null;
for (ServerStaff s : servers) {
    if (!s.isBusy()) {
        server = s;
        break;
    }
}
```

**Avec stream**: plus court, plus lisible, intention claire.

#### UTILISATION DE THREADS #2
```java
Thread dispatcher = new Thread(this::dispatch, "Server-Dispatcher");
dispatcher.setDaemon(true);
dispatcher.start();
```

**Explication**:
- **Thread daemon**: tourne en arrière-plan
- **Reference de méthode** `this::dispatch`: équivalent à `() -> this.dispatch()`
- Écoute continuellement la queue des demandes clients

#### UTILISATION DE LAMBDAS #2
```java
serverExecutor.submit(() -> handleWithServer(server, req));
```

**Explication**: 
- Soumet une tâche au thread pool des serveurs
- Lambda crée un Runnable inline

---

### 3.4 OrderService.java

Simple service qui:
1. Crée une commande avec UUID unique
2. L'envoie à la cuisine

---

## 🎬 PARTIE 4: MAIN (Démo)

### Scénario complet avec émojis pour visualisation

```java
// SCÉNARIO 1: Self-service
serverManager.requestService(
    new ClientRequest("Marie", items, true)  // selfService = true
);
// → Commande va directement à la cuisine (pas de serveur)
```

```java
// SCÉNARIO 2: Avec serveur
serverManager.requestService(
    new ClientRequest("Jean", items, false)  // selfService = false
);
// → Demande mise en queue
// → Semaphore.acquire() (attend un serveur libre)
// → Stream trouve un serveur disponible
// → Serveur va à la table
// → Envoie commande à cuisine
// → Serveur devient libre (Semaphore.release())
```

```java
// SCÉNARIO 3: Saturation (5 clients, 2 serveurs)
for (int i = 3; i <= 7; i++) {
    serverManager.requestService(...);
}
// → Clients 1-2: servis immédiatement (2 permits disponibles)
// → Clients 3-5: bloqués sur acquire() (0 permits)
// → Dès qu'un serveur finit → release() → client 3 débloqué
// → Et ainsi de suite...
```

#### UTILISATION DE STREAMS #4 (Dans Main)
```java
long readyCount = allOrders.stream()
    .filter(o -> o.getStatus() == Order.Status.READY)
    .count();
```

**Explication**: 
- Compte combien de commandes sont prêtes
- **Déclaratif**: "filtre les prêtes et compte-les"
- Alternative à un compteur manuel dans une boucle

---

## 📊 TABLEAU RÉCAPITULATIF DES CONCEPTS

| Concept | Fichier | Ligne approx. | Raison d'utilisation |
|---------|---------|---------------|----------------------|
| **Stream** | ServerManager.java | 66 | Trouver premier serveur libre |
| **Stream** | UserService.java | 29, 34 | Filtrer/chercher users (CRUD) |
| **Stream** | Main.java | 104 | Compter commandes prêtes |
| **Lambda** | KitchenService.java | 21, 26 | Créer Runnable concis |
| **Lambda** | ServerManager.java | 66, 86 | Filter + submit tasks |
| **Lambda** | UserService.java | 29, 34 | Prédicats de filtrage |
| **Thread** | KitchenService.java | 19-25 | Dispatcher cuisine (asynchrone) |
| **Thread** | ServerManager.java | 41-44 | Dispatcher serveurs (asynchrone) |
| **ExecutorService** | KitchenService.java | 13 | Pool de cuisiniers (parallélisme) |
| **ExecutorService** | ServerManager.java | 18 | Pool de serveurs (parallélisme) |
| **Semaphore** | ServerManager.java | 15, 60-62 | Limite serveurs disponibles |
| **BlockingQueue** | KitchenService.java | 12 | Queue thread-safe commandes |
| **BlockingQueue** | ServerManager.java | 14 | Queue thread-safe demandes clients |
| **AtomicBoolean** | ServerStaff.java | 6 | Flag thread-safe occupation |

---

## 🎓 RÉPONSES AUX QUESTIONS DU PROF

### Q1: "Pourquoi utiliser des streams?"
**R**: 
- Code plus **déclaratif** et **lisible**
- Évite les boucles manuelles avec variables temporaires
- Facilite le **chaînage d'opérations** (filter → map → collect)
- Exploite le paradigme **fonctionnel** de Java 8+

Exemple concret: 
```java
// Impératif (old style)
List<User> admins = new ArrayList<>();
for (User u : users) {
    if (u instanceof Admin) admins.add(u);
}

// Déclaratif avec stream
List<User> admins = users.stream()
    .filter(u -> u instanceof Admin)
    .collect(Collectors.toList());
```

### Q2: "Pourquoi utiliser des threads?"
**R**:
- **Asynchronisme**: Le client ne bloque pas en attendant que sa commande soit prête
- **Parallélisme**: Plusieurs cuisiniers préparent en même temps (meilleure performance)
- **Réactivité**: Le système répond immédiatement aux nouvelles demandes
- **Réalisme**: Simule le fonctionnement réel d'un restaurant

### Q3: "Pourquoi utiliser des lambdas?"
**R**:
- **Concision**: Moins de code boilerplate
- **Lisibilité**: Intention claire en une ligne
- **Moderne**: Style Java 8+ attendu en entreprise

Comparaison:
```java
// AVANT Java 8 (classe anonyme)
Thread t = new Thread(new Runnable() {
    @Override
    public void run() {
        dispatch();
    }
});

// APRÈS Java 8 (lambda)
Thread t = new Thread(() -> dispatch());
```

### Q4: "Pourquoi BlockingQueue?"
**R**:
- **Thread-safe**: Plusieurs threads peuvent ajouter/retirer sans corruption
- **Synchronisation automatique**: `take()` bloque si vide (évite polling actif)
- **Pattern Producteur-Consommateur**: Clients produisent des demandes, dispatcher consomme

### Q5: "Pourquoi Semaphore et pas synchronized?"
**R**:
- **Limiter l'accès** à N ressources (pas juste 1 comme un lock)
- **File d'attente intégrée**: gère automatiquement l'ordre d'attente
- **Fairness optionnelle**: garantit que le premier qui attend sera le premier servi
- **Flexibilité**: peut acquérir/libérer plusieurs permits à la fois

### Q6: "Comment le serveur devient libre?"
**R**: Dans la méthode `handleWithServer()`:
```java
finally {
    server.setBusy(false);      // 1. Marque serveur comme libre
    available.release();        // 2. Libère un permit
}
```
Le `finally` garantit que le serveur est **toujours** libéré, même en cas d'erreur.

---

## 🔍 FLUX D'EXÉCUTION DÉTAILLÉ

### Cas: Client appelle un serveur

```
1. Main.java
   ↓ serverManager.requestService(..., selfService=false)

2. ServerManager.java
   ↓ requests.offer(req)  // Ajoute à la queue
   
3. Thread Dispatcher (tourne en boucle)
   ↓ req = requests.take()  // Récupère demande
   ↓ available.acquire()    // ⏱️ ATTEND si serveurs occupés
   
4. Stream + Lambda
   ↓ servers.stream().filter(s -> !s.isBusy()).findFirst()
   ↓ Trouve serveur libre
   
5. ExecutorService
   ↓ serverExecutor.submit(() -> handleWithServer(...))
   
6. Thread du pool
   ↓ handleWithServer() exécuté
   ↓ sleep(500-1300) // Simule aller à table
   ↓ orderService.createOrder(...)
   
7. OrderService
   ↓ kitchen.submitOrder(order)
   
8. KitchenService
   ↓ queue.offer(order)
   ↓ Dispatcher cuisine prend commande
   ↓ cooks.submit(() -> process(order))
   
9. Thread cuisinier
   ↓ sleep(1000-3000) // Simule préparation
   ↓ order.setStatus(READY)
   ↓ Affiche "order READY"
   
10. Retour à ServerManager
    ↓ finally { server.setBusy(false); available.release(); }
    ↓ ✅ Serveur libre pour prochain client
```

---

## 💡 POINTS IMPORTANTS POUR LA PRÉSENTATION

1. **Semaphore résout le problème principal**: garantir qu'on attend un serveur libre
2. **Streams rendent le code lisible**: facile de voir ce qu'on cherche
3. **Lambdas évitent le boilerplate**: code plus court et clair
4. **Threads simulent la réalité**: restaurant = événements simultanés
5. **BlockingQueue évite le polling**: efficacité CPU (pas de boucle active)

---

## 🧪 TEST MANUEL

Après compilation et exécution, vous devriez voir:
```
════════════════════════════════════════
   RESTAURANT MANAGEMENT SYSTEM DEMO
════════════════════════════════════════

--- Initialisation des services ---
✓ Serveurs créés: Alice, Bob
✓ Menu initialisé
✓ Admin created users: 3 users in system

════════════════════════════════════════
   SCÉNARIO 1: SELF-SERVICE
════════════════════════════════════════
📱 Client (self-service) Marie placed order directly.
🍳 Kitchen: received order ORD-xxx from Marie

════════════════════════════════════════
   SCÉNARIO 2: APPEL SERVEUR
════════════════════════════════════════
🔔 Client Jean requested a server (queued).
👨‍🍳 Server Alice goes to table for Jean
📋 Server Alice submitted order for Jean
✔️  Server Alice is now FREE again
🍳 Kitchen: received order ORD-yyy from Jean

════════════════════════════════════════
   SCÉNARIO 3: TOUS SERVEURS OCCUPÉS
════════════════════════════════════════
...
(Vous verrez l'alternance serveurs occupés/libres)
...

✅ Kitchen: order READY ORD-xxx for Marie
✅ Kitchen: order READY ORD-yyy for Jean
...

════════════════════════════════════════
   RÉSUMÉ FINAL
════════════════════════════════════════
Total commandes créées: 7
Commandes prêtes: 7

✅ DEMO TERMINÉE
```

---

## 📝 CHECKLIST AVANT PRÉSENTATION

- [ ] Java JDK installé
- [ ] Projet compile sans erreur
- [ ] Démo s'exécute et affiche les logs
- [ ] Comprendre le rôle de chaque classe
- [ ] Savoir expliquer Semaphore, BlockingQueue, Streams, Lambdas
- [ ] Prêt à montrer les lignes de code spécifiques

---

**Bon courage pour votre présentation! 🚀**
