<?php
/**
 * Test OTP Verification
 * Access: http://localhost/onlinebidding/api/test_verify_otp.php?email=harsha168656@gmail.com&otp=123456
 */

header('Content-Type: text/html; charset=utf-8');

$testEmail = isset($_GET['email']) ? $_GET['email'] : 'harsha168656@gmail.com';
$testOtp = isset($_GET['otp']) ? $_GET['otp'] : '';

echo "<h1>🧪 Test OTP Verification</h1>";
echo "<p><strong>Email:</strong> $testEmail</p>";
echo "<p><strong>OTP:</strong> " . ($testOtp ? $testOtp : "Not provided") . "</p>";
echo "<hr>";

if (!$testOtp) {
    echo "<p style='color:orange;'>⚠️ Please provide OTP as parameter: ?email=...&otp=123456</p>";
    echo "<p>Or check recent OTPs in database:</p>";
    echo "<p><a href='CHECK_OTP_IN_DB.php' target='_blank'>Check OTPs in Database</a></p>";
    exit;
}

// Database
$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Check OTP
    $stmt = $pdo->prepare("
        SELECT prt.*, u.id as user_id, u.name, u.email,
               CASE 
                   WHEN prt.used = 1 THEN 'USED'
                   WHEN prt.expires_at <= NOW() THEN 'EXPIRED'
                   ELSE 'VALID'
               END as status
        FROM password_reset_tokens prt
        INNER JOIN users u ON prt.user_id = u.id
        WHERE prt.email = ? 
        AND prt.otp = ?
        ORDER BY prt.created_at DESC
        LIMIT 1
    ");
    $stmt->execute([$testEmail, $testOtp]);
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($result) {
        echo "<h2>✅ OTP Found in Database</h2>";
        echo "<table border='1' cellpadding='10' style='border-collapse:collapse;'>";
        echo "<tr><th>Field</th><th>Value</th></tr>";
        echo "<tr><td>ID</td><td>{$result['id']}</td></tr>";
        echo "<tr><td>User ID</td><td>{$result['user_id']}</td></tr>";
        echo "<tr><td>Email</td><td>{$result['email']}</td></tr>";
        echo "<tr><td>OTP</td><td><strong style='font-size:20px;'>{$result['otp']}</strong></td></tr>";
        echo "<tr><td>Status</td><td><strong style='color:" . ($result['status'] == 'VALID' ? 'green' : 'red') . ";'>{$result['status']}</strong></td></tr>";
        echo "<tr><td>Used</td><td>" . ($result['used'] ? 'YES ❌' : 'NO ✅') . "</td></tr>";
        echo "<tr><td>Expires At</td><td>{$result['expires_at']}</td></tr>";
        echo "<tr><td>Created At</td><td>{$result['created_at']}</td></tr>";
        echo "</table>";
        
        if ($result['status'] == 'VALID') {
            echo "<h3 style='color:green;'>✅ OTP is VALID and can be used!</h3>";
        } else {
            echo "<h3 style='color:red;'>❌ OTP is {$result['status']}</h3>";
            if ($result['status'] == 'USED') {
                echo "<p>This OTP has already been used. Request a new one.</p>";
            } elseif ($result['status'] == 'EXPIRED') {
                echo "<p>This OTP has expired. Request a new one.</p>";
            }
        }
    } else {
        echo "<h2 style='color:red;'>❌ OTP Not Found</h2>";
        echo "<p>OTP <strong>$testOtp</strong> not found for email <strong>$testEmail</strong></p>";
        
        // Show recent OTPs
        $stmt = $pdo->prepare("
            SELECT otp, used, expires_at, created_at
            FROM password_reset_tokens
            WHERE email = ?
            ORDER BY created_at DESC
            LIMIT 5
        ");
        $stmt->execute([$testEmail]);
        $recentOtps = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        if (count($recentOtps) > 0) {
            echo "<h3>Recent OTPs for this email:</h3>";
            echo "<table border='1' cellpadding='5' style='border-collapse:collapse;'>";
            echo "<tr><th>OTP</th><th>Used</th><th>Expires At</th><th>Created At</th></tr>";
            foreach ($recentOtps as $otp) {
                echo "<tr>";
                echo "<td><strong>{$otp['otp']}</strong></td>";
                echo "<td>" . ($otp['used'] ? 'YES' : 'NO') . "</td>";
                echo "<td>{$otp['expires_at']}</td>";
                echo "<td>{$otp['created_at']}</td>";
                echo "</tr>";
            }
            echo "</table>";
        }
    }
    
} catch (PDOException $e) {
    echo "<h2 style='color:red;'>❌ Database Error</h2>";
    echo "<p>{$e->getMessage()}</p>";
}
?>

