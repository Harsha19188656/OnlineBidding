<?php
/**
 * Test endpoint to verify database connection and table structure
 * Access via browser: http://localhost/onlinebidding/api/admin/products/test_connection.php
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

$result = [
    'success' => false,
    'database_connected' => false,
    'table_exists' => false,
    'columns' => [],
    'condition_column_exists' => false,
    'condition_column_added' => false,
    'message' => ''
];

try {
    // Test database connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $result['database_connected'] = true;
    
    // Check products table
    $stmt = $pdo->query("DESCRIBE products");
    $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $columnNames = array_column($columns, 'Field');
    $result['columns'] = $columnNames;
    $result['table_exists'] = true;
    
    // Check if condition column exists
    if (in_array('condition', $columnNames)) {
        $result['condition_column_exists'] = true;
        $result['message'] = 'All columns exist. Database is ready!';
        $result['success'] = true;
    } else {
        // Try to add it
        try {
            if (in_array('specs', $columnNames)) {
                $pdo->exec("ALTER TABLE `products` ADD COLUMN `condition` VARCHAR(255) NULL AFTER `specs`");
            } else {
                $pdo->exec("ALTER TABLE `products` ADD COLUMN `condition` VARCHAR(255) NULL");
            }
            $result['condition_column_added'] = true;
            $result['condition_column_exists'] = true;
            $result['message'] = 'Condition column was missing but has been added successfully!';
            $result['success'] = true;
        } catch (PDOException $e) {
            $result['message'] = 'Condition column missing and could not be added: ' . $e->getMessage();
            $result['error'] = $e->getMessage();
        }
    }
    
} catch (PDOException $e) {
    $result['message'] = 'Database connection error: ' . $e->getMessage();
    $result['error'] = $e->getMessage();
} catch (Exception $e) {
    $result['message'] = 'Error: ' . $e->getMessage();
    $result['error'] = $e->getMessage();
}

echo json_encode($result, JSON_PRETTY_PRINT);
?>

