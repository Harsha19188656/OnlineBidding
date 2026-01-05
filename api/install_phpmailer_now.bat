@echo off
echo Installing PHPMailer...
cd /d "C:\xampp\htdocs\onlinebidding\api"
powershell -Command "Invoke-WebRequest -Uri 'https://github.com/PHPMailer/PHPMailer/archive/refs/tags/v6.9.0.zip' -OutFile 'PHPMailer.zip'"
powershell -Command "Expand-Archive -Path 'PHPMailer.zip' -DestinationPath '.' -Force"
if exist "PHPMailer-6.9.0" move "PHPMailer-6.9.0" "PHPMailer"
del PHPMailer.zip
echo Done! Check: C:\xampp\htdocs\onlinebidding\api\PHPMailer\src\PHPMailer.php
pause


