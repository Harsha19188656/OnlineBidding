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
    if (!isset($data['user_id']) || empty($data['user_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'User ID is required']);
        exit();
    }
    
    if (!isset($data['amount']) || !is_numeric($data['amount']) || $data['amount'] <= 0) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Valid payment amount is required']);
        exit();
    }
    
    if (!isset($data['payment_method']) || empty($data['payment_method'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Payment method is required']);
        exit();
    }
    
    if (!isset($data['upi_id']) || empty($data['upi_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'UPI ID is required']);
        exit();
    }
    
    // Validate UPI ID format (basic validation)
    $upiId = trim($data['upi_id']);
    if (!preg_match('/^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$/', $upiId)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Invalid UPI ID format. Use format: yourname@upi']);
        exit();
    }
    
    // Log the payment request for debugging
    error_log("PAYMENT REQUEST: " . json_encode($data));
    
    $userId = (int)$data['user_id'];
    $amount = (float)$data['amount'];
    $paymentMethod = trim($data['payment_method']);
    $auctionId = isset($data['auction_id']) && !empty($data['auction_id']) ? (int)$data['auction_id'] : null;
    
    // Check if user exists
    $userStmt = $pdo->prepare("SELECT id FROM users WHERE id = ?");
    $userStmt->execute([$userId]);
    if (!$userStmt->fetch()) {
        http_response_code(404);
        echo json_encode(['success' => false, 'error' => 'User not found']);
        exit();
    }
    
    // Check if auction exists (if provided)
    if ($auctionId !== null) {
        $auctionStmt = $pdo->prepare("SELECT id FROM auctions WHERE id = ?");
        $auctionStmt->execute([$auctionId]);
        if (!$auctionStmt->fetch()) {
            http_response_code(404);
            echo json_encode(['success' => false, 'error' => 'Auction not found']);
            exit();
        }
    }
    
    // Generate transaction ID (in real app, this would come from payment gateway)
    $transactionId = 'TXN' . date('YmdHis') . rand(1000, 9999);
    
    // Check if payments table exists
    $tableCheck = $pdo->query("SHOW TABLES LIKE 'payments'");
    if ($tableCheck->rowCount() == 0) {
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'error' => 'Payments table does not exist. Please run create_payments_table.sql first.'
        ]);
        exit();
    }
    
    // Insert payment record
    $insertStmt = $pdo->prepare("
        INSERT INTO payments (user_id, auction_id, amount, payment_method, upi_id, status, transaction_id, payment_date) 
        VALUES (?, ?, ?, ?, ?, 'pending', ?, NOW())
    ");
    
    $insertResult = $insertStmt->execute([$userId, $auctionId, $amount, $paymentMethod, $upiId, $transactionId]);
    
    if (!$insertResult) {
        error_log("Payment insert failed: " . json_encode($insertStmt->errorInfo()));
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'error' => 'Failed to save payment. Please try again.'
        ]);
        exit();
    }
    
    $paymentId = $pdo->lastInsertId();
    error_log("PAYMENT INSERTED: ID=$paymentId, user_id=$userId, amount=$amount, upi_id=$upiId");
    
    // In a real app, you would:
    // 1. Call payment gateway API here
    // 2. Update status based on gateway response
    // 3. For now, we'll mark it as completed (simulated)
    
    // Simulate payment processing (update status to completed)
    $updateStmt = $pdo->prepare("UPDATE payments SET status = 'completed', payment_date = NOW() WHERE id = ?");
    $updateStmt->execute([$paymentId]);
    
    http_response_code(200);
    echo json_encode([
        'success' => true,
        'payment_id' => $paymentId,
        'transaction_id' => $transactionId,
        'status' => 'completed',
        'message' => 'Payment processed successfully'
    ]);
    
} catch (PDOException $e) {
    $errorMsg = $e->getMessage();
    error_log("Database error in payments/create.php: " . $errorMsg);
    
    // Check if it's a table doesn't exist error
    if (strpos($errorMsg, "doesn't exist") !== false || strpos($errorMsg, "Unknown table") !== false) {
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'error' => 'Payments table not found. Please create it first using create_payments_table.sql'
        ]);
    } else {
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'error' => 'Database error: ' . $errorMsg
        ]);
    }
} catch (Exception $e) {
    error_log("Error in payments/create.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'An error occurred: ' . $e->getMessage()
    ]);
}
?>

