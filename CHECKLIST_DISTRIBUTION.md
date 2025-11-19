# ✅ Checklist de Distribution - Secret Dictionary

## 📋 Avant la Distribution

### 1. Fichiers Essentiels
- [ ] `docker-compose.yml` - Configuration PostgreSQL
- [ ] `start.bat` - Script Windows
- [ ] `start.sh` - Script Linux/macOS (rendre exécutable : `chmod +x start.sh`)
- [ ] `stop.bat` - Script d'arrêt Windows
- [ ] `stop.sh` - Script d'arrêt Linux/macOS (rendre exécutable : `chmod +x stop.sh`)
- [ ] `pom.xml` - Configuration Maven
- [ ] Maven Wrapper : `mvnw`, `mvnw.cmd`, `.mvn/wrapper/`

### 2. Documentation
- [ ] `README.md` - Documentation technique
- [ ] `GUIDE_SIMPLIFIE.md` - Guide simplifié utilisateur final
- [ ] `GUIDE_DISTRIBUTION.md` - Guide de distribution détaillé

### 3. Code Source
- [ ] `src/main/java/` - Code Java complet
- [ ] `src/main/resources/` - Ressources (FXML, CSS, SQL migrations)
- [ ] `module-info.java` - Configuration des modules Java

### 4. Fichiers de Configuration
- [ ] `.gitignore` - Fichiers à exclure du versioning
- [ ] `.dockerignore` - Fichiers à exclure de Docker

---

## 🔒 Sécurité (CRITIQUE pour Production)

### Avant Distribution Publique
- [ ] **Changer les credentials PostgreSQL** dans `docker-compose.yml`
    - [ ] `POSTGRES_USER` : choisir un nom d'utilisateur sécurisé
    - [ ] `POSTGRES_PASSWORD` : générer un mot de passe fort
    - [ ] `POSTGRES_DB` : vérifier le nom de la base

- [ ] **Mettre à jour `DataBase.java`** avec les nouveaux credentials par défaut

- [ ] **Créer un fichier `.env.example`** avec des placeholders :
  ```env
  DB_URL=jdbc:postgresql://localhost:5432/dictionary
  DB_USER=votre_utilisateur
  DB_PASSWORD=votre_mot_de_passe
  ```

- [ ] **Ajouter `.env` au `.gitignore`**

- [ ] **Documenter l'utilisation des variables d'environnement**

### Variables d'Environnement Sensibles
- [ ] Ne jamais committer `.env` avec des vraies valeurs
- [ ] Fournir `.env.example` comme template
- [ ] Documenter comment configurer `.env`

---

## 🧪 Tests Avant Distribution

### Test Multiplateforme
- [ ] **Windows 10/11**
    - [ ] Lancer `start.bat`
    - [ ] Vérifier connexion DB
    - [ ] Tester l'interface JavaFX
    - [ ] Arrêter avec `stop.bat`

- [ ] **macOS (Intel)**
    - [ ] Exécuter `./start.sh`
    - [ ] Vérifier connexion DB
    - [ ] Tester l'interface
    - [ ] Arrêter avec `./stop.sh`

- [ ] **macOS (Apple Silicon M1/M2)**
    - [ ] Vérifier compatibilité Docker
    - [ ] Tester le démarrage complet

- [ ] **Linux (Ubuntu/Debian)**
    - [ ] Exécuter `./start.sh`
    - [ ] Vérifier connexion DB
    - [ ] Tester l'interface
    - [ ] Arrêter avec `./stop.sh`

### Test des Fonctionnalités
- [ ] Migrations Flyway s'exécutent correctement
- [ ] Tables créées : `mots`
- [ ] Index créés : `idx_id`, `idx_mot`
- [ ] Connexion DB persistante
- [ ] Interface JavaFX s'affiche correctement
- [ ] Redémarrage après arrêt complet

### Test de Persistence
- [ ] Ajouter des données (quand CRUD sera implémenté)
- [ ] Arrêter l'application : `docker-compose down`
- [ ] Redémarrer : `docker-compose up -d`
- [ ] Vérifier que les données sont toujours présentes

---

## 📦 Préparation du Package de Distribution

