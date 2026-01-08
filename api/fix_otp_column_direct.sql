-- Fix OTP Column Size - Direct SQL
-- Run this in phpMyAdmin or MySQL command line

USE onlinebidding;

-- Check current column size
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    CHARACTER_MAXIMUM_LENGTH, 
    COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'onlinebidding'
AND TABLE_NAME = 'password_reset_tokens'
AND COLUMN_NAME = 'otp';

-- Alter column to VARCHAR(255) to store reset tokens (64 chars)
ALTER TABLE password_reset_tokens 
MODIFY COLUMN otp VARCHAR(255) NOT NULL;

-- Verify the change
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    CHARACTER_MAXIMUM_LENGTH, 
    COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'onlinebidding'
AND TABLE_NAME = 'password_reset_tokens'
AND COLUMN_NAME = 'otp';

-- Check recent tokens
SELECT 
    id,
    email,
    LEFT(otp, 20) as otp_preview,
    LENGTH(otp) as otp_length,
    used,
    expires_at,
    created_at
FROM password_reset_tokens
WHERE email = 'harsha168656@gmail.com'
ORDER BY created_at DESC
LIMIT 5;



