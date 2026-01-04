@echo off
echo Compilation du projet Restaurant...
echo.

REM Create output directory
if not exist "bin" mkdir bin

REM Compile all Java files
dir /s /B src\main\java\*.java > sources.txt
javac -d bin @sources.txt

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Compilation réussie!
    echo Les fichiers .class sont dans le dossier 'bin'
) else (
    echo.
    echo ✗ Erreur de compilation
)

del sources.txt
pause
