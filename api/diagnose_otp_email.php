<?php
/**
 * OTP Email Diagnostic Script
 * Access: http://localhost/onlinebidding/api/diagnose_otp_email.php
 */

error_reporting(E_ALL);
ini_set('display_errors', 1);

echo "<h1>🔍 OTP Email Diagnostic</h1>";
echo "<hr>";

// 1. Check PHPMailer
echo "<h2>1. PHPMailer Check</h2>";
$phpmailerPath = __DIR__ . '/PHPMailer/src/PHPMailer.php';
$phpmailerExists = file_exists($phpmailerPath);
echo "PHPMailer Path: $phpmailerPath<br>";
echo "PHPMailer Exists: " . ($phpmailerExists ? "✅ YES" : "❌ NO") . "<br>";

if ($phpmailerExists) {
    require_once __DIR__ . '/PHPMailer/src/Exception.php';
    require_once __DIR__ . '/PHPMailer/src/PHPMailer.php';
    require_once __DIR__ . '/PHPMailer/src/SMTP.php';
    echo "PHPMailer Loaded: ✅ YES<br>";
} else {
    echo "PHPMailer Loaded: ❌ NO<br>";
}
echo "<hr>";

// 2. Check Email Config
echo "<h2>2. Email Configuration</h2>";
$emailConfigPath = __DIR__ . '/config_email.php';
$configExists = file_exists($emailConfigPath);
echo "Config Path: $emailConfigPath<br>";
echo "Config Exists: " . ($configExists ? "✅ YES" : "❌ NO") . "<br>";

if ($configExists) {
    $emailConfig = require $emailConfigPath;
    echo "SMTP Host: " . $emailConfig['smtp_host'] . "<br>";
    echo "SMTP Port: " . $emailConfig['smtp_port'] . "<br>";
    echo "SMTP Secure: " . $emailConfig['smtp_secure'] . "<br>";
    echo "SMTP Username: " . $emailConfig['smtp_username'] . "<br>";
    echo "SMTP Password: " . (strlen($emailConfig['smtp_password']) > 0 ? "✅ SET (" . strlen($emailConfig['smtp_password']) . " chars)" : "❌ NOT SET") . "<br>";
    echo "From Email: " . $emailConfig['from_email'] . "<br>";
} else {
    echo "❌ Config file not found!<br>";
}
echo "<hr>";

// 3. Check Database
echo "<h2>3. Database Check</h2>";
$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    echo "Database Connection: ✅ SUCCESS<br>";
    
    // Check if table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'password_reset_tokens'");
    $tableExists = $stmt->rowCount() > 0;
    echo "password_reset_tokens table: " . ($tableExists ? "✅ EXISTS" : "❌ NOT FOUND") . "<br>";
    
    if ($tableExists) {
        // Check table structure
        $stmt = $pdo->query("DESCRIBE password_reset_tokens");
        $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
        echo "Table Columns:<br>";
        echo "<ul>";
        foreach ($columns as $col) {
            echo "<li>{$col['Field']} ({$col['Type']})</li>";
        }
        echo "</ul>";
        
        // Check recent OTPs
        $stmt = $pdo->query("SELECT * FROM password_reset_tokens ORDER BY created_at DESC LIMIT 5");
        $recentOtps = $stmt->fetchAll(PDO::FETCH_ASSOC);
        echo "Recent OTPs (last 5):<br>";
        if (count($recentOtps) > 0) {
            echo "<table border='1' cellpadding='5'>";
            echo "<tr><th>ID</th><th>Email</th><th>OTP</th><th>Expires At</th><th>Used</th><th>Created At</th></tr>";
            foreach ($recentOtps as $otp) {
                echo "<tr>";
                echo "<td>{$otp['id']}</td>";
                echo "<td>{$otp['email']}</td>";
                echo "<td>{$otp['otp']}</td>";
                echo "<td>{$otp['expires_at']}</td>";
                echo "<td>" . ($otp['used'] ? 'YES' : 'NO') . "</td>";
                echo "<td>{$otp['created_at']}</td>";
                echo "</tr>";
            }
            echo "</table>";
        } else {
            echo "No OTPs found in database.<br>";
        }
    }
    
    // Check if user exists
    $testEmail = 'harsha168656@gmail.com';
    $stmt = $pdo->prepare("SELECT id, name, email FROM users WHERE email = ?");
    $stmt->execute([$testEmail]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    echo "User ($testEmail): " . ($user ? "✅ EXISTS (ID: {$user['id']}, Name: {$user['name']})" : "❌ NOT FOUND") . "<br>";
    
} catch (PDOException $e) {
    echo "Database Connection: ❌ FAILED<br>";
    echo "Error: " . $e->getMessage() . "<br>";
}
echo "<hr>";

