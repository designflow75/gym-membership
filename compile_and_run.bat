@echo off
title Smart Gym Management System 2.0 - Build and Run
echo ========================================================
echo       SMART GYM MANAGEMENT SYSTEM 2.0 - LAUNCHER       
echo ========================================================
echo.

if not exist "bin" mkdir bin

echo [1/2] Compiling Java source files...
javac -encoding UTF-8 -cp "lib\mysql-connector-j-8.3.0.jar" -d "bin" src\com\smartgym\model\*.java src\com\smartgym\exception\*.java src\com\smartgym\database\*.java src\com\smartgym\dao\*.java src\com\smartgym\util\*.java src\com\smartgym\service\*.java src\com\smartgym\ui\*.java src\com\smartgym\test\*.java src\com\smartgym\main\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed! Please check JDK installation.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Launching Application GUI...
echo.
java -cp "bin;lib\mysql-connector-j-8.3.0.jar" com.smartgym.main.Main

pause
