-- Alter password_reset_tokens table to ensure correct structure
-- Run this in phpMyAdmin or MySQL command line

USE onlinebidding;

-- Check if table exists, if not create it
CREATE TABLE IF NOT EXISTS password_reset_tokens (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Add missing columns if they don't exist
ALTER TABLE password_reset_tokens 
ADD COLUMN IF NOT EXISTS id INT AUTO_INCREMENT PRIMARY KEY FIRST,
ADD COLUMN IF NOT EXISTS user_id INT NOT NULL AFTER id,
ADD COLUMN IF NOT EXISTS email VARCHAR(255) NOT NULL AFTER user_id,
ADD COLUMN IF NOT EXISTS otp VARCHAR(6) NOT NULL AFTER email,
ADD COLUMN IF NOT EXISTS expires_at DATETIME NOT NULL AFTER otp,
ADD COLUMN IF NOT EXISTS used TINYINT(1) DEFAULT 0 AFTER expires_at,
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER used;

-- Add indexes if they don't exist
CREATE INDEX IF NOT EXISTS idx_email ON password_reset_tokens(email);
CREATE INDEX IF NOT EXISTS idx_otp ON password_reset_tokens(otp);
CREATE INDEX IF NOT EXISTS idx_expires ON password_reset_tokens(expires_at);
CREATE INDEX IF NOT EXISTS idx_user_id ON password_reset_tokens(user_id);

-- Verify table structure
DESCRIBE password_reset_tokens;

