<?php
/**
 * Test script to check database connection and table structure
 * This will show the exact error when trying to add a product
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
    <title>Test Add Product - Debug</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 900px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
        .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; border-bottom: 2px solid #FF6B6B; padding-bottom: 10px; }
        .success { background: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 15px; border-radius: 4px; margin: 20px 0; }
        .error { background: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; padding: 15px; border-radius: 4px; margin: 20px 0; }
        .info { background: #d1ecf1; border: 1px solid #bee5eb; color: #0c5460; padding: 15px; border-radius: 4px; margin: 20px 0; }
        pre { background: #f4f4f4; padding: 15px; border-radius: 4px; overflow-x: auto; font-size: 12px; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background: #4CAF50; color: white; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🔍 Test Add Product - Debug Information</h1>
        
        <?php
        try {
            // Connect to database
            $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            
            echo '<div class="success">✅ Database connection successful!</div>';
            
            // Check table structure
            echo '<h2>1. Checking Products Table Structure:</h2>';
            $stmt = $pdo->query("DESCRIBE products");
            $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
            
            echo '<table>';
            echo '<tr><th>Column</th><th>Type</th><th>Null</th><th>Key</th><th>Default</th></tr>';
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
                echo '<td>' . htmlspecialchars($col['Default'] ?? 'NULL') . '</td>';
                echo '</tr>';
            }
            echo '</table>';
            
            if (!$hasCondition) {
                echo '<div class="error">❌ MISSING: The `condition` column does not exist!</div>';
                echo '<p>Adding it now...</p>';
                
                try {
                    $pdo->exec("ALTER TABLE `products` ADD COLUMN `condition` VARCHAR(255) NULL AFTER `specs`");
                    echo '<div class="success">✅ Added `condition` column successfully!</div>';
                    $hasCondition = true;
                } catch (PDOException $e) {
                    echo '<div class="error">❌ Failed to add column: ' . htmlspecialchars($e->getMessage()) . '</div>';
                }
            } else {
                echo '<div class="success">✅ The `condition` column exists!</div>';
            }
            
            // Test inserting a product
            echo '<h2>2. Testing Product Insert:</h2>';
            
            $testData = [
                'title' => 'Test Product',
                'description' => 'Test Description',
                'category' => 'tablet',
                'image_url' => null,
                'specs' => json_encode(['test' => 'value']),
                'condition' => 'Excellent',
                'base_price' => 50000.00
            ];
            
            echo '<div class="info">Testing with data:</div>';
            echo '<pre>' . print_r($testData, true) . '</pre>';
            
            // Start transaction for testing
            $pdo->beginTransaction();
            
            try {
                // Test the exact query from add.php
                $productSql = "INSERT INTO products (title, description, category, image_url, specs, `condition`, base_price, created_at) 
                               VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
                $productStmt = $pdo->prepare($productSql);
                
                echo '<div class="info">SQL Query:</div>';
                echo '<pre>' . htmlspecialchars($productSql) . '</pre>';
                
                $result = $productStmt->execute([
                    $testData['title'],
                    $testData['description'],
                    $testData['category'],
                    $testData['image_url'],
                    $testData['specs'],
                    $testData['condition'],
                    $testData['base_price']
                ]);
                
                $productId = $pdo->lastInsertId();
                
                echo '<div class="success">✅ Product insert successful! Product ID: ' . $productId . '</div>';
                
                // Test auction insert
                $startPrice = 5000.00;
                $auctionSql = "INSERT INTO auctions (product_id, start_price, current_price, status, start_at, end_at, created_at) 
                               VALUES (?, ?, ?, 'active', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), NOW())";
                $auctionStmt = $pdo->prepare($auctionSql);
                $auctionStmt->execute([$productId, $startPrice, $startPrice]);
                $auctionId = $pdo->lastInsertId();
                
                echo '<div class="success">✅ Auction insert successful! Auction ID: ' . $auctionId . '</div>';
                
                // Rollback the transaction (we're just testing)
                $pdo->rollBack();
                echo '<div class="info">ℹ️ Transaction rolled back (test only - no data saved)</div>';
                
                echo '<div class="success"><h3>✅ ALL TESTS PASSED!</h3>';
                echo 'Your database structure is correct. The add product API should work now.</div>';
                
            } catch (PDOException $e) {
                $pdo->rollBack();
                echo '<div class="error"><h3>❌ INSERT TEST FAILED</h3>';
                echo 'Error: ' . htmlspecialchars($e->getMessage()) . '</div>';
                echo '<pre>SQL State: ' . $e->getCode() . '</pre>';
            }
            
        } catch (PDOException $e) {
            echo '<div class="error">❌ Database Connection Error: ' . htmlspecialchars($e->getMessage()) . '</div>';
            echo '<p>Please check:</p>';
            echo '<ul>';
            echo '<li>MySQL/XAMPP is running</li>';
            echo '<li>Database name: <strong>onlinebidding</strong></li>';
            echo '<li>Username: <strong>root</strong></li>';
            echo '<li>Password: <strong>(empty)</strong></li>';
            echo '</ul>';
        } catch (Exception $e) {
            echo '<div class="error">❌ Error: ' . htmlspecialchars($e->getMessage()) . '</div>';
        }
        ?>
        
        <p><a href="javascript:location.reload()" class="btn" style="background: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; display: inline-block;">🔄 Refresh / Test Again</a></p>
    </div>
</body>
</html>

