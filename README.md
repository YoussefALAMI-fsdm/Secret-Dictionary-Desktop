# Secret-Dictionary-Desktop

Une application JavaFX moderne et élégante pour la gestion de mots et définitions avec interface graphique intuitive.

<br>

---

<br>

## ✨ Fonctionnalités

### 📖 Gestion des mots

- ➕ **Ajout de mots** avec définition, catégorie et emoji personnalisé
- ✏️ **Modification complète** : changez le mot, sa définition, catégorie ou emoji
- 🔍 **Recherche intelligente** avec autocomplétion floue en temps réel (pg_trgm)
- 📋 **Affichage détaillé** de chaque mot avec toutes ses informations
- 📚 **Liste complète** de tous les mots du dictionnaire triés alphabétiquement
- 😊 **Support natif des emojis** avec affichage coloré

### 🔗 Relations entre mots

- 🔗 **Synonymes** : Créez des relations de synonymie entre deux mots existants
- ⚡ **Antonymes** : Créez des relations d'antonymie entre deux mots existants
- ✅ **Validation automatique** : vérification que les deux mots existent
- 🚫 **Protection intelligente** : un mot ne peut être son propre synonyme/antonyme
- 🔍 **Autocomplétion** dans les dialogues de création de relations
- 📊 **Affichage des relations** : visualisation des synonymes et antonymes dans les détails du mot

### 🏷️ Catégorisation et organisation

- 📁 **Catégories prédéfinies** : General, Verbe, Adjectif, Nom, Adverbe, Expression
- 🎯 **Recherche par catégorie** : filtrez les mots selon leur catégorie
- 😊 **Recherche par emoji** : trouvez tous les mots associés à un emoji spécifique
- 📊 **Statistiques** : comptage du nombre de mots par catégorie

### 🎨 Interface utilisateur

- 🌙 **Thème sombre moderne** avec couleurs professionnelles
- 🔄 **Autocomplétion en temps réel** dans tous les champs de recherche
- 😊 **Affichage natif des emojis** en couleurs (Windows, macOS, Linux)
- 📱 **Navigation intuitive** entre les différentes vues
- 🎯 **Dialogues modaux élégants** pour toutes les opérations
- ✨ **Animations fluides** et effets visuels
- 🖼️ **Image de fond personnalisée** pour une expérience visuelle immersive

### 🗄️ Base de données avancée

- 🐘 **PostgreSQL** comme système de gestion performant
- 🔄 **Migrations automatiques** avec Flyway (versionnement du schéma)
- 📊 **Indexation optimisée** (index unique, covering index, GIN)
- 🔍 **Recherche insensible à la casse** avec extension CITEXT
- 🔗 **Relations N-N bidirectionnelles** pour synonymes et antonymes
- 🛡️ **Contraintes d'intégrité** avec suppression en cascade
- 📝 **Données de test** : 30+ mots avec relations pré-configurées

<br>

---

<br>

## 🛠️ Technologies

| Catégorie | Technologies                                              |
|-----------|-----------------------------------------------------------|
| **Interface** | JavaFX 17, FXML, CSS personnalisé modulaire               |
| **Architecture** | MVC en couches avec injection de dépendances              |
| **Base de données** | PostgreSQL 16 avec extensions (CITEXT, pg_trgm)           |
| **Migrations** | Flyway 10.0 pour gestion automatique du schéma            |
| **Build** | Maven avec module-info.java (Java Platform Module System) |
| **Conteneurisation** | Docker & Docker Compose                                   |

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
- ✅ Vérifier que Docker est installé et configuré
- 🔧 Démarrer PostgreSQL automatiquement
- 📊 Charger les données de test (30+ mots)
- 🚀 Lancer l'application JavaFX

### 3️⃣ Utiliser l'application

Une fois lancée, vous pouvez :

- 🔍 **Rechercher un mot** : Bouton "🔍 Recherche" avec autocomplétion floue
- ➕ **Ajouter un mot** : Bouton "➕ Ajouter" (mot, définition, catégorie, emoji)
- 🔗 **Créer un synonyme** : Bouton "🔗 Ajout de synonyme" avec autocomplétion
- ⚡ **Créer un antonyme** : Bouton "⚡ Ajout d'antonyme" avec autocomplétion
- 📚 **Parcourir tous les mots** : Liste de droite avec emojis (cliquez pour voir les détails)
- ✏️ **Modifier un mot** : Bouton "✏️ Modifier" dans la vue détails (modifiez tout : mot, définition, catégorie, emoji)

