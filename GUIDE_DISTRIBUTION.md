# 📦 Guide de Distribution - Secret Dictionary

## 🎯 Architecture de Distribution

Cette application utilise l'approche **Docker DB + UI natif** :
- **PostgreSQL** : tourne dans Docker (multiplateforme)
- **Interface JavaFX** : tourne nativement sur la machine de l'utilisateur

---

## 📋 Prérequis pour l'Utilisateur Final

### 1. Docker Desktop
- **Windows/macOS** : [Docker Desktop](https://www.docker.com/products/docker-desktop)
- **Linux** : [Docker Engine](https://docs.docker.com/engine/install/)

### 2. Java 17 ou supérieur
- **Recommandé** : [Eclipse Temurin (Adoptium)](https://adoptium.net/)
- Alternatives : Oracle JDK, OpenJDK, Amazon Corretto

### 3. Vérification des installations

#### Windows (PowerShell ou CMD)
```cmd
docker --version
java -version
```

#### Linux/macOS (Terminal)
```bash
docker --version
java -version
```

---

## 🚀 Démarrage de l'Application

### Windows
Double-cliquez sur `start.bat` ou exécutez dans CMD :
```cmd
start.bat
```

### Linux/macOS
Rendez le script exécutable puis lancez-le :
```bash
chmod +x start.sh
./start.sh
```

---

## 📂 Structure de Distribution

```
Secret-Dictionary-Desktop/
├── start.bat                    # Script de démarrage Windows
├── start.sh                     # Script de démarrage Linux/macOS
├── docker-compose.yml           # Configuration PostgreSQL
├── pom.xml                      # Configuration Maven
├── mvnw / mvnw.cmd              # Maven Wrapper (inclus)
├── src/                         # Code source
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
└── README.md                    # Documentation
```

---

## 🔧 Configuration Avancée

### Modifier les Paramètres de la Base de Données

Éditez `docker-compose.yml` :
```yaml
environment:
  POSTGRES_DB: dictionary        # Nom de la base
  POSTGRES_USER: FSDM           # Nom d'utilisateur
  POSTGRES_PASSWORD: IA         # Mot de passe
```

### Variables d'Environnement (optionnel)

Pour une configuration personnalisée sans modifier le code :

#### Windows
```cmd
set DB_URL=jdbc:postgresql://localhost:5432/dictionary
set DB_USER=FSDM
set DB_PASSWORD=IA
start.bat
```

#### Linux/macOS
```bash
export DB_URL=jdbc:postgresql://localhost:5432/dictionary
export DB_USER=FSDM
export DB_PASSWORD=IA
./start.sh
```

---

## 🛠️ Commandes Utiles

### Gestion de Docker

#### Démarrer uniquement PostgreSQL
```bash
docker-compose up -d
```

#### Arrêter PostgreSQL
```bash
docker-compose down
```

#### Voir les logs PostgreSQL
```bash
docker-compose logs -f
```

#### Supprimer les données (ATTENTION : perte de données)
```bash
docker-compose down -v
```

### Gestion de l'Application

#### Compiler manuellement
```bash
# Avec Maven installé
mvn clean compile

# Avec Maven Wrapper (Windows)
mvnw.cmd clean compile

# Avec Maven Wrapper (Linux/macOS)
./mvnw clean compile
```

#### Lancer sans script
```bash
# Avec Maven installé
mvn javafx:run

# Avec Maven Wrapper (Windows)
mvnw.cmd javafx:run

# Avec Maven Wrapper (Linux/macOS)
./mvnw javafx:run
```

---

## 🐛 Résolution de Problèmes

### Erreur : "Docker n'est pas installé"
- Installez Docker Desktop
- Vérifiez que Docker est bien dans le PATH
- Redémarrez votre terminal après l'installation

### Erreur : "Java n'est pas installé"
- Installez Java 17+
- Configurez la variable d'environnement `JAVA_HOME`
- Ajoutez Java au PATH

### Erreur : "Port 5432 déjà utilisé"
PostgreSQL est peut-être déjà installé localement. Solutions :
1. Arrêtez PostgreSQL local
2. Modifiez le port dans `docker-compose.yml` :
   ```yaml
   ports:
     - "5433:5432"  # Utiliser le port 5433 au lieu de 5432
   ```
3. Mettez à jour `DB_URL` dans `DataBase.java` ou via variables d'environnement

### Erreur : "Impossible de se connecter à la base de données"
- Vérifiez que Docker est lancé : `docker ps`
- Attendez quelques secondes après le démarrage de Docker
- Vérifiez les logs : `docker-compose logs`

### Erreur de compilation Maven
```bash
# Nettoyer le cache Maven et recompiler
mvn clean install -U
```

---

## 📊 Persistence des Données

Les données sont **automatiquement persistées** dans un volume Docker nommé `postgres_data`.

- Les données survivent aux redémarrages
- Les données survivent aux arrêts/démarrages de Docker
- Les données sont supprimées uniquement avec : `docker-compose down -v`

---

## 🔐 Sécurité (Production)

⚠️ **IMPORTANT pour la production** :

1. **Changez les credentials par défaut** dans `docker-compose.yml`
2. **Utilisez des variables d'environnement** pour les secrets
3. **Ne committez jamais** les mots de passe dans Git

Exemple avec fichier `.env` :
```env
POSTGRES_USER=mon_utilisateur
POSTGRES_PASSWORD=mon_mot_de_passe_securise
```

Puis dans `docker-compose.yml` :
```yaml
environment:
  POSTGRES_USER: ${POSTGRES_USER}
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
```

---

## 📦 Distribution aux Utilisateurs

### Option 1 : Code Source (Développeurs)
Distribuez l'archive complète du projet avec :
- Les scripts de démarrage
- Le code source
- La documentation

### Option 2 : JAR Exécutable (Utilisateurs Finaux)

1. Créer un JAR exécutable :
```bash
mvn clean package
```

2. Distribuer :
    - Le fichier JAR (`target/Secret-Dictionary-Desktop-1.0-SNAPSHOT.jar`)
    - `docker-compose.yml`
    - Scripts de démarrage modifiés pour exécuter le JAR

### Option 3 : JLink (Application Native)

Pour créer une application autonome avec JRE embarqué :
```bash
mvn javafx:jlink
```

Cela crée un runtime Java minimal avec l'application.

---

## 🌍 Support Multiplateforme

✅ **Windows** : Testé avec Docker Desktop + Java 17+
✅ **macOS** : Compatible (Intel et Apple Silicon via Docker)
✅ **Linux** : Compatible (toutes distributions avec Docker)

---

## 📞 Support

En cas de problème :
1. Consultez la section **Résolution de Problèmes**
2. Vérifiez les logs Docker : `docker-compose logs`
3. Vérifiez les logs de l'application
4. Ouvrez une issue sur le dépôt GitHub

---

## ✨ Avantages de cette Approche

✅ **Multiplateforme** : Un seul code source pour Windows, macOS, Linux
✅ **Simple à maintenir** : Pas de builds spécifiques par OS
✅ **Base de données isolée** : Pas de conflit avec d'autres installations
✅ **Données persistantes** : Conservation automatique des données
✅ **Facile à développer** : Modification du code sans recompilation complète

---

## ⚠️ Inconvénients à Considérer

⚠️ L'utilisateur doit installer Java et Docker
⚠️ Moins "clé en main" qu'une application native unique
⚠️ Configuration Java peut poser problème sur certaines machines

---

## 🔄 Mises à Jour

Pour mettre à jour l'application :
1. Récupérez la nouvelle version (git pull ou téléchargement)
2. Arrêtez l'application
3. Relancez avec les scripts de démarrage
4. Flyway appliquera automatiquement les nouvelles migrations SQL

---

**Version** : 1.0.0
**Dernière mise à jour** : Novembre 2025