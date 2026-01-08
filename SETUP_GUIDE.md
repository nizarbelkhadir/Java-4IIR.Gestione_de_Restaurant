# 🚀 GUIDE D'INSTALLATION - Pour Nouveaux Contributeurs

## Après avoir cloné/pull le projet

### ✅ Prérequis
- Java JDK installé
- XAMPP installé

---

## 📋 ÉTAPES D'INSTALLATION (5 minutes)

### 1️⃣ Télécharger le Driver MySQL JDBC

**Option A : Téléchargement automatique**
```powershell
# Exécuter dans le dossier du projet
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar" -OutFile "lib\mysql-connector-j-8.3.0.jar"
```

**Option B : Téléchargement manuel**
1. Téléchargez : https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar
2. Placez le fichier dans le dossier `lib/` du projet

---

### 2️⃣ Démarrer XAMPP

1. Ouvrir **XAMPP Control Panel**
2. Cliquer sur **"Start"** pour **MySQL**
3. Cliquer sur **"Start"** pour **Apache**

---

### 3️⃣ Créer la Base de Données

1. Ouvrir votre navigateur : http://localhost/phpmyadmin
2. Cliquer sur l'onglet **"SQL"**
3. Copier TOUT le contenu du fichier `database/schema.sql`
4. Coller dans la zone de texte SQL
5. Cliquer sur **"Exécuter"** (ou "Go")

**✅ Vérification** : Dans le menu de gauche, vous devez voir `restaurant_db` avec les tables `users` et `orders`

---

### 4️⃣ Tester la Connexion

```bash
test-connection.bat
```

Vous devez voir :
```
✅ Test de connexion à MySQL... OK
✅ Vérification des tables... OK
✅ Lecture des utilisateurs... OK (4 utilisateurs trouvés)
```

---

### 5️⃣ Compiler le Projet

```bash
compile-db.bat
```

---

### 6️⃣ Lancer l'Application

```bash
run-db.bat
```

---

## 🔑 Comptes de Test

| Username | Password | Rôle |
|----------|----------|------|
| admin | admin123 | Administrateur |
| serveur1 | pass123 | Serveur |
| chef1 | pass123 | Cuisinier |
| client1 | pass123 | Client |

---

## ⚠️ Problèmes Fréquents

### "Driver JDBC introuvable"
→ Téléchargez le fichier JAR et placez-le dans `lib/`

### "Unknown database 'restaurant_db'"
→ Exécutez le script `database/schema.sql` dans phpMyAdmin

### "Connection refused"
→ Démarrez MySQL dans XAMPP Control Panel

### "Compilation error"
→ Vérifiez que le driver JDBC est dans `lib/`

---

## 📁 Structure Attendue

```
Projet/
├── lib/
│   └── mysql-connector-j-8.3.0.jar  ← À télécharger
├── database/
│   └── schema.sql                    ← Exécuter dans phpMyAdmin
├── bin/                              ← Créé automatiquement
└── src/
```

---

## 🆘 Support

- **Guide détaillé** : Voir `README_MYSQL.md`
- **Guide rapide** : Voir `MIGRATION_MYSQL.md`
- **Tests** : Utiliser `test-all.bat`

---

## 🎯 Checklist Rapide

- [ ] XAMPP installé
- [ ] Driver JDBC téléchargé dans `lib/`
- [ ] MySQL démarré dans XAMPP
- [ ] Base de données `restaurant_db` créée via phpMyAdmin
- [ ] Script `database/schema.sql` exécuté
- [ ] Test de connexion réussi (`test-connection.bat`)
- [ ] Projet compilé (`compile-db.bat`)
- [ ] Application lancée (`run-db.bat`)

---

**Une fois ces étapes complétées, le projet est prêt ! 🚀**
