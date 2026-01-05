<?php
/**
 * Verify and Fix password_reset_tokens Table Structure
 * Access: http://localhost/onlinebidding/api/verify_table_structure.php
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
    
    echo "<h1>🔍 Verify password_reset_tokens Table Structure</h1>";
    echo "<hr>";
    
    // Check if table exists
    $stmt = $pdo->query("SHOW TABLES LIKE 'password_reset_tokens'");
    $tableExists = $stmt->rowCount() > 0;
    
    if (!$tableExists) {
        echo "<h2 style='color:red;'>❌ Table does not exist!</h2>";
        echo "<p>Creating table...</p>";
        
        $pdo->exec("CREATE TABLE password_reset_tokens (
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
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        
        echo "<p style='color:green;'>✅ Table created successfully!</p>";
    } else {
        echo "<h2 style='color:green;'>✅ Table exists</h2>";
    }
    
    // Check table structure
    echo "<h2>Table Structure:</h2>";
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
    
    // Verify required columns
    $requiredColumns = ['id', 'user_id', 'email', 'otp', 'expires_at', 'used', 'created_at'];
    $existingColumns = array_column($columns, 'Field');
    $missingColumns = array_diff($requiredColumns, $existingColumns);
    
    if (count($missingColumns) > 0) {
        echo "<h2 style='color:red;'>❌ Missing Columns:</h2>";
        echo "<ul>";
        foreach ($missingColumns as $col) {
            echo "<li>$col</li>";
        }
        echo "</ul>";
        echo "<p>Please add these columns manually or recreate the table.</p>";
    } else {
        echo "<h2 style='color:green;'>✅ All required columns exist</h2>";
    }
    
    // Check indexes
    echo "<hr><h2>Indexes:</h2>";
    $stmt = $pdo->query("SHOW INDEXES FROM password_reset_tokens");
    $indexes = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (count($indexes) > 0) {
        echo "<table border='1' cellpadding='5' style='border-collapse:collapse;'>";
        echo "<tr style='background:#f0f0f0;'><th>Key Name</th><th>Column</th><th>Non Unique</th></tr>";
        foreach ($indexes as $idx) {
            echo "<tr>";
            echo "<td>{$idx['Key_name']}</td>";
            echo "<td>{$idx['Column_name']}</td>";
            echo "<td>" . ($idx['Non_unique'] ? 'YES' : 'NO') . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    }
    
    // Test OTP insertion
    echo "<hr><h2>Test OTP Insertion:</h2>";
    $testEmail = 'harsha168656@gmail.com';
    $testOtp = '123456';
    $testExpires = date('Y-m-d H:i:s', strtotime('+5 minutes'));
    
    // Check if user exists
    $stmt = $pdo->prepare("SELECT id FROM users WHERE email = ?");
    $stmt->execute([$testEmail]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($user) {
        echo "<p>✅ User found: ID {$user['id']}</p>";
        
        // Try to insert test OTP
        try {
            $stmt = $pdo->prepare("INSERT INTO password_reset_tokens (user_id, email, otp, expires_at) VALUES (?, ?, ?, ?)");
            $stmt->execute([$user['id'], $testEmail, $testOtp, $testExpires]);
            echo "<p style='color:green;'>✅ Test OTP inserted successfully!</p>";
            
            // Try to retrieve it
            $stmt = $pdo->prepare("SELECT * FROM password_reset_tokens WHERE email = ? AND otp = ?");
            $stmt->execute([$testEmail, $testOtp]);
            $retrieved = $stmt->fetch(PDO::FETCH_ASSOC);
            
            if ($retrieved) {
                echo "<p style='color:green;'>✅ Test OTP retrieved successfully!</p>";
                echo "<pre>";
                print_r($retrieved);
                echo "</pre>";
                
                // Clean up test OTP
                $stmt = $pdo->prepare("DELETE FROM password_reset_tokens WHERE email = ? AND otp = ?");
                $stmt->execute([$testEmail, $testOtp]);
                echo "<p>🧹 Test OTP cleaned up</p>";
            } else {
                echo "<p style='color:red;'>❌ Test OTP could not be retrieved!</p>";
            }
        } catch (PDOException $e) {
            echo "<p style='color:red;'>❌ Error inserting test OTP: {$e->getMessage()}</p>";
        }
    } else {
        echo "<p style='color:orange;'>⚠️ User not found: $testEmail</p>";
        echo "<p>Cannot test OTP insertion without user.</p>";
    }
    
    // Show recent OTPs
    echo "<hr><h2>Recent OTPs (Last 5):</h2>";
    $stmt = $pdo->query("SELECT * FROM password_reset_tokens ORDER BY created_at DESC LIMIT 5");
    $recentOtps = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (count($recentOtps) > 0) {
        echo "<table border='1' cellpadding='5' style='border-collapse:collapse;'>";
        echo "<tr style='background:#f0f0f0;'>";
        echo "<th>ID</th><th>Email</th><th>OTP</th><th>Used</th><th>Expires At</th><th>Created At</th>";
        echo "</tr>";
        foreach ($recentOtps as $otp) {
            echo "<tr>";
            echo "<td>{$otp['id']}</td>";
            echo "<td>{$otp['email']}</td>";
            echo "<td><strong>{$otp['otp']}</strong></td>";
            echo "<td>" . ($otp['used'] ? 'YES' : 'NO') . "</td>";
            echo "<td>{$otp['expires_at']}</td>";
            echo "<td>{$otp['created_at']}</td>";
            echo "</tr>";
        }
        echo "</table>";
    } else {
        echo "<p>No OTPs in database.</p>";
    }
    
    echo "<hr><h2 style='color:green;'>✅ Table Structure Verification Complete</h2>";
    
} catch (PDOException $e) {
    echo "<h2 style='color:red;'>❌ Database Error</h2>";
    echo "<p>{$e->getMessage()}</p>";
}
?>

