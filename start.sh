#!/bin/bash

# ====================================================
# Script de démarrage pour Secret Dictionary
# ====================================================

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

echo ""
echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}  Secret Dictionary - Démarrage${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

# ============================================
# 1. Vérifier Java
# ============================================
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java n'est pas installé${NC}"
    echo "   Installez Java 17+ : sudo apt install openjdk-17-jdk"
    exit 1
fi

# Extraire la version Java
JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[0-9]+')

if [ "$JAVA_VERSION" -lt 17 ]; then
    echo -e "${RED}❌ Java $JAVA_VERSION détecté, mais Java 17+ est requis${NC}"
    echo ""
    echo "Solutions :"
    echo "  → Installer Java 17 : sudo apt install openjdk-17-jdk"
    echo "  → Ou modifier pom.xml pour utiliser Java $JAVA_VERSION"
    exit 1
fi

echo -e "${GREEN}✅ Java $JAVA_VERSION détecté${NC}"
echo ""

# ============================================
# 2. Vérifier Docker
# ============================================
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker n'est pas installé${NC}"
    echo ""
    echo "Installez Docker avec :"
    echo "  curl -fsSL https://get.docker.com -o get-docker.sh"
    echo "  sudo sh get-docker.sh"
    exit 1
fi

# ============================================
# 3. Vérifier les permissions Docker
# ============================================
echo "🔍 Vérification des permissions Docker..."

if ! docker ps &> /dev/null; then
    echo -e "${YELLOW}⚠️  Permissions Docker manquantes${NC}"
    echo ""
    echo "Pour utiliser Docker sans sudo, exécutez ces commandes UNE SEULE FOIS :"
    echo ""
    echo -e "${CYAN}  sudo usermod -aG docker \$USER${NC}"
    echo -e "${CYAN}  newgrp docker${NC}"
    echo ""
    echo "Puis relancez ce script."
    echo ""

    # Proposer d'exécuter avec sudo comme solution temporaire
    read -p "Voulez-vous lancer avec sudo temporairement ? (o/n) : " USE_SUDO

    if [ "$USE_SUDO" = "o" ] || [ "$USE_SUDO" = "O" ]; then
        DOCKER_CMD="sudo docker"
        COMPOSE_CMD="sudo docker compose"
        echo -e "${YELLOW}⚠️  Utilisation de sudo (temporaire)${NC}"
    else
        echo "Configurez d'abord les permissions Docker, puis relancez."
        exit 1
    fi
else
    DOCKER_CMD="docker"
    COMPOSE_CMD="docker compose"
    echo -e "${GREEN}✅ Permissions Docker OK${NC}"
fi

echo ""

# ============================================
# 4. Proposer d'exécuter les tests
# ============================================
echo -e "${BLUE}🧪 Tests automatisés${NC}"
echo "Voulez-vous exécuter les tests unitaires avant de lancer l'application ?"
read -p "(o/n) : " RUN_TESTS
echo ""

if [ "$RUN_TESTS" = "o" ] || [ "$RUN_TESTS" = "O" ]; then
    echo -e "${CYAN}🧪 Exécution des tests...${NC}"
    echo ""

    # Exécuter les tests avec Maven
    if command -v mvn &> /dev/null; then
        mvn test -q
    else
        chmod +x mvnw
        ./mvnw test -q
    fi

    TEST_RESULT=$?

    echo ""
    if [ $TEST_RESULT -eq 0 ]; then
        echo -e "${GREEN}✅ Tous les tests ont réussi !${NC}"
        echo -e "${GREEN}📝 Logs détaillés : logs/LogMotDAOTest.log${NC}"
    else
        echo -e "${RED}❌ Certains tests ont échoué${NC}"
        echo -e "${YELLOW}📝 Consultez logs/LogMotDAOTest.log pour plus de détails${NC}"
        echo ""
        read -p "Continuer malgré les erreurs ? (o/n) : " CONTINUE
        if [ "$CONTINUE" != "o" ] && [ "$CONTINUE" != "O" ]; then
            echo "Arrêt du script."
            exit 1
        fi
    fi
    echo ""
fi

# ============================================
# 5. Démarrer PostgreSQL
# ============================================
echo -e "${CYAN}🔧 Démarrage de PostgreSQL...${NC}"

# Vérifier si docker-compose ou docker compose existe
if command -v docker-compose &> /dev/null; then
    if [ -n "$USE_SUDO" ] && [ "$USE_SUDO" = "o" ]; then
        sudo docker-compose up -d > /dev/null 2>&1
    else
        docker-compose up -d > /dev/null 2>&1
    fi
else
    $COMPOSE_CMD up -d > /dev/null 2>&1
fi

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Échec du démarrage de PostgreSQL${NC}"
    exit 1
fi

echo "⏳ Attente de PostgreSQL (10 secondes)..."
sleep 10

echo -e "${GREEN}✅ PostgreSQL prêt !${NC}"
echo ""

# ============================================
# 6. Lancer l'application
# ============================================
echo -e "${CYAN}🚀 Lancement de l'application...${NC}"
echo ""

if command -v mvn &> /dev/null; then
    mvn javafx:run
else
    chmod +x mvnw
    ./mvnw javafx:run
fi

# ============================================
# 7. Nettoyage
# ============================================
echo ""
echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}  Application fermée${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""
read -p "Arrêter PostgreSQL ? (o/n) : " STOP

if [ "$STOP" = "o" ] || [ "$STOP" = "O" ]; then
    if command -v docker-compose &> /dev/null; then
        if [ -n "$USE_SUDO" ] && [ "$USE_SUDO" = "o" ]; then
            sudo docker-compose down > /dev/null 2>&1
        else
            docker-compose down > /dev/null 2>&1
        fi
    else
        $COMPOSE_CMD down > /dev/null 2>&1
    fi
    echo -e "${GREEN}✅ PostgreSQL arrêté${NC}"
fi

echo ""
echo -e "${GREEN}Merci d'avoir utilisé Secret Dictionary !${NC}"
echo ""