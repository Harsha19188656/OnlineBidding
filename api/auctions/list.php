<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

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
    
    // Get query parameters
    $category = isset($_GET['category']) ? trim($_GET['category']) : null;
    $search = isset($_GET['search']) ? trim($_GET['search']) : null;
    $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 20;
    $offset = isset($_GET['offset']) ? (int)$_GET['offset'] : 0;
    
    // Validate limit and offset
    $limit = max(1, min(100, $limit)); // Between 1 and 100
    $offset = max(0, $offset);
    
    // Build query
    $whereConditions = ["a.status = 'active'"];
    $params = [];
    
    if ($category && !empty($category)) {
        $whereConditions[] = "p.category = ?";
        $params[] = $category;
    }
    
    if ($search && !empty($search)) {
        $whereConditions[] = "(p.title LIKE ? OR p.description LIKE ?)";
        $searchTerm = "%$search%";
        $params[] = $searchTerm;
        $params[] = $searchTerm;
    }
    
    $whereClause = implode(' AND ', $whereConditions);
    
    // Get total count
    $countSql = "SELECT COUNT(*) as total 
                 FROM auctions a
                 INNER JOIN products p ON a.product_id = p.id
                 WHERE $whereClause";
    $countStmt = $pdo->prepare($countSql);
    $countStmt->execute($params);
    $totalCount = $countStmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    // Get auctions with products
    // Note: LIMIT and OFFSET cannot use placeholders, so we use intval() for safety
    $limit = intval($limit);
    $offset = intval($offset);
    
    $sql = "SELECT 
                p.id as product_id,
                p.title,
                p.description,
                p.category,
                p.image_url,
                p.specs,
                p.condition_label as product_condition,
                p.base_price,
                p.created_at as product_created_at,
                a.id as auction_id,
                a.start_price,
                a.current_price,
                a.status as auction_status,
                a.start_at,
                a.end_at
            FROM auctions a
            INNER JOIN products p ON a.product_id = p.id
            WHERE $whereClause
            ORDER BY a.created_at DESC
            LIMIT $limit OFFSET $offset";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format response
    $items = [];
    foreach ($results as $row) {
        $items[] = [
            'product' => [
                'id' => (int)$row['product_id'],
                'title' => $row['title'],
                'description' => $row['description'],
                'category' => $row['category'],
                'image_url' => $row['image_url'],
                'specs' => $row['specs'],
                'condition' => $row['product_condition'],
                'base_price' => (float)$row['base_price'],
                'created_at' => $row['product_created_at']
            ],
            'auction' => [
                'id' => (int)$row['auction_id'],
                'product_id' => (int)$row['product_id'],
                'start_price' => (float)$row['start_price'],
                'current_price' => (float)$row['current_price'],
                'status' => $row['auction_status'],
                'start_at' => $row['start_at'],
                'end_at' => $row['end_at']
            ],
            // Additional fields for compatibility
            'name' => $row['title'],
            'specs' => $row['specs'],
            'price' => '₹' . number_format($row['current_price'], 0),
            'image_url' => $row['image_url']
        ];
    }
    
    http_response_code(200);
    echo json_encode([
        'success' => true,
        'items' => $items,
        'count' => count($items),
        'total' => (int)$totalCount
    ]);
    
} catch (PDOException $e) {
    error_log("Database error in auctions/list.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error. Please try again later.'
    ]);
} catch (Exception $e) {
    error_log("Error in auctions/list.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'An error occurred. Please try again later.'
    ]);
}
?>

