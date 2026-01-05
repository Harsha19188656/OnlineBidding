-- Fix products table - Add missing columns if they don't exist
-- Run this in phpMyAdmin or MySQL command line

USE onlinebidding;

-- Add specs column if it doesn't exist
ALTER TABLE products 
ADD COLUMN IF NOT EXISTS specs TEXT NULL;

-- Add condition column if it doesn't exist (condition is reserved word, so use backticks)
ALTER TABLE products 
ADD COLUMN IF NOT EXISTS `condition` VARCHAR(255) NULL;

-- Verify the table structure
DESCRIBE products;

