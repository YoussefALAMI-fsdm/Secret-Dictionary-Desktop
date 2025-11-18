@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM Empêcher la fermeture automatique du terminal
set "SCRIPT_RUNNING=1"

echo.
echo ================================
echo  Secret-Dictionary-Desktop
echo ================================
echo.
echo 🚀 Demarrage de l'application...
echo.

REM Vérifier si Docker est installé
where docker >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Docker n'est pas installe.
    echo.
    echo Solution : Installez Docker Desktop depuis :
    echo https://www.docker.com/products/docker-desktop
    echo.
    echo Appuyez sur une touche pour fermer...
    pause >nul
    exit /b 1
)

echo [OK] Docker detecte
echo.

REM Vérifier si Docker est en cours d'exécution
docker ps >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Docker Desktop n'est pas demarre.
    echo.
    echo Solution : Lancez Docker Desktop et attendez qu'il soit pret.
    echo Ensuite, relancez ce script.
    echo.
    echo Appuyez sur une touche pour fermer...
    pause >nul
    exit /b 1
)

echo [OK] Docker est en cours d'execution
echo.

REM Vérifier Docker Compose
docker compose version >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    docker-compose --version >nul 2>nul
    if %ERRORLEVEL% NEQ 0 (
        echo [ERREUR] Docker Compose n'est pas disponible
        echo.
        echo Solution : Reinstallez Docker Desktop.
        echo.
        echo Appuyez sur une touche pour fermer...
        pause >nul
        exit /b 1
    )
    set DOCKER_COMPOSE=docker-compose
) else (
    set DOCKER_COMPOSE=docker compose
)

echo [OK] Docker Compose disponible
echo.

REM Vérifier si PostgreSQL est déjà en cours
docker ps --format "{{.Names}}" | findstr /C:"dictionary_pg" >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [INFO] PostgreSQL est deja en cours d'execution
    echo.
    goto postgres_ready
)

REM Démarrer PostgreSQL
echo [ACTION] Demarrage de PostgreSQL...
echo.
%DOCKER_COMPOSE% up -d postgres 2>&1

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERREUR] Impossible de demarrer PostgreSQL
    echo.
    echo Solutions possibles :
    echo - Le port 5432 est peut-etre deja utilise
    echo - Verifiez docker-compose.yml
    echo - Essayez : docker compose down
    echo.
    echo Appuyez sur une touche pour fermer...
    pause >nul
    exit /b 1
)

echo.
echo [INFO] Attente de PostgreSQL...
echo.

REM Attendre que PostgreSQL soit prêt (30 tentatives max)
set MAX_ATTEMPTS=30
set ATTEMPT=0

:wait_loop
set /a ATTEMPT+=1

%DOCKER_COMPOSE% exec -T postgres pg_isready -U FSDM -d dictionary >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [OK] PostgreSQL est pret !
    echo.
    goto postgres_ready
)

if %ATTEMPT% GEQ %MAX_ATTEMPTS% (
    echo.
    echo [ERREUR] Timeout : PostgreSQL n'a pas demarre dans les temps
    echo.
    echo Solutions :
    echo - Consultez les logs : docker compose logs postgres
    echo - Redemarrez : docker compose restart postgres
    echo.
    echo Appuyez sur une touche pour fermer...
    pause >nul
    exit /b 1
)

echo [INFO] PostgreSQL en cours de demarrage... (%ATTEMPT%/%MAX_ATTEMPTS%)
timeout /t 2 /nobreak >nul
goto wait_loop

:postgres_ready

REM Configurer les variables d'environnement
set DB_URL=jdbc:postgresql://localhost:5432/dictionary
set DB_USER=FSDM
set DB_PASSWORD=IA

echo [ACTION] Configuration de la connexion database...
echo - URL  : %DB_URL%
echo - User : %DB_USER%
echo.

REM Vérifier Maven
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [OK] Maven detecte
    echo.
    echo [ACTION] Compilation et lancement de l'application...
    echo.
    echo ================================================================
    echo.
    call mvn clean javafx:run
    set MVN_EXIT=%ERRORLEVEL%
) else (
    if exist "mvnw.cmd" (
        echo [INFO] Maven non installe, utilisation du wrapper...
        echo.
        echo [ACTION] Compilation et lancement de l'application...
        echo.
        echo ================================================================
        echo.
        call mvnw.cmd clean javafx:run
        set MVN_EXIT=%ERRORLEVEL%
    ) else (
        echo.
        echo [ERREUR] Ni Maven ni le wrapper Maven n'ont ete trouves
        echo.
        echo Solution : Installez Maven depuis :
        echo https://maven.apache.org/download.cgi
        echo.
        echo Ou assurez-vous que mvnw.cmd existe dans le projet.
        echo.
        echo Appuyez sur une touche pour fermer...
        pause >nul
        exit /b 1
    )
)

echo.
echo ================================================================
echo.

if %MVN_EXIT% NEQ 0 (
    echo [ERREUR] L'application s'est terminee avec des erreurs
    echo.
    echo Appuyez sur une touche pour continuer...
    pause >nul
) else (
    echo [OK] Application terminee normalement
    echo.
)

echo.
set /p STOP_PG="Voulez-vous arreter PostgreSQL ? (o/N) : "

if /i "!STOP_PG!"=="o" (
    echo.
    echo [ACTION] Arret de PostgreSQL...
    %DOCKER_COMPOSE% down
    echo [OK] PostgreSQL arrete
    echo.
)

echo.
echo Appuyez sur une touche pour fermer...
pause >nul