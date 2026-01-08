@echo off
echo ======================================
echo   Execution avec MySQL
echo ======================================
echo.
echo IMPORTANT: Assurez-vous que:
echo  1. XAMPP MySQL est demarre
echo  2. La base restaurant_db existe
echo  3. Le script schema.sql a ete execute
echo.
pause

java -cp "bin;lib\mysql-connector-j-8.3.0.jar" com.example.restaurant.InteractiveMain

pause
