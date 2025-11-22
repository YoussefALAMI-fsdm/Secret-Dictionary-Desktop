# Secret-Dictionary-Desktop

Une application JavaFX moderne et élégante pour la gestion de mots et définitions avec interface graphique intuitive.

<br>

---

<br>

## ✨ Fonctionnalités

- 🔍 **Recherche rapide** : Trouvez instantanément la définition d'un mot
- ➕ **Ajout de mots** : Créez de nouvelles entrées avec mot, catégorie et définition
- 🎨 **Interface moderne** : Design épuré avec des couleurs professionnelles et des animations
- 🗄️ **Base de données PostgreSQL** : Stockage fiable et performant avec indexation
- 🔄 **Migrations automatiques** : Gestion des versions de la base de données avec Flyway

<br>

---

<br>

## 🛠️ Technologies

- **JavaFX 17** - Framework d'interface graphique
- **FXML** - Architecture MVC pour une séparation claire du code
- **CSS intégré** - Styles modernes et responsive
- **PostgreSQL 16** - Base de données relationnelle
- **Docker & Docker Compose** - Conteneurisation de la base de données
- **Flyway** - Gestion des migrations de base de données
- **Maven** - Gestion des dépendances et build

<br>

---

<br>

## 📋 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- ☕ **Java 17 ou supérieur** - [Télécharger ici](https://adoptium.net/)
- 🐳 **Docker Desktop** - [Télécharger ici](https://www.docker.com/products/docker-desktop)
- 📦 **Maven** (optionnel, Maven Wrapper inclus)

<br>

---

<br>

## 🚀 Installation et Démarrage

### 1️⃣ Cloner le projet

```bash
git clone https://github.com/YoussefALAMI-fsdm/Secret-Dictionary-Desktop.git
cd Secret-Dictionary-Desktop
```

### 2️⃣ Démarrer l'application

#### 🐧 **Sur Linux/macOS** :

```bash
chmod +x start.sh
./start.sh
```

#### 🪟 **Sur Windows** :

```cmd
start.bat
```

Le script automatisé va :
- ✅ Vérifier que Java 17+ est installé
- ✅ Vérifier que Docker est installé
- 🔧 Démarrer PostgreSQL automatiquement
- 🚀 Lancer l'application JavaFX

### 3️⃣ Utiliser l'application

Une fois lancée, vous pouvez :
- Rechercher un mot avec le bouton **🔍 Recherche**
- Ajouter un nouveau mot avec **➕ Nouveau**
- Parcourir tous les mots dans la liste de droite
- Cliquer sur un mot pour voir ses détails

---

<br>

## 🏗️ Architecture du Projet

```
Secret-Dictionary-Desktop/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/secret/dictionary/
│   │   │       ├── app/           # Point d'entrée (Main.java)
│   │   │       ├── controller/    # Controllers JavaFX (ControllerFX)
│   │   │       ├── dao/           # Accès aux données (MotDAO, MotDAOImp)
│   │   │       ├── dto/           # Objets de transfert (MotDTO)
│   │   │       ├── model/         # Entités métier (Mot)
│   │   │       ├── service/       # Logique métier (MotService)
│   │   │       └── util/          # Utilitaires (DataBase, DataBaseInit)
│   │   └── resources/
│   │       ├── com/secret/dictionary/
│   │       │   ├── fxml/         # Fichiers FXML (hello-view.fxml)
│   │       │   └── styles/       # Fichiers CSS (style.css)
│   │       └── db/migration/     # Scripts SQL Flyway (V1__, V2__, ...)
├── docker-compose.yml            # Configuration PostgreSQL
├── start.sh                      # Script de démarrage Linux/macOS
├── start.bat                     # Script de démarrage Windows
├── pom.xml                       # Configuration Maven
└── README.md
```

<br>

---

<br>

## 🎨 Captures d'écran

> *À venir : captures d'écran de l'interface*

<br>

---

---

<br>

## 🐛 Résolution de problèmes

### ❌ Erreur : "invalid target release: 25"

**Cause** : Java est installé, mais Java 17+ est requis.

**Solution** :
```bash
# Ubuntu/Debian
sudo apt install openjdk-17-jdk

# Vérifier
java -version
```

### ❌ Erreur : "Docker n'est pas installé"

**Solution** : Installez Docker Desktop depuis [docker.com](https://www.docker.com/products/docker-desktop)

### ❌ Port 5432 déjà utilisé

**Cause** : PostgreSQL est déjà en cours d'exécution.

**Solution** :
```bash
# Arrêter le conteneur existant
docker stop secret-dictionary-db
docker rm secret-dictionary-db

# Ou modifier le port dans docker-compose.yml
```

<br>

---

<br>

## 👨‍💻 Auteurs

**Youssef ALAMI & Aya EL FATHI** - Futurs génies logiciels

📧 Contact 1 : [GitHub - Youssef ALAMI](https://github.com/YoussefALAMI-fsdm)  
📧 Contact 2 : [GitHub - Aya EL FATHI](https://github.com/Aya-El-Fathi-FSDM)

<br>

---

<br>

## 🤝 Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :

- 🐛 **Signaler des bugs** : Ouvrez une issue sur GitHub
- 💡 **Proposer de nouvelles fonctionnalités** : Partagez vos idées
- 🔧 **Soumettre des pull requests** : Contribuez au code

<br>

---