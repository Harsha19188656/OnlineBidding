<?php
/**
 * Script to update admin password to admin@123
 * Run this once, then delete it for security
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
    
    // Admin email
    $adminEmail = 'admin@gmail.com';
    $newPassword = 'admin@123';
    
    // Hash the new password using the same method as login.php (password_verify)
    $hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);
    
    // Check which column exists: password_hash or password
    $stmt = $pdo->query("DESCRIBE users");
    $columns = array_column($stmt->fetchAll(PDO::FETCH_ASSOC), 'Field');
    
    $passwordColumn = 'password_hash'; // Default to password_hash (as used in login.php)
    if (!in_array('password_hash', $columns) && in_array('password', $columns)) {
        $passwordColumn = 'password';
    } elseif (!in_array('password_hash', $columns) && !in_array('password', $columns)) {
        // Neither exists, create password_hash column
        $pdo->exec("ALTER TABLE `users` ADD COLUMN `password_hash` VARCHAR(255) NULL");
        $passwordColumn = 'password_hash';
        error_log("✅ Created 'password_hash' column in users table");
    }
    
    // Update admin password using the correct column name
    $stmt = $pdo->prepare("UPDATE users SET `$passwordColumn` = ? WHERE email = ?");
    $stmt->execute([$hashedPassword, $adminEmail]);
    
    $rowsAffected = $stmt->rowCount();
    
    if ($rowsAffected > 0) {
        echo "✅ Admin password updated successfully!\n";
        echo "Email: $adminEmail\n";
        echo "New password: $newPassword\n";
        echo "\n⚠️ Please delete this file after use for security.\n";
    } else {
        echo "⚠️ No rows updated. Admin user with email '$adminEmail' may not exist.\n";
        echo "Please verify the email address in the database.\n";
    }
    
} catch (PDOException $e) {
    echo "❌ Database error: " . $e->getMessage() . "\n";
} catch (Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "\n";
}
?>

