<?php
/**
 * Check and Fix OTP Column Size
 * This will automatically fix the column if needed
 * Access: http://localhost/onlinebidding/api/check_and_fix_column.php
 */

header('Content-Type: text/html; charset=utf-8');

$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    echo "<h1>🔧 Check and Fix OTP Column</h1>";
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
    
    if (!$currentColumn) {
        echo "<p style='color:red;'>❌ Table 'password_reset_tokens' or column 'otp' not found!</p>";
        exit;
    }
    
    echo "<h2>Current Column Info:</h2>";
    echo "<p><strong>Column:</strong> {$currentColumn['COLUMN_NAME']}</p>";
    echo "<p><strong>Type:</strong> {$currentColumn['DATA_TYPE']}</p>";
    echo "<p><strong>Max Length:</strong> {$currentColumn['CHARACTER_MAXIMUM_LENGTH']}</p>";
    echo "<p><strong>Full Type:</strong> {$currentColumn['COLUMN_TYPE']}</p>";
    echo "<hr>";
    
    $currentLength = $currentColumn['CHARACTER_MAXIMUM_LENGTH'];
    
    if ($currentLength < 64) {
        echo "<h2 style='color:orange;'>⚠️ Column is too small! (Current: $currentLength, Needed: 64+)</h2>";
        echo "<p>Fixing column size...</p>";
        
        try {
            // Alter the column to VARCHAR(255)
            $pdo->exec("ALTER TABLE password_reset_tokens MODIFY COLUMN otp VARCHAR(255) NOT NULL");
            
            echo "<p style='color:green; font-size:18px;'><strong>✅ Column fixed successfully!</strong></p>";
            
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
            
        } catch (PDOException $e) {
            echo "<p style='color:red;'>❌ Error fixing column: {$e->getMessage()}</p>";
            echo "<p><strong>Try running this SQL manually in phpMyAdmin:</strong></p>";
            echo "<pre style='background:#f0f0f0;padding:10px;'>ALTER TABLE password_reset_tokens MODIFY COLUMN otp VARCHAR(255) NOT NULL;</pre>";
        }
    } else {
        echo "<p style='color:green; font-size:18px;'><strong>✅ Column size is already correct! (Max length: $currentLength)</strong></p>";
    }
    
    echo "<hr>";
    
    // Show recent tokens
    echo "<h2>Recent Tokens for harsha168656@gmail.com:</h2>";
    $tokenStmt = $pdo->prepare("
        SELECT 
            id,
            email,
            LEFT(otp, 30) as otp_preview,
            LENGTH(otp) as otp_length,
            used,
            expires_at,
            created_at,
            CASE 
                WHEN expires_at <= NOW() THEN 'EXPIRED'
                ELSE 'VALID'
            END as status
        FROM password_reset_tokens
        WHERE email = ?
        ORDER BY created_at DESC
        LIMIT 5
    ");
    $tokenStmt->execute(['harsha168656@gmail.com']);
    $tokens = $tokenStmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (count($tokens) > 0) {
        echo "<table border='1' cellpadding='8' style='border-collapse:collapse;width:100%;'>";
        echo "<tr style='background:#f0f0f0;'>";
        echo "<th>ID</th><th>OTP/Token Preview</th><th>Length</th><th>Used</th><th>Status</th><th>Expires</th><th>Created</th>";
        echo "</tr>";
        foreach ($tokens as $token) {
            $rowColor = $token['status'] == 'VALID' && $token['used'] == 0 ? '#ccffcc' : '#ffcccc';
            $tokenDisplay = $token['otp_preview'] . (strlen($token['otp_preview']) >= 30 ? '...' : '');
            echo "<tr style='background:$rowColor;'>";
            echo "<td>{$token['id']}</td>";
            echo "<td><code style='font-size:11px;'>$tokenDisplay</code></td>";
            echo "<td>{$token['otp_length']}</td>";
            echo "<td>" . ($token['used'] ? 'YES' : 'NO') . "</td>";
            echo "<td><strong>{$token['status']}</strong></td>";
            echo "<td>{$token['expires_at']}</td>";
            echo "<td>{$token['created_at']}</td>";
            echo "</tr>";
        }
        echo "</table>";
        
        // Check if any token is a reset token (64 chars)
        $resetTokens = array_filter($tokens, function($t) {
            return $t['otp_length'] > 20; // Reset tokens are 64 chars
        });
        
        if (count($resetTokens) > 0) {
            echo "<p style='color:green;'>✅ Found reset tokens (length > 20 chars)</p>";
        } else {
            echo "<p style='color:orange;'>⚠️ No reset tokens found. All tokens are 6-digit OTPs. Verify OTP again to generate reset token.</p>";
        }
    } else {
        echo "<p>No tokens found for this email.</p>";
    }
    
    echo "<hr>";
    echo "<h2>📋 Next Steps:</h2>";
    echo "<ol>";
    echo "<li>If column was fixed, <strong>verify OTP again</strong> from the app</li>";
    echo "<li>Reset token will be stored correctly (64 chars)</li>";
    echo "<li>Try resetting password</li>";
    echo "</ol>";
    
} catch (PDOException $e) {
    echo "<h2 style='color:red;'>❌ Database Error</h2>";
    echo "<p>{$e->getMessage()}</p>";
}
?>

