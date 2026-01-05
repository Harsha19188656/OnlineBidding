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
    
    // Automatically ensure products table has all required columns
    try {
        $stmt = $pdo->query("DESCRIBE products");
        $columns = array_column($stmt->fetchAll(PDO::FETCH_ASSOC), 'Field');
        
        // Check and add specs column first (if missing and needed for AFTER clause)
        if (!in_array('specs', $columns)) {
            try {
                $pdo->exec("ALTER TABLE `products` ADD COLUMN specs TEXT NULL");
                error_log("✅ Added missing 'specs' column to products table");
                $columns[] = 'specs'; // Update columns array
            } catch (PDOException $e) {
                error_log("Warning: Could not add specs column: " . $e->getMessage());
            }
        }
        
        // Check and add condition column if missing
        if (!in_array('condition', $columns)) {
            try {
                // Try to add after specs if specs exists, otherwise add at end
                if (in_array('specs', $columns)) {
                    $pdo->exec("ALTER TABLE `products` ADD COLUMN `condition` VARCHAR(255) NULL AFTER `specs`");
                } else {
                    $pdo->exec("ALTER TABLE `products` ADD COLUMN `condition` VARCHAR(255) NULL");
                }
                error_log("✅ Added missing 'condition' column to products table");
            } catch (PDOException $e) {
                error_log("Warning: Could not add condition column: " . $e->getMessage());
                // Continue anyway - will fail on insert with clearer error
            }
        }
    } catch (PDOException $e) {
        // If table doesn't exist, log error but continue (will fail on insert with clearer error)
        error_log("Warning: Could not verify/update products table structure: " . $e->getMessage());
    }
    
    // TODO: Verify admin authorization token
    // For now, we'll skip authentication for development
    
    // Get JSON input
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
    
    // Validate required fields
    if (!isset($data['title']) || empty(trim($data['title']))) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Title is required']);
        exit();
    }
    
    if (!isset($data['category']) || empty(trim($data['category']))) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Category is required']);
        exit();
    }
    
    if (!isset($data['base_price']) || !is_numeric($data['base_price']) || $data['base_price'] <= 0) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Valid base price is required']);
        exit();
    }
    
    $title = trim($data['title']);
    $description = isset($data['description']) ? trim($data['description']) : null;
    $category = trim($data['category']);
    $imageUrl = isset($data['image_url']) ? trim($data['image_url']) : null;
    $conditionLabel = isset($data['condition_label']) ? trim($data['condition_label']) : null;
    $basePrice = (float)$data['base_price'];
    // All auctions start at ₹5000 regardless of base price
    $startPrice = 5000.00;
    
    // Validate category
    $validCategories = ['laptop', 'mobile', 'computer', 'monitor', 'tablet'];
    if (!in_array(strtolower($category), $validCategories)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Invalid category. Must be one of: ' . implode(', ', $validCategories)]);
        exit();
    }
    
    // Process specs - convert map to JSON string
    $specsJson = null;
    if (isset($data['specs']) && is_array($data['specs'])) {
        $specsJson = json_encode($data['specs']);
    }
    
    // Start transaction
    $pdo->beginTransaction();
    
    // Insert product - dynamically build query based on available columns
    // Check if condition column exists
    $stmt = $pdo->query("DESCRIBE products");
    $columns = array_column($stmt->fetchAll(PDO::FETCH_ASSOC), 'Field');
    $hasCondition = in_array('condition', $columns);
    
    if ($hasCondition) {
        $productSql = "INSERT INTO products (title, description, category, image_url, specs, `condition`, base_price, created_at) 
                   VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
    $productStmt = $pdo->prepare($productSql);
    $productStmt->execute([$title, $description, $category, $imageUrl, $specsJson, $conditionLabel, $basePrice]);
    } else {
        // Fallback if condition column doesn't exist (shouldn't happen with auto-fix, but just in case)
        $productSql = "INSERT INTO products (title, description, category, image_url, specs, base_price, created_at) 
                       VALUES (?, ?, ?, ?, ?, ?, NOW())";
        $productStmt = $pdo->prepare($productSql);
        $productStmt->execute([$title, $description, $category, $imageUrl, $specsJson, $basePrice]);
        error_log("⚠️ Inserted product without condition column (column missing)");
    }
    
    $productId = $pdo->lastInsertId();
    
    // Create auction for this product
    $auctionSql = "INSERT INTO auctions (product_id, start_price, current_price, status, start_at, end_at, created_at) 
                   VALUES (?, ?, ?, 'active', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), NOW())";
    $auctionStmt = $pdo->prepare($auctionSql);
    $auctionStmt->execute([$productId, $startPrice, $startPrice]);
    
    $auctionId = $pdo->lastInsertId();
    
    // Commit transaction
    $pdo->commit();
    
    http_response_code(200);
    echo json_encode([
        'success' => true,
        'message' => 'Product and auction created successfully',
        'product_id' => (int)$productId,
        'auction_id' => (int)$auctionId
    ]);
    
} catch (PDOException $e) {
    if (isset($pdo) && $pdo->inTransaction()) {
        $pdo->rollBack();
    }
    $errorMessage = $e->getMessage();
    error_log("Database error in admin/products/add.php: " . $errorMessage);
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error: ' . $errorMessage
    ]);
} catch (Exception $e) {
    if (isset($pdo) && $pdo->inTransaction()) {
        $pdo->rollBack();
    }
    $errorMessage = $e->getMessage();
    error_log("Error in admin/products/add.php: " . $errorMessage);
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'An error occurred: ' . $errorMessage
    ]);
}
?>

