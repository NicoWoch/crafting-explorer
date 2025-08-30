@echo off

echo.
echo ==========================
echo      Building Mod...
echo ==========================
echo.

call .\gradlew build

if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    exit /b
)

echo.
echo =============================
echo   Copying mod to .minecraft
echo =============================
echo.

copy /Y ".\build\libs\recipe_exporter-1.0.jar" "%appdata%\.minecraft\versions\Enigmatica 2 Expert - E2E Enigmatica2Expert-1.90h\mods"

if %ERRORLEVEL% NEQ 0 (
    echo Copy failed!
    exit /b
)

echo.
echo ============================================
echo       Starting TLauncher for Minecraft
echo ============================================
echo.

C:\Users\nico\AppData\Roaming\.minecraft\TLauncher.exe

echo.
echo TLauncher started!
echo.
