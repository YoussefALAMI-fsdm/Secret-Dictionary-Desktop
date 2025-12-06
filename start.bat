@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

REM ====================================================
REM Script de démarrage pour Secret Dictionary (Windows)
REM Version améliorée avec couleurs et gestion complète
REM ====================================================

REM Définir les couleurs (codes ANSI)
set "ESC="
set "RESET=%ESC%[0m"
set "RED=%ESC%[91m"
set "GREEN=%ESC%[92m"
set "YELLOW=%ESC%[93m"
set "BLUE=%ESC%[94m"
set "CYAN=%ESC%[96m"
set "WHITE=%ESC%[97m"

echo.
echo %CYAN%========================================%RESET%
echo %CYAN%  Secret Dictionary - Demarrage%RESET%
echo %CYAN%========================================%RESET%
echo.

REM ============================================
REM 1. Vérifier Java
REM ============================================
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo %RED%[ERREUR] Java n'est pas installe%RESET%
    echo          Installez Java 17+ depuis : https://adoptium.net/
    pause
    exit /b 1
)

REM Extraire la version Java
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VER=%%g
)
set JAVA_VER=!JAVA_VER:"=!
for /f "delims=. tokens=1" %%v in ("!JAVA_VER!") do set JAVA_MAJOR=%%v

if !JAVA_MAJOR! LSS 17 (
    echo %RED%[ERREUR] Java !JAVA_MAJOR! detecte, mais Java 17+ est requis%RESET%
    echo.
    echo Solutions :
    echo   ^> Installer Java 17 depuis : https://adoptium.net/
    echo   ^> Ou modifier pom.xml pour utiliser Java !JAVA_MAJOR!
    pause
    exit /b 1
)

echo %GREEN%[OK] Java !JAVA_MAJOR! detecte%RESET%
echo.

REM ============================================
REM 2. Vérifier Docker
REM ============================================
where docker >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo %RED%[ERREUR] Docker n'est pas installe%RESET%
    echo          Installez Docker Desktop : https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

echo %GREEN%[OK] Docker detecte%RESET%
echo.

REM ============================================
REM 3. Proposer d'exécuter les tests
REM ============================================
echo %BLUE%[INFO] Tests automatises%RESET%
set /p RUN_TESTS="Voulez-vous executer les tests unitaires ? (o/n) : "
echo.

if /i "%RUN_TESTS%"=="o" (
    echo %CYAN%[INFO] Execution des tests DAO et Service...%RESET%
    echo.

    REM Exécuter les tests avec Maven
    where mvn >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        mvn test -q
    ) else (
        call mvnw.cmd test -q
    )

    if %ERRORLEVEL% EQU 0 (
        echo.
        echo %GREEN%[OK] Tous les tests ont reussi !%RESET%
        echo %CYAN%[INFO] Logs detailles :%RESET%
        echo       - logs\LogMotDAOTest.log
        echo       - logs\LogMotServiceTest.log
        echo.
    ) else (
        echo.
        echo %YELLOW%[AVERTISSEMENT] Certains tests ont echoue%RESET%
        echo %CYAN%[INFO] Consultez les logs pour plus de details%RESET%
        echo.
        set /p CONTINUE="Continuer malgre les erreurs ? (o/n) : "
        if /i not "!CONTINUE!"=="o" (
            echo %RED%Arret du script.%RESET%
            pause
            exit /b 1
        )
        echo.
    )
)

REM ============================================
REM 4. Démarrer PostgreSQL
REM ============================================
echo %CYAN%[INFO] Demarrage de PostgreSQL...%RESET%

REM Vérifier si un conteneur existe déjà
docker ps -a --format "{{.Names}}" | findstr /i "secret-dictionary-db" >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo %YELLOW%[INFO] Conteneur PostgreSQL deja existant%RESET%
    docker start secret-dictionary-db >nul 2>&1
    if %ERRORLEVEL% EQU 0 (
        echo %GREEN%[OK] Conteneur redémarre%RESET%
    ) else (
        echo %YELLOW%[INFO] Tentative avec docker-compose...%RESET%
        where docker-compose >nul 2>nul
        if %ERRORLEVEL% EQU 0 (
            docker-compose up -d >nul 2>&1
        ) else (
            docker compose up -d >nul 2>&1
        )
    )
) else (
    where docker-compose >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        docker-compose up -d >nul 2>&1
    ) else (
        docker compose up -d >nul 2>&1
    )
)

if %ERRORLEVEL% NEQ 0 (
    echo %RED%[ERREUR] Echec du demarrage de PostgreSQL%RESET%
    pause
    exit /b 1
)

echo %CYAN%[INFO] Attente de PostgreSQL (10 secondes)...%RESET%
timeout /t 10 /nobreak >nul

echo %GREEN%[OK] PostgreSQL pret !%RESET%
echo.

REM ============================================
REM 5. Lancer l'application
REM ============================================
echo %CYAN%[INFO] Lancement de l'application JavaFX...%RESET%
echo %YELLOW%[INFO] Fermeture de cette fenetre = Arret de l'application%RESET%
echo.

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
echo %CYAN%  Application fermee%RESET%
echo %CYAN%========================================%RESET%
echo.

set /p STOP="Arreter PostgreSQL ? (o/n) : "
if /i "%STOP%"=="o" (
    echo %CYAN%[INFO] Arret de PostgreSQL...%RESET%

    where docker-compose >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        docker-compose down >nul 2>&1
    ) else (
        docker compose down >nul 2>&1
    )

    if %ERRORLEVEL% EQU 0 (
        echo %GREEN%[OK] PostgreSQL arrete%RESET%
    ) else (
        echo %YELLOW%[AVERTISSEMENT] Impossible d'arreter PostgreSQL automatiquement%RESET%
        echo %CYAN%[INFO] Utilisez : docker stop secret-dictionary-db%RESET%
    )
) else (
    echo %YELLOW%[INFO] PostgreSQL reste actif en arriere-plan%RESET%
    echo %CYAN%[INFO] Pour l'arreter : docker stop secret-dictionary-db%RESET%
)

echo.
echo %GREEN%Merci d'avoir utilise Secret Dictionary !%RESET%
echo.
pause