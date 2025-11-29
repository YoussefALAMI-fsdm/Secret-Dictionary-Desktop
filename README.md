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

### 🔗 Relations entre mots

- 🔗 **Synonymes** : Créez des relations de synonymie entre deux mots existants
- ⚡ **Antonymes** : Créez des relations d'antonymie entre deux mots existants
- ✅ **Validation automatique** : vérification que les deux mots existent
- 🚫 **Protection intelligente** : un mot ne peut être son propre synonyme/antonyme
- 🔍 **Autocomplétion** dans les dialogues de création de relations

### 🎨 Interface utilisateur

- 🌙 **Thème sombre moderne** avec couleurs professionnelles
- 🔄 **Autocomplétion en temps réel** dans tous les champs de recherche
- 😊 **Support natif des emojis** avec affichage coloré
- 📱 **Navigation intuitive** entre les différentes vues
- 🎯 **Dialogues modaux élégants** pour toutes les opérations
- ✨ **Animations fluides** et effets visuels

### 🗄️ Base de données avancée

- 🐘 **PostgreSQL** comme système de gestion performant
- 🔄 **Migrations automatiques** avec Flyway (versionnement du schéma)
- 📊 **Indexation optimisée** (index unique, covering index, GIN)
- 🔍 **Recherche insensible à la casse** avec extension CITEXT
- 🔗 **Relations N-N bidirectionnelles** pour synonymes et antonymes
- 🛡️ **Contraintes d'intégrité** avec suppression en cascade

<br>

---

<br>

## 🛠️ Technologies


| Catégorie | Technologies |
|-----------|-------------|
| **Interface** | JavaFX 21, FXML, CSS personnalisé |
| **Architecture** | MVC en couches avec injection de dépendances |
| **Base de données** | PostgreSQL 16 avec extensions (CITEXT, pg_trgm) |
| **Migrations** | Flyway 9.0 pour gestion automatique du schéma |
| **Build** | Maven avec module-info.java (Java Platform Module System) |
| **Conteneurisation** | Docker & Docker Compose |

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

- 🔍 **Rechercher un mot** : Bouton "🔍 Recherche" avec autocomplétion
- ➕ **Ajouter un mot** : Bouton "➕ Ajouter" (mot, définition, catégorie, emoji)
- 🔗 **Créer un synonyme** : Bouton "🔗 Ajout de synonyme"
- ⚡ **Créer un antonyme** : Bouton "⚡ Ajout d'antonyme"
- 📚 **Parcourir tous les mots** : Liste de droite (cliquez pour voir les détails)
- ✏️ **Modifier un mot** : Bouton "✏️ Modifier" dans la vue détails


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
│   │   │       └── EmojiUtils.java                    # Gestion emojis
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
│   │           └── V10__creation_index_unique_mot_couvrant_id.sql
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

⭐ **N'oubliez pas de mettre une étoile si ce projet vous a aidé !** ⭐

---