### Structure de l'Archive
```
Secret-Dictionary-Desktop-v1.0/
├── start.bat
├── start.sh
├── stop.bat
├── stop.sh
├── docker-compose.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/
├── src/
├── README_UTILISATEUR.md
├── DISTRIBUTION.md
└── README.md
```

### Fichiers à EXCLURE de l'Archive
- [ ] `.git/` - Historique Git
- [ ] `target/` - Compilations
- [ ] `.idea/` - Fichiers IDE
- [ ] `*.iml` - Fichiers IntelliJ
- [ ] `.env` - Variables d'environnement réelles

### Créer l'Archive
```bash
# Exclure les dossiers et fichiers inutiles
zip -r Secret-Dictionary-v1.0.zip . \
  -x "*.git*" \
  -x "*target/*" \
  -x "*.idea*" \
  -x "*.iml" \
  -x ".env"
```

Ou manuellement :
- [ ] Sélectionner les fichiers listés ci-dessus
- [ ] Créer une archive ZIP ou TAR.GZ
- [ ] Nommer : `Secret-Dictionary-Desktop-v1.0.zip`

---

## 📝 Documentation de Version

### Changelog à Créer
- [ ] Créer `CHANGELOG.md`
- [ ] Documenter les versions :
    - [ ] Version actuelle
    - [ ] Fonctionnalités
    - [ ] Corrections de bugs
    - [ ] Modifications

Exemple :
```markdown
# Changelog

## [1.0.0] - 2025-11-19
### Ajouté
- Architecture MVC complète
- Connexion PostgreSQL avec Docker
- Migrations Flyway (V1, V2, V3)
- Interface JavaFX de base
- Scripts de démarrage multiplateforme

### À venir
- CRUD complet pour les mots
- Interface utilisateur avancée
- Recherche de mots
```

---

## 🌐 Publication

### GitHub Release
- [ ] Créer un tag : `git tag -a v1.0.0 -m "Version 1.0.0"`
- [ ] Pousser le tag : `git push origin v1.0.0`
- [ ] Créer une Release sur GitHub
- [ ] Attacher l'archive ZIP
- [ ] Copier le changelog dans la description

### Documentation sur GitHub
- [ ] Mettre à jour le README principal
- [ ] Ajouter badges de version
- [ ] Documenter les prérequis clairement
- [ ] Ajouter des captures d'écran (quand UI sera prête)

---

## 🎯 Checklist Post-Distribution

### Support Utilisateurs
- [ ] Configurer GitHub Issues
- [ ] Créer des templates d'issues (Bug, Feature Request)
- [ ] Préparer des FAQ basées sur les questions communes

### Monitoring
- [ ] Suivre les issues GitHub
- [ ] Collecter les retours utilisateurs
- [ ] Noter les bugs critiques pour prochaine version

---

## 🔄 Mises à Jour Futures

### Avant Chaque Nouvelle Version
- [ ] Incrémenter le numéro de version dans `pom.xml`
- [ ] Mettre à jour le `CHANGELOG.md`
- [ ] Tester sur toutes les plateformes
- [ ] Créer une nouvelle release GitHub
- [ ] Notifier les utilisateurs existants

### Gestion des Migrations SQL
- [ ] Créer de nouvelles migrations : `V4__`, `V5__`, etc.
- [ ] Tester les migrations sur une DB propre
- [ ] Tester les migrations sur une DB existante (mise à jour)
- [ ] Documenter les changements de schéma

---

## 📊 Métriques de Qualité

Avant chaque release, vérifier :
- [ ] Aucune erreur de compilation
- [ ] Aucun warning critique
- [ ] Scripts testés sur les 3 OS
- [ ] Documentation à jour
- [ ] Credentials de production changés

---

## ⚡ Quick Start (pour vous)

Commandes rapides pour tester :

```bash
# 1. Tester localement
./start.sh  # ou start.bat sur Windows

# 2. Créer l'archive de distribution
zip -r Secret-Dictionary-v1.0.zip . \
  -x "*.git*" -x "*target/*" -x "*.idea*" -x "*.iml"

# 3. Créer un tag et release
git tag -a v1.0.0 -m "Version 1.0.0"
git push origin v1.0.0

# 4. Uploader sur GitHub Releases
```

---

**Date de création** : Novembre 2025
**Auteur** : Youssef ALAMI && Aya EL FATHI
**Statut** : ✅ Prêt pour distribution