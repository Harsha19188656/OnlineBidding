<?php
/**
 * Database Check API
 * Check database connection and products table structure
 * Access: http://localhost/onlinebidding/api/check_database.php
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

// Database configuration
$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

$response = [
    'success' => false,
    'database_connected' => false,
    'products_table_exists' => false,
    'condition_column_exists' => false,
    'table_structure' => [],
    'errors' => []
];

try {
    // Connect to database
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $response['database_connected'] = true;
    
    // Check if products table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'products'");
    if ($stmt->rowCount() > 0) {
        $response['products_table_exists'] = true;
        
        // Get table structure
        $stmt = $pdo->query("DESCRIBE products");
        $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $response['table_structure'] = $columns;
        
        // Check if condition column exists
        $columnNames = array_column($columns, 'Field');
        if (in_array('condition', $columnNames)) {
            $response['condition_column_exists'] = true;
        } else {
            $response['errors'][] = 'Missing: condition column';
            
            // Try to add it automatically
            try {
                $pdo->exec("ALTER TABLE `products` ADD COLUMN `condition` VARCHAR(255) NULL AFTER `specs`");
                $response['condition_column_added'] = true;
                $response['condition_column_exists'] = true;
                $response['message'] = 'Condition column was missing but has been added successfully!';
                
                // Refresh table structure
                $stmt = $pdo->query("DESCRIBE products");
                $response['table_structure'] = $stmt->fetchAll(PDO::FETCH_ASSOC);
            } catch (PDOException $e) {
                $response['errors'][] = 'Failed to add condition column: ' . $e->getMessage();
            }
        }
    } else {
        $response['errors'][] = 'Products table does not exist';
    }
    
    $response['success'] = true;
    
} catch (PDOException $e) {
    $response['errors'][] = 'Database connection error: ' . $e->getMessage();
} catch (Exception $e) {
    $response['errors'][] = 'Error: ' . $e->getMessage();
}

// Add summary
if ($response['success'] && $response['products_table_exists'] && $response['condition_column_exists']) {
    $response['status'] = 'All checks passed! Database is ready.';
} else {
    $response['status'] = 'Some checks failed. See errors above.';
}

echo json_encode($response, JSON_PRETTY_PRINT);
?>

