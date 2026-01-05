<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

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
    
    // TODO: Verify admin authorization token
    
    // Get JSON input
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
    
    // Validate product_id
    if (!isset($data['product_id']) || empty($data['product_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Product ID is required']);
        exit();
    }
    
    $productId = (int)$data['product_id'];
    
    // Check if product exists
    $checkStmt = $pdo->prepare("SELECT id FROM products WHERE id = ?");
    $checkStmt->execute([$productId]);
    if (!$checkStmt->fetch()) {
        http_response_code(404);
        echo json_encode(['success' => false, 'error' => 'Product not found']);
        exit();
    }
    
    // Start transaction
    $pdo->beginTransaction();
    
    // Delete related bids first (if foreign key constraints allow)
    $deleteBidsStmt = $pdo->prepare("DELETE b FROM bids b INNER JOIN auctions a ON b.auction_id = a.id WHERE a.product_id = ?");
    $deleteBidsStmt->execute([$productId]);
    
    // Delete related auction
    $deleteAuctionStmt = $pdo->prepare("DELETE FROM auctions WHERE product_id = ?");
    $deleteAuctionStmt->execute([$productId]);
    
    // Delete product
    $deleteProductStmt = $pdo->prepare("DELETE FROM products WHERE id = ?");
    $deleteProductStmt->execute([$productId]);
    
    // Commit transaction
    $pdo->commit();
    
    http_response_code(200);
    echo json_encode([
        'success' => true,
        'message' => 'Product deleted successfully',
        'product_id' => $productId
    ]);
    
} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    error_log("Database error in admin/products/delete.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error. Please try again later.'
    ]);
} catch (Exception $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    error_log("Error in admin/products/delete.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'An error occurred. Please try again later.'
    ]);
}
?>


