#!/bin/bash

# Couleurs pour les messages
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Fonction pour afficher un message avec pause
pause() {
    echo ""
    read -p "Appuyez sur Entrée pour continuer..."
}

# Fonction pour quitter avec erreur
exit_with_error() {
    echo ""
    echo -e "${RED}[ERREUR] $1${NC}"
    echo ""
    if [ -n "$2" ]; then
        echo -e "${YELLOW}Solution : $2${NC}"
        echo ""
    fi
    pause
    exit 1
}

# Header
clear
echo -e "${CYAN}================================${NC}"
echo -e "${CYAN} Secret-Dictionary-Desktop${NC}"
echo -e "${CYAN}================================${NC}"
echo ""
echo -e "${BLUE}🚀 Démarrage de l'application...${NC}"
echo ""

# Vérifier si Docker est installé
if ! command -v docker &> /dev/null; then
    exit_with_error "Docker n'est pas installé" \
        "Installez Docker Desktop depuis : https://www.docker.com/products/docker-desktop"
fi

echo -e "${GREEN}[OK]${NC} Docker détecté"
echo ""

# Vérifier si Docker est en cours d'exécution
if ! docker ps &> /dev/null; then
    exit_with_error "Docker Desktop n'est pas démarré" \
        "Lancez Docker Desktop et attendez qu'il soit prêt, puis relancez ce script"
fi

echo -e "${GREEN}[OK]${NC} Docker est en cours d'exécution"
echo ""

# Fonction pour détecter la commande docker compose
get_docker_compose_cmd() {
    if docker compose version &> /dev/null 2>&1; then
        echo "docker compose"
    elif command -v docker-compose &> /dev/null; then
        echo "docker-compose"
    else
        echo ""
    fi
}

DOCKER_COMPOSE=$(get_docker_compose_cmd)

if [ -z "$DOCKER_COMPOSE" ]; then
    exit_with_error "Docker Compose n'est pas disponible" \
        "Réinstallez Docker Desktop pour obtenir Docker Compose"
fi

echo -e "${GREEN}[OK]${NC} Docker Compose disponible"
echo ""

# Vérifier si PostgreSQL est déjà en cours d'exécution
if docker ps --format '{{.Names}}' | grep -q "^dictionary_pg$"; then
    echo -e "${BLUE}[INFO]${NC} PostgreSQL est déjà en cours d'exécution"
    echo ""
else
    echo -e "${BLUE}[ACTION]${NC} Démarrage de PostgreSQL..."
    echo ""

    if ! $DOCKER_COMPOSE up -d postgres 2>&1; then
        echo ""
        exit_with_error "Impossible de démarrer PostgreSQL" \
            "Vérifiez si le port 5432 est libre avec : sudo lsof -i :5432
Ou essayez : docker compose down && docker compose up -d postgres"
    fi

    echo ""
fi

# Attendre que PostgreSQL soit prêt
echo -e "${BLUE}[INFO]${NC} Attente de PostgreSQL..."
echo ""

MAX_ATTEMPTS=30
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if $DOCKER_COMPOSE exec -T postgres pg_isready -U FSDM -d dictionary &> /dev/null; then
        echo -e "${GREEN}[OK]${NC} PostgreSQL est prêt !"
        echo ""
        break
    fi

    ATTEMPT=$((ATTEMPT + 1))
    if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
        echo ""
        echo -e "${RED}[ERREUR]${NC} Timeout : PostgreSQL n'a pas démarré dans les temps"
        echo ""
        echo -e "${YELLOW}Solutions possibles :${NC}"
        echo "  - Consultez les logs : $DOCKER_COMPOSE logs postgres"
        echo "  - Redémarrez : $DOCKER_COMPOSE restart postgres"
        echo "  - Réinitialisez : $DOCKER_COMPOSE down -v && $DOCKER_COMPOSE up -d postgres"
        echo ""
        pause
        exit 1
    fi

    echo -e "${BLUE}[INFO]${NC} PostgreSQL en cours de démarrage... ($ATTEMPT/$MAX_ATTEMPTS)"
    sleep 2
done

# Configurer les variables d'environnement
export DB_URL="jdbc:postgresql://localhost:5432/dictionary"
export DB_USER="FSDM"
export DB_PASSWORD="IA"

echo -e "${BLUE}[ACTION]${NC} Configuration de la connexion database..."
echo "  - URL  : $DB_URL"
echo "  - User : $DB_USER"
echo ""

# Vérifier si Maven est installé
MVN_CMD=""
if command -v mvn &> /dev/null; then
    echo -e "${GREEN}[OK]${NC} Maven détecté"
    MVN_CMD="mvn"
elif [ -f "./mvnw" ]; then
    echo -e "${YELLOW}[INFO]${NC} Maven non installé, utilisation du wrapper..."
    chmod +x ./mvnw 2>/dev/null
    MVN_CMD="./mvnw"
else
    exit_with_error "Ni Maven ni le wrapper Maven n'ont été trouvés" \
        "Installez Maven depuis : https://maven.apache.org/download.cgi
Ou assurez-vous que le fichier mvnw existe dans le projet"
fi

echo ""
echo -e "${BLUE}[ACTION]${NC} Compilation et lancement de l'application..."
echo ""
echo "================================================================"
echo ""

# Lancer l'application
$MVN_CMD clean javafx:run
MVN_EXIT=$?

echo ""
echo "================================================================"
echo ""

if [ $MVN_EXIT -ne 0 ]; then
    echo -e "${RED}[ERREUR]${NC} L'application s'est terminée avec des erreurs (code: $MVN_EXIT)"
    echo ""
    echo -e "${YELLOW}Solutions possibles :${NC}"
    echo "  - Vérifiez les dépendances : $MVN_CMD dependency:tree"
    echo "  - Nettoyez le cache : $MVN_CMD clean"
    echo "  - Consultez les logs ci-dessus pour plus de détails"
    echo ""
    pause
else
    echo -e "${GREEN}[OK]${NC} Application terminée normalement"
    echo ""
fi

# Proposer d'arrêter PostgreSQL
echo ""
read -p "Voulez-vous arrêter PostgreSQL ? (o/N) : " -n 1 -r STOP_PG
echo ""

if [[ $STOP_PG =~ ^[OoYy]$ ]]; then
    echo ""
    echo -e "${BLUE}[ACTION]${NC} Arrêt de PostgreSQL..."
    $DOCKER_COMPOSE down
    echo -e "${GREEN}[OK]${NC} PostgreSQL arrêté"
    echo ""
fi

echo ""
echo -e "${CYAN}Merci d'avoir utilisé Secret-Dictionary-Desktop !${NC}"
echo ""
pause