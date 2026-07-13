# ============================================================================
# HRM_Epath - Script khoi dong he thong + Seed Data
#
# Lan dau:  .\start-hrm.ps1 -FirstRun
# Thuong:  .\start-hrm.ps1
#
# Moi chay xong se in ra thong tin truy cap (URL, tai khoan)
# ============================================================================

param(
    [switch]$FirstRun,    # Reset DB + seed data (lan dau tien hoac muon reset)
    [switch]$SkipSeed,    # Chi khoi dong, khong seed
    [string]$BackendPort = "8080",
    [string]$FrontendPort = "5173"
)

$ErrorActionPreference = "Stop"
$ProjectRoot = "D:\LP & EP IT\HRM"
$BackendDir = Join-Path $ProjectRoot "hrm-system\backend"
$FrontendDir = Join-Path $ProjectRoot "hrm-system\frontend"
$SeedSqlFile = Join-Path $BackendDir "src\main\resources\db\seed-demo-data.sql"
$LogDir = $env:TEMP

# ============================================================================
# Helper functions
# ============================================================================

function Log { param($msg) Write-Host "[$(Get-Date -Format 'HH:mm:ss')] $msg" }
function LogOk { param($msg) Write-Host "[$(Get-Date -Format 'HH:mm:ss')] [OK] $msg" -ForegroundColor Green }
function LogWarn { param($msg) Write-Host "[$(Get-Date -Format 'HH:mm:ss')] [WARN] $msg" -ForegroundColor Yellow }
function LogError { param($msg) Write-Host "[$(Get-Date -Format 'HH:mm:ss')] [ERROR] $msg" -ForegroundColor Red }

function Run-Command {
    param($cmd, $desc, $timeoutSec = 30)
    try {
        $out = Invoke-Expression $cmd 2>&1 | Out-String
        if ($LASTEXITCODE -ne 0) { throw "Exit code $LASTEXITCODE" }
        return $out
    } catch {
        LogWarn "$desc failed: $_"
        return $null
    }
}

function Wait-ForPort {
    param($port, $timeoutSec = 120, $desc = "port $port")
    Log "Dang doi $desc..."
    $sw = [System.Diagnostics.Stopwatch]::StartNew()
    while ($sw.Elapsed.TotalSeconds -lt $timeoutSec) {
        try {
            $conn = New-Object System.Net.Sockets.TcpClient
            $conn.Connect("localhost", $port)
            $conn.Close()
            $sw.Stop()
            LogOk "$desc san sang"
            return $true
        } catch { Start-Sleep -Seconds 2 }
    }
    $sw.Stop()
    LogError "$desc khong san sang sau $timeoutSec s"
    return $false
}

function Stop-Port {
    param($port)
    $proc = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue |
             Select-Object -ExpandProperty OwningProcess -First 1
    if ($proc) {
        Stop-Process -Id $proc -Force -ErrorAction SilentlyContinue
        Log "Da dung process tren port $port"
        Start-Sleep -Seconds 1
    }
}

function Reset-Database {
    Log "Dang reset database..."
    $env:PGPASSWORD = "postgres"
    $psql = "C:\Program Files\PostgreSQL\17\bin\psql.exe"
    & $psql -U postgres -h localhost -c "DROP DATABASE IF EXISTS hrm;" 2>$null
    & $psql -U postgres -h localhost -c "CREATE DATABASE hrm;"
    LogOk "Database hrm da tao moi"
}

function Seed-Sql {
    param($msg)
    Log "Dang chay seed SQL: $msg"
    $env:PGPASSWORD = "postgres"
    $psql = "C:\Program Files\PostgreSQL\17\bin\psql.exe"
    $out = & $psql -U postgres -h localhost -d hrm -f $SeedSqlFile 2>&1 | Out-String
    if ($LASTEXITCODE -eq 0) {
        LogOk "Seed SQL hoan thanh"
    } else {
        LogWarn "Seed SQL co loi (exit $LASTEXITCODE). Kiem tra lai file seed."
        $out -split "`n" | Select-String "NOTICE|ERROR" | Select-Object -First 5 |
            ForEach-Object { Write-Host "  $_" }
    }
}

