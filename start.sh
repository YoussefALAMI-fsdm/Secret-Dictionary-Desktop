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
    echo "❌ Docker n'est pas installé"
    exit 1
fi

# ============================================
# 3. Démarrer PostgreSQL
# ============================================
echo "🔧 Démarrage de PostgreSQL..."

if command -v docker-compose &> /dev/null; then
    docker-compose up -d
else
    docker compose up -d
fi

echo "⏳ Attente de PostgreSQL (10 secondes)..."
sleep 10

echo "✅ PostgreSQL prêt !"
echo ""

# ============================================
# 4. Lancer l'application
# ============================================
echo "🚀 Lancement de l'application..."
echo ""

if command -v mvn &> /dev/null; then
    mvn javafx:run
else
    chmod +x mvnw
    ./mvnw javafx:run
fi

# ============================================
# 5. Nettoyage
# ============================================
echo ""
echo "========================================"
echo "  Application fermée"
echo "========================================"
echo ""
read -p "Arrêter PostgreSQL ? (o/n) : " STOP

if [ "$STOP" = "o" ] || [ "$STOP" = "O" ]; then
    if command -v docker-compose &> /dev/null; then
        docker-compose down
    else
        docker compose down
    fi
    echo "✅ PostgreSQL arrêté"
fi