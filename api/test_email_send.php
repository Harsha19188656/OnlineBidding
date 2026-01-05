<?php
/**
 * Test Email Sending with PHPMailer
 * Access: http://localhost/onlinebidding/api/test_email_send.php
 */

// Load PHPMailer
require_once __DIR__ . '/PHPMailer/src/Exception.php';
require_once __DIR__ . '/PHPMailer/src/PHPMailer.php';
require_once __DIR__ . '/PHPMailer/src/SMTP.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

// Load email configuration
$emailConfig = require __DIR__ . '/config_email.php';

// Test email address (change this to your email)
$testEmail = 'harsha168656@gmail.com'; // Change this to test email

try {
    // Create PHPMailer instance
    $mail = new PHPMailer(true);
    
    // Server settings
    $mail->isSMTP();
    $mail->Host = $emailConfig['smtp_host'];
    $mail->SMTPAuth = $emailConfig['smtp_auth'];
    $mail->Username = $emailConfig['smtp_username'];
    $mail->Password = $emailConfig['smtp_password'];
    $mail->SMTPSecure = $emailConfig['smtp_secure'];
    $mail->Port = $emailConfig['smtp_port'];
    $mail->CharSet = 'UTF-8';
    
    // Enable verbose debug output (set to 0 in production)
    $mail->SMTPDebug = 2; // 0 = off, 1 = client, 2 = client and server
    $mail->Debugoutput = function($str, $level) {
        echo "<pre>$str</pre>";
    };
    
    // Recipients
    $mail->setFrom($emailConfig['from_email'], $emailConfig['from_name']);
    $mail->addAddress($testEmail, 'Test User');
    $mail->addReplyTo($emailConfig['reply_to_email'], $emailConfig['reply_to_name']);
    
    // Content
    $mail->isHTML(true);
    $mail->Subject = 'Test Email - Online Bidding';
    
    // HTML email body
    $htmlBody = '
    <!DOCTYPE html>
    <html>
    <head>
        <style>
            body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
            .header { background-color: #FF9800; color: white; padding: 20px; text-align: center; }
            .content { background-color: #f9f9f9; padding: 30px; }
            .success { background-color: #4CAF50; color: white; padding: 15px; text-align: center; margin: 20px 0; }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1>✅ Email Test Successful!</h1>
            </div>
            <div class="content">
                <p>Hello!</p>
                <p>If you are reading this email, it means PHPMailer is working correctly!</p>
                <div class="success">
                    <strong>Email Configuration: SUCCESS</strong>
                </div>
                <p>Your forgot password OTP emails will now be sent successfully.</p>
                <p>Best regards,<br>Online Bidding Team</p>
            </div>
        </div>
    </body>
    </html>';
    
    // Plain text version
    $textBody = "Hello!\n\n";
    $textBody .= "If you are reading this email, it means PHPMailer is working correctly!\n\n";
    $textBody .= "Your forgot password OTP emails will now be sent successfully.\n\n";
    $textBody .= "Best regards,\nOnline Bidding Team";
    
    $mail->Body = $htmlBody;
    $mail->AltBody = $textBody;
    
    // Send email
    echo "<h2>Testing Email Configuration...</h2>";
    echo "<p><strong>From:</strong> {$emailConfig['from_email']}</p>";
    echo "<p><strong>To:</strong> $testEmail</p>";
    echo "<p><strong>SMTP Host:</strong> {$emailConfig['smtp_host']}</p>";
    echo "<p><strong>SMTP Port:</strong> {$emailConfig['smtp_port']}</p>";
    echo "<hr>";
    
    $mail->send();
    
    echo "<h2 style='color: green;'>✅ Email sent successfully!</h2>";
    echo "<p>Check your inbox at: <strong>$testEmail</strong></p>";
    echo "<p>Also check spam/junk folder if not in inbox.</p>";
    
} catch (Exception $e) {
    echo "<h2 style='color: red;'>❌ Email sending failed!</h2>";
    echo "<p><strong>Error:</strong> {$mail->ErrorInfo}</p>";
    echo "<p><strong>Exception:</strong> {$e->getMessage()}</p>";
    echo "<hr>";
    echo "<h3>Troubleshooting:</h3>";
    echo "<ul>";
    echo "<li>Verify Gmail App Password is correct (no spaces)</li>";
    echo "<li>Check 2-Step Verification is enabled</li>";
    echo "<li>Verify SMTP settings in config_email.php</li>";
    echo "<li>Check firewall/antivirus blocking port 587</li>";
    echo "</ul>";
}
?>

