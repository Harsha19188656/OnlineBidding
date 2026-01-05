<?php
/**
 * Update password_reset_tokens Table Structure
 * Access: http://localhost/onlinebidding/api/update_table_structure.php
 */

header('Content-Type: text/html; charset=utf-8');
error_reporting(E_ALL);
ini_set('display_errors', 1);

$host = 'localhost';
$dbname = 'onlinebidding';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    echo "<h1>🔧 Update password_reset_tokens Table Structure</h1>";
    echo "<hr>";
    
    // Create table if it doesn't exist
    echo "<h2>1. Creating table if it doesn't exist...</h2>";
    $pdo->exec("CREATE TABLE IF NOT EXISTS password_reset_tokens (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        email VARCHAR(255) NOT NULL,
        otp VARCHAR(6) NOT NULL,
        expires_at DATETIME NOT NULL,
        used TINYINT(1) DEFAULT 0,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        INDEX idx_email (email),
        INDEX idx_otp (otp),
        INDEX idx_expires (expires_at),
        INDEX idx_user_id (user_id),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
    echo "<p style='color:green;'>✅ Table created/verified</p>";
    
    // Check current structure
    echo "<hr><h2>2. Current Table Structure:</h2>";
    $stmt = $pdo->query("DESCRIBE password_reset_tokens");
    $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<table border='1' cellpadding='8' style='border-collapse:collapse;'>";
    echo "<tr style='background:#f0f0f0;'><th>Field</th><th>Type</th><th>Null</th><th>Key</th><th>Default</th><th>Extra</th></tr>";
    foreach ($columns as $col) {
        echo "<tr>";
        echo "<td><strong>{$col['Field']}</strong></td>";
        echo "<td>{$col['Type']}</td>";
        echo "<td>{$col['Null']}</td>";
        echo "<td>{$col['Key']}</td>";
        echo "<td>{$col['Default']}</td>";
        echo "<td>{$col['Extra']}</td>";
        echo "</tr>";
    }
    echo "</table>";
    
    // Add missing columns
    echo "<hr><h2>3. Adding missing columns...</h2>";
    $requiredColumns = [
        'id' => 'INT AUTO_INCREMENT PRIMARY KEY',
        'user_id' => 'INT NOT NULL',
        'email' => 'VARCHAR(255) NOT NULL',
        'otp' => 'VARCHAR(6) NOT NULL',
        'expires_at' => 'DATETIME NOT NULL',
        'used' => 'TINYINT(1) DEFAULT 0',
        'created_at' => 'TIMESTAMP DEFAULT CURRENT_TIMESTAMP'
    ];
    
    $existingColumns = array_column($columns, 'Field');
    $added = 0;
    
    foreach ($requiredColumns as $colName => $colDef) {
        if (!in_array($colName, $existingColumns)) {
            try {
                if ($colName == 'id') {
                    // Skip ID as it's primary key
                    continue;
                }
                $position = 'AFTER ' . ($colName == 'user_id' ? 'id' : ($colName == 'email' ? 'user_id' : ($colName == 'otp' ? 'email' : ($colName == 'expires_at' ? 'otp' : ($colName == 'used' ? 'expires_at' : 'used')))));
                $pdo->exec("ALTER TABLE password_reset_tokens ADD COLUMN $colName $colDef $position");
                echo "<p style='color:green;'>✅ Added column: $colName</p>";
                $added++;
            } catch (PDOException $e) {
                echo "<p style='color:orange;'>⚠️ Column $colName: {$e->getMessage()}</p>";
            }
        } else {
            echo "<p style='color:blue;'>ℹ️ Column $colName already exists</p>";
        }
    }
    
    if ($added == 0) {
        echo "<p style='color:green;'>✅ All required columns exist</p>";
    }
    
    // Add indexes
    echo "<hr><h2>4. Adding indexes...</h2>";
    $indexes = [
        'idx_email' => 'email',
        'idx_otp' => 'otp',
        'idx_expires' => 'expires_at',
        'idx_user_id' => 'user_id'
    ];
    
    // Check existing indexes
    $stmt = $pdo->query("SHOW INDEXES FROM password_reset_tokens");
    $existingIndexes = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $existingIndexNames = array_unique(array_column($existingIndexes, 'Key_name'));
    
    foreach ($indexes as $indexName => $column) {
        if (!in_array($indexName, $existingIndexNames)) {
            try {
                $pdo->exec("CREATE INDEX $indexName ON password_reset_tokens($column)");
                echo "<p style='color:green;'>✅ Added index: $indexName on $column</p>";
            } catch (PDOException $e) {
                echo "<p style='color:orange;'>⚠️ Index $indexName: {$e->getMessage()}</p>";
            }
        } else {
            echo "<p style='color:blue;'>ℹ️ Index $indexName already exists</p>";
        }
    }
    
    // Verify final structure
    echo "<hr><h2>5. Final Table Structure:</h2>";
    $stmt = $pdo->query("DESCRIBE password_reset_tokens");
    $finalColumns = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<table border='1' cellpadding='8' style='border-collapse:collapse;'>";
    echo "<tr style='background:#f0f0f0;'><th>Field</th><th>Type</th><th>Null</th><th>Key</th><th>Default</th><th>Extra</th></tr>";
    foreach ($finalColumns as $col) {
        echo "<tr>";
        echo "<td><strong>{$col['Field']}</strong></td>";
        echo "<td>{$col['Type']}</td>";
        echo "<td>{$col['Null']}</td>";
        echo "<td>{$col['Key']}</td>";
        echo "<td>{$col['Default']}</td>";
        echo "<td>{$col['Extra']}</td>";
        echo "</tr>";
    }
    echo "</table>";
    
    // Show indexes
    echo "<hr><h2>6. Indexes:</h2>";
    $stmt = $pdo->query("SHOW INDEXES FROM password_reset_tokens");
    $finalIndexes = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (count($finalIndexes) > 0) {
        echo "<table border='1' cellpadding='5' style='border-collapse:collapse;'>";
        echo "<tr style='background:#f0f0f0;'><th>Key Name</th><th>Column</th><th>Non Unique</th></tr>";
        foreach ($finalIndexes as $idx) {
            echo "<tr>";
            echo "<td>{$idx['Key_name']}</td>";
            echo "<td>{$idx['Column_name']}</td>";
            echo "<td>" . ($idx['Non_unique'] ? 'YES' : 'NO') . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    }
    
    echo "<hr><h2 style='color:green;'>✅ Table Structure Update Complete!</h2>";
    echo "<p><strong>Next steps:</strong></p>";
    echo "<ul>";
    echo "<li>Request a new OTP from the app</li>";
    echo "<li>Test OTP verification</li>";
    echo "</ul>";
    
} catch (PDOException $e) {
    echo "<h2 style='color:red;'>❌ Database Error</h2>";
    echo "<p>{$e->getMessage()}</p>";
}
?>

