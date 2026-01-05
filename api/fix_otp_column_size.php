<?php
/**
 * Fix OTP Column Size
 * This script alters the password_reset_tokens table to allow storing reset tokens (64 chars)
 * Access: http://localhost/onlinebidding/api/fix_otp_column_size.php
 */

header('Content-Type: text/html; charset=utf-8');

$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    echo "<h1>🔧 Fix OTP Column Size</h1>";
    echo "<hr>";
    
    // Check current column size
    $checkStmt = $pdo->query("
        SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, COLUMN_TYPE
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = 'onlinebidding'
        AND TABLE_NAME = 'password_reset_tokens'
        AND COLUMN_NAME = 'otp'
    ");
    $currentColumn = $checkStmt->fetch(PDO::FETCH_ASSOC);
    
    if ($currentColumn) {
        echo "<h2>Current Column Info:</h2>";
        echo "<p><strong>Column:</strong> {$currentColumn['COLUMN_NAME']}</p>";
        echo "<p><strong>Type:</strong> {$currentColumn['DATA_TYPE']}</p>";
        echo "<p><strong>Max Length:</strong> {$currentColumn['CHARACTER_MAXIMUM_LENGTH']}</p>";
        echo "<p><strong>Full Type:</strong> {$currentColumn['COLUMN_TYPE']}</p>";
        echo "<hr>";
        
        $currentLength = $currentColumn['CHARACTER_MAXIMUM_LENGTH'];
        
        if ($currentLength < 64) {
            echo "<h2 style='color:orange;'>⚠️ Column is too small for reset tokens (64 chars needed)</h2>";
            echo "<p>Altering column to VARCHAR(255)...</p>";
            
            // Alter the column to VARCHAR(255) to accommodate reset tokens
            $pdo->exec("ALTER TABLE password_reset_tokens MODIFY COLUMN otp VARCHAR(255) NOT NULL");
            
            echo "<p style='color:green;'><strong>✅ Column altered successfully!</strong></p>";
            
            // Verify the change
            $verifyStmt = $pdo->query("
                SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, COLUMN_TYPE
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = 'onlinebidding'
                AND TABLE_NAME = 'password_reset_tokens'
                AND COLUMN_NAME = 'otp'
            ");
            $newColumn = $verifyStmt->fetch(PDO::FETCH_ASSOC);
            
            echo "<h2>New Column Info:</h2>";
            echo "<p><strong>Column:</strong> {$newColumn['COLUMN_NAME']}</p>";
            echo "<p><strong>Type:</strong> {$newColumn['DATA_TYPE']}</p>";
            echo "<p><strong>Max Length:</strong> {$newColumn['CHARACTER_MAXIMUM_LENGTH']}</p>";
            echo "<p><strong>Full Type:</strong> {$newColumn['COLUMN_TYPE']}</p>";
            
        } else {
            echo "<p style='color:green;'><strong>✅ Column size is already sufficient (max length: $currentLength)</strong></p>";
        }
    } else {
        echo "<p style='color:red;'>❌ Column 'otp' not found in password_reset_tokens table.</p>";
    }
    
    echo "<hr>";
    echo "<h2>📋 Next Steps:</h2>";
    echo "<ol>";
    echo "<li>Verify OTP again from the app</li>";
    echo "<li>Reset token should now be stored correctly</li>";
    echo "<li>Try resetting password</li>";
    echo "</ol>";
    
} catch (PDOException $e) {
    echo "<h2 style='color:red;'>❌ Database Error</h2>";
    echo "<p>{$e->getMessage()}</p>";
    echo "<pre>" . $e->getTraceAsString() . "</pre>";
}
?>

