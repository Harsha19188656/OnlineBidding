<?php
/**
 * Direct OTP Test - Simulates forgot-password.php exactly
 * Access: http://localhost/onlinebidding/api/test_otp_direct.php?email=harsha168656@gmail.com
 */

error_reporting(E_ALL);
ini_set('display_errors', 1);
header('Content-Type: text/html; charset=utf-8');

$testEmail = isset($_GET['email']) ? $_GET['email'] : 'harsha168656@gmail.com';

echo "<h1>🧪 Direct OTP Email Test</h1>";
echo "<p>Testing email to: <strong>$testEmail</strong></p>";
echo "<hr>";

// Load PHPMailer
$phpmailerPath = __DIR__ . '/PHPMailer/src/PHPMailer.php';
if (!file_exists($phpmailerPath)) {
    die("❌ PHPMailer not found at: $phpmailerPath");
}

require_once __DIR__ . '/PHPMailer/src/Exception.php';
require_once __DIR__ . '/PHPMailer/src/PHPMailer.php';
require_once __DIR__ . '/PHPMailer/src/SMTP.php';

// Load config
$emailConfigPath = __DIR__ . '/config_email.php';
if (!file_exists($emailConfigPath)) {
    die("❌ Config not found at: $emailConfigPath");
}
$emailConfig = require $emailConfigPath;

// Database
$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Check user
    $stmt = $pdo->prepare("SELECT id, name, email FROM users WHERE email = ?");
    $stmt->execute([$testEmail]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$user) {
        die("❌ User not found: $testEmail");
    }
    
    echo "<p>✅ User found: {$user['name']} (ID: {$user['id']})</p>";
    
    // Generate OTP
    $otp = str_pad(rand(0, 999999), 6, '0', STR_PAD_LEFT);
    $expires_at = date('Y-m-d H:i:s', strtotime('+10 minutes'));
    
    echo "<p>✅ OTP Generated: <strong style='font-size:24px;color:green;'>$otp</strong></p>";
    echo "<p>✅ Expires: $expires_at</p>";
    
    // Send email
    echo "<hr><h2>Sending Email...</h2>";
    
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
        echo "<pre style='background:#f0f0f0;padding:5px;margin:2px;'>$str</pre>";
    };
    
    $mail->setFrom($emailConfig['from_email'], $emailConfig['from_name']);
    $mail->addAddress($testEmail, $user['name']);
    
    $mail->isHTML(true);
    $mail->Subject = 'Password Reset OTP - Online Bidding';
    
    $htmlBody = '
    <!DOCTYPE html>
    <html>
    <head>
        <style>
            body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
            .header { background-color: #FF9800; color: white; padding: 20px; text-align: center; }
            .content { background-color: #f9f9f9; padding: 30px; }
            .otp-box { background-color: #fff; border: 2px solid #FF9800; padding: 20px; text-align: center; margin: 20px 0; }
            .otp-code { font-size: 32px; font-weight: bold; color: #FF9800; letter-spacing: 5px; }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1>Password Reset Request</h1>
            </div>
            <div class="content">
                <p>Hello <strong>' . htmlspecialchars($user['name']) . '</strong>,</p>
                <p>You requested a password reset for your Online Bidding account.</p>
                <div class="otp-box">
                    <p>Your OTP Code is:</p>
                    <div class="otp-code">' . $otp . '</div>
                </div>
                <p>This OTP will expire in 10 minutes.</p>
            </div>
        </div>
    </body>
    </html>';
    
    $mail->Body = $htmlBody;
    $mail->AltBody = "Your OTP is: $otp\n\nThis OTP will expire in 10 minutes.";
    
    echo "<hr>";
    $mail->send();
    
    echo "<h2 style='color:green;'>✅ Email sent successfully!</h2>";
    echo "<p>Check inbox: <strong>$testEmail</strong></p>";
    echo "<p>OTP: <strong style='font-size:20px;color:green;'>$otp</strong></p>";
    echo "<p>Also check spam/junk folder.</p>";
    
} catch (\PHPMailer\PHPMailer\Exception $e) {
    echo "<h2 style='color:red;'>❌ Email sending failed!</h2>";
    echo "<p><strong>Error:</strong> {$mail->ErrorInfo}</p>";
    echo "<p><strong>Exception:</strong> {$e->getMessage()}</p>";
} catch (Exception $e) {
    echo "<h2 style='color:red;'>❌ Error!</h2>";
    echo "<p>{$e->getMessage()}</p>";
}
?>

