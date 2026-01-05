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
    
    // Log the incoming request for debugging
    error_log("BID REQUEST: " . json_encode($data));
    
    // Validate input
    if (!isset($data['auction_id']) || empty($data['auction_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Auction ID is required']);
        exit();
    }
    
    if (!isset($data['amount']) || !is_numeric($data['amount']) || $data['amount'] <= 0) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Valid bid amount is required']);
        exit();
    }
    
    // Minimum bid amount is ₹5000
    if ($data['amount'] < 5000) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Minimum bid amount is ₹5,000']);
        exit();
    }
    
    if (!isset($data['user_id']) || empty($data['user_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'User ID is required']);
        exit();
    }
    
    $auctionId = (int)$data['auction_id'];
    $amount = (float)$data['amount'];
    $userId = (int)$data['user_id'];
    
    // Start transaction
    $pdo->beginTransaction();
    
    // Check if auction exists and is active
    $auctionStmt = $pdo->prepare("SELECT id, current_price, status FROM auctions WHERE id = ? FOR UPDATE");
    $auctionStmt->execute([$auctionId]);
    $auction = $auctionStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$auction) {
        $pdo->rollBack();
        http_response_code(404);
        echo json_encode(['success' => false, 'error' => 'Auction not found']);
        exit();
    }
    
    if ($auction['status'] !== 'active') {
        $pdo->rollBack();
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Auction is not active']);
        exit();
    }
    
    // Check if bid amount is higher than current price
    $currentPrice = (float)$auction['current_price'];
    if ($amount <= $currentPrice) {
        $pdo->rollBack();
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'error' => 'Bid amount must be higher than current price (₹' . number_format($currentPrice, 2) . ')'
        ]);
        exit();
    }
    
    // Check if user exists and get user name
    $userStmt = $pdo->prepare("SELECT id, name FROM users WHERE id = ?");
    $userStmt->execute([$userId]);
    $user = $userStmt->fetch(PDO::FETCH_ASSOC);
    if (!$user) {
        $pdo->rollBack();
        http_response_code(404);
        echo json_encode(['success' => false, 'error' => 'User not found']);
        exit();
    }
    $userName = $user['name'];
    
    // Get product name/model from auction
    $productStmt = $pdo->prepare("
        SELECT p.title as product_name, p.category 
        FROM auctions a 
        INNER JOIN products p ON a.product_id = p.id 
        WHERE a.id = ?
    ");
    $productStmt->execute([$auctionId]);
    $product = $productStmt->fetch(PDO::FETCH_ASSOC);
    $productName = $product ? $product['product_name'] : 'Unknown';
    $productCategory = $product ? $product['category'] : 'unknown';
    
    // Insert bid with name and model
    // Check if candidate_name and device_model columns exist
    $checkColumns = $pdo->query("SHOW COLUMNS FROM bids LIKE 'candidate_name'");
    $hasNameColumn = $checkColumns->rowCount() > 0;
    
    $checkColumns2 = $pdo->query("SHOW COLUMNS FROM bids LIKE 'device_model'");
    $hasModelColumn = $checkColumns2->rowCount() > 0;
    
    if ($hasNameColumn && $hasModelColumn) {
        // Insert with name and model columns
        $insertStmt = $pdo->prepare("INSERT INTO bids (auction_id, user_id, candidate_name, device_model, amount, created_at) VALUES (?, ?, ?, ?, ?, NOW())");
        $insertResult = $insertStmt->execute([$auctionId, $userId, $userName, $productName, $amount]);
    } else {
        // Insert without name and model columns (backward compatible)
        $insertStmt = $pdo->prepare("INSERT INTO bids (auction_id, user_id, amount, created_at) VALUES (?, ?, ?, NOW())");
        $insertResult = $insertStmt->execute([$auctionId, $userId, $amount]);
    }
    
    if (!$insertResult) {
        $pdo->rollBack();
        error_log("BID INSERT FAILED: " . json_encode($insertStmt->errorInfo()));
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'error' => 'Failed to insert bid into database'
        ]);
        exit();
    }
    
    // Get the inserted bid ID before updating auction
    $bidId = $pdo->lastInsertId();
    error_log("BID INSERTED: ID=$bidId | Name: $userName | Model: $productName | Amount: ₹$amount | Auction: $auctionId");
    
    // Update auction current_price
    $updateStmt = $pdo->prepare("UPDATE auctions SET current_price = ? WHERE id = ?");
    $updateResult = $updateStmt->execute([$amount, $auctionId]);
    
    if (!$updateResult) {
        $pdo->rollBack();
        error_log("AUCTION UPDATE FAILED: " . json_encode($updateStmt->errorInfo()));
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'error' => 'Failed to update auction price'
        ]);
        exit();
    }
    
    // Commit transaction
    $pdo->commit();
    error_log("BID COMMITTED: Bid ID=$bidId successfully stored");
    
    http_response_code(200);
    echo json_encode([
        'success' => true,
        'bid_id' => $bidId,
        'current_price' => $amount,
        'message' => 'Bid placed successfully'
    ]);
    
} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    error_log("Database error in bids/place.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error. Please try again later.'
    ]);
} catch (Exception $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    error_log("Error in bids/place.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'An error occurred. Please try again later.'
    ]);
}
?>