// 4. Test Email Sending
echo "<h2>4. Test Email Sending</h2>";
if ($phpmailerExists && $configExists) {
    try {
        $mail = new \PHPMailer\PHPMailer\PHPMailer(true);
        
        $mail->isSMTP();
        $mail->Host = $emailConfig['smtp_host'];
        $mail->SMTPAuth = $emailConfig['smtp_auth'];
        $mail->Username = $emailConfig['smtp_username'];
        $mail->Password = $emailConfig['smtp_password'];
        $mail->SMTPSecure = $emailConfig['smtp_secure'];
        $mail->Port = $emailConfig['smtp_port'];
        $mail->CharSet = 'UTF-8';
        
        // Enable debug
        $mail->SMTPDebug = 2;
        $mail->Debugoutput = function($str, $level) {
            echo "<pre style='background:#f0f0f0;padding:5px;'>$str</pre>";
        };
        
        $testEmail = 'harsha168656@gmail.com';
        $mail->setFrom($emailConfig['from_email'], $emailConfig['from_name']);
        $mail->addAddress($testEmail, 'Test User');
        
        $mail->isHTML(true);
        $mail->Subject = 'Test OTP Email - Diagnostic';
        $mail->Body = '<h1>Test OTP: 123456</h1><p>If you receive this, email is working!</p>';
        $mail->AltBody = 'Test OTP: 123456 - If you receive this, email is working!';
        
        echo "Attempting to send test email to: $testEmail<br>";
        echo "<hr>";
        
        $mail->send();
        echo "<h3 style='color:green;'>✅ Email sent successfully!</h3>";
        echo "Check inbox: <strong>$testEmail</strong><br>";
        echo "Also check spam/junk folder.<br>";
        
    } catch (\PHPMailer\PHPMailer\Exception $e) {
        echo "<h3 style='color:red;'>❌ Email sending failed!</h3>";
        echo "Error: " . $mail->ErrorInfo . "<br>";
        echo "Exception: " . $e->getMessage() . "<br>";
    }
} else {
    echo "❌ Cannot test email - PHPMailer or config missing.<br>";
}
echo "<hr>";

// 5. Check Error Logs
echo "<h2>5. Recent Error Logs</h2>";
$errorLogPath = 'C:/xampp/apache/logs/error.log';
if (file_exists($errorLogPath)) {
    $lines = file($errorLogPath);
    $recentLines = array_slice($lines, -20);
    echo "Last 20 lines from error.log:<br>";
    echo "<pre style='background:#f0f0f0;padding:10px;max-height:300px;overflow:auto;'>";
    foreach ($recentLines as $line) {
        if (stripos($line, 'email') !== false || 
            stripos($line, 'otp') !== false || 
            stripos($line, 'phpmailer') !== false ||
            stripos($line, 'smtp') !== false ||
            stripos($line, 'forgot') !== false) {
            echo htmlspecialchars($line);
        }
    }
    echo "</pre>";
} else {
    echo "Error log not found at: $errorLogPath<br>";
}
echo "<hr>";

echo "<h2>✅ Diagnostic Complete</h2>";
echo "<p><strong>Next Steps:</strong></p>";
echo "<ul>";
echo "<li>If email test failed, check Gmail App Password</li>";
echo "<li>If OTPs are in database but not sent, check SMTP settings</li>";
echo "<li>Check error logs above for specific errors</li>";
echo "</ul>";
?>

