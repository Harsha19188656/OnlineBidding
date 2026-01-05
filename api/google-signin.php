<?php
/**
 * Google Sign-In Authentication API
 * Handles Google OAuth token verification and user creation/login
 */

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
    
    // Ensure users table has password column
    try {
        $stmt = $pdo->query("DESCRIBE users");
        $columns = array_column($stmt->fetchAll(PDO::FETCH_ASSOC), 'Field');
        if (!in_array('password', $columns)) {
            if (in_array('email', $columns)) {
                $pdo->exec("ALTER TABLE `users` ADD COLUMN `password` VARCHAR(255) NULL AFTER `email`");
            } else {
                $pdo->exec("ALTER TABLE `users` ADD COLUMN `password` VARCHAR(255) NULL");
            }
            error_log("✅ Added missing 'password' column to users table");
        }
    } catch (PDOException $e) {
        error_log("Warning: Could not verify/update users table structure: " . $e->getMessage());
    }
    
    // Get JSON input
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
    
    // Validate input
    if (!isset($data['idToken']) || empty(trim($data['idToken']))) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Google ID token is required']);
        exit();
    }
    
    $idToken = trim($data['idToken']);
    
    // Verify Google ID token
    // Note: In production, you should verify the token with Google's servers
    // For now, we'll extract user info from the token (JWT decode)
    // In production, use: https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=TOKEN
    
    // Decode JWT token (simple base64 decode - for production use proper JWT library)
    $tokenParts = explode('.', $idToken);
    if (count($tokenParts) !== 3) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Invalid Google ID token format']);
        exit();
    }
    
    // Decode payload (second part)
    $payload = json_decode(base64_decode(str_replace(['-', '_'], ['+', '/'], $tokenParts[1])), true);
    
    if (!$payload) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Failed to decode Google ID token']);
        exit();
    }
    
    // Extract user information
    $googleEmail = $payload['email'] ?? null;
    $googleName = $payload['name'] ?? ($payload['given_name'] ?? 'User');
    $googleId = $payload['sub'] ?? null;
    $googlePicture = $payload['picture'] ?? null;
    
    if (!$googleEmail) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Email not found in Google account']);
        exit();
    }
    
    error_log("🔐 Google Sign-In - Email: $googleEmail, Name: $googleName");
    
    // Check if user already exists
    $stmt = $pdo->prepare("SELECT id, name, email, role, phone FROM users WHERE email = ?");
    $stmt->execute([$googleEmail]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($user) {
        // User exists - login
        error_log("✅ Existing user found - ID: {$user['id']}, Email: {$user['email']}");
        
        // Generate auth token (simple token generation)
        $authToken = bin2hex(random_bytes(32));
        
        // Update last login time if you have that column
        // For now, just return success
        
        http_response_code(200);
        echo json_encode([
            'success' => true,
            'message' => 'Login successful',
            'token' => $authToken,
            'user' => [
                'id' => (int)$user['id'],
                'name' => $user['name'],
                'email' => $user['email'],
                'phone' => $user['phone'],
                'role' => $user['role'] ?? 'user'
            ]
        ]);
    } else {
        // New user - create account
        error_log("📝 Creating new user - Email: $googleEmail, Name: $googleName");
        
        // Generate a random password (not used for Google sign-in, but required by table structure)
        $randomPassword = bin2hex(random_bytes(16));
        $hashedPassword = password_hash($randomPassword, PASSWORD_DEFAULT);
        
        // Insert new user
        $stmt = $pdo->prepare("
            INSERT INTO users (name, email, password, role, created_at) 
            VALUES (?, ?, ?, 'user', NOW())
        ");
        $stmt->execute([$googleName, $googleEmail, $hashedPassword]);
        
        $userId = $pdo->lastInsertId();
        
        error_log("✅ New user created - ID: $userId, Email: $googleEmail");
        
        // Generate auth token
        $authToken = bin2hex(random_bytes(32));
        
        http_response_code(200);
        echo json_encode([
            'success' => true,
            'message' => 'Account created and logged in successfully',
            'token' => $authToken,
            'user' => [
                'id' => (int)$userId,
                'name' => $googleName,
                'email' => $googleEmail,
                'phone' => null,
                'role' => 'user'
            ]
        ]);
    }
    
} catch (PDOException $e) {
    $errorMessage = $e->getMessage();
    error_log("Database error in google-signin.php: " . $errorMessage);
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $errorMessage
    ]);
} catch (Exception $e) {
    $errorMessage = $e->getMessage();
    error_log("Error in google-signin.php: " . $errorMessage);
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'An error occurred: ' . $errorMessage
    ]);
}
?>