function Start-Backend {
    param($resetDb, $doSeed)
    Log "Dang khoi dong Backend (port $BackendPort)..."

    # Dung process cu
    Stop-Port $BackendPort

    if ($resetDb) { Reset-Database }

    # Chay Maven Spring Boot
    $mvnLog = Join-Path $LogDir "hrm-backend-run.log"
    $job = Start-Job -ScriptBlock {
        param($dir, $log, $port)
        Set-Location $dir
        $env:JAVA_HOME = $env:JAVA_HOME
        mvn spring-boot:run -D"maven.compiler.source=17" -D"maven.compiler.target=17" -D"maven.compiler.release=17" 2>&1 | Out-File $log -Encoding UTF8
    } -ArgumentList $BackendDir, $mvnLog, $BackendPort

    # Cho Maven download deps (lan dau)
    Start-Sleep -Seconds 5
    $depsDone = $false
    for ($i = 0; $i -lt 30; $i++) {
        if ((Test-Path $mvnLog) -and ((Get-Content $mvnLog -Raw) -match "BUILD SUCCESS|Downloaded|Downloading")) {
            $depsDone = $true; break
        }
        Start-Sleep -Seconds 3
    }

    # Doi backend ready
    if (Wait-ForPort $BackendPort 120 "Backend (port $BackendPort)") {
        LogOk "Backend da khoi dong tai http://localhost:$BackendPort"

        if ($doSeed) {
            # Seed users
            Seed-Users
            Seed-Sql "du lieu nghiep vu"
        }
    } else {
        LogError "Backend khong khoi dong duoc. Xem log: $mvnLog"
        Log "Loi cuoi cung:"
        Get-Content $mvnLog -Tail 15 -ErrorAction SilentlyContinue | ForEach-Object { Write-Host $_ }
    }
}

function Seed-Users {
    Log "Dang seed users (password = '123456')..."

    # Tao fixed hash (salt = "DevSeedSalt2026!")
    $salt = [System.Text.Encoding]::UTF8.GetBytes("DevSeedSalt2026!")
    $sha = [System.Security.Cryptography.SHA256]::Create()
    $sha.TransformUpdate($salt)
    $sha.TransformFinalBlock([byte[]][char[]]"123456", 0, 6)
    $hash = [Convert]::ToBase64String($sha.Hash)
    $saltB64 = [Convert]::ToBase64String($salt)
    $passwordHash = "$saltB64`:$hash"
    $sha.Dispose()

    $users = @('a.nguyen', 'b.tran', 'c.le', 'd.pham', 'e.hoang')
    $env:PGPASSWORD = "postgres"
    $psql = "C:\Program Files\PostgreSQL\17\bin\psql.exe"

    foreach ($u in $users) {
        & $psql -U postgres -h localhost -d hrm -c "
            UPDATE system.user_account
            SET password_hash = '$passwordHash'
            WHERE username = '$u' AND password_hash NOT LIKE '%:%'
        " 2>$null | Out-Null
    }
    LogOk "Users da duoc seed (password = 123456)"
}

function Start-Frontend {
    Log "Dang khoi dong Frontend (port $FrontendPort)..."
    Stop-Port $FrontendPort
    Start-Sleep -Seconds 1

    $feLog = Join-Path $LogDir "hrm-frontend-run.log"
    $feJob = Start-Job -ScriptBlock {
        param($dir, $port, $log)
        Set-Location $dir
        npm run dev -- --port $port --host 2>&1 | Out-File $log -Encoding UTF8
    } -ArgumentList $FrontendDir, $FrontendPort, $feLog

    if (Wait-ForPort $FrontendPort 60 "Frontend (port $FrontendPort)") {
        LogOk "Frontend da khoi dong tai http://localhost:$FrontendPort"
    } else {
        LogWarn "Frontend khoi dong cham. Xem log: $feLog"
    }
}

# ============================================================================
# MAIN
# ============================================================================

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "  HRM_Epath - Khoi Dong He Thong" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

$doReset = $FirstRun -or (-not (Test-Path $SeedSqlFile))
$doSeed = -not $SkipSeed

if ($SkipSeed) {
    Log "Che do: Chi khoi dong (khong seed)"
} elseif ($FirstRun) {
    Log "Che do: First Run (reset DB + seed day du)"
} else {
    Log "Che do: Khoi dong binh thuong (backend da chay)"
}

Write-Host ""

if (-not $SkipSeed) {
    Start-Backend -resetDb $FirstRun -doSeed $true
}

Start-Frontend

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "  HE THONG DA SAN SANG" -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Backend:  http://localhost:$BackendPort" -ForegroundColor White
Write-Host "  Frontend: http://localhost:$FrontendPort" -ForegroundColor White
Write-Host ""
Write-Host "  Tai khoan test (password = 123456):" -ForegroundColor Yellow
Write-Host "    a.nguyen  => HR_MANAGER + HR" -ForegroundColor Gray
Write-Host "    b.tran    => EMPLOYEE" -ForegroundColor Gray
Write-Host "    c.le      => MANAGER" -ForegroundColor Gray
Write-Host "    d.pham    => ACCOUNTANT + PAYROLL_ACCOUNTANT" -ForegroundColor Gray
Write-Host "    e.hoang   => COMPANY_ADMIN + HR_MANAGER" -ForegroundColor Gray
Write-Host ""
Write-Host "  Lenh reset + seed (First Run):" -ForegroundColor Yellow
Write-Host "    .\start-hrm.ps1 -FirstRun" -ForegroundColor Gray
Write-Host "  Lenh chi khoi dong (khong reset):" -ForegroundColor Yellow
Write-Host "    .\start-hrm.ps1" -ForegroundColor Gray
Write-Host ""
