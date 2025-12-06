@echo off
chcp 65001 >nul 2>&1
setlocal EnableDelayedExpansion

REM ====================================================
REM Script de démarrage pour Secret Dictionary (Windows)
REM Version 3.1 - Couleurs corrigées
REM ====================================================

REM Configuration
set "MIN_JAVA_VERSION=17"
set "POSTGRES_WAIT_TIME=10"
set "DOCKER_CONTAINER_NAME=secret-dictionary-db"

REM Couleurs ANSI (Windows 10+)
REM Le caractère ESC doit être défini avec la séquence hexadécimale 0x1B
for /F %%a in ('echo prompt $E ^| cmd') do set "ESC=%%a"
set "RESET=%ESC%[0m"
set "RED=%ESC%[91m"
set "GREEN=%ESC%[92m"
set "YELLOW=%ESC%[93m"
set "BLUE=%ESC%[94m"
set "CYAN=%ESC%[96m"

REM ============================================
REM En-tête
REM ============================================
echo.
echo %CYAN%========================================%RESET%
echo %CYAN%  Secret Dictionary - Demarrage%RESET%
echo %CYAN%========================================%RESET%
echo.

REM ============================================
REM 1. Vérifier Java
REM ============================================
echo %BLUE%[INFO] Verification de Java...%RESET%

where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo %RED%[ERREUR] Java n'est pas installe%RESET%
    echo.
    echo Installez Java %MIN_JAVA_VERSION%+ depuis :
    echo   ^> https://adoptium.net/
    echo.
    pause
    exit /b 1
)

REM Extraire la version Java majeure
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VER=%%g
)
set JAVA_VER=!JAVA_VER:"=!
for /f "delims=. tokens=1" %%v in ("!JAVA_VER!") do set JAVA_MAJOR=%%v

if !JAVA_MAJOR! LSS %MIN_JAVA_VERSION% (
    echo %RED%[ERREUR] Java !JAVA_MAJOR! detecte, mais Java %MIN_JAVA_VERSION%+ est requis%RESET%
    echo.
    echo Solutions :
    echo   ^> Installer Java %MIN_JAVA_VERSION% : https://adoptium.net/
    echo   ^> Ou modifier pom.xml pour Java !JAVA_MAJOR!
    echo.
    pause
    exit /b 1
)

echo %GREEN%[OK] Java !JAVA_MAJOR! detecte%RESET%
echo.

REM ============================================
REM 2. Vérifier Docker
REM ============================================
echo %BLUE%[INFO] Verification de Docker...%RESET%

where docker >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo %RED%[ERREUR] Docker n'est pas installe%RESET%
    echo.
    echo Installez Docker Desktop depuis :
    echo   ^> https://www.docker.com/products/docker-desktop
    echo.
    pause
    exit /b 1
)

REM Vérifier que Docker Desktop est lancé
docker ps >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo %YELLOW%[AVERTISSEMENT] Docker Desktop n'est pas lance%RESET%
    echo.
    echo Lancez Docker Desktop puis reessayez.
    pause
    exit /b 1
)

echo %GREEN%[OK] Docker operationnel%RESET%
echo.

REM ============================================
REM 3. Exécution des tests
REM ============================================
echo %CYAN%========================================%RESET%
echo %CYAN%  Tests Automatises%RESET%
echo %CYAN%========================================%RESET%
echo.

set /p RUN_TESTS="Executer les tests unitaires ? (o/n) : "

if /i "!RUN_TESTS!"=="o" (
    echo.
    echo %BLUE%[INFO] Execution des tests...%RESET%
    echo.

    REM Détecter Maven ou Maven Wrapper
    where mvn >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        set MVN_CMD=mvn
    ) else (
        set MVN_CMD=mvnw.cmd
    )

    REM Exécuter les tests
    call !MVN_CMD! test -q

    if %ERRORLEVEL% EQU 0 (
        echo.
        echo %GREEN%[OK] Tous les tests ont reussi !%RESET%
        echo %BLUE%[INFO] Logs detailles disponibles :%RESET%
        echo   ^> logs\LogMotDAOTest.log
        echo   ^> logs\LogMotServiceTest.log
    ) else (
        echo.
        echo %YELLOW%[AVERTISSEMENT] Certains tests ont echoue%RESET%
        echo %BLUE%[INFO] Consultez les fichiers de logs%RESET%
        echo.

        set /p CONTINUE="Continuer malgre les erreurs ? (o/n) : "
        if /i not "!CONTINUE!"=="o" (
            echo %RED%[ERREUR] Arret du script%RESET%
            pause
            exit /b 1
        )
    )
)
echo.

