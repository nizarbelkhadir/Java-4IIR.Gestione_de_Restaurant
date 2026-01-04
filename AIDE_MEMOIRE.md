# 📋 AIDE-MÉMOIRE RAPIDE - Questions Prof

## 🎯 STREAMS (3 utilisations)

### 1. ServerManager.java - Ligne ~66
```java
ServerStaff server = servers.stream()
    .filter(s -> !s.isBusy())
    .findFirst()
    .orElse(null);
```
**Pourquoi?** Trouver le premier serveur libre de manière déclarative (plus lisible qu'une boucle for).

### 2. UserService.java - Ligne ~29
```java
return users.stream()
    .filter(u -> u.getId().equals(id))
    .findFirst();
```
**Pourquoi?** Recherche dans une collection - code concis pour le CRUD de l'Admin.

### 3. Main.java - Ligne ~104
```java
long readyCount = allOrders.stream()
    .filter(o -> o.getStatus() == Order.Status.READY)
    .count();
```
**Pourquoi?** Compter les éléments qui satisfont une condition (évite un compteur manuel).

---

## 🧵 THREADS (2 utilisations principales)

### 1. KitchenService.java - Constructeur
```java
ExecutorService cooks = Executors.newFixedThreadPool(2);

Thread dispatcher = new Thread(() -> {
    while (...) {
        Order o = queue.take();
        cooks.submit(() -> process(o));
    }
});
dispatcher.start();
```
**Pourquoi?**
- **Pool de threads**: Simule 2 cuisiniers qui travaillent en **parallèle**
- **Thread dispatcher**: Écoute **continuellement** les nouvelles commandes (asynchrone)

### 2. ServerManager.java - Constructeur
```java
ExecutorService serverExecutor = Executors.newCachedThreadPool();

Thread dispatcher = new Thread(this::dispatch);
dispatcher.start();
```
**Pourquoi?**
- **Pool dynamique**: Gère plusieurs serveurs en même temps
- **Dispatcher**: Traite les demandes clients de manière **asynchrone**

---

## 🔧 LAMBDAS (4 utilisations)

### 1. Dans les streams (filter)
```java
.filter(s -> !s.isBusy())
.filter(u -> u.getId().equals(id))
```
**Pourquoi?** Remplace les classes anonymes - plus court et lisible.

### 2. Créer des Runnable
```java
Thread t = new Thread(() -> dispatch());
```
**Avant Java 8**:
```java
Thread t = new Thread(new Runnable() {
    public void run() { dispatch(); }
});
```
**Pourquoi?** Réduit le boilerplate (code répétitif).

### 3. Soumettre des tâches
```java
cooks.submit(() -> process(o));
serverExecutor.submit(() -> handleWithServer(s, req));
```
**Pourquoi?** Créer une tâche inline sans classe séparée.

### 4. removeIf (UserService)
```java
users.removeIf(u -> u.getId().equals(id));
```
**Pourquoi?** Méthode moderne pour supprimer avec condition.

---

## 🔐 CONCURRENCE (Outils avancés)

### Semaphore (ServerManager.java)
```java
Semaphore available = new Semaphore(servers.size());

// Dans dispatch()
available.acquire();  // Attend si tous serveurs occupés
...
available.release();  // Libère pour le prochain client
```
**Pourquoi?**
- Implémente: "le premier serveur libre prend le prochain client"
- Bloque automatiquement quand tous sont occupés
- File d'attente intégrée (fairness)

### BlockingQueue
```java
BlockingQueue<Order> queue = new LinkedBlockingQueue<>();
queue.offer(order);  // Ajoute
Order o = queue.take();  // Retire (bloque si vide)
```
**Pourquoi?**
- **Thread-safe**: Plusieurs threads peuvent ajouter/retirer sans problème
- **Évite le polling**: take() bloque proprement au lieu de boucler activement

### AtomicBoolean (ServerStaff.java)
```java
private final AtomicBoolean busy = new AtomicBoolean(false);
public boolean isBusy() { return busy.get(); }
public void setBusy(boolean b) { busy.set(b); }
```
**Pourquoi?**
- **Thread-safe**: Plusieurs threads lisent/écrivent sans race condition
- Alternative aux synchronized pour un simple flag

---

## 📊 FONCTIONNALITÉS IMPLÉMENTÉES

✅ **Client**: Self-service OU appel serveur  
✅ **Serveur**: Reçoit appels, va à table, prend commande, envoie à cuisine  
✅ **Cuisine**: Reçoit commandes (self + serveur), prépare, confirme prêt  
✅ **Admin**: CRUD users (addUser, findById, deleteUser, list)  
✅ **Serveur occupé/libre**: Géré par AtomicBoolean + Semaphore  
✅ **File d'attente**: Si tous occupés → attend premier libre (Semaphore)  
✅ **Serveur libre après envoi**: Dans finally de handleWithServer()  

---

## 🎬 DEMO RAPIDE

1. **Scénario 1**: Client self-service → direct à cuisine
2. **Scénario 2**: Client appelle serveur → serveur va, prend commande, envoie
3. **Scénario 3**: 5 clients, 2 serveurs → file d'attente visible

---

## 💡 PHRASES CLÉS POUR LE PROF

**Sur Streams**:  
"J'ai utilisé streams pour rendre le code plus déclaratif. Au lieu de boucler manuellement, je dis simplement 'filtre les serveurs libres et prends le premier', ce qui est plus lisible et maintenable."

**Sur Threads**:  
"Les threads permettent de simuler la nature asynchrone et parallèle d'un vrai restaurant. La cuisine peut préparer plusieurs plats en même temps grâce au thread pool, et le dispatcher écoute continuellement les nouvelles demandes."

**Sur Lambdas**:  
"Les lambdas réduisent le code boilerplate. Au lieu de créer des classes anonymes, j'écris directement la logique en une ligne, ce qui suit le style Java moderne."

**Sur Semaphore**:  
"Le Semaphore résout le problème central: garantir qu'on n'assigne pas plus de clients que de serveurs disponibles. Quand tous sont occupés, les demandes sont automatiquement mises en attente, et le premier serveur qui se libère prend la prochaine demande."

---

## 🚨 SI LE PROF DEMANDE: "Montre-moi dans le code"

**Streams**: Ouvre [ServerManager.java](src/main/java/com/example/restaurant/service/ServerManager.java#L66)  
**Threads**: Ouvre [KitchenService.java](src/main/java/com/example/restaurant/service/KitchenService.java#L19)  
**Lambdas**: Ouvre [ServerManager.java](src/main/java/com/example/restaurant/service/ServerManager.java#L41)  
**Semaphore**: Ouvre [ServerManager.java](src/main/java/com/example/restaurant/service/ServerManager.java#L15)  

---

## ✅ CHECKLIST PRÉSENTATION

- [ ] Java installé et testé (java -version)
- [ ] Projet compile (compile.bat)
- [ ] Démo fonctionne (run.bat)
- [ ] Ouvrir VS Code avec le projet
- [ ] Marquer les lignes importantes dans le code
- [ ] Relire GUIDE_COMPLET.md une fois
- [ ] Tester d'expliquer Semaphore à voix haute

---

**Confiance! Vous maîtrisez le code. 💪**
