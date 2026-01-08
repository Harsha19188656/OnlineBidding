@echo off
echo ========================================
echo Creating Database Tables...
echo ========================================
echo.

set MYSQL_PATH=C:\xampp\mysql\bin\mysql.exe
set DB_NAME=onlinebidding
set DB_USER=root
set DB_PASS=

echo Checking MySQL...
if not exist "%MYSQL_PATH%" (
    echo [ERROR] MySQL not found at %MYSQL_PATH%
    echo Please check your XAMPP installation path.
    pause
    exit /b 1
)

echo.
echo Running SQL script to create tables...
echo.

"%MYSQL_PATH%" -u %DB_USER% -e "USE %DB_NAME%; CREATE TABLE IF NOT EXISTS auctions (id INT AUTO_INCREMENT PRIMARY KEY, product_id INT NOT NULL, start_price DECIMAL(10,2) NOT NULL, current_price DECIMAL(10,2) NOT NULL, status VARCHAR(20) DEFAULT 'active', start_at DATETIME DEFAULT CURRENT_TIMESTAMP, end_at DATETIME NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE, INDEX idx_status (status), INDEX idx_product (product_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"

if %errorlevel% neq 0 (
    echo [ERROR] Failed to create auctions table
    echo Make sure MySQL is running in XAMPP!
    pause
    exit /b 1
) else (
    echo [OK] auctions table created successfully
)

echo.

"%MYSQL_PATH%" -u %DB_USER% -e "USE %DB_NAME%; CREATE TABLE IF NOT EXISTS bids (id INT AUTO_INCREMENT PRIMARY KEY, auction_id INT NOT NULL, user_id INT NOT NULL, amount DECIMAL(10,2) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, INDEX idx_auction (auction_id), INDEX idx_user (user_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"

if %errorlevel% neq 0 (
    echo [ERROR] Failed to create bids table
    pause
    exit /b 1
) else (
    echo [OK] bids table created successfully
)

echo.
echo ========================================
echo Done! Tables created successfully!
echo ========================================
echo.
echo You can now test the API:
echo http://localhost/onlinebidding/api/auctions/list.php
echo.
pause




