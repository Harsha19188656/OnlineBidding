<?php
/**
 * Debug OTP Verification - Shows exactly what's happening
 * Access: http://localhost/onlinebidding/api/debug_otp_verification.php?email=harsha168656@gmail.com&otp=123456
 */

header('Content-Type: text/html; charset=utf-8');
error_reporting(E_ALL);
ini_set('display_errors', 1);

$testEmail = isset($_GET['email']) ? trim($_GET['email']) : 'harsha168656@gmail.com';
$testOtp = isset($_GET['otp']) ? trim($_GET['otp']) : '';

echo "<h1>🔍 Debug OTP Verification</h1>";
echo "<p><strong>Email:</strong> $testEmail</p>";
echo "<p><strong>OTP:</strong> " . ($testOtp ? "'$testOtp'" : "Not provided") . "</p>";
echo "<p><strong>OTP Length:</strong> " . strlen($testOtp) . " characters</p>";
echo "<hr>";

if (!$testOtp) {
    echo "<p style='color:orange;'>⚠️ Add OTP as parameter: ?email=...&otp=123456</p>";
    echo "<p><a href='CHECK_OTP_IN_DB.php' target='_blank'>Check OTPs in Database</a></p>";
    echo "<hr>";
}

// Database
$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    echo "<h2>1. Check All OTPs for This Email</h2>";
    $stmt = $pdo->prepare("
        SELECT id, user_id, email, otp, used, expires_at, created_at,
               TIMESTAMPDIFF(SECOND, NOW(), expires_at) as seconds_remaining,
               CASE 
                   WHEN used = 1 THEN 'USED'
                   WHEN expires_at <= NOW() THEN 'EXPIRED'
                   ELSE 'VALID'
               END as status
        FROM password_reset_tokens
        WHERE email = ?
        ORDER BY created_at DESC
        LIMIT 10
    ");
    $stmt->execute([$testEmail]);
    $allOtps = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (count($allOtps) > 0) {
        echo "<table border='1' cellpadding='8' style='border-collapse:collapse;width:100%;'>";
        echo "<tr style='background:#f0f0f0;'>";
        echo "<th>ID</th><th>OTP</th><th>Status</th><th>Used</th><th>Expires At</th><th>Time Remaining</th><th>Created At</th>";
        echo "</tr>";
        foreach ($allOtps as $otpData) {
            $rowColor = $otpData['status'] == 'VALID' ? '#ccffcc' : ($otpData['status'] == 'USED' ? '#ffffcc' : '#ffcccc');
            $timeRemaining = $otpData['seconds_remaining'] > 0 ? round($otpData['seconds_remaining'] / 60, 1) . " min" : "Expired";
            echo "<tr style='background:$rowColor;'>";
            echo "<td>{$otpData['id']}</td>";
            echo "<td><strong style='font-size:18px;'>{$otpData['otp']}</strong></td>";
            echo "<td><strong>{$otpData['status']}</strong></td>";
            echo "<td>" . ($otpData['used'] ? 'YES' : 'NO') . "</td>";
            echo "<td>{$otpData['expires_at']}</td>";
            echo "<td>$timeRemaining</td>";
            echo "<td>{$otpData['created_at']}</td>";
            echo "</tr>";
        }
        echo "</table>";
    } else {
        echo "<p style='color:red;'>❌ No OTPs found for this email.</p>";
    }
    
    if ($testOtp) {
        echo "<hr><h2>2. Exact OTP Match Test</h2>";
        
        // Test 1: Exact match
        echo "<h3>Test 1: Exact Match (as stored in DB)</h3>";
        $stmt = $pdo->prepare("
            SELECT * FROM password_reset_tokens
            WHERE email = ? AND otp = ?
            ORDER BY created_at DESC
            LIMIT 1
        ");
        $stmt->execute([$testEmail, $testOtp]);
        $exactMatch = $stmt->fetch(PDO::FETCH_ASSOC);
        
        if ($exactMatch) {
            echo "<p style='color:green;'>✅ Exact match found!</p>";
            echo "<pre>";
            print_r($exactMatch);
            echo "</pre>";
        } else {
            echo "<p style='color:red;'>❌ No exact match found.</p>";
        }
        
        // Test 2: Check with used and expired conditions
        echo "<h3>Test 2: Valid OTP (not used, not expired)</h3>";
        $stmt = $pdo->prepare("
            SELECT prt.*, u.id as user_id, u.name, u.email,
                   TIMESTAMPDIFF(SECOND, NOW(), prt.expires_at) as seconds_remaining
            FROM password_reset_tokens prt
            INNER JOIN users u ON prt.user_id = u.id
            WHERE prt.email = ? 
            AND prt.otp = ? 
            AND prt.used = 0 
            AND prt.expires_at > NOW()
            ORDER BY prt.created_at DESC
            LIMIT 1
        ");
        $stmt->execute([$testEmail, $testOtp]);
        $validOtp = $stmt->fetch(PDO::FETCH_ASSOC);
        
        if ($validOtp) {
            echo "<p style='color:green;'>✅ Valid OTP found!</p>";
            echo "<pre>";
            print_r($validOtp);
            echo "</pre>";
        } else {
            echo "<p style='color:red;'>❌ No valid OTP found.</p>";
            
            // Check why it failed
            if ($exactMatch) {
                echo "<h4>Why it failed:</h4>";
                if ($exactMatch['used'] == 1) {
                    echo "<p style='color:orange;'>⚠️ OTP was already used.</p>";
                }
                if (strtotime($exactMatch['expires_at']) <= time()) {
                    $expiredTime = strtotime($exactMatch['expires_at']);
                    $currentTime = time();
                    $minutesExpired = round(($currentTime - $expiredTime) / 60, 1);
                    echo "<p style='color:orange;'>⚠️ OTP expired $minutesExpired minutes ago.</p>";
                }
            } else {
                echo "<p style='color:orange;'>⚠️ OTP not found in database. Check if:</p>";
                echo "<ul>";
                echo "<li>OTP was entered correctly (no spaces, correct digits)</li>";
                echo "<li>Email matches exactly</li>";
                echo "<li>OTP was generated for this email</li>";
                echo "</ul>";
            }
        }
        
        // Test 3: Check OTP format
        echo "<h3>Test 3: OTP Format Check</h3>";
        echo "<p>OTP provided: <strong>'$testOtp'</strong></p>";
        echo "<p>Length: " . strlen($testOtp) . " characters</p>";
        echo "<p>Is numeric: " . (ctype_digit($testOtp) ? "✅ YES" : "❌ NO") . "</p>";
        echo "<p>Is 6 digits: " . (preg_match('/^\d{6}$/', $testOtp) ? "✅ YES" : "❌ NO") . "</p>";
        
        // Test 4: Compare with database OTPs
        echo "<h3>Test 4: Compare with Recent OTPs</h3>";
        if (count($allOtps) > 0) {
            echo "<p>Recent OTPs in database:</p>";
            echo "<ul>";
            foreach ($allOtps as $otpData) {
                $match = ($otpData['otp'] === $testOtp) ? "✅ MATCH" : "❌ Different";
                echo "<li>OTP: <strong>{$otpData['otp']}</strong> - $match</li>";
            }
            echo "</ul>";
        }
    }
    
} catch (PDOException $e) {
    echo "<h2 style='color:red;'>❌ Database Error</h2>";
    echo "<p>{$e->getMessage()}</p>";
}
?>

