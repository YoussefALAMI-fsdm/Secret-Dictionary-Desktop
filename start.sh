#!/bin/bash

# ====================================================
# Script de démarrage pour Secret Dictionary (Linux/macOS)
# ====================================================

echo ""
echo "========================================"
echo "  Secret Dictionary - Démarrage"
echo "========================================"
echo ""

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages
error() {
    echo -e "${RED}[ERREUR]${NC} $1"
}

info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

success() {
    echo -e "${GREEN}[OK]${NC} $1"
}

warning() {
    echo -e "${YELLOW}[AVERTISSEMENT]${NC} $1"
}

# Vérifier si Docker est installé
if ! command -v docker &> /dev/null; then
    error "Docker n'est pas installé ou pas dans le PATH."
    echo "Veuillez installer Docker : https://docs.docker.com/get-docker/"
    exit 1
fi

# Vérifier si Docker Compose est installé
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    error "Docker Compose n'est pas installé."
    echo "Veuillez installer Docker Compose : https://docs.docker.com/compose/install/"
    exit 1
fi

# Déterminer la commande Docker Compose
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
else
    DOCKER_COMPOSE="docker compose"
fi

# Vérifier si Java est installé
if ! command -v java &> /dev/null; then
    error "Java n'est pas installé ou pas dans le PATH."
    echo "Veuillez installer Java 17+ : https://adoptium.net/"
    exit 1
fi

# Afficher la version Java
info "Version Java détectée :"
java -version
echo ""

# Déterminer la commande Maven
if command -v mvn &> /dev/null; then
    MVN_CMD="mvn"
    info "Maven détecté : utilisation de Maven"
else
    MVN_CMD="./mvnw"
    info "Maven non détecté : utilisation de Maven Wrapper"
    # Rendre mvnw exécutable si nécessaire
    chmod +x mvnw
fi

# Démarrer Docker PostgreSQL
echo "[ÉTAPE 1/3] Démarrage de la base de données PostgreSQL..."
$DOCKER_COMPOSE up -d

# Attendre que PostgreSQL soit prêt
info "Attente de la disponibilité de PostgreSQL..."
sleep 5

# Vérifier si PostgreSQL est prêt
if ! docker exec secret-dictionary-db pg_isready -U FSDM -d dictionary &> /dev/null; then
    warning "PostgreSQL n'est pas encore prêt, attente supplémentaire..."
    sleep 5
fi

success "PostgreSQL est prêt !"
echo ""

# Compiler l'application (optionnel, décommenter si nécessaire)
# echo "[ÉTAPE 2/3] Compilation de l'application..."
# $MVN_CMD clean compile
# if [ $? -ne 0 ]; then
#     error "Échec de la compilation"
#     exit 1
# fi
# echo ""

# Lancer l'application JavaFX
echo "[ÉTAPE 2/3] Lancement de l'application JavaFX..."
echo ""
$MVN_CMD javafx:run

# Si l'application se ferme, proposer d'arrêter Docker
echo ""
echo "========================================"
echo "  Application fermée"
echo "========================================"
echo ""
read -p "Voulez-vous arrêter PostgreSQL ? (o/n) : " STOP_DOCKER
if [ "$STOP_DOCKER" = "o" ] || [ "$STOP_DOCKER" = "O" ]; then
    info "Arrêt de PostgreSQL..."
    $DOCKER_COMPOSE down
    success "PostgreSQL arrêté"
fi