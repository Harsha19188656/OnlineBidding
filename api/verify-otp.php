<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

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
    
    if (!isset($data['otp']) || empty(trim($data['otp']))) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'OTP is required']);
        exit();
    }
    
    $email = trim($data['email']);
    $otp = trim($data['otp']);
    
    // Remove any whitespace or non-digit characters from OTP (just in case)
    $otp = preg_replace('/[^0-9]/', '', $otp);
    
    // Log verification attempt
    error_log("🔍 OTP Verification Attempt - Email: $email, OTP: $otp (Length: " . strlen($otp) . ")");
    
    // Validate email format
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        error_log("❌ Invalid email format: $email");
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Invalid email format']);
        exit();
    }
    
    // Validate OTP format (6 digits)
    if (!preg_match('/^\d{6}$/', $otp)) {
        error_log("❌ Invalid OTP format: $otp (must be 6 digits)");
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'OTP must be 6 digits']);
        exit();
    }
    
    // First, check if OTP exists (for debugging)
    $checkStmt = $pdo->prepare("
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
    $checkStmt->execute([$email, $otp]);
    $checkResult = $checkStmt->fetch(PDO::FETCH_ASSOC);
    
    if ($checkResult) {
        error_log("📋 OTP Found - Status: {$checkResult['status']}, Used: {$checkResult['used']}, Expires: {$checkResult['expires_at']}");
    } else {
        // Check if OTP exists for this email (different OTP)
        $emailCheckStmt = $pdo->prepare("
            SELECT otp, used, expires_at, created_at
            FROM password_reset_tokens
            WHERE email = ?
            ORDER BY created_at DESC
            LIMIT 3
        ");
        $emailCheckStmt->execute([$email]);
        $recentOtps = $emailCheckStmt->fetchAll(PDO::FETCH_ASSOC);
        error_log("❌ OTP not found. Recent OTPs for $email:");
        foreach ($recentOtps as $recent) {
            error_log("   - OTP: {$recent['otp']}, Used: {$recent['used']}, Expires: {$recent['expires_at']}, Created: {$recent['created_at']}");
        }
    }
    
    // Verify OTP (must be unused and not expired)
    // Use a more robust expiration check with timezone handling
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
        AND prt.otp = ?
        ORDER BY prt.created_at DESC
        LIMIT 1
    ");
    $stmt->execute([$email, $otp]);
    $tokenCheck = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($tokenCheck) {
        $currentTime = time();
        $expiresTime = strtotime($tokenCheck['expires_at']);
        $secondsRemaining = $expiresTime - $currentTime;
        $isExpired = $expiresTime <= $currentTime;
        $isUsed = $tokenCheck['used'] == 1;
        
        error_log("📋 OTP Found in DB:");
        error_log("   - OTP: {$tokenCheck['otp']}");
        error_log("   - Used: " . ($isUsed ? 'YES' : 'NO'));
        error_log("   - Expires At: {$tokenCheck['expires_at']}");
        error_log("   - Current Time: " . date('Y-m-d H:i:s', $currentTime));
        error_log("   - Expires Time: " . date('Y-m-d H:i:s', $expiresTime));
        error_log("   - Seconds Remaining: $secondsRemaining");
        error_log("   - Is Expired: " . ($isExpired ? 'YES' : 'NO'));
        error_log("   - Status: {$tokenCheck['status']}");
        
        // Check if it's valid (not used and not expired)
        if (!$isUsed && !$isExpired && $secondsRemaining > 0) {
            $token = $tokenCheck;
            error_log("✅ OTP is VALID - Proceeding with verification");
        } else {
            $token = null;
            if ($isUsed) {
                error_log("❌ OTP is already USED");
            } elseif ($isExpired) {
                $minutesExpired = abs(round($secondsRemaining / 60, 1));
                error_log("❌ OTP is EXPIRED - Expired $minutesExpired minutes ago");
            }
        }
    } else {
        $token = null;
        error_log("❌ OTP not found in database - Email: $email, OTP: $otp");
    }
    
    if (!$token) {
        // Provide more specific error message
        $errorMsg = 'Invalid or expired OTP. Please request a new one.';
        
        // Use tokenCheck if available (from the query above)
        $checkData = $tokenCheck ? $tokenCheck : ($checkResult ? $checkResult : null);
        
        if ($checkData) {
            if ($checkData['used'] == 1) {
                $errorMsg = 'This OTP has already been used. Please request a new one.';
            } elseif ($checkData['status'] == 'EXPIRED' || strtotime($checkData['expires_at']) <= time()) {
                $expiredTime = strtotime($checkData['expires_at']);
                $currentTime = time();
                $minutesExpired = round(($currentTime - $expiredTime) / 60, 1);
                if ($minutesExpired > 0) {
                    $errorMsg = "This OTP expired $minutesExpired minutes ago. OTPs are valid for 5 minutes. Please request a new one.";
                } else {
                    $errorMsg = "This OTP has expired. OTPs are valid for 5 minutes. Please request a new one.";
                }
            } else {
                $errorMsg = 'OTP not found or invalid. Please check the OTP from your email and try again.';
            }
        } else {
            $errorMsg = 'OTP not found. Please make sure you entered the correct OTP from your email.';
        }
        error_log("❌ OTP Verification Failed - $errorMsg");
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'error' => $errorMsg
        ]);
        exit();
    }
    
    // Log remaining time
    $secondsRemaining = isset($token['seconds_remaining']) ? $token['seconds_remaining'] : 0;
    $minutesRemaining = round($secondsRemaining / 60, 1);
    error_log("✅ OTP Verified Successfully - User: {$token['user_id']}, Email: {$token['email']}, Time Remaining: {$minutesRemaining} minutes");
    
    // Mark OTP as used
    $stmt = $pdo->prepare("UPDATE password_reset_tokens SET used = 1 WHERE id = ?");
    $stmt->execute([$token['id']]);
    
    // Generate a temporary token for password reset
    // This token can be used to reset the password
    $resetToken = bin2hex(random_bytes(32));
    
    // Use database time for expiration to avoid timezone issues
    $expiresStmt = $pdo->query("SELECT DATE_ADD(NOW(), INTERVAL 1 HOUR) as expires_time");
    $expiresResult = $expiresStmt->fetch(PDO::FETCH_ASSOC);
    $resetTokenExpires = $expiresResult['expires_time'];
    
    // Store reset token (you can use the same table or create a separate one)
    // For simplicity, we'll update the password_reset_tokens table
    $stmt = $pdo->prepare("UPDATE password_reset_tokens SET otp = ?, expires_at = ? WHERE id = ?");
    $stmt->execute([$resetToken, $resetTokenExpires, $token['id']]);
    
    // Return success with reset token
    error_log("✅ Reset token generated: $resetToken (Expires: $resetTokenExpires)");
    http_response_code(200);
    echo json_encode([
        'success' => true,
        'message' => 'OTP verified successfully',
        'token' => $resetToken // This token can be used for password reset
    ]);
    
} catch (PDOException $e) {
    error_log("Database error in verify-otp.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error. Please try again later.'
    ]);
} catch (Exception $e) {
    error_log("Error in verify-otp.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'An error occurred. Please try again later.'
    ]);
}
?>


