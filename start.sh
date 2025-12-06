#!/bin/bash

# ====================================================
# Script de démarrage pour Secret Dictionary
# Version 3.0 - Optimisé et robuste
# ====================================================

set -e  # Arrêt sur erreur

# Couleurs ANSI
readonly RED='\033[0;31m'
readonly GREEN='\033[0;32m'
readonly YELLOW='\033[1;33m'
readonly BLUE='\033[0;34m'
readonly CYAN='\033[0;36m'
readonly NC='\033[0m'  # No Color

# Configuration
readonly MIN_JAVA_VERSION=17
readonly POSTGRES_WAIT_TIME=10
readonly DOCKER_CONTAINER_NAME="secret-dictionary-db"

# ============================================
# Fonctions utilitaires
# ============================================

print_header() {
    echo ""
    echo -e "${CYAN}========================================${NC}"
    echo -e "${CYAN}  $1${NC}"
    echo -e "${CYAN}========================================${NC}"
    echo ""
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# ============================================
# Vérifications des prérequis
# ============================================

check_java() {
    print_info "Vérification de Java..."

    if ! command -v java &> /dev/null; then
        print_error "Java n'est pas installé"
        echo ""
        echo "Installez Java $MIN_JAVA_VERSION+ avec :"
        echo "  • Ubuntu/Debian : sudo apt install openjdk-17-jdk"
        echo "  • Fedora/RHEL   : sudo dnf install java-17-openjdk-devel"
        echo "  • macOS         : brew install openjdk@17"
        echo ""
        echo "Ou téléchargez depuis : https://adoptium.net/"
        exit 1
    fi

    JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[0-9]+' | head -1)

    if [ "$JAVA_VERSION" -lt "$MIN_JAVA_VERSION" ]; then
        print_error "Java $JAVA_VERSION détecté, mais Java $MIN_JAVA_VERSION+ est requis"
        echo ""
        echo "Veuillez mettre à jour Java ou modifier pom.xml"
        exit 1
    fi

    print_success "Java $JAVA_VERSION détecté"
}

check_docker() {
    print_info "Vérification de Docker..."

    if ! command -v docker &> /dev/null; then
        print_error "Docker n'est pas installé"
        echo ""
        echo "Installez Docker avec :"
        echo "  curl -fsSL https://get.docker.com -o get-docker.sh"
        echo "  sudo sh get-docker.sh"
        echo ""
        echo "Ou téléchargez Docker Desktop : https://www.docker.com/products/docker-desktop"
        exit 1
    fi

    # Vérifier les permissions Docker
    if ! docker ps &> /dev/null; then
        print_warning "Permissions Docker manquantes"
        echo ""
        echo "Pour utiliser Docker sans sudo, exécutez :"
        echo -e "${CYAN}  sudo usermod -aG docker \$USER${NC}"
        echo -e "${CYAN}  newgrp docker${NC}"
        echo ""

        read -p "Voulez-vous lancer avec sudo temporairement ? (o/n) : " USE_SUDO

        if [[ "$USE_SUDO" =~ ^[oO]$ ]]; then
            DOCKER_CMD="sudo docker"
            COMPOSE_CMD="sudo docker compose"
            print_warning "Utilisation de sudo (temporaire)"
        else
            print_info "Configurez d'abord les permissions Docker, puis relancez."
            exit 1
        fi
    else
        DOCKER_CMD="docker"
        COMPOSE_CMD="docker compose"
        print_success "Docker opérationnel"
    fi
    echo ""
}

# ============================================
# Exécution des tests
# ============================================

run_tests() {
    print_header "Tests Automatisés"

    read -p "Exécuter les tests unitaires avant le démarrage ? (o/n) : " RUN_TESTS

    if [[ "$RUN_TESTS" =~ ^[oO]$ ]]; then
        echo ""
        print_info "Exécution des tests..."
        echo ""

        # Détecter Maven ou Maven Wrapper
        if command -v mvn &> /dev/null; then
            MVN_CMD="mvn"
        else
            chmod +x mvnw 2>/dev/null || true
            MVN_CMD="./mvnw"
        fi

        # Exécuter les tests
        if $MVN_CMD test -q; then
            echo ""
            print_success "Tous les tests ont réussi !"
            print_info "Logs détaillés disponibles :"
            echo "   → logs/LogMotDAOTest.log"
            echo "   → logs/LogMotServiceTest.log"
        else
            echo ""
            print_warning "Certains tests ont échoué"
            print_info "Consultez les fichiers de logs pour plus de détails"
            echo ""

            read -p "Continuer malgré les erreurs ? (o/n) : " CONTINUE
            if [[ ! "$CONTINUE" =~ ^[oO]$ ]]; then
                print_error "Arrêt du script"
                exit 1
            fi
        fi
    fi
    echo ""
}

# ============================================
# Gestion de PostgreSQL
# ============================================

start_postgres() {
    print_header "PostgreSQL"
    print_info "Démarrage de la base de données..."

    # Vérifier si le conteneur existe
    if $DOCKER_CMD ps -a --format "{{.Names}}" | grep -q "^${DOCKER_CONTAINER_NAME}\$"; then
        print_info "Conteneur PostgreSQL existant détecté"

        # Vérifier s'il est déjà en cours d'exécution
        if $DOCKER_CMD ps --format "{{.Names}}" | grep -q "^${DOCKER_CONTAINER_NAME}\$"; then
            print_success "PostgreSQL déjà en cours d'exécution"
        else
            # Redémarrer le conteneur existant
            if $DOCKER_CMD start "$DOCKER_CONTAINER_NAME" &> /dev/null; then
                print_success "Conteneur PostgreSQL redémarré"
            else
                print_warning "Échec du redémarrage, tentative avec docker compose..."
                start_postgres_compose
            fi
        fi
    else
        # Aucun conteneur n'existe, utiliser docker compose
        start_postgres_compose
    fi

    # Attendre que PostgreSQL soit prêt
    print_info "Attente de PostgreSQL ($POSTGRES_WAIT_TIME secondes)..."
    sleep "$POSTGRES_WAIT_TIME"

    # Vérifier la connexion
    if $DOCKER_CMD exec "$DOCKER_CONTAINER_NAME" pg_isready -U FSDM &> /dev/null; then
        print_success "PostgreSQL prêt !"
    else
        print_warning "PostgreSQL démarre toujours..."
        print_info "L'application se lancera quand même"
    fi
    echo ""
}

start_postgres_compose() {
    # Détecter docker-compose ou docker compose
    if command -v docker-compose &> /dev/null; then
        COMPOSE_CMD="${COMPOSE_CMD:-docker-compose}"
    else
        COMPOSE_CMD="${COMPOSE_CMD:-docker compose}"
    fi

    if $COMPOSE_CMD up -d &> /dev/null; then
        print_success "PostgreSQL démarré avec Docker Compose"
    else
        print_error "Échec du démarrage de PostgreSQL"
        echo ""
        echo "Vérifiez que docker-compose.yml existe et que Docker fonctionne."
        exit 1
    fi
}

# ============================================
# Lancement de l'application
# ============================================

launch_app() {
    print_header "Lancement de l'Application"
    print_info "Démarrage de l'interface JavaFX..."
    echo ""

    # Détecter Maven ou Maven Wrapper
    if command -v mvn &> /dev/null; then
        mvn javafx:run
    else
        chmod +x mvnw 2>/dev/null || true
        ./mvnw javafx:run
    fi
}

# ============================================
# Nettoyage après fermeture
# ============================================

cleanup() {
    echo ""
    print_header "Fermeture de l'Application"

    read -p "Arrêter PostgreSQL ? (o/n) : " STOP_POSTGRES

    if [[ "$STOP_POSTGRES" =~ ^[oO]$ ]]; then
        print_info "Arrêt de PostgreSQL..."

        # Détecter la commande compose
        if command -v docker-compose &> /dev/null; then
            COMPOSE_CMD="${COMPOSE_CMD:-docker-compose}"
        else
            COMPOSE_CMD="${COMPOSE_CMD:-docker compose}"
        fi

        if $COMPOSE_CMD down &> /dev/null; then
            print_success "PostgreSQL arrêté avec succès"
        else
            print_warning "Impossible d'arrêter PostgreSQL automatiquement"
            print_info "Utilisez : docker stop $DOCKER_CONTAINER_NAME"
        fi
    else
        print_info "PostgreSQL reste actif en arrière-plan"
        print_info "Pour l'arrêter plus tard : docker stop $DOCKER_CONTAINER_NAME"
    fi

    echo ""
    print_header "Merci d'avoir utilisé Secret Dictionary !"
    echo ""
}

# ============================================
# Programme Principal
# ============================================

main() {
    print_header "Secret Dictionary - Démarrage"

    check_java
    check_docker
    run_tests
    start_postgres
    launch_app
    cleanup
}

# Lancer le programme principal
main