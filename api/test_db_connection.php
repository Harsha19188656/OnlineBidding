<?php
/**
 * Simple Database Connection Test
 * Access: http://localhost/onlinebidding/api/test_db_connection.php
 */

header('Content-Type: text/html; charset=utf-8');
?>
<!DOCTYPE html>
<html>
<head>
    <title>Database Connection Test</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
        .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; }
        .success { background: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 15px; border-radius: 4px; margin: 10px 0; }
        .error { background: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; padding: 15px; border-radius: 4px; margin: 10px 0; }
        .info { background: #d1ecf1; border: 1px solid #bee5eb; color: #0c5460; padding: 15px; border-radius: 4px; margin: 10px 0; }
        pre { background: #f4f4f4; padding: 15px; border-radius: 4px; overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background: #4CAF50; color: white; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🔍 Database Connection Test</h1>
        
        <?php
        $host = 'localhost';
        $dbname = 'onlinebidding';
        $username = 'root';
        $password = '';
        
        try {
            $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            
            echo '<div class="success">✅ Database connection successful!</div>';
            
            // Check products table
            echo '<h2>Products Table Check:</h2>';
            
            $stmt = $pdo->query("SHOW TABLES LIKE 'products'");
            if ($stmt->rowCount() > 0) {
                echo '<div class="success">✅ Products table exists</div>';
                
                // Get table structure
                $stmt = $pdo->query("DESCRIBE products");
                $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
                
                echo '<h3>Table Structure:</h3>';
                echo '<table>';
                echo '<tr><th>Column</th><th>Type</th><th>Null</th><th>Key</th></tr>';
                
                $hasCondition = false;
                foreach ($columns as $col) {
                    if ($col['Field'] === 'condition') {
                        $hasCondition = true;
                        echo '<tr style="background: #d4edda;">';
                    } else {
                        echo '<tr>';
                    }
                    echo '<td><strong>' . htmlspecialchars($col['Field']) . '</strong></td>';
                    echo '<td>' . htmlspecialchars($col['Type']) . '</td>';
                    echo '<td>' . htmlspecialchars($col['Null']) . '</td>';
                    echo '<td>' . htmlspecialchars($col['Key']) . '</td>';
                    echo '</tr>';
                }
                echo '</table>';
                
                if (!$hasCondition) {
                    echo '<div class="error">❌ Missing: condition column</div>';
                    echo '<p>Click the button below to add it automatically:</p>';
                    echo '<form method="POST">';
                    echo '<button type="submit" name="add_column" style="background: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer;">Add Condition Column</button>';
                    echo '</form>';
                    
                    if (isset($_POST['add_column'])) {
                        try {
                            $pdo->exec("ALTER TABLE `products` ADD COLUMN `condition` VARCHAR(255) NULL AFTER `specs`");
                            echo '<div class="success">✅ Condition column added successfully! Refresh the page to see it.</div>';
                            echo '<script>setTimeout(function(){location.reload();}, 1000);</script>';
                        } catch (PDOException $e) {
                            echo '<div class="error">❌ Error: ' . htmlspecialchars($e->getMessage()) . '</div>';
                        }
                    }
                } else {
                    echo '<div class="success">✅ All required columns exist! Database is ready.</div>';
                }
                
            } else {
                echo '<div class="error">❌ Products table does not exist!</div>';
            }
            
        } catch (PDOException $e) {
            echo '<div class="error">❌ Database Error: ' . htmlspecialchars($e->getMessage()) . '</div>';
            echo '<p>Check:</p><ul><li>XAMPP MySQL is running</li><li>Database name: onlinebidding</li><li>Username: root</li><li>Password: (empty)</li></ul>';
        }
        ?>
        
        <hr>
        <p><small>API JSON version: <a href="check_database.php">check_database.php</a></small></p>
    </div>
</body>
</html>

