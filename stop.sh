# ============================================
# stop.sh (Linux/macOS)
# ============================================
#!/bin/bash
echo "Arrêt de PostgreSQL..."
if command -v docker-compose &> /dev/null; then
    docker-compose down
else
    docker compose down
fi
echo "PostgreSQL arrêté avec succès !"