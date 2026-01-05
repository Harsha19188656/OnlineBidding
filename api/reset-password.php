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
    error_log("📥 Reset Password Request - Raw input: " . substr($input, 0, 200));
    
    $data = json_decode($input, true);
    
    // Check if JSON decode failed
    if ($data === null && json_last_error() !== JSON_ERROR_NONE) {
        $error = 'Invalid JSON: ' . json_last_error_msg() . ' | Input: ' . substr($input, 0, 100);
        error_log("❌ JSON Error: " . $error);
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'error' => $error
        ]);
        exit();
    }
    
    error_log("📋 Parsed data: " . json_encode($data));
    
    // Validate input
    if (!isset($data['email']) || empty(trim($data['email']))) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Email is required']);
        exit();
    }
    
    if (!isset($data['token']) || empty(trim($data['token']))) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Reset token is required']);
        exit();
    }
    
    // Check for both newPassword and new_password (handle different field names)
    $newPassword = null;
    if (isset($data['newPassword']) && !empty(trim($data['newPassword']))) {
        $newPassword = trim($data['newPassword']);
    } elseif (isset($data['new_password']) && !empty(trim($data['new_password']))) {
        $newPassword = trim($data['new_password']);
    } else {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'New password is required']);
        exit();
    }
    
    $email = trim($data['email']);
    $token = trim($data['token']);
    
    // Validate email format
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Invalid email format']);
        exit();
    }
    
    // Validate password length
    if (strlen($newPassword) < 6) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Password must be at least 6 characters long']);
        exit();
    }
    
    // Log reset attempt
    error_log("🔐 Password Reset Attempt - Email: $email, Token: " . substr($token, 0, 10) . "...");
    
    // First check if token exists (for debugging)
    $checkStmt = $pdo->prepare("
        SELECT prt.*, u.id as user_id, u.name, u.email,
               TIMESTAMPDIFF(SECOND, NOW(), prt.expires_at) as seconds_remaining,
               CASE 
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
    $checkStmt->execute([$email, $token]);
    $tokenCheck = $checkStmt->fetch(PDO::FETCH_ASSOC);
    
    if ($tokenCheck) {
        error_log("📋 Reset token found - Status: {$tokenCheck['status']}, Expires: {$tokenCheck['expires_at']}, Now: " . date('Y-m-d H:i:s'));
    } else {
        // Check recent tokens for this email
        $recentStmt = $pdo->prepare("
            SELECT otp, expires_at, created_at, used
            FROM password_reset_tokens
            WHERE email = ?
            ORDER BY created_at DESC
            LIMIT 3
        ");
        $recentStmt->execute([$email]);
        $recentTokens = $recentStmt->fetchAll(PDO::FETCH_ASSOC);
        error_log("❌ Reset token not found. Recent tokens for $email:");
        foreach ($recentTokens as $rt) {
            $tokenPreview = strlen($rt['otp']) > 20 ? substr($rt['otp'], 0, 20) . "..." : $rt['otp'];
            error_log("   - Token: $tokenPreview, Used: {$rt['used']}, Expires: {$rt['expires_at']}");
        }
    }
    
    // Verify reset token (token is stored in 'otp' column after OTP verification)
    $stmt = $pdo->prepare("
        SELECT prt.*, u.id as user_id, u.name, u.email,
               TIMESTAMPDIFF(SECOND, NOW(), prt.expires_at) as seconds_remaining
        FROM password_reset_tokens prt
        INNER JOIN users u ON prt.user_id = u.id
        WHERE prt.email = ? 
        AND prt.otp = ? 
        AND prt.expires_at > NOW()
        ORDER BY prt.created_at DESC
        LIMIT 1
    ");
    $stmt->execute([$email, $token]);
    $resetToken = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$resetToken) {
        $errorMsg = 'Invalid or expired reset token. Please request a new OTP.';
        if ($tokenCheck) {
            if ($tokenCheck['status'] == 'EXPIRED') {
                $errorMsg = 'Reset token has expired. Please request a new OTP.';
            }
        }
        error_log("❌ Reset token verification failed - $errorMsg");
        http_response_code(401);
        echo json_encode([
            'success' => false,
            'error' => $errorMsg
        ]);
        exit();
    }
    
    error_log("✅ Reset token verified - User: {$resetToken['user_id']}, Email: {$resetToken['email']}");
    
    // Hash the new password
    $hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);
    
    // Update user's password
    $stmt = $pdo->prepare("UPDATE users SET password = ? WHERE id = ?");
    $stmt->execute([$hashedPassword, $resetToken['user_id']]);
    
    $rowsAffected = $stmt->rowCount();
    if ($rowsAffected > 0) {
        error_log("✅ Password updated successfully for user ID: {$resetToken['user_id']}");
    } else {
        error_log("⚠️ Password update returned 0 rows affected for user ID: {$resetToken['user_id']}");
    }
    
    // Mark reset token as used (delete it)
    $stmt = $pdo->prepare("DELETE FROM password_reset_tokens WHERE id = ?");
    $stmt->execute([$resetToken['id']]);
    
    error_log("✅ Reset token deleted - ID: {$resetToken['id']}");
    
    // Return success
    http_response_code(200);
    echo json_encode([
        'success' => true,
        'message' => 'Password has been reset successfully'
    ]);
    
} catch (PDOException $e) {
    $errorMessage = $e->getMessage();
    $errorDetails = "Database error in reset-password.php: " . $errorMessage . " | File: " . __FILE__ . " | Line: " . $e->getLine();
    error_log("❌ " . $errorDetails);
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $errorMessage
    ]);
} catch (Exception $e) {
    $errorMessage = $e->getMessage();
    $errorDetails = "Error in reset-password.php: " . $errorMessage . " | File: " . __FILE__ . " | Line: " . $e->getLine();
    error_log("❌ " . $errorDetails);
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'An error occurred: ' . $errorMessage
    ]);
}
?>


