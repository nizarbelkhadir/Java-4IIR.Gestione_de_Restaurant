@echo off
echo ======================================
echo   Compilation avec MySQL JDBC
echo ======================================
echo.

REM Vérifier si le driver MySQL existe
if not exist "lib\mysql-connector-j-8.3.0.jar" (
    echo ERREUR: Driver MySQL JDBC introuvable!
    echo Telechargez mysql-connector-j-8.3.0.jar et placez-le dans lib/
    echo URL: https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar
    pause
    exit /b 1
)

echo Compilation en cours...

REM Créer le dossier bin s'il n'existe pas
if not exist bin mkdir bin

REM Compiler tous les fichiers Java
for /R src\main\java %%f in (*.java) do (
    javac -cp "lib\mysql-connector-j-8.3.0.jar;bin" -d bin "%%f" 2>nul
)

javac -cp "lib\mysql-connector-j-8.3.0.jar;bin" -d bin src\main\java\com\example\restaurant\*.java src\main\java\com\example\restaurant\model\*.java src\main\java\com\example\restaurant\service\*.java src\main\java\com\example\restaurant\storage\*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ======================================
    echo   Compilation reussie! [OK]
    echo ======================================
    echo.
    echo Utilisez run-db.bat pour executer
) else (
    echo.
    echo ======================================
    echo   Erreur de compilation! [ERREUR]
    echo ======================================
)

pause
