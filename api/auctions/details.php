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
    
    // Get auction ID
    if (!isset($_GET['id']) || empty(trim($_GET['id']))) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Auction ID is required']);
        exit();
    }
    
    $auctionId = (int)$_GET['id'];
    
    // Get auction with product details
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
                a.product_id,
                a.start_price,
                a.current_price,
                a.status as auction_status,
                a.start_at,
                a.end_at
            FROM auctions a
            INNER JOIN products p ON a.product_id = p.id
            WHERE a.id = ?";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$auctionId]);
    $auction = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$auction) {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'error' => 'Auction not found'
        ]);
        exit();
    }
    
    // Get bids for this auction with user name and product name
    $bidsSql = "SELECT 
                    b.id,
                    b.auction_id,
                    b.user_id,
                    b.amount,
                    b.created_at,
                    u.name as user_name,
                    p.title as product_name,
                    p.category as product_category
                FROM bids b
                LEFT JOIN users u ON b.user_id = u.id
                LEFT JOIN auctions a ON b.auction_id = a.id
                LEFT JOIN products p ON a.product_id = p.id
                WHERE b.auction_id = ?
                ORDER BY b.amount DESC, b.created_at DESC
                LIMIT 50";
    
    $bidsStmt = $pdo->prepare($bidsSql);
    $bidsStmt->execute([$auctionId]);
    $bids = $bidsStmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Format bids
    $formattedBids = [];
    foreach ($bids as $bid) {
        $formattedBids[] = [
            'id' => (int)$bid['id'],
            'auction_id' => (int)$bid['auction_id'],
            'user_id' => (int)$bid['user_id'],
            'amount' => (float)$bid['amount'],
            'created_at' => $bid['created_at'],
            'user_name' => $bid['user_name'],
            'product_name' => $bid['product_name'],
            'product_category' => $bid['product_category']
        ];
    }
    
    // Format response
    http_response_code(200);
    echo json_encode([
        'success' => true,
        'product' => [
            'id' => (int)$auction['product_id'],
            'title' => $auction['title'],
            'description' => $auction['description'],
            'category' => $auction['category'],
            'image_url' => $auction['image_url'],
            'specs' => $auction['specs'],
            'condition' => $auction['condition_label'],
            'base_price' => (float)$auction['base_price'],
            'created_at' => $auction['product_created_at']
        ],
        'auction' => [
            'id' => (int)$auction['auction_id'],
            'product_id' => (int)$auction['product_id'],
            'start_price' => (float)$auction['start_price'],
            'current_price' => (float)$auction['current_price'],
            'status' => $auction['auction_status'],
            'start_at' => $auction['start_at'],
            'end_at' => $auction['end_at']
        ],
        'bids' => $formattedBids
    ]);
    
} catch (PDOException $e) {
    error_log("Database error in auctions/details.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'Database error. Please try again later.'
    ]);
} catch (Exception $e) {
    error_log("Error in auctions/details.php: " . $e->getMessage());
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'error' => 'An error occurred. Please try again later.'
    ]);
}
?>

