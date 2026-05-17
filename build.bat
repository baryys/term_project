@echo off
:: build.bat — Compile and package TravelPlanner on Windows (no Maven/Gradle).
::
:: Usage:
::   build.bat          compile + package
::   build.bat clean    remove out\ and TravelPlanner.jar, then build

setlocal

set SRC_DIR=src
set OUT_DIR=out
set JAR=TravelPlanner.jar
set MANIFEST=manifest.txt

:: ---- 0. Optional clean -------------------------------------------------------
if /i "%1"=="clean" (
    echo [clean] Removing %OUT_DIR%\ and %JAR%
    if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
    if exist "%JAR%"     del /q "%JAR%"
)

:: ---- 1. Prepare output directory ---------------------------------------------
echo [1/4] Preparing output directory: %OUT_DIR%\
if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%"

:: ---- 2. Compile all Java sources ---------------------------------------------
echo [2/4] Compiling Java sources...
dir /s /b "%SRC_DIR%\*.java" > sources.txt
javac -encoding UTF-8 -d "%OUT_DIR%" @sources.txt
del sources.txt
echo       Done.

:: ---- 3. Copy resources into the class-file tree ------------------------------
echo [3/4] Copying resources...
xcopy /s /q /y "resources\*" "%OUT_DIR%\" >nul
echo       Copied resources\ to %OUT_DIR%\

:: ---- 4. Package everything into a JAR ----------------------------------------
echo [4/4] Packaging %JAR%...
jar cfm "%JAR%" "%MANIFEST%" -C "%OUT_DIR%" .
echo       Done.

echo.
echo Build successful.
echo Run with:  java -jar %JAR%

endlocal
