<?php
/**
 * Script to add missing columns to products table
 * Run this once to fix the table structure
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
    
    echo "Checking and fixing products table structure...\n\n";
    
    // Get existing columns
    $stmt = $pdo->query("DESCRIBE products");
    $existingColumns = array_column($stmt->fetchAll(PDO::FETCH_ASSOC), 'Field');
    
    echo "Existing columns: " . implode(', ', $existingColumns) . "\n\n";
    
    // Columns to add with their definitions
    $columnsToAdd = [
        'specs' => 'TEXT NULL',
        'condition' => 'VARCHAR(255) NULL'  // condition is reserved word
    ];
    
    foreach ($columnsToAdd as $columnName => $columnDef) {
        if (!in_array($columnName, $existingColumns)) {
            echo "Adding column: $columnName...\n";
            try {
                // Use backticks for reserved words
                $escapedColumnName = $columnName === 'condition' ? '`condition`' : $columnName;
                $sql = "ALTER TABLE products ADD COLUMN $escapedColumnName $columnDef";
                $pdo->exec($sql);
                echo "✅ Column $columnName added successfully\n\n";
            } catch (PDOException $e) {
                // Column might already exist or other error
                if (strpos($e->getMessage(), 'Duplicate column name') !== false) {
                    echo "ℹ️  Column $columnName already exists (skipped)\n\n";
                } else {
                    echo "❌ Error adding column $columnName: " . $e->getMessage() . "\n\n";
                }
            }
        } else {
            echo "✅ Column $columnName already exists (skipped)\n\n";
        }
    }
    
    // Verify final structure
    echo "\nFinal table structure:\n";
    echo str_repeat("-", 60) . "\n";
    $stmt = $pdo->query("DESCRIBE products");
    $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
    foreach ($columns as $col) {
        echo sprintf("%-20s %-30s %s\n", $col['Field'], $col['Type'], $col['Null']);
    }
    echo str_repeat("-", 60) . "\n";
    
    echo "\n✅ Table structure check complete!\n";
    echo "You can now try adding products again.\n";
    
} catch (PDOException $e) {
    echo "❌ Database error: " . $e->getMessage() . "\n";
    exit(1);
}
?>

