<?php
/**
 * Script to check if products table has all required columns
 */

// Database configuration
$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

try {
    // Connect to database
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    echo "Checking products table structure...\n\n";
    
    // Check if table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'products'");
    if ($stmt->rowCount() == 0) {
        echo "❌ ERROR: products table does not exist!\n";
        echo "You need to create the products table first.\n";
        exit(1);
    }
    
    echo "✅ products table exists\n\n";
    
    // Get table structure
    $stmt = $pdo->query("DESCRIBE products");
    $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "Current columns in products table:\n";
    echo str_repeat("-", 50) . "\n";
    foreach ($columns as $col) {
        echo sprintf("%-20s %-20s\n", $col['Field'], $col['Type']);
    }
    echo str_repeat("-", 50) . "\n\n";
    
    // Required columns
    $requiredColumns = [
        'id' => 'INT AUTO_INCREMENT PRIMARY KEY',
        'title' => 'VARCHAR',
        'description' => 'TEXT or VARCHAR',
        'category' => 'VARCHAR',
        'image_url' => 'VARCHAR or TEXT',
        'specs' => 'JSON or TEXT',
        'condition' => 'VARCHAR or TEXT',
        'base_price' => 'DECIMAL',
        'created_at' => 'TIMESTAMP'
    ];
    
    $existingColumns = array_column($columns, 'Field');
    $missingColumns = [];
    
    echo "Checking for required columns:\n";
    foreach ($requiredColumns as $colName => $colType) {
        if (in_array($colName, $existingColumns)) {
            echo "✅ $colName exists\n";
        } else {
            echo "❌ $colName MISSING (expected: $colType)\n";
            $missingColumns[] = $colName;
        }
    }
    
    if (!empty($missingColumns)) {
        echo "\n⚠️  Missing columns: " . implode(', ', $missingColumns) . "\n";
        echo "\nYou need to add these columns to the products table.\n";
        echo "Example SQL to add missing columns:\n\n";
        
        foreach ($missingColumns as $col) {
            switch ($col) {
                case 'specs':
                    echo "ALTER TABLE products ADD COLUMN specs TEXT NULL;\n";
                    break;
                case 'condition':
                    echo "ALTER TABLE products ADD COLUMN `condition` VARCHAR(255) NULL;\n";
                    break;
                default:
                    echo "-- Add $col column as needed\n";
            }
        }
    } else {
        echo "\n✅ All required columns exist!\n";
    }
    
} catch (PDOException $e) {
    echo "❌ Database error: " . $e->getMessage() . "\n";
    exit(1);
}
?>

