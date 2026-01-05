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
    
    // Build update query dynamically based on provided fields
    $updateFields = [];
    $params = [];
    
    if (isset($data['title']) && !empty(trim($data['title']))) {
        $updateFields[] = "title = ?";
        $params[] = trim($data['title']);
    }
    
    if (isset($data['description'])) {
        $updateFields[] = "description = ?";
        $params[] = trim($data['description']);
    }
    
    if (isset($data['category']) && !empty(trim($data['category']))) {
        $validCategories = ['laptop', 'mobile', 'computer', 'monitor', 'tablet'];
        if (!in_array(strtolower(trim($data['category'])), $validCategories)) {
            http_response_code(400);
            echo json_encode(['success' => false, 'error' => 'Invalid category']);
            exit();
        }
        $updateFields[] = "category = ?";
        $params[] = trim($data['category']);
    }
    
    if (isset($data['image_url'])) {
        $updateFields[] = "image_url = ?";
        $params[] = trim($data['image_url']);
    }
    
    if (isset($data['specs']) && is_array($data['specs'])) {
        $updateFields[] = "specs = ?";
        $params[] = json_encode($data['specs']);
    }
    
    if (isset($data['condition_label'])) {
        $updateFields[] = "`condition` = ?";
        $params[] = trim($data['condition_label']);
    }
    
    if (isset($data['base_price']) && is_numeric($data['base_price']) && $data['base_price'] > 0) {
        $updateFields[] = "base_price = ?";
        $params[] = (float)$data['base_price'];
    }
    
    if (empty($updateFields)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'No fields to update']);
        exit();
    }
    
    // Add product_id to params for WHERE clause
    $params[] = $productId;
    
    // Execute update
    $sql = "UPDATE products SET " . implode(', ', $updateFields) . " WHERE id = ?";
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    
    http_response_code(200);
    echo json_encode([
        'success' => true,
        'message' => 'Product updated successfully',
        'product_id' => $productId
    ]);
    
} catch (PDOException $e) {
    error_log("Database error in admin/products/update.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error. Please try again later.'
    ]);
} catch (Exception $e) {
    error_log("Error in admin/products/update.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'An error occurred. Please try again later.'
    ]);
}
?>


