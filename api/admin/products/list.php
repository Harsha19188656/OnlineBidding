<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Only allow GET requests
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
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
    // For now, we'll skip authentication for development
    // In production, verify the Authorization header token
    
    // Get query parameters
    $category = isset($_GET['category']) ? trim($_GET['category']) : null;
    $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 100;
    $offset = isset($_GET['offset']) ? (int)$_GET['offset'] : 0;
    
    // Validate limit and offset
    $limit = max(1, min(100, $limit));
    $offset = max(0, $offset);
    
    // Build query
    $whereConditions = [];
    $params = [];
    
    if ($category && !empty($category)) {
        $whereConditions[] = "p.category = ?";
        $params[] = $category;
    }
    
    $whereClause = !empty($whereConditions) ? 'WHERE ' . implode(' AND ', $whereConditions) : '';
    
    // Get total count
    $countSql = "SELECT COUNT(*) as total FROM products p $whereClause";
    $countStmt = $pdo->prepare($countSql);
    $countStmt->execute($params);
    $totalCount = $countStmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    // Get products with auction info
    // Note: LIMIT and OFFSET cannot use placeholders, so we use intval() for safety
    $limit = intval($limit);
    $offset = intval($offset);
    
    $sql = "SELECT 
                p.id,
                p.title,
                p.description,
                p.category,
                p.image_url,
                p.specs,
                p.`condition` as condition_label,
                p.base_price,
                p.created_at,
                a.id as auction_id,
                a.start_price,
                a.current_price,
                a.status as auction_status
            FROM products p
            LEFT JOIN auctions a ON p.id = a.product_id
            $whereClause
            ORDER BY p.created_at DESC
            LIMIT $limit OFFSET $offset";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format response
    $products = [];
    foreach ($results as $row) {
        // Parse specs JSON if it exists
        $specs = null;
        if ($row['specs']) {
            $decoded = json_decode($row['specs'], true);
            $specs = $decoded ? $decoded : null;
        }
        
        $products[] = [
            'id' => (int)$row['id'],
            'title' => $row['title'],
            'description' => $row['description'],
            'category' => $row['category'],
            'image_url' => $row['image_url'],
            'specs' => $specs,
            'condition_label' => $row['condition_label'],
            'base_price' => (float)$row['base_price'],
            'auction_id' => $row['auction_id'] ? (int)$row['auction_id'] : null,
            'start_price' => $row['start_price'] ? (float)$row['start_price'] : null,
            'current_price' => $row['current_price'] ? (float)$row['current_price'] : null,
            'auction_status' => $row['auction_status'],
            'created_at' => $row['created_at']
        ];
    }
    
    http_response_code(200);
    echo json_encode([
        'success' => true,
        'products' => $products,
        'total' => (int)$totalCount,
        'count' => count($products)
    ]);
    
} catch (PDOException $e) {
    error_log("Database error in admin/products/list.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error. Please try again later.'
    ]);
} catch (Exception $e) {
    error_log("Error in admin/products/list.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'An error occurred. Please try again later.'
    ]);
}
?>

