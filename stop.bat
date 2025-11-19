# ============================================
# stop.bat (Windows)
# ============================================
@echo off
echo Arret de PostgreSQL...
docker-compose down
echo PostgreSQL arrete avec succes !
pause