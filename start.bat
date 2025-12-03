@echo off
chcp 65001 >nul
REM ====================================================
REM Script de démarrage pour Secret Dictionary (Windows)
REM ====================================================

echo.
echo ========================================
echo   Secret Dictionary - Demarrage
echo ========================================
echo.

REM ============================================
REM 1. Verifier Java
REM ============================================
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Java n'est pas installe
    echo          Installez Java 17+ depuis : https://adoptium.net/
    pause
    exit /b 1
)

REM Extraire la version Java
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VER=%%g
)
set JAVA_VER=%JAVA_VER:"=%
for /f "delims=. tokens=1" %%v in ("%JAVA_VER%") do set JAVA_MAJOR=%%v

if %JAVA_MAJOR% LSS 17 (
    echo [ERREUR] Java %JAVA_MAJOR% detecte, mais Java 17+ est requis
    echo.
    echo Solutions :
    echo   ^> Installer Java 17 depuis : https://adoptium.net/
    echo   ^> Ou modifier pom.xml pour utiliser Java %JAVA_MAJOR%
    pause
    exit /b 1
)

echo [OK] Java %JAVA_MAJOR% detecte
echo.

REM ============================================
REM 2. Verifier Docker
REM ============================================
where docker >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Docker n'est pas installe
    echo          Installez Docker Desktop : https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

echo [OK] Docker detecte
echo.

REM ============================================
REM 3. Proposer d'executer les tests
REM ============================================
echo [INFO] Tests automatises
set /p RUN_TESTS="Voulez-vous executer les tests unitaires ? (o/n) : "
echo.

if /i "%RUN_TESTS%"=="o" (
    echo [INFO] Execution des tests...
    echo.

    REM Executer les tests avec Maven (mode silencieux)
    where mvn >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        mvn test -q
    ) else (
        mvnw.cmd test -q
    )

    if %ERRORLEVEL% EQU 0 (
        echo.
        echo [OK] Tous les tests ont reussi !
        echo [INFO] Logs detailles : logs\LogMotDAOTest.log
        echo.
    ) else (
        echo.
        echo [ERREUR] Certains tests ont echoue
        echo [INFO] Consultez logs\LogMotDAOTest.log pour plus de details
        echo.
        set /p CONTINUE="Continuer malgre les erreurs ? (o/n) : "
        if /i not "%CONTINUE%"=="o" (
            echo Arret du script.
            pause
            exit /b 1
        )
        echo.
    )
)

REM ============================================
REM 4. Demarrer PostgreSQL
REM ============================================
echo [INFO] Demarrage de PostgreSQL...

where docker-compose >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    docker-compose up -d >nul 2>&1
) else (
    docker compose up -d >nul 2>&1
)

if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Echec du demarrage de PostgreSQL
    pause
    exit /b 1
)

echo [INFO] Attente de PostgreSQL (10 secondes)...
timeout /t 10 /nobreak >nul

echo [OK] PostgreSQL pret !
echo.

REM ============================================
REM 5. Lancer l'application
REM ============================================
echo [INFO] Lancement de l'application...
echo.

where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    mvn javafx:run
) else (
    mvnw.cmd javafx:run
)

REM ============================================
REM 6. Nettoyage
REM ============================================
echo.
echo ========================================
echo   Application fermee
echo ========================================
echo.
set /p STOP="Arreter PostgreSQL ? (o/n) : "
if /i "%STOP%"=="o" (
    where docker-compose >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        docker-compose down >nul 2>&1
    ) else (
        docker compose down >nul 2>&1
    )
    echo [OK] PostgreSQL arrete
)

echo.
echo Merci d'avoir utilise Secret Dictionary !
echo.
pause