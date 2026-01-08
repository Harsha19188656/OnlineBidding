@echo off
echo ========================================
echo Testing Forgot Password Endpoint
echo ========================================
echo.

set TEST_URL=http://10.148.199.81/onlinebidding/api/forgot-password.php
set LOCAL_URL=http://localhost/onlinebidding/api/forgot-password.php

echo Testing remote URL: %TEST_URL%
echo.
curl -X POST "%TEST_URL%" ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\"}" ^
  -v

echo.
echo.
echo ========================================
echo Testing localhost URL: %LOCAL_URL%
echo.
curl -X POST "%LOCAL_URL%" ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\"}" ^
  -v

echo.
echo.
echo ========================================
echo Checking if files exist in XAMPP...
echo ========================================
echo.

if exist "C:\xampp\htdocs\onlinebidding\api\forgot-password.php" (
    echo [OK] forgot-password.php exists
) else (
    echo [ERROR] forgot-password.php NOT FOUND at C:\xampp\htdocs\onlinebidding\api\
    echo Please run copy_to_xampp.bat first!
)

if exist "C:\xampp\htdocs\onlinebidding\api\verify-otp.php" (
    echo [OK] verify-otp.php exists
) else (
    echo [ERROR] verify-otp.php NOT FOUND at C:\xampp\htdocs\onlinebidding\api\
    echo Please run copy_to_xampp.bat first!
)

echo.
echo ========================================
echo Test complete!
echo ========================================
echo.
pause




