@echo off
echo ======================================
echo   Test de Connexion MySQL
echo ======================================
echo.

REM Compiler le test
echo Compilation de TestConnection...
javac -cp "lib\mysql-connector-j-8.3.0.jar;bin" -d bin src\main\java\com\example\restaurant\TestConnection.java

if %ERRORLEVEL% NEQ 0 (
    echo Erreur de compilation!
    pause
    exit /b 1
)

echo.
echo Execution du test...
echo.
java -cp "bin;lib\mysql-connector-j-8.3.0.jar" com.example.restaurant.TestConnection

pause
