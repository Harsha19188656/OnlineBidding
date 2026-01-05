<?php
/**
 * Check OTP in Database
 * Access: http://localhost/onlinebidding/api/CHECK_OTP_IN_DB.php
 */

header('Content-Type: text/html; charset=utf-8');

$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    echo "<h1>📊 OTP Database Check</h1>";
    echo "<hr>";
    
    // Check table structure
    $stmt = $pdo->query("DESCRIBE password_reset_tokens");
    $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<h2>Table Structure:</h2>";
    echo "<table border='1' cellpadding='5' style='border-collapse:collapse;'>";
    echo "<tr><th>Field</th><th>Type</th><th>Null</th><th>Key</th><th>Default</th><th>Extra</th></tr>";
    foreach ($columns as $col) {
        echo "<tr>";
        echo "<td>{$col['Field']}</td>";
        echo "<td>{$col['Type']}</td>";
        echo "<td>{$col['Null']}</td>";
        echo "<td>{$col['Key']}</td>";
        echo "<td>{$col['Default']}</td>";
        echo "<td>{$col['Extra']}</td>";
        echo "</tr>";
    }
    echo "</table>";
    echo "<hr>";
    
    // Get all OTPs for harsha168656@gmail.com
    $email = 'harsha168656@gmail.com';
    $stmt = $pdo->prepare("SELECT * FROM password_reset_tokens WHERE email = ? ORDER BY created_at DESC");
    $stmt->execute([$email]);
    $otps = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<h2>OTPs for: $email</h2>";
    if (count($otps) > 0) {
        echo "<table border='1' cellpadding='5' style='border-collapse:collapse;'>";
        echo "<tr><th>ID</th><th>User ID</th><th>Email</th><th>OTP</th><th>Expires At</th><th>Used</th><th>Created At</th></tr>";
        foreach ($otps as $otp) {
            $expired = strtotime($otp['expires_at']) < time();
            $rowColor = $expired ? '#ffcccc' : ($otp['used'] ? '#ffffcc' : '#ccffcc');
            echo "<tr style='background-color:$rowColor;'>";
            echo "<td>{$otp['id']}</td>";
            echo "<td>{$otp['user_id']}</td>";
            echo "<td>{$otp['email']}</td>";
            echo "<td><strong>{$otp['otp']}</strong></td>";
            echo "<td>{$otp['expires_at']} " . ($expired ? "❌ EXPIRED" : "✅ Valid") . "</td>";
            echo "<td>" . ($otp['used'] ? "✅ YES" : "❌ NO") . "</td>";
            echo "<td>{$otp['created_at']}</td>";
            echo "</tr>";
        }
        echo "</table>";
        
        // Show latest valid OTP
        $latestValid = null;
        foreach ($otps as $otp) {
            if (!$otp['used'] && strtotime($otp['expires_at']) > time()) {
                $latestValid = $otp;
                break;
            }
        }
        
        if ($latestValid) {
            echo "<hr>";
            echo "<h2 style='color:green;'>✅ Latest Valid OTP:</h2>";
            echo "<p style='font-size:24px;font-weight:bold;color:green;'>OTP: {$latestValid['otp']}</p>";
            echo "<p>Expires: {$latestValid['expires_at']}</p>";
            echo "<p>Created: {$latestValid['created_at']}</p>";
        } else {
            echo "<hr>";
            echo "<h2 style='color:red;'>❌ No Valid OTP Found</h2>";
            echo "<p>All OTPs are either used or expired. Request a new one.</p>";
        }
    } else {
        echo "<p style='color:red;'>❌ No OTPs found for this email.</p>";
    }
    
    echo "<hr>";
    echo "<h2>All Recent OTPs (Last 10)</h2>";
    $stmt = $pdo->query("SELECT * FROM password_reset_tokens ORDER BY created_at DESC LIMIT 10");
    $allOtps = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (count($allOtps) > 0) {
        echo "<table border='1' cellpadding='5' style='border-collapse:collapse;'>";
        echo "<tr><th>ID</th><th>Email</th><th>OTP</th><th>Expires At</th><th>Used</th><th>Created At</th></tr>";
        foreach ($allOtps as $otp) {
            echo "<tr>";
            echo "<td>{$otp['id']}</td>";
            echo "<td>{$otp['email']}</td>";
            echo "<td><strong>{$otp['otp']}</strong></td>";
            echo "<td>{$otp['expires_at']}</td>";
            echo "<td>" . ($otp['used'] ? "YES" : "NO") . "</td>";
            echo "<td>{$otp['created_at']}</td>";
            echo "</tr>";
        }
        echo "</table>";
    } else {
        echo "<p>No OTPs in database.</p>";
    }
    
} catch (PDOException $e) {
    echo "<h2 style='color:red;'>❌ Database Error</h2>";
    echo "<p>" . $e->getMessage() . "</p>";
}
?>

