@echo off
REM ====================================================
REM Script de démarrage pour Secret Dictionary (Windows)
REM ====================================================

echo.
echo ========================================
echo   Secret Dictionary - Demarrage
echo ========================================
echo.

REM Vérifier si Docker est installé
where docker >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Docker n'est pas installe ou pas dans le PATH.
    echo Veuillez installer Docker Desktop : https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

REM Vérifier si Java est installé
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Java n'est pas installe ou pas dans le PATH.
    echo Veuillez installer Java 17+ : https://adoptium.net/
    pause
    exit /b 1
)

REM Afficher la version Java
echo [INFO] Version Java detectee :
java -version
echo.

REM Vérifier si Maven est installé (sinon utiliser mvnw)
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    set MVN_CMD=mvn
    echo [INFO] Maven detecte : utilisation de Maven
) else (
    set MVN_CMD=mvnw.cmd
    echo [INFO] Maven non detecte : utilisation de Maven Wrapper
)

REM Démarrer Docker PostgreSQL
echo [ETAPE 1/3] Demarrage de la base de donnees PostgreSQL...
docker-compose up -d

REM Attendre que PostgreSQL soit prêt
echo [INFO] Attente de la disponibilite de PostgreSQL...
timeout /t 5 /nobreak >nul

REM Vérifier si PostgreSQL est prêt
docker exec secret-dictionary-db pg_isready -U FSDM -d dictionary >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [AVERTISSEMENT] PostgreSQL n'est pas encore pret, attente supplementaire...
    timeout /t 5 /nobreak >nul
)

echo [OK] PostgreSQL est pret !
echo.

REM Compiler l'application (optionnel, décommenter si nécessaire)
REM echo [ETAPE 2/3] Compilation de l'application...
REM %MVN_CMD% clean compile
REM if %ERRORLEVEL% NEQ 0 (
REM     echo [ERREUR] Echec de la compilation
REM     pause
REM     exit /b 1
REM )
REM echo.

REM Lancer l'application JavaFX
echo [ETAPE 2/3] Lancement de l'application JavaFX...
echo.
%MVN_CMD% javafx:run

REM Si l'application se ferme, proposer d'arrêter Docker
echo.
echo ========================================
echo   Application fermee
echo ========================================
echo.
set /p STOP_DOCKER="Voulez-vous arreter PostgreSQL ? (o/n) : "
if /i "%STOP_DOCKER%"=="o" (
    echo [INFO] Arret de PostgreSQL...
    docker-compose down
    echo [OK] PostgreSQL arrete
)

pause