<br>

---

<br>

## 🏗️ Architecture

### 📐 Architecture MVC en couches
```
┌─────────────────────────────────────────┐
│           UI (FXML + CSS)               │  
│    (MainController orchestration)       │
└──────────────┬──────────────────────────┘
               │ injection
┌──────────────▼──────────────────────────┐
│          Service Layer                   │
│  (Logique métier + DTO ↔ Entity)       │
└──────────────┬──────────────────────────┘
               │ injection
┌──────────────▼──────────────────────────┐
│            DAO Layer                     │
│    (Accès données SQL JDBC)             │
└──────────────┬──────────────────────────┘
               │ injection
┌──────────────▼──────────────────────────┐
│      DataBase (Singleton)                │
│    (Connexion PostgreSQL unique)        │
└──────────────────────────────────────────┘
```

**Principe d'injection de dépendances** :
- Chaque couche reçoit ses dépendances via constructeur
- Évite les couplages forts (`new DataBase()` directement dans DAO)
- Facilite les tests unitaires et la maintenance

### 📁 Structure du projet
```
Secret-Dictionary-Desktop/
├── src/
│   ├── main/
│   │   ├── java/com/secret/dictionary/
│   │   │   ├── app/
│   │   │   │   └── Main.java                          # Point d'entrée JavaFX
│   │   │   ├── controller/
│   │   │   │   ├── MainController.java                # Orchestration générale
│   │   │   │   ├── MenuController.java                # Menu latéral gauche
│   │   │   │   ├── WordListController.java            # Liste des mots (droite)
│   │   │   │   ├── WordDetailsController.java         # Détails d'un mot (centre)
│   │   │   │   ├── SearchDialogController.java        # Dialogue de recherche
│   │   │   │   ├── AddWordDialogController.java       # Dialogue ajout mot
│   │   │   │   ├── UpdateWordDialogController.java    # Dialogue modification
│   │   │   │   ├── AddSynonymeDialogController.java   # Dialogue ajout synonyme
│   │   │   │   └── AddAntonymeDialogController.java   # Dialogue ajout antonyme
│   │   │   ├── dao/
│   │   │   │   ├── MotDAO.java                        # Interface DAO
│   │   │   │   ├── MotDAOImp.java                     # Implémentation JDBC
│   │   │   │   └── DAOExeption.java                   # Exception personnalisée
│   │   │   ├── dto/
│   │   │   │   └── MotDTO.java                        # Record immuable
│   │   │   ├── model/
│   │   │   │   └── Mot.java                           # Entité métier
│   │   │   ├── service/
│   │   │   │   ├── MotService.java                    # Interface Service
│   │   │   │   └── MotServiceImp.java                 # Logique métier
│   │   │   └── util/
│   │   │       ├── DataBase.java                      # Singleton connexion
│   │   │       ├── DataBaseInit.java                  # Flyway init
│   │   │       └── EmojiUtils.java                    # Gestion emojis colorés
│   │   └── resources/
│   │       ├── com/secret/dictionary/
│   │       │   ├── fxml/
│   │       │   │   ├── main-view.fxml                 # Vue principale
│   │       │   │   ├── side-menu.fxml                 # Menu latéral
│   │       │   │   ├── word-list.fxml                 # Liste mots
│   │       │   │   └── word-details.fxml              # Détails mot
│   │       │   └── styles/
│   │       │       ├── style.css                      # Import principal
│   │       │       ├── base.css                       # Variables & base
│   │       │       ├── buttons.css                    # Styles boutons
│   │       │       ├── panels.css                     # Styles panneaux
│   │       │       ├── lists.css                      # Styles listes
│   │       │       ├── dialogs.css                    # Styles dialogues
│   │       │       ├── buttonModifier.css             # Styles modification
│   │       │       └── fond-ecran.jpg                 # Image de fond
│   │       └── db/migration/
│   │           ├── V1__creation_table_mots.sql
│   │           ├── V2__creation_index_mots_id.sql
│   │           ├── V3__creation_index_mots_mot.sql
│   │           ├── V4__creation_index_unique_mots_mot.sql
│   │           ├── V5__rendre_mots_mot_incessible_case.sql
│   │           ├── V6__activer_extension_pg_trgm_autocompilition.sql
│   │           ├── V7__ajout_collone_table_mots.sql
│   │           ├── V8__creation_table_mots_synonymes.sql
│   │           ├── V9__creation_table_mots_antonymes.sql
│   │           ├── V10__creation_index_unique_mot_couvrant_id.sql
│   │           └── V11__insertion_donnees_test.sql
├── docker-compose.yml              # Configuration PostgreSQL
├── start.sh                        # Script démarrage Linux/macOS
├── start.bat                       # Script démarrage Windows
├── pom.xml                         # Configuration Maven
└── README.md
```

