<?php
/**
 * Debug script to check why bids might not be storing
 * Access: http://localhost/onlinebidding/api/debug_bid_storage.php
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

$debug = [
    'timestamp' => date('Y-m-d H:i:s'),
    'checks' => []
];

try {
    // Check 1: Database connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $debug['checks']['database_connection'] = '✅ Connected';
    
    // Check 2: Bids table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'bids'");
    $debug['checks']['bids_table_exists'] = $stmt->rowCount() > 0 ? '✅ Exists' : '❌ Missing';
    
    // Check 3: Auctions table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'auctions'");
    $debug['checks']['auctions_table_exists'] = $stmt->rowCount() > 0 ? '✅ Exists' : '❌ Missing';
    
    // Check 4: Users table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'users'");
    $debug['checks']['users_table_exists'] = $stmt->rowCount() > 0 ? '✅ Exists' : '❌ Missing';
    
    // Check 5: Check recent bids
    $stmt = $pdo->query("SELECT COUNT(*) as count FROM bids");
    $bidCount = $stmt->fetch(PDO::FETCH_ASSOC)['count'];
    $debug['checks']['total_bids'] = "✅ $bidCount bids found";
    
    // Check 6: Check recent bids (last 5)
    $stmt = $pdo->query("SELECT * FROM bids ORDER BY id DESC LIMIT 5");
    $recentBids = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $debug['checks']['recent_bids'] = $recentBids;
    
    // Check 7: Check auction 7 (common test auction)
    $stmt = $pdo->prepare("SELECT * FROM auctions WHERE id = 7");
    $stmt->execute();
    $auction7 = $stmt->fetch(PDO::FETCH_ASSOC);
    if ($auction7) {
        $debug['checks']['auction_7'] = [
            'exists' => '✅',
            'current_price' => $auction7['current_price'],
            'status' => $auction7['status']
        ];
    } else {
        $debug['checks']['auction_7'] = '❌ Not found';
    }
    
    // Check 8: Check user 25
    $stmt = $pdo->prepare("SELECT * FROM users WHERE id = 25");
    $stmt->execute();
    $user25 = $stmt->fetch(PDO::FETCH_ASSOC);
    if ($user25) {
        $debug['checks']['user_25'] = [
            'exists' => '✅',
            'name' => $user25['name'],
            'email' => $user25['email']
        ];
    } else {
        $debug['checks']['user_25'] = '❌ Not found';
    }
    
    // Check 9: Check PHP error log location
    $debug['checks']['php_error_log'] = ini_get('error_log') ?: 'Default system log';
    
    // Check 10: Test insert (dry run)
    $testStmt = $pdo->prepare("SELECT 1 FROM bids WHERE auction_id = ? AND user_id = ? AND amount = ? LIMIT 1");
    $debug['checks']['test_query'] = '✅ Query prepared successfully';
    
    $debug['status'] = 'success';
    
} catch (PDOException $e) {
    $debug['status'] = 'error';
    $debug['error'] = $e->getMessage();
    $debug['checks']['database_connection'] = '❌ Failed: ' . $e->getMessage();
}

echo json_encode($debug, JSON_PRETTY_PRINT);
?>

