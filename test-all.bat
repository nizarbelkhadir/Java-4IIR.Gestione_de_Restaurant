@echo off
echo ======================================
echo   Test Complet du Projet Restaurant
echo ======================================
echo.

echo [1/5] Verification du driver MySQL...
if not exist "lib\mysql-connector-j-8.3.0.jar" (
    echo [ERREUR] Driver MySQL introuvable!
    pause
    exit /b 1
)
echo [OK] Driver trouve

echo.
echo [2/5] Verification de MySQL...
tasklist /FI "IMAGENAME eq mysqld.exe" 2>NUL | find /I /N "mysqld.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo [OK] MySQL est actif
) else (
    echo [ERREUR] MySQL n'est pas demarre. Demarrez XAMPP!
    pause
    exit /b 1
)

echo.
echo [3/5] Compilation du projet...
call compile-db.bat
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Compilation echouee
    pause
    exit /b 1
)

echo.
echo [4/5] Test de connexion a la base de donnees...
java -cp "bin;lib\mysql-connector-j-8.3.0.jar" com.example.restaurant.TestConnection
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Test de connexion echoue
    pause
    exit /b 1
)

echo.
echo [5/5] Verification des fichiers compiles...
dir /b /s bin\*.class >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Aucun fichier compile trouve
    pause
    exit /b 1
)

for /f %%a in ('dir /b /s bin\*.class ^| find /c ".class"') do set COUNT=%%a
echo [OK] %COUNT% classes compilees

echo.
echo ======================================
echo   TOUS LES TESTS SONT PASSES!
echo ======================================
echo.
echo Le projet est pret a etre execute:
echo   run-db.bat
echo.
pause