<br>

---

<br>

## 🎨 Captures d'écran

> *À venir : captures d'écran de l'interface*

<br>

---

<br>

## 🚀 Fonctionnalités à venir

Les fonctionnalités suivantes sont **déjà implémentées dans la couche Service** mais ne disposent pas encore d'interface graphique :

### 📊 Recherche avancée
- **Recherche par catégorie** : Filtrer et afficher tous les mots d'une catégorie spécifique
- **Recherche par emoji** : Trouver tous les mots associés à un emoji donné
- **Recherche de synonymes** : Interface dédiée pour explorer les relations de synonymie
- **Recherche d'antonymes** : Interface dédiée pour explorer les relations d'antonymie

### 📈 Statistiques et visualisation
- **Tableau de bord** : Vue d'ensemble avec statistiques (nombre de mots par catégorie)
- **Graphiques** : Visualisation de la répartition des mots par catégorie

> **Note technique** : Toutes ces fonctionnalités disposent déjà de méthodes complètes dans `MotService.java` et `MotDAO.java`. Seule l'interface graphique JavaFX reste à développer.

<br>

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

### ❌ Permissions Docker (Linux uniquement)

**Cause** : L'utilisateur n'a pas les permissions pour exécuter Docker.

**Solution** :
```bash
# Ajouter l'utilisateur au groupe docker
sudo usermod -aG docker $USER

# Activer le nouveau groupe
newgrp docker

# Relancer le script
./start.sh
```

### ❌ Les emojis s'affichent en noir et blanc

**Cause** : La police emoji colorée n'est pas installée sur votre système.

**Solution** :
```bash
# Windows : Segoe UI Emoji (préinstallé sur Windows 10+)
# macOS : Apple Color Emoji (préinstallé)
# Linux (Ubuntu/Debian)
sudo apt install fonts-noto-color-emoji

# Linux (Arch)
sudo pacman -S noto-fonts-emoji
```

<br>

---

<br>

## 📚 Documentation technique

### Schéma de base de données

```sql
-- Table principale
mots (
    id SERIAL PRIMARY KEY,
    mot CITEXT NOT NULL UNIQUE,
    def TEXT,
    categorie TEXT DEFAULT 'General',
    emojie TEXT
)

-- Table des synonymes (N-N bidirectionnelle)
mots_synonymes (
    mot_id INT REFERENCES mots(id) ON DELETE CASCADE,
    synonyme_id INT REFERENCES mots(id) ON DELETE CASCADE,
    PRIMARY KEY (mot_id, synonyme_id),
    CHECK (mot_id <> synonyme_id)
)

-- Table des antonymes (N-N bidirectionnelle)
mots_antonymes (
    mot_id INT REFERENCES mots(id) ON DELETE CASCADE,
    antonyme_id INT REFERENCES mots(id) ON DELETE CASCADE,
    PRIMARY KEY (mot_id, antonyme_id),
    CHECK (mot_id <> antonyme_id)
)
```

### Index optimisés

- `idx_mot_covering` : Index unique couvrant sur `mot` incluant `id`
- `idx_mots_trgm` : Index GIN pour recherche floue (pg_trgm)
- Clés primaires automatiques sur `id`, `(mot_id, synonyme_id)`, `(mot_id, antonyme_id)`

<br>

---

<br>

---

<br>

## 👨‍💻 Auteurs

**Youssef ALAMI & Aya EL FATHI** - Étudiants en génie logiciel

📧 Contact 1 : [GitHub - Youssef ALAMI](https://github.com/YoussefALAMI-fsdm)  
📧 Contact 2 : [GitHub - Aya EL FATHI](https://github.com/Aya-El-Fathi-FSDM)

<br>

---

<br>

## 🙏 Remerciements

- **PostgreSQL** pour la puissance de recherche full-text et trigrammes
- **Flyway** pour la gestion élégante des migrations de base de données
- **JavaFX** pour le framework d'interface graphique moderne
- **Docker** pour la conteneurisation simplifiée

<br>

---

<br>

⭐ **N'oubliez pas de mettre une étoile si ce projet vous a aidé !** ⭐

---