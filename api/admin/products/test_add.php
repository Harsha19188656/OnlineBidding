<?php
/**
 * Test endpoint to verify add product API is working
 * Access: http://localhost/onlinebidding/api/admin/products/test_add.php
 */

header('Content-Type: text/html; charset=utf-8');
?>
<!DOCTYPE html>
<html>
<head>
    <title>Test Add Product API</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 900px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
        .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; border-bottom: 2px solid #4CAF50; padding-bottom: 10px; }
        .success { background: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 15px; border-radius: 4px; margin: 10px 0; }
        .error { background: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; padding: 15px; border-radius: 4px; margin: 10px 0; }
        .info { background: #d1ecf1; border: 1px solid #bee5eb; color: #0c5460; padding: 15px; border-radius: 4px; margin: 10px 0; }
        pre { background: #f4f4f4; padding: 15px; border-radius: 4px; overflow-x: auto; }
        .btn { background: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; display: inline-block; margin: 5px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🧪 Test Add Product API Connection</h1>
        
        <?php
        $host = 'localhost';
        $dbname = 'onlinebidding';
        $username = 'root';
        $password = '';
        
        try {
            // Test database connection
            echo '<h2>1. Database Connection Test:</h2>';
            $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            echo '<div class="success">✅ Database connection successful!</div>';
            
            // Check products table structure
            echo '<h2>2. Products Table Structure:</h2>';
            $stmt = $pdo->query("DESCRIBE products");
            $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $columnNames = array_column($columns, 'Field');
            
            echo '<div class="info">Table columns: ' . implode(', ', $columnNames) . '</div>';
            
            $hasCondition = in_array('condition', $columnNames);
            $hasSpecs = in_array('specs', $columnNames);
            
            if (!$hasCondition) {
                echo '<div class="error">❌ Missing: condition column</div>';
                echo '<p>Adding it now...</p>';
                try {
                    if ($hasSpecs) {
                        $pdo->exec("ALTER TABLE `products` ADD COLUMN `condition` VARCHAR(255) NULL AFTER `specs`");
                    } else {
                        $pdo->exec("ALTER TABLE `products` ADD COLUMN `condition` VARCHAR(255) NULL");
                    }
                    echo '<div class="success">✅ Condition column added successfully!</div>';
                    $hasCondition = true;
                } catch (PDOException $e) {
                    echo '<div class="error">❌ Failed to add condition column: ' . htmlspecialchars($e->getMessage()) . '</div>';
                }
            } else {
                echo '<div class="success">✅ Condition column exists</div>';
            }
            
            if (!$hasSpecs) {
                echo '<div class="error">❌ Missing: specs column</div>';
                echo '<p>Adding it now...</p>';
                try {
                    $pdo->exec("ALTER TABLE `products` ADD COLUMN specs TEXT NULL");
                    echo '<div class="success">✅ Specs column added successfully!</div>';
                } catch (PDOException $e) {
                    echo '<div class="error">❌ Failed to add specs column: ' . htmlspecialchars($e->getMessage()) . '</div>';
                }
            } else {
                echo '<div class="success">✅ Specs column exists</div>';
            }
            
            // Test API endpoint
            echo '<h2>3. Testing Add Product API:</h2>';
            echo '<div class="info">Testing API endpoint: <code>admin/products/add.php</code></div>';
            
            $testData = [
                'title' => 'Test Product ' . date('Y-m-d H:i:s'),
                'description' => 'Test Description',
                'category' => 'tablet',
                'image_url' => null,
                'specs' => ['test' => 'value'],
                'condition_label' => 'Excellent',
                'base_price' => 50000
            ];
            
            echo '<pre>Test Data: ' . json_encode($testData, JSON_PRETTY_PRINT) . '</pre>';
            
            // Simulate API call
            $ch = curl_init('http://localhost/onlinebidding/api/admin/products/add.php');
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($testData));
            curl_setopt($ch, CURLOPT_HTTPHEADER, [
                'Content-Type: application/json',
                'Content-Length: ' . strlen(json_encode($testData))
            ]);
            
            $response = curl_exec($ch);
            $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
            curl_close($ch);
            
            echo '<h3>API Response (HTTP ' . $httpCode . '):</h3>';
            
            if ($response) {
                $responseData = json_decode($response, true);
                if ($responseData) {
                    if (isset($responseData['success']) && $responseData['success']) {
                        echo '<div class="success">✅ API Test Successful!</div>';
                        echo '<pre>' . json_encode($responseData, JSON_PRETTY_PRINT) . '</pre>';
                    } else {
                        echo '<div class="error">❌ API Test Failed</div>';
                        echo '<pre>' . json_encode($responseData, JSON_PRETTY_PRINT) . '</pre>';
                    }
                } else {
                    echo '<div class="error">❌ Invalid JSON response</div>';
                    echo '<pre>' . htmlspecialchars($response) . '</pre>';
                }
            } else {
                echo '<div class="error">❌ No response from API</div>';
            }
            
            echo '<h2>4. Summary:</h2>';
            if ($hasCondition && $hasSpecs && isset($responseData['success']) && $responseData['success']) {
                echo '<div class="success"><h3>✅ All Tests Passed!</h3>';
                echo '<p>Your backend API is properly connected and ready to add products.</p></div>';
            } else {
                echo '<div class="info"><h3>⚠️ Some Issues Found</h3>';
                echo '<p>Please check the errors above and fix them.</p></div>';
            }
            
        } catch (PDOException $e) {
            echo '<div class="error">❌ Database Error: ' . htmlspecialchars($e->getMessage()) . '</div>';
        } catch (Exception $e) {
            echo '<div class="error">❌ Error: ' . htmlspecialchars($e->getMessage()) . '</div>';
        }
        ?>
        
        <hr>
        <p><a href="test_add.php" class="btn">🔄 Refresh Test</a></p>
        <p><small>This test verifies your backend API connection and database structure.</small></p>
    </div>
</body>
</html>

