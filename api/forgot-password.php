<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Check if PHPMailer exists, load it if available
$phpmailerPath = __DIR__ . '/PHPMailer/src/PHPMailer.php';
$phpmailerExists = file_exists($phpmailerPath);

if ($phpmailerExists) {
    require_once __DIR__ . '/PHPMailer/src/Exception.php';
    require_once __DIR__ . '/PHPMailer/src/PHPMailer.php';
    require_once __DIR__ . '/PHPMailer/src/SMTP.php';
}

// Handle preflight request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['success' => false, 'error' => 'Method not allowed']);
    exit();
}

// Database configuration
$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

try {
    // Connect to database
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get JSON input
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
    
    // Validate input
    if (!isset($data['email']) || empty(trim($data['email']))) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Email is required']);
        exit();
    }
    
    $email = trim($data['email']);
    
    // Validate email format
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Invalid email format']);
        exit();
    }
    
    // Check if user exists
    $stmt = $pdo->prepare("SELECT id, name, email FROM users WHERE email = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$user) {
        // Don't reveal if email exists or not for security
        // But still return success to prevent email enumeration
        http_response_code(200);
        echo json_encode([
            'success' => true,
            'message' => 'If the email exists, an OTP has been sent.'
        ]);
        exit();
    }
    
    // Generate 6-digit OTP
    $otp = str_pad(rand(0, 999999), 6, '0', STR_PAD_LEFT);
    
    // Set expiration time (5 minutes from now) - Use database time to avoid timezone issues
    // Calculate expiration using database time (avoid reserved keyword 'current_time')
    $expiresStmt = $pdo->query("SELECT DATE_ADD(NOW(), INTERVAL 5 MINUTE) as expires_at");
    $expiresResult = $expiresStmt->fetch(PDO::FETCH_ASSOC);
    $expires_at = $expiresResult['expires_at'];
    
    // Get current DB time for logging
    $timeStmt = $pdo->query("SELECT NOW() as db_now");
    $timeResult = $timeStmt->fetch(PDO::FETCH_ASSOC);
    $currentDbTime = $timeResult['db_now'];
    
    error_log("🕐 Time Check - Current DB Time: $currentDbTime, Expires At: $expires_at");
    
    // Create or update password_reset_tokens table if it doesn't exist
    $pdo->exec("CREATE TABLE IF NOT EXISTS password_reset_tokens (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        email VARCHAR(255) NOT NULL,
        otp VARCHAR(255) NOT NULL,
        expires_at DATETIME NOT NULL,
        used TINYINT(1) DEFAULT 0,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        INDEX idx_email (email),
        INDEX idx_otp (otp),
        INDEX idx_expires (expires_at),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
    
    // Alter existing table if otp column is too small (for reset tokens)
    try {
        $pdo->exec("ALTER TABLE password_reset_tokens MODIFY COLUMN otp VARCHAR(255) NOT NULL");
        error_log("✅ OTP column size updated to VARCHAR(255)");
    } catch (PDOException $e) {
        // Column might already be correct size, ignore error
        error_log("ℹ️ OTP column check: " . $e->getMessage());
    }
    
    // Delete old unused OTPs for this email
    $stmt = $pdo->prepare("DELETE FROM password_reset_tokens WHERE email = ? AND (used = 1 OR expires_at < NOW())");
    $stmt->execute([$email]);
    
    // Insert new OTP
    $stmt = $pdo->prepare("INSERT INTO password_reset_tokens (user_id, email, otp, expires_at) VALUES (?, ?, ?, ?)");
    $stmt->execute([$user['id'], $email, $otp, $expires_at]);
    
    // Verify OTP was inserted
    $insertedId = $pdo->lastInsertId();
    error_log("✅ OTP inserted - ID: $insertedId, Email: $email, OTP: $otp, Expires: $expires_at");
    
    // Double-check it was stored correctly
    $verifyStmt = $pdo->prepare("SELECT * FROM password_reset_tokens WHERE id = ?");
    $verifyStmt->execute([$insertedId]);
    $verifyResult = $verifyStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$verifyResult) {
        error_log("❌ ERROR: OTP was not stored correctly! Insert ID: $insertedId");
    } else {
        error_log("✅ OTP verified in database - OTP: {$verifyResult['otp']}, Email: {$verifyResult['email']}");
    }
    
    // Send OTP via email using PHPMailer
    $mailSent = false;
    $mailError = '';
    
    // Only try to send email if PHPMailer is available
    if ($phpmailerExists) {
        try {
            // Load email configuration
            $emailConfigPath = __DIR__ . '/config_email.php';
            if (!file_exists($emailConfigPath)) {
                throw new Exception('Email configuration file not found: ' . $emailConfigPath);
            }
            $emailConfig = require $emailConfigPath;
            
            // Create PHPMailer instance (use fully qualified name since use statement is conditional)
            $mail = new \PHPMailer\PHPMailer\PHPMailer(true);
            
            // Server settings
            $mail->isSMTP();
            $mail->Host = $emailConfig['smtp_host'];
            $mail->SMTPAuth = $emailConfig['smtp_auth'];
            $mail->Username = $emailConfig['smtp_username'];
            $mail->Password = $emailConfig['smtp_password'];
            $mail->SMTPSecure = $emailConfig['smtp_secure'];
            $mail->Port = $emailConfig['smtp_port'];
            $mail->CharSet = 'UTF-8';
            
            // Debug mode (0 = off, 2 = client messages, 3 = client and server messages)
            if ($emailConfig['debug']) {
                $mail->SMTPDebug = $emailConfig['debug'];
                $mail->Debugoutput = function($str, $level) use ($email) {
                    $logMessage = "PHPMailer [$email]: $str";
                    error_log($logMessage);
                    // Also log to a separate file for easier debugging
                    file_put_contents(__DIR__ . '/email_debug.log', date('Y-m-d H:i:s') . " - $logMessage\n", FILE_APPEND);
                };
            }
            
            // Recipients
            $mail->setFrom($emailConfig['from_email'], $emailConfig['from_name']);
            $mail->addAddress($email, $user['name']);
            $mail->addReplyTo($emailConfig['reply_to_email'], $emailConfig['reply_to_name']);
            
            // Content
            $mail->isHTML(true);
            $mail->Subject = 'Password Reset OTP - Online Bidding';
        
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
                .otp-box { background-color: #fff; border: 2px solid #FF9800; padding: 20px; text-align: center; margin: 20px 0; }
                .otp-code { font-size: 32px; font-weight: bold; color: #FF9800; letter-spacing: 5px; }
                .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
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
                        <p style="margin: 0 0 10px 0;">Your OTP Code is:</p>
                        <div class="otp-code">' . $otp . '</div>
                    </div>
                    
                    <p><strong>Important:</strong></p>
                    <ul>
                        <li>This OTP will expire in <strong>5 minutes</strong></li>
                        <li>Do not share this code with anyone</li>
                        <li>If you didn\'t request this, please ignore this email</li>
                    </ul>
                    
                    <p>Best regards,<br>Online Bidding Team</p>
                </div>
                <div class="footer">
                    <p>This is an automated email. Please do not reply.</p>
                </div>
            </div>
        </body>
            </html>';
            
            // Plain text version
            $textBody = "Hello " . htmlspecialchars($user['name']) . ",\n\n";
            $textBody .= "You requested a password reset. Your OTP is: " . $otp . "\n\n";
            $textBody .= "This OTP will expire in 5 minutes.\n\n";
            $textBody .= "If you didn't request this, please ignore this email.\n\n";
            $textBody .= "Best regards,\nOnline Bidding Team";
            
            $mail->Body = $htmlBody;
            $mail->AltBody = $textBody;
            
            // Send email
            $mail->send();
            $mailSent = true;
            error_log("✅ Email sent successfully to: $email | OTP: $otp");
            file_put_contents(__DIR__ . '/email_success.log', date('Y-m-d H:i:s') . " - Email sent to: $email | OTP: $otp\n", FILE_APPEND);
            
            } catch (\PHPMailer\PHPMailer\Exception $e) {
                $mailSent = false;
                $mailError = isset($mail) ? $mail->ErrorInfo : $e->getMessage();
                $errorDetails = "❌ Email sending failed to: $email\n";
                $errorDetails .= "Error: $mailError\n";
                $errorDetails .= "Exception: " . $e->getMessage() . "\n";
                $errorDetails .= "OTP: $otp (Expires: $expires_at)\n";
                $errorDetails .= "SMTP Host: {$emailConfig['smtp_host']}\n";
                $errorDetails .= "SMTP Port: {$emailConfig['smtp_port']}\n";
                $errorDetails .= "SMTP Username: {$emailConfig['smtp_username']}\n";
                $errorDetails .= "SMTP Password Length: " . strlen($emailConfig['smtp_password']) . " chars\n";
                
                error_log($errorDetails);
                file_put_contents(__DIR__ . '/email_error.log', date('Y-m-d H:i:s') . " - $errorDetails\n", FILE_APPEND);
            }
    } else {
        // PHPMailer not installed
        error_log("❌ PHPMailer not found. OTP generated but email not sent.");
        error_log("OTP for $email: $otp (Expires: $expires_at)");
        error_log("Please install PHPMailer: Run api/INSTALL_PHPMailer.bat");
        $mailError = "PHPMailer not installed";
    }
    
    // Return success response
    // Note: Always return success even if email fails to prevent email enumeration
    // OTP is NEVER returned in response for security
    $response = [
        'success' => true,
        'message' => 'OTP has been sent to your email address.'
    ];
    
    // Log email status for debugging (server-side only)
    if (!$mailSent) {
        error_log("⚠️ Email not sent to $email. Error: $mailError");
        error_log("⚠️ OTP generated: $otp (Expires: $expires_at) - Check email configuration");
    }
    
    http_response_code(200);
    echo json_encode($response);
    
} catch (PDOException $e) {
    error_log("Database error in forgot-password.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error. Please try again later.'
    ]);
} catch (Exception $e) {
    error_log("Error in forgot-password.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'An error occurred. Please try again later.'
    ]);
}
?>

