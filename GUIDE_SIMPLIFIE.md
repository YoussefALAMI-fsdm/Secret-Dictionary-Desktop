# 🔐 Secret Dictionary - Guide Utilisateur

## 📥 Installation Rapide

### Étape 1 : Installer les Prérequis

#### 1.1 Docker Desktop
- Téléchargez et installez [Docker Desktop](https://www.docker.com/products/docker-desktop)
- Créez un compte Docker (gratuit)
- Lancez Docker Desktop et attendez qu'il soit prêt

#### 1.2 Java 17+
- Téléchargez et installez [Java 17](https://adoptium.net/)
- Choisissez la version LTS (Long Term Support)

### Étape 2 : Vérifier l'Installation

Ouvrez un terminal et vérifiez :

**Windows (PowerShell ou CMD)**
```cmd
docker --version
java -version
```

**macOS/Linux (Terminal)**
```bash
docker --version
java -version
```

Vous devriez voir les versions installées.

---

## 🚀 Lancer l'Application

### Sur Windows
1. Double-cliquez sur `start.bat`
2. Attendez que la fenêtre s'ouvre
3. L'application démarre automatiquement

### Sur macOS/Linux
1. Ouvrez un terminal dans le dossier de l'application
2. Tapez : `./start.sh`
3. Appuyez sur Entrée

---

## 🛑 Arrêter l'Application

### Arrêt Normal
- Fermez simplement la fenêtre de l'application
- Le script vous demandera si vous voulez arrêter la base de données
- Tapez `o` (oui) ou `n` (non)

### Arrêt Manuel de la Base de Données

**Windows** : Double-cliquez sur `stop.bat`

**macOS/Linux** : Exécutez `./stop.sh`

---

## ❓ Problèmes Courants

### "Docker n'est pas installé"
➡️ Installez Docker Desktop et redémarrez votre ordinateur

### "Java n'est pas installé"
➡️ Installez Java 17+ et redémarrez votre terminal

### L'application ne démarre pas
1. Vérifiez que Docker Desktop est lancé
2. Attendez quelques secondes après le premier lancement
3. Relancez le script de démarrage

### "Port 5432 déjà utilisé"
➡️ Un autre programme utilise ce port. Arrêtez PostgreSQL s'il est installé localement.

---

## 📞 Besoin d'Aide ?

Si vous rencontrez un problème :
1. Consultez le fichier `GUIDE_DISTRIBUTION.md` pour plus de détails
2. Contactez le support technique
3. Ouvrez une issue sur GitHub

---

## ✨ Bon à Savoir

- 💾 **Vos données sont sauvegardées automatiquement**
- 🔄 **Les mises à jour sont faciles** : téléchargez la nouvelle version et relancez
- 🛡️ **Vos données restent sur votre ordinateur** : elles ne sont jamais envoyées sur Internet
- 🌐 **Fonctionne sans connexion Internet** (après la première installation)

---

**Bonne utilisation ! 🎉**