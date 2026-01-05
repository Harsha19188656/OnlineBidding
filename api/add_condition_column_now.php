<?php
/**
 * Script to automatically add condition column to products table
 * Open this in browser: http://localhost/onlinebidding/api/add_condition_column_now.php
 */

// Database configuration
$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

header('Content-Type: text/html; charset=utf-8');
?>
<!DOCTYPE html>
<html>
<head>
    <title>Add Condition Column to Products Table</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
        .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; border-bottom: 2px solid #4CAF50; padding-bottom: 10px; }
        .success { background: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 15px; border-radius: 4px; margin: 20px 0; }
        .error { background: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; padding: 15px; border-radius: 4px; margin: 20px 0; }
        .info { background: #d1ecf1; border: 1px solid #bee5eb; color: #0c5460; padding: 15px; border-radius: 4px; margin: 20px 0; }
        pre { background: #f4f4f4; padding: 15px; border-radius: 4px; overflow-x: auto; }
        .btn { background: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; display: inline-block; margin-top: 20px; }
        .btn:hover { background: #45a049; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🔧 Add Condition Column to Products Table</h1>
        
        <?php
        try {
            // Connect to database
            $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            
            echo '<div class="info">📊 Checking current table structure...</div>';
            
            // Check if condition column already exists
            $stmt = $pdo->query("DESCRIBE products");
            $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $existingColumns = array_column($columns, 'Field');
            
            if (in_array('condition', $existingColumns)) {
                echo '<div class="info">✅ The `condition` column already exists in the products table!</div>';
                echo '<p>The table structure is correct. You can now add products without errors.</p>';
            } else {
                echo '<div class="info">⚠️ The `condition` column is missing. Adding it now...</div>';
                
                // Add condition column after specs
                $sql = "ALTER TABLE `products` ADD COLUMN `condition` VARCHAR(255) NULL AFTER `specs`";
                $pdo->exec($sql);
                
                echo '<div class="success">✅ SUCCESS! The `condition` column has been added to the products table!</div>';
                echo '<p>You can now add products without database errors.</p>';
            }
            
            // Show updated table structure
            echo '<h2>Current Products Table Structure:</h2>';
            echo '<pre>';
            $stmt = $pdo->query("DESCRIBE products");
            $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
            
            echo str_pad("Column", 20) . str_pad("Type", 25) . str_pad("Null", 8) . "Key\n";
            echo str_repeat("-", 70) . "\n";
            foreach ($columns as $col) {
                echo str_pad($col['Field'], 20) . 
                     str_pad($col['Type'], 25) . 
                     str_pad($col['Null'], 8) . 
                     $col['Key'] . "\n";
            }
            echo '</pre>';
            
            echo '<div class="success">✅ Database connection successful!<br>';
            echo '✅ Table structure updated!<br>';
            echo '✅ You can now add products from your admin panel!</div>';
            
        } catch (PDOException $e) {
            echo '<div class="error">❌ Database Error: ' . htmlspecialchars($e->getMessage()) . '</div>';
            echo '<p>Please check:</p>';
            echo '<ul>';
            echo '<li>MySQL/XAMPP is running</li>';
            echo '<li>Database name is correct: <strong>onlinebidding</strong></li>';
            echo '<li>Username is correct: <strong>root</strong></li>';
            echo '<li>Password is empty (default XAMPP setting)</li>';
            echo '</ul>';
        } catch (Exception $e) {
            echo '<div class="error">❌ Error: ' . htmlspecialchars($e->getMessage()) . '</div>';
        }
        ?>
        
        <p><a href="javascript:location.reload()" class="btn">🔄 Refresh / Check Again</a></p>
        <p><small>This script automatically adds the missing `condition` column to your products table.</small></p>
    </div>
</body>
</html>

