@echo off
echo ========================================
echo Verifying Forgot Password Setup
echo ========================================
echo.

echo [1] Checking if files exist in XAMPP...
if exist "C:\xampp\htdocs\onlinebidding\api\forgot-password.php" (
    echo     [OK] forgot-password.php exists
) else (
    echo     [ERROR] forgot-password.php NOT FOUND
    echo     Please copy the file manually!
    goto :end
)

if exist "C:\xampp\htdocs\onlinebidding\api\verify-otp.php" (
    echo     [OK] verify-otp.php exists
) else (
    echo     [ERROR] verify-otp.php NOT FOUND
    echo     Please copy the file manually!
    goto :end
)

echo.
echo [2] Testing endpoint accessibility...
echo     Opening browser to test: http://localhost/onlinebidding/api/forgot-password.php
start http://localhost/onlinebidding/api/forgot-password.php
timeout /t 2 /nobreak >nul

echo.
echo [3] Important Checks:
echo     - Make sure XAMPP Apache is RUNNING (green in XAMPP Control Panel)
echo     - Make sure your IP address is correct: 10.148.199.81
echo     - If using emulator, change BASE_URL to: http://10.0.2.2/onlinebidding/
echo.

:end
echo ========================================
echo Verification complete!
echo ========================================
pause




