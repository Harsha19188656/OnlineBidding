@echo off
echo ========================================
echo Installing PHPMailer for Email Sending
echo ========================================
echo.

cd /d "%~dp0"

set "API_PATH=%~dp0"
set "PHPMailer_PATH=%API_PATH%PHPMailer"
set "ZIP_FILE=%API_PATH%PHPMailer.zip"
set "DOWNLOAD_URL=https://github.com/PHPMailer/PHPMailer/archive/refs/tags/v6.9.0.zip"

echo API Path: %API_PATH%
echo.

REM Check if PHPMailer already exists
if exist "%PHPMailer_PATH%\src\PHPMailer.php" (
    echo PHPMailer already exists!
    echo Location: %PHPMailer_PATH%
    echo.
    echo Do you want to reinstall? (Y/N)
    set /p REINSTALL=
    if /i not "%REINSTALL%"=="Y" (
        echo Installation cancelled.
        pause
        exit /b
    )
    echo Removing existing PHPMailer...
    rmdir /s /q "%PHPMailer_PATH%"
)

echo.
echo Step 1: Downloading PHPMailer...
echo URL: %DOWNLOAD_URL%
echo.

REM Download using PowerShell
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%ZIP_FILE%'}"

if not exist "%ZIP_FILE%" (
    echo.
    echo ERROR: Download failed!
    echo.
    echo Please download manually:
    echo 1. Go to: https://github.com/PHPMailer/PHPMailer/releases
    echo 2. Download: PHPMailer-6.9.0.zip
    echo 3. Extract to: %API_PATH%PHPMailer
    echo.
    pause
    exit /b 1
)

echo Download complete!
echo.

echo Step 2: Extracting PHPMailer...
echo.

REM Extract using PowerShell
powershell -Command "Expand-Archive -Path '%ZIP_FILE%' -DestinationPath '%API_PATH%' -Force"

REM Check if extraction created versioned folder
if exist "%API_PATH%PHPMailer-6.9.0" (
    echo Moving files from PHPMailer-6.9.0 to PHPMailer...
    move "%API_PATH%PHPMailer-6.9.0" "%PHPMailer_PATH%"
)

REM Clean up ZIP file
if exist "%ZIP_FILE%" (
    del "%ZIP_FILE%"
)

echo.
echo Step 3: Verifying installation...
echo.

if exist "%PHPMailer_PATH%\src\PHPMailer.php" (
    echo ========================================
    echo SUCCESS! PHPMailer installed!
    echo ========================================
    echo.
    echo Location: %PHPMailer_PATH%
    echo.
    echo Next steps:
    echo 1. Make sure config_email.php is configured
    echo 2. Test email: http://localhost/onlinebidding/api/test_email_send.php
    echo 3. Try forgot password in your app
    echo.
) else (
    echo ========================================
    echo ERROR: Installation failed!
    echo ========================================
    echo.
    echo Please download manually:
    echo 1. Go to: https://github.com/PHPMailer/PHPMailer/releases
    echo 2. Download: PHPMailer-6.9.0.zip
    echo 3. Extract to: %API_PATH%PHPMailer
    echo 4. Make sure folder structure is: PHPMailer\src\PHPMailer.php
    echo.
)

pause