REM ============================================
REM 4. Démarrer PostgreSQL
REM ============================================
echo %CYAN%========================================%RESET%
echo %CYAN%  PostgreSQL%RESET%
echo %CYAN%========================================%RESET%
echo.
echo %BLUE%[INFO] Demarrage de PostgreSQL...%RESET%

REM Vérifier si le conteneur existe déjà
docker ps -a --format "{{.Names}}" | findstr /i "^%DOCKER_CONTAINER_NAME%$" >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo %BLUE%[INFO] Conteneur PostgreSQL existant detecte%RESET%

    REM Vérifier s'il est déjà en cours d'exécution
    docker ps --format "{{.Names}}" | findstr /i "^%DOCKER_CONTAINER_NAME%$" >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        echo %GREEN%[OK] PostgreSQL deja en cours d'execution%RESET%
    ) else (
        REM Redémarrer le conteneur
        docker start %DOCKER_CONTAINER_NAME% >nul 2>&1
        if %ERRORLEVEL% EQU 0 (
            echo %GREEN%[OK] Conteneur PostgreSQL redemarre%RESET%
        ) else (
            echo %YELLOW%[INFO] Tentative avec docker compose...%RESET%
            call :start_postgres_compose
        )
    )
) else (
    REM Aucun conteneur n'existe, utiliser docker compose
    call :start_postgres_compose
)

REM Attendre que PostgreSQL soit prêt
echo %BLUE%[INFO] Attente de PostgreSQL (%POSTGRES_WAIT_TIME% secondes)...%RESET%
timeout /t %POSTGRES_WAIT_TIME% /nobreak >nul

REM Vérifier la connexion
docker exec %DOCKER_CONTAINER_NAME% pg_isready -U FSDM >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo %GREEN%[OK] PostgreSQL pret !%RESET%
) else (
    echo %YELLOW%[AVERTISSEMENT] PostgreSQL demarre toujours...%RESET%
    echo %BLUE%[INFO] L'application se lancera quand meme%RESET%
)
echo.

REM ============================================
REM 5. Lancer l'application
REM ============================================
echo %CYAN%========================================%RESET%
echo %CYAN%  Lancement de l'Application%RESET%
echo %CYAN%========================================%RESET%
echo.
echo %BLUE%[INFO] Demarrage de l'interface JavaFX...%RESET%
echo.

REM Détecter Maven ou Maven Wrapper
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    mvn javafx:run
) else (
    call mvnw.cmd javafx:run
)

REM ============================================
REM 6. Nettoyage après fermeture
REM ============================================
echo.
echo %CYAN%========================================%RESET%
echo %CYAN%  Fermeture de l'Application%RESET%
echo %CYAN%========================================%RESET%
echo.

set /p STOP_POSTGRES="Arreter PostgreSQL ? (o/n) : "

if /i "!STOP_POSTGRES!"=="o" (
    echo %BLUE%[INFO] Arret de PostgreSQL...%RESET%

    REM Détecter docker-compose ou docker compose
    where docker-compose >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        docker-compose down >nul 2>&1
    ) else (
        docker compose down >nul 2>&1
    )

    if %ERRORLEVEL% EQU 0 (
        echo %GREEN%[OK] PostgreSQL arrete avec succes%RESET%
    ) else (
        echo %YELLOW%[AVERTISSEMENT] Impossible d'arreter PostgreSQL automatiquement%RESET%
        echo %BLUE%[INFO] Utilisez : docker stop %DOCKER_CONTAINER_NAME%%RESET%
    )
) else (
    echo %BLUE%[INFO] PostgreSQL reste actif en arriere-plan%RESET%
    echo %BLUE%[INFO] Pour l'arreter : docker stop %DOCKER_CONTAINER_NAME%%RESET%
)

echo.
echo %CYAN%========================================%RESET%
echo %CYAN%  Merci d'avoir utilise Secret Dictionary !%RESET%
echo %CYAN%========================================%RESET%
echo.
pause
exit /b 0

REM ============================================
REM Fonction : Démarrer PostgreSQL avec Compose
REM ============================================
:start_postgres_compose
where docker-compose >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    docker-compose up -d >nul 2>&1
) else (
    docker compose up -d >nul 2>&1
)

if %ERRORLEVEL% EQU 0 (
    echo %GREEN%[OK] PostgreSQL demarre avec Docker Compose%RESET%
) else (
    echo %RED%[ERREUR] Echec du demarrage de PostgreSQL%RESET%
    echo.
    echo Verifiez que docker-compose.yml existe et que Docker fonctionne.
    pause
    exit /b 1
)
goto :eof