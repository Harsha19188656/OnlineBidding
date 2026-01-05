<?php
/**
 * Test Reset Token
 * Access: http://localhost/onlinebidding/api/test_reset_token.php?email=harsha168656@gmail.com
 */

header('Content-Type: text/html; charset=utf-8');

$testEmail = isset($_GET['email']) ? $_GET['email'] : 'harsha168656@gmail.com';

$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    echo "<h1>🔍 Check Reset Tokens</h1>";
    echo "<p><strong>Email:</strong> $testEmail</p>";
    echo "<hr>";
    
    // Get all tokens for this email
    $stmt = $pdo->prepare("
        SELECT prt.*, u.id as user_id, u.name, u.email,
               TIMESTAMPDIFF(SECOND, NOW(), prt.expires_at) as seconds_remaining,
               CASE 
                   WHEN prt.used = 1 THEN 'USED'
                   WHEN prt.expires_at <= NOW() THEN 'EXPIRED'
                   ELSE 'VALID'
               END as status
        FROM password_reset_tokens prt
        INNER JOIN users u ON prt.user_id = u.id
        WHERE prt.email = ?
        ORDER BY prt.created_at DESC
        LIMIT 10
    ");
    $stmt->execute([$testEmail]);
    $tokens = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (count($tokens) > 0) {
        echo "<h2>Recent Tokens/OTPs:</h2>";
        echo "<table border='1' cellpadding='8' style='border-collapse:collapse;width:100%;'>";
        echo "<tr style='background:#f0f0f0;'>";
        echo "<th>ID</th><th>OTP/Token</th><th>Status</th><th>Used</th><th>Expires At</th><th>Time Remaining</th><th>Created At</th>";
        echo "</tr>";
        foreach ($tokens as $token) {
            $rowColor = $token['status'] == 'VALID' ? '#ccffcc' : ($token['status'] == 'USED' ? '#ffffcc' : '#ffcccc');
            $timeRemaining = $token['seconds_remaining'] > 0 ? round($token['seconds_remaining'] / 60, 1) . " min" : "Expired";
            $tokenDisplay = strlen($token['otp']) > 20 ? substr($token['otp'], 0, 20) . "..." : $token['otp'];
            echo "<tr style='background:$rowColor;'>";
            echo "<td>{$token['id']}</td>";
            echo "<td><strong style='font-size:12px;'>$tokenDisplay</strong></td>";
            echo "<td><strong>{$token['status']}</strong></td>";
            echo "<td>" . ($token['used'] ? 'YES' : 'NO') . "</td>";
            echo "<td>{$token['expires_at']}</td>";
            echo "<td>$timeRemaining</td>";
            echo "<td>{$token['created_at']}</td>";
            echo "</tr>";
        }
        echo "</table>";
        
        // Show latest valid reset token
        $validTokens = array_filter($tokens, function($t) {
            return $t['status'] == 'VALID' && $t['used'] == 0 && strlen($t['otp']) > 20;
        });
        
        if (count($validTokens) > 0) {
            $latestToken = reset($validTokens);
            echo "<hr><h2 style='color:green;'>✅ Latest Valid Reset Token:</h2>";
            echo "<p><strong>Token:</strong> <code style='font-size:14px;'>{$latestToken['otp']}</code></p>";
            echo "<p><strong>Expires:</strong> {$latestToken['expires_at']}</p>";
            echo "<p><strong>Time Remaining:</strong> " . round($latestToken['seconds_remaining'] / 60, 1) . " minutes</p>";
        } else {
            echo "<hr><h2 style='color:orange;'>⚠️ No Valid Reset Token Found</h2>";
            echo "<p>All tokens are either used or expired. Verify OTP again to generate a new reset token.</p>";
        }
    } else {
        echo "<p style='color:red;'>❌ No tokens found for this email.</p>";
    }
    
} catch (PDOException $e) {
    echo "<h2 style='color:red;'>❌ Database Error</h2>";
    echo "<p>{$e->getMessage()}</p>";
}
?>

