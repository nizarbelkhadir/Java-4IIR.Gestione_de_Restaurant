# 🚀 GUIDE RAPIDE - Migration vers MySQL

## ⚡ Installation Express (5 minutes)

### 1. Télécharger les fichiers nécessaires
- **XAMPP**: https://www.apachefriends.org/download.html
- **MySQL JDBC Driver**: https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar

### 2. Installation
```
1. Installer XAMPP
2. Créer un dossier "lib" dans votre projet
3. Placer mysql-connector-j-8.3.0.jar dans lib/
```

### 3. Configuration Base de Données
```
1. Démarrer XAMPP (Apache + MySQL)
2. Ouvrir http://localhost/phpmyadmin
3. Onglet SQL
4. Copier/coller tout le contenu de database/schema.sql
5. Cliquer "Exécuter"
```

### 4. Test
```bash
test-connection.bat
```

### 5. Compilation et Exécution
```bash
compile-db.bat
run-db.bat
```

---

## 📁 Structure des Nouveaux Fichiers

```
Projet/
├── database/
│   └── schema.sql              ← Script SQL à exécuter
├── lib/
│   └── mysql-connector-j.jar   ← Driver JDBC à télécharger
├── src/main/java/.../storage/
│   ├── DatabaseConfig.java     ← Configuration connexion
│   └── DatabaseStorage.java    ← Accès base de données
├── compile-db.bat              ← Compilation avec JDBC
├── run-db.bat                  ← Exécution avec MySQL
└── test-connection.bat         ← Test de connexion
```

---

## ✅ Checklist Rapide

- [ ] XAMPP installé et démarré (MySQL + Apache)
- [ ] Base `restaurant_db` créée via phpMyAdmin
- [ ] Driver MySQL JDBC dans `lib/`
- [ ] `test-connection.bat` ✅ réussi
- [ ] `compile-db.bat` ✅ réussi

---

## 🆘 Problèmes Fréquents

### "Driver JDBC introuvable"
→ Placez mysql-connector-j-8.3.0.jar dans lib/

### "Unknown database 'restaurant_db'"
→ Exécutez schema.sql dans phpMyAdmin

### "Connection refused"
→ Démarrez MySQL dans XAMPP Control Panel

---

**Documentation complète**: Voir [README_MYSQL.md](README_MYSQL.md)
