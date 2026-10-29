# Smart Gym Membership Manager 2.0 - PowerShell Launcher
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "      SMART GYM MEMBERSHIP MANAGER 2.0 - LAUNCHER       " -ForegroundColor Yellow
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "[1/2] Compiling Java source files..." -ForegroundColor Green
$sources = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
javac -encoding UTF-8 -cp "lib\mysql-connector-j-8.3.0.jar" -d "bin" $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed! Check JDK installation." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[2/2] Launching Application GUI..." -ForegroundColor Green
Write-Host ""
java -cp "bin;lib\mysql-connector-j-8.3.0.jar" com.smartgym.main.Main
