<?php
/**
 * Fix users table - Add password column if missing
 * Access: http://localhost/onlinebidding/api/fix_users_password_column.php
 */

header('Content-Type: text/html; charset=utf-8');
?>
<!DOCTYPE html>
<html>
<head>
    <title>Fix Users Table - Add Password Column</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
        .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; border-bottom: 2px solid #4CAF50; padding-bottom: 10px; }
        .success { background: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 15px; border-radius: 4px; margin: 10px 0; }
        .error { background: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; padding: 15px; border-radius: 4px; margin: 10px 0; }
        .info { background: #d1ecf1; border: 1px solid #bee5eb; color: #0c5460; padding: 15px; border-radius: 4px; margin: 10px 0; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background: #4CAF50; color: white; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🔧 Fix Users Table - Add Password Column</h1>
        
        <?php
        $host = 'localhost';
        $dbname = 'onlinebidding';
        $username = 'root';
        $password = '';
        
        try {
            $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            
            echo '<div class="success">✅ Database connection successful!</div>';
            
            // Check users table structure
            echo '<h2>1. Checking Users Table Structure:</h2>';
            $stmt = $pdo->query("DESCRIBE users");
            $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $columnNames = array_column($columns, 'Field');
            
            echo '<table>';
            echo '<tr><th>Column</th><th>Type</th><th>Null</th><th>Key</th></tr>';
            foreach ($columns as $col) {
                echo '<tr>';
                echo '<td><strong>' . htmlspecialchars($col['Field']) . '</strong></td>';
                echo '<td>' . htmlspecialchars($col['Type']) . '</td>';
                echo '<td>' . htmlspecialchars($col['Null']) . '</td>';
                echo '<td>' . htmlspecialchars($col['Key']) . '</td>';
                echo '</tr>';
            }
            echo '</table>';
            
            // Check if password column exists
            $hasPassword = in_array('password', $columnNames);
            
            if (!$hasPassword) {
                echo '<div class="error">❌ Missing: password column</div>';
                echo '<p>Adding password column now...</p>';
                
                try {
                    // Add password column - try to add after email or at the end
                    if (in_array('email', $columnNames)) {
                        $pdo->exec("ALTER TABLE `users` ADD COLUMN `password` VARCHAR(255) NULL AFTER `email`");
                    } else {
                        $pdo->exec("ALTER TABLE `users` ADD COLUMN `password` VARCHAR(255) NULL");
                    }
                    echo '<div class="success">✅ Password column added successfully!</div>';
                    $hasPassword = true;
                    
                    // Refresh table structure
                    $stmt = $pdo->query("DESCRIBE users");
                    $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
                    
                    echo '<h2>2. Updated Table Structure:</h2>';
                    echo '<table>';
                    echo '<tr><th>Column</th><th>Type</th><th>Null</th><th>Key</th></tr>';
                    foreach ($columns as $col) {
                        $highlight = ($col['Field'] === 'password') ? 'style="background: #d4edda;"' : '';
                        echo '<tr ' . $highlight . '>';
                        echo '<td><strong>' . htmlspecialchars($col['Field']) . '</strong></td>';
                        echo '<td>' . htmlspecialchars($col['Type']) . '</td>';
                        echo '<td>' . htmlspecialchars($col['Null']) . '</td>';
                        echo '<td>' . htmlspecialchars($col['Key']) . '</td>';
                        echo '</tr>';
                    }
                    echo '</table>';
                    
                } catch (PDOException $e) {
                    echo '<div class="error">❌ Failed to add password column: ' . htmlspecialchars($e->getMessage()) . '</div>';
                }
            } else {
                echo '<div class="success">✅ Password column already exists!</div>';
            }
            
            if ($hasPassword) {
                echo '<div class="success"><h3>✅ Users table is ready!</h3>';
                echo '<p>You can now reset passwords successfully.</p></div>';
            }
            
        } catch (PDOException $e) {
            echo '<div class="error">❌ Database Error: ' . htmlspecialchars($e->getMessage()) . '</div>';
        }
        ?>
        
        <hr>
        <p><a href="fix_users_password_column.php" class="btn" style="background: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; display: inline-block;">🔄 Refresh</a></p>
    </div>
</body>
</html>

