@echo off
echo ========================================
echo Copying PHP files to XAMPP backend...
echo ========================================
echo.

set XAMPP_PATH=C:\xampp\htdocs\onlinebidding\api
set XAMPP_AUCTIONS_PATH=%XAMPP_PATH%\auctions
set XAMPP_BIDS_PATH=%XAMPP_PATH%\bids
set XAMPP_ADMIN_PATH=%XAMPP_PATH%\admin\products

if not exist "%XAMPP_PATH%" (
    echo Creating directory: %XAMPP_PATH%
    mkdir "%XAMPP_PATH%"
)

if not exist "%XAMPP_AUCTIONS_PATH%" (
    echo Creating directory: %XAMPP_AUCTIONS_PATH%
    mkdir "%XAMPP_AUCTIONS_PATH%"
)

if not exist "%XAMPP_BIDS_PATH%" (
    echo Creating directory: %XAMPP_BIDS_PATH%
    mkdir "%XAMPP_BIDS_PATH%"
)

if not exist "%XAMPP_ADMIN_PATH%" (
    echo Creating directory: %XAMPP_ADMIN_PATH%
    mkdir "%XAMPP_ADMIN_PATH%"
)

echo Copying forgot-password.php...
copy /Y "forgot-password.php" "%XAMPP_PATH%\forgot-password.php"
if %errorlevel% equ 0 (
    echo [OK] forgot-password.php copied successfully
) else (
    echo [ERROR] Failed to copy forgot-password.php
)

echo.
echo Copying verify-otp.php...
copy /Y "verify-otp.php" "%XAMPP_PATH%\verify-otp.php"
if %errorlevel% equ 0 (
    echo [OK] verify-otp.php copied successfully
) else (
    echo [ERROR] Failed to copy verify-otp.php
)

echo.
echo Copying reset-password.php...
copy /Y "reset-password.php" "%XAMPP_PATH%\reset-password.php"
if %errorlevel% equ 0 (
    echo [OK] reset-password.php copied successfully
) else (
    echo [ERROR] Failed to copy reset-password.php
)

echo.
echo Copying auctions/list.php...
copy /Y "auctions\list.php" "%XAMPP_AUCTIONS_PATH%\list.php"
if %errorlevel% equ 0 (
    echo [OK] auctions/list.php copied successfully
) else (
    echo [ERROR] Failed to copy auctions/list.php
)

echo.
echo Copying auctions/details.php...
copy /Y "auctions\details.php" "%XAMPP_AUCTIONS_PATH%\details.php"
if %errorlevel% equ 0 (
    echo [OK] auctions/details.php copied successfully
) else (
    echo [ERROR] Failed to copy auctions/details.php
)

echo.
echo Copying bids/place.php...
copy /Y "bids\place.php" "%XAMPP_BIDS_PATH%\place.php"
if %errorlevel% equ 0 (
    echo [OK] bids/place.php copied successfully
) else (
    echo [ERROR] Failed to copy bids/place.php
)

echo.
echo Copying admin/products/list.php...
copy /Y "admin\products\list.php" "%XAMPP_ADMIN_PATH%\list.php"
if %errorlevel% equ 0 (
    echo [OK] admin/products/list.php copied successfully
) else (
    echo [ERROR] Failed to copy admin/products/list.php
)

echo.
echo Copying admin/products/add.php...
copy /Y "admin\products\add.php" "%XAMPP_ADMIN_PATH%\add.php"
if %errorlevel% equ 0 (
    echo [OK] admin/products/add.php copied successfully
) else (
    echo [ERROR] Failed to copy admin/products/add.php
)

echo.
echo Copying admin/products/update.php...
copy /Y "admin\products\update.php" "%XAMPP_ADMIN_PATH%\update.php"
if %errorlevel% equ 0 (
    echo [OK] admin/products/update.php copied successfully
) else (
    echo [ERROR] Failed to copy admin/products/update.php
)

echo.
echo Copying admin/products/delete.php...
copy /Y "admin\products\delete.php" "%XAMPP_ADMIN_PATH%\delete.php"
if %errorlevel% equ 0 (
    echo [OK] admin/products/delete.php copied successfully
) else (
    echo [ERROR] Failed to copy admin/products/delete.php
)

echo.
echo ========================================
echo Copy complete!
echo ========================================
echo.
echo Files should now be at:
echo %XAMPP_PATH%\forgot-password.php
echo %XAMPP_PATH%\verify-otp.php
echo %XAMPP_PATH%\reset-password.php
echo %XAMPP_AUCTIONS_PATH%\list.php
echo %XAMPP_AUCTIONS_PATH%\details.php
echo %XAMPP_BIDS_PATH%\place.php
echo %XAMPP_ADMIN_PATH%\list.php
echo %XAMPP_ADMIN_PATH%\add.php
echo %XAMPP_ADMIN_PATH%\update.php
echo %XAMPP_ADMIN_PATH%\delete.php
echo.
echo Make sure XAMPP Apache and MySQL are running!
echo.
pause

