# 🐳 Secret-Dictionary-Desktop - Guide d'Installation Docker

## 📋 Prérequis

### ✅ Obligatoire
- **Docker Desktop** : [Télécharger ici](https://www.docker.com/products/docker-desktop)
    - Windows : Docker Desktop 4.x ou supérieur
    - macOS : Docker Desktop 4.x ou supérieur
    - Linux : Docker Engine + Docker Compose

### 📦 Optionnel (recommandé pour développeurs)
- **Java JDK 17+** : [Télécharger ici](https://adoptium.net/)
- **Maven 3.8+** : [Télécharger ici](https://maven.apache.org/download.cgi)

> **Note :** Le projet inclut Maven Wrapper (`mvnw` / `mvnw.cmd`), Maven n'est donc pas strictement nécessaire.

---

## 🚀 Installation Rapide (3 étapes)

### 1️⃣ Cloner le Projet

```bash
git clone https://github.com/YoussefALAMI-fsdm/Secret-Dictionary-Desktop.git
cd Secret-Dictionary-Desktop
git checkout docker-setup
```

### 2️⃣ Lancer Docker Desktop

- **Windows** : Ouvrez Docker Desktop depuis le menu Démarrer
- **macOS** : Ouvrez Docker Desktop depuis Applications
- **Linux** : `sudo systemctl start docker`

Attendez que Docker affiche **"Engine running"** ✅

### 3️⃣ Lancer l'Application

#### 🪟 Windows
```cmd
start.bat
```
*Double-cliquez sur `start.bat` ou exécutez-le depuis CMD/PowerShell*

#### 🐧 Linux / 🍎 macOS
```bash
chmod +x start.sh
./start.sh
```

---

## 🔧 Dépannage

### 🛠️ Script de Diagnostic

Si vous rencontrez des problèmes, lancez d'abord le script de diagnostic :

#### Windows
```cmd
debug.bat
```

#### Linux/Mac
```bash
chmod +x debug.sh
./debug.sh
```

Ce script vérifie :
- ✅ Installation de Docker
- ✅ État de Docker Desktop
- ✅ Disponibilité de Docker Compose
- ✅ Installation de Java et Maven
- ✅ État du conteneur PostgreSQL
- ✅ Ports réseau
- ✅ Structure du projet
- ✅ Dépendances Maven

---

## ❌ Erreurs Courantes

### Erreur 1 : "No database found to handle jdbc:postgresql"

**Cause :** La dépendance `flyway-database-postgresql` est manquante

**Solution :**
```bash
# Vérifier que pom.xml contient :
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
    <version>10.0.0</version>
</dependency>

# Nettoyer et relancer
mvn clean
start.bat  # ou ./start.sh
```

### Erreur 2 : "Port 5432 already in use"

**Cause :** PostgreSQL local ou un autre conteneur utilise déjà le port

**Solutions :**

#### Windows
```cmd
# Identifier le processus
netstat -ano | findstr :5432

# Arrêter PostgreSQL local (si présent)
net stop postgresql-x64-16

# Ou arrêter le conteneur Docker
docker compose down
```

#### Linux/Mac
```bash
# Identifier le processus
sudo lsof -i :5432

# Arrêter PostgreSQL local
sudo systemctl stop postgresql

# Ou arrêter le conteneur Docker
docker compose down
```

### Erreur 3 : "Docker Desktop is not running"

**Solution :**
1. Ouvrez Docker Desktop
2. Attendez l'affichage de "Engine running"
3. Relancez le script

### Erreur 4 : "Maven not found"

**Solution :**
Le projet inclut Maven Wrapper, aucune installation nécessaire !

Si l'erreur persiste :
```bash
# Linux/Mac
chmod +x mvnw
./mvnw clean javafx:run

# Windows
mvnw.cmd clean javafx:run
```

### Erreur 5 : Timeout PostgreSQL

**Solution :**
```bash
# Voir les logs PostgreSQL
docker compose logs postgres

# Redémarrer PostgreSQL
docker compose restart postgres

# Ou réinitialiser complètement
docker compose down -v
docker compose up -d postgres
```

---

## 🛑 Commandes Utiles

### Gérer PostgreSQL

```bash
# Démarrer
docker compose up -d postgres

# Arrêter
docker compose down

# Arrêter et supprimer les données
docker compose down -v

# Voir les logs
docker compose logs -f postgres

# Entrer dans PostgreSQL
docker compose exec postgres psql -U FSDM -d dictionary
```

### Nettoyer le Cache Maven

```bash
# Nettoyer les artefacts compilés
mvn clean

# Supprimer le cache Maven (Windows)
rmdir /s /q %USERPROFILE%\.m2\repository

# Supprimer le cache Maven (Linux/Mac)
rm -rf ~/.m2/repository
```

### Vérifier l'État

```bash
# Conteneurs en cours
docker ps

# Tous les conteneurs
docker ps -a

# Vérifier la connexion PostgreSQL
docker compose exec postgres pg_isready -U FSDM -d dictionary
```

---

## 📂 Structure du Projet

```
Secret-Dictionary-Desktop/
├── docker-compose.yml          # Configuration Docker
├── start.sh                    # Lanceur Linux/Mac
├── start.bat                   # Lanceur Windows
├── debug.sh                    # Diagnostic Linux/Mac
├── debug.bat                   # Diagnostic Windows
├── pom.xml                     # Configuration Maven
├── src/
│   ├── main/
│   │   ├── java/              # Code source Java
│   │   │   ├── app/           # Point d'entrée
│   │   │   ├── controller/    # Contrôleurs JavaFX
│   │   │   ├── dao/           # Accès aux données
│   │   │   ├── dto/           # Objets de transfert
│   │   │   ├── model/         # Modèles de données
│   │   │   ├── service/       # Logique métier
│   │   │   └── util/          # Utilitaires
│   │   └── resources/
│   │       └── com/secret/dictionary/
│   │           ├── fxml/      # Interfaces JavaFX
│   │           ├── scripts/   # Migrations SQL Flyway
│   │           └── styles/    # Fichiers CSS
└── README-DOCKER.md           # Ce fichier
```

---

## 🏗️ Architecture du Projet

### Couche Présentation (UI)
- **JavaFX 17** : Framework d'interface graphique
- **FXML** : Séparation vue/logique
- **CSS** : Styles personnalisés

### Couche Métier
- **Service** : Logique métier
- **DTO** : Transfert de données UI ↔ Service

### Couche Persistance
- **DAO** : Accès aux données
- **PostgreSQL** : Base de données relationnelle
- **Flyway** : Gestion des migrations SQL

### Infrastructure
- **Docker** : Conteneurisation PostgreSQL
- **Maven** : Gestion des dépendances

```
┌─────────────────────────────────────────┐
│          Interface JavaFX               │
│          (ControllerFX)                 │
└──────────────┬──────────────────────────┘
               │
               ↓
┌─────────────────────────────────────────┐
│          Service Layer                  │
│       (MotService + DTO)                │
└──────────────┬──────────────────────────┘
               │
               ↓
┌─────────────────────────────────────────┐
│         Data Access Layer               │
│            (MotDAO)                     │
└──────────────┬──────────────────────────┘
               │
               ↓
┌─────────────────────────────────────────┐
│      PostgreSQL Database                │
│      (Docker Container)                 │
└─────────────────────────────────────────┘
```

---

## 🔐 Configuration

### Variables d'Environnement

Le script `start.sh` / `start.bat` configure automatiquement :

```bash
DB_URL=jdbc:postgresql://localhost:5432/dictionary
DB_USER=FSDM
DB_PASSWORD=IA
```

Pour une configuration personnalisée, modifiez `docker-compose.yml` :

```yaml
environment:
  POSTGRES_USER: votre_user
  POSTGRES_PASSWORD: votre_password
  POSTGRES_DB: votre_database
```

Et `start.sh` / `start.bat` en conséquence.

---

## 📊 Migrations de Base de Données (Flyway)

Le projet utilise Flyway pour gérer les migrations SQL de manière versionnée.

### Scripts SQL Disponibles

```
src/main/resources/com/secret/dictionary/scripts/
├── V1__creation_table_mots.sql
├── V2__creation_index_mots(id).sql
└── V3__creation_index_mots(mot).sql
```

### Ajouter une Migration

1. Créez un nouveau fichier : `V4__votre_migration.sql`
2. Ajoutez votre SQL :
   ```sql
   ALTER TABLE mots ADD COLUMN categorie VARCHAR(50);
   ```
3. Au prochain lancement, Flyway appliquera automatiquement la migration

---

## 🧪 Tests

### Tester la Connexion PostgreSQL

```bash
# Depuis le terminal
docker compose exec postgres psql -U FSDM -d dictionary

# Dans psql
\dt          # Lister les tables
SELECT * FROM mots;
\q           # Quitter
```

### Tester l'Application

```bash
# Compiler sans lancer
mvn clean compile

# Lancer uniquement les tests
mvn test

# Lancer l'application
mvn javafx:run
```

---

## 🤝 Contribution

Voir le [README principal](README.md) pour les instructions de contribution.

### Workflow Git

```bash
# Créer une branche
git checkout -b feature/ma-fonctionnalite

# Faire vos modifications
# ...

# Committer
git add .
git commit -m "✨ Add: ma fonctionnalité"

# Pousser
git push origin feature/ma-fonctionnalite
```

---

## 📧 Support

- **Youssef ALAMI** : [GitHub](https://github.com/YoussefALAMI-fsdm)
- **Aya EL FATHI** : [GitHub](https://github.com/Aya-El-Fathi-FSDM)

### Signaler un Bug

[Ouvrir une issue sur GitHub](https://github.com/YoussefALAMI-fsdm/Secret-Dictionary-Desktop/issues)

---

## 📜 Licence

Voir le fichier `LICENSE` à la racine du projet.

---

## 🎉 Remerciements

Merci d'utiliser Secret-Dictionary-Desktop !

**Développé avec ❤️ par Youssef ALAMI & Aya EL FATHI**