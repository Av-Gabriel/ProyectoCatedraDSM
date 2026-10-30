@echo off
setlocal
cd /d "%~dp0"
where node >nul 2>nul
if errorlevel 1 (
    echo Instala Node.js 22.13 o superior y vuelve a abrir este archivo.
    pause
    exit /b 1
)
node -e "const [a,b]=process.versions.node.split('.').map(Number);process.exit(a>22||(a===22&&b>=13)?0:1)"
if errorlevel 1 (
    echo Este servidor necesita Node.js 22.13 o superior.
    pause
    exit /b 1
)
node "%~dp0backend\server.cjs"
if errorlevel 1 pause
