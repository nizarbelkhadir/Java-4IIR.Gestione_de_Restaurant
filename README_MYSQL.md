# 🗄️ Configuration MySQL avec XAMPP pour le Projet Restaurant

## 📋 Prérequis

### 1. Installer XAMPP
- Téléchargez XAMPP depuis: https://www.apachefriends.org/
- Installez XAMPP (suivez l'assistant d'installation)
- Par défaut, XAMPP s'installe dans `C:\xampp`

### 2. Télécharger le Driver MySQL JDBC
- Téléchargez **MySQL Connector/J** depuis: https://dev.mysql.com/downloads/connector/j/
- Ou téléchargez directement: https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar
- Placez le fichier JAR dans le dossier `lib/` de votre projet

---

## 🚀 Configuration Étape par Étape

### Étape 1: Démarrer XAMPP

1. Ouvrez **XAMPP Control Panel**
2. Démarrez les modules suivants:
   - ✅ **Apache** (pour phpMyAdmin)
   - ✅ **MySQL** (pour la base de données)

![XAMPP Control Panel](https://i.imgur.com/XAMPPExample.png)

### Étape 2: Créer la Base de Données

1. Ouvrez votre navigateur web
2. Allez sur: `http://localhost/phpmyadmin`
3. Cliquez sur l'onglet **"SQL"**
4. Copiez tout le contenu du fichier `database/schema.sql`
5. Collez-le dans la zone de texte SQL
6. Cliquez sur **"Exécuter"** (Go)

### Étape 3: Vérifier la Création

Après l'exécution du script SQL, vérifiez que:
- ✅ La base de données `restaurant_db` existe
- ✅ La table `users` existe avec 4 utilisateurs par défaut
- ✅ La table `orders` existe (vide au début)

Vous pouvez vérifier en cliquant sur `restaurant_db` dans le menu de gauche.

---

## 📂 Structure du Projet avec MySQL

```
Java-4IIR.Gestione_de_Restaurant/
├── database/
│   └── schema.sql                    # Script SQL à exécuter
├── lib/
│   └── mysql-connector-j-8.3.0.jar  # Driver JDBC MySQL
├── src/main/java/com/example/restaurant/
│   └── storage/
│       ├── DatabaseConfig.java       # Configuration connexion
│       ├── DatabaseStorage.java      # Accès base de données
│       └── JsonStorage.java          # Ancienne version (à garder en backup)
```

---

## ⚙️ Configuration de la Connexion

Le fichier `DatabaseConfig.java` contient les paramètres par défaut pour XAMPP:

```java
// Configuration par défaut XAMPP
private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = ""; // Vide par défaut sur XAMPP
```

### ⚠️ Si vous avez modifié le mot de passe root de MySQL:

Modifiez la ligne dans `DatabaseConfig.java`:
```java
private static final String DB_PASSWORD = "votre_mot_de_passe";
```

---

## 🔧 Compilation avec le Driver JDBC

### Option 1: Avec le fichier batch (recommandé)

Créez un nouveau fichier `compile-db.bat`:

```batch
@echo off
echo Compilation du projet avec MySQL JDBC...
javac -cp "lib\mysql-connector-j-8.3.0.jar" -d bin src\main\java\com\example\restaurant\**\*.java
if %ERRORLEVEL% EQU 0 (
    echo Compilation reussie!
) else (
    echo Erreur de compilation!
)
pause
```

### Option 2: Ligne de commande

```bash
javac -cp "lib\mysql-connector-j-8.3.0.jar" -d bin src\main\java\com\example\restaurant\**\*.java
```

---

## ▶️ Exécution avec le Driver JDBC

### Option 1: Avec le fichier batch

Créez un nouveau fichier `run-db.bat`:

```batch
@echo off
echo Execution du projet avec MySQL...
java -cp "bin;lib\mysql-connector-j-8.3.0.jar" com.example.restaurant.InteractiveMain
pause
```

### Option 2: Ligne de commande

```bash
java -cp "bin;lib\mysql-connector-j-8.3.0.jar" com.example.restaurant.InteractiveMain
```

⚠️ **IMPORTANT**: Le `;` sépare les chemins sur Windows. Sur Linux/Mac, utilisez `:` à la place.

---

## 👥 Utilisateurs par Défaut

Après avoir exécuté `schema.sql`, vous aurez ces comptes:

| Username  | Password  | Type     | Description           |
|-----------|-----------|----------|-----------------------|
| admin     | admin123  | ADMIN    | Administrateur        |
| serveur1  | pass123   | SERVER   | Serveur               |
| chef1     | pass123   | KITCHEN  | Chef de cuisine       |
| client1   | pass123   | CLIENT   | Client (test)         |

---

## 🧪 Test de Connexion

Pour tester si tout fonctionne, créez un fichier de test `TestConnection.java`:

```java
package com.example.restaurant;

import com.example.restaurant.storage.DatabaseStorage;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Test de connexion à MySQL...");
        
        if (DatabaseStorage.testConnection()) {
            System.out.println("✅ Connexion réussie!");
            DatabaseStorage.initializeTables();
        } else {
            System.out.println("❌ Connexion échouée!");
            System.out.println("Vérifiez que:");
            System.out.println("1. XAMPP MySQL est démarré");
            System.out.println("2. La base restaurant_db existe");
            System.out.println("3. Le driver MySQL JDBC est dans lib/");
        }
    }
}
```

Compilez et exécutez:
```bash
javac -cp "lib\mysql-connector-j-8.3.0.jar" -d bin src\main\java\com\example\restaurant\TestConnection.java
java -cp "bin;lib\mysql-connector-j-8.3.0.jar" com.example.restaurant.TestConnection
```

---

## 🔍 Dépannage

### Erreur: "Driver MySQL JDBC introuvable"
- ✅ Vérifiez que `mysql-connector-j-8.3.0.jar` est dans `lib/`
- ✅ Vérifiez que vous compilez/exécutez avec `-cp "lib\mysql-connector-j-8.3.0.jar"`

### Erreur: "Access denied for user 'root'@'localhost'"
- ✅ Vérifiez le mot de passe dans `DatabaseConfig.java`
- ✅ Par défaut sur XAMPP, le mot de passe est vide

### Erreur: "Unknown database 'restaurant_db'"
- ✅ Exécutez le script `database/schema.sql` dans phpMyAdmin
- ✅ Vérifiez que la base de données apparaît dans phpMyAdmin

### Erreur: "Communications link failure"
- ✅ Vérifiez que MySQL est démarré dans XAMPP Control Panel
- ✅ Vérifiez que le port 3306 n'est pas bloqué

---

## 📊 Visualisation des Données

### Dans phpMyAdmin:
1. Allez sur `http://localhost/phpmyadmin`
2. Cliquez sur `restaurant_db`
3. Cliquez sur une table (`users` ou `orders`)
4. Vous verrez toutes les données stockées

### Requêtes SQL utiles:

```sql
-- Voir tous les utilisateurs
SELECT * FROM users;

-- Voir toutes les commandes
SELECT * FROM orders ORDER BY timestamp DESC;

-- Compter les commandes par statut
SELECT status, COUNT(*) as total FROM orders GROUP BY status;

-- Voir les commandes récentes
SELECT order_id, client_name, status, FROM_UNIXTIME(timestamp/1000) as date_commande 
FROM orders 
ORDER BY timestamp DESC 
LIMIT 10;
```

---

## 🔄 Migration depuis JSON

Si vous aviez déjà des données dans `users.json` et `orders.json`, vous pouvez:

1. **Garder les anciens fichiers JSON comme backup**
2. **Les données par défaut sont déjà dans la base** via `schema.sql`
3. **Pour migrer des données JSON personnalisées**, créez un script de migration (optionnel)

---

## 📝 Différences JSON vs MySQL

| Aspect              | JSON (Ancien)              | MySQL (Nouveau)            |
|---------------------|----------------------------|----------------------------|
| Stockage            | Fichiers texte             | Base de données            |
| Performance         | Lent pour grandes données  | Rapide avec index          |
| Recherche           | Lecture complète           | Requêtes SQL optimisées    |
| Concurrence         | Problèmes possibles        | Transactions ACID          |
| Intégrité           | Manuelle                   | Contraintes SQL            |
| Visualisation       | Éditeur texte              | phpMyAdmin                 |

---

## ✅ Checklist de Vérification

Avant d'exécuter le projet:

- [ ] XAMPP installé
- [ ] Apache et MySQL démarrés dans XAMPP
- [ ] Base de données `restaurant_db` créée via phpMyAdmin
- [ ] Script `schema.sql` exécuté (tables créées)
- [ ] Driver MySQL JDBC téléchargé et placé dans `lib/`
- [ ] Projet compilé avec le driver JDBC dans le classpath
- [ ] Test de connexion réussi

---

## 🎯 Pour Aller Plus Loin

### Sécurité:
- Utilisez des **passwords hashés** (BCrypt) au lieu de texte clair
- Créez un **utilisateur MySQL dédié** (pas root) avec permissions limitées

### Performance:
- Ajoutez des **index** sur les colonnes fréquemment recherchées
- Utilisez des **prepared statements** (déjà fait dans DatabaseStorage)

### Fonctionnalités:
- Ajoutez l'**historique des modifications**
- Implémentez des **transactions** pour les opérations complexes
- Ajoutez des **logs** dans une table dédiée

---

## 📞 Support

En cas de problème:
1. Vérifiez les logs dans XAMPP Control Panel (bouton "Logs")
2. Consultez les messages d'erreur dans la console Java
3. Vérifiez la connexion avec `TestConnection.java`

---

**Bonne utilisation de votre application avec MySQL! 🚀**
