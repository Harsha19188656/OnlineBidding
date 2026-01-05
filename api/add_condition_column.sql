-- Add condition column to products table
-- Run this in phpMyAdmin SQL tab

USE onlinebidding;

-- Add condition column (using backticks because condition is a reserved word)
ALTER TABLE `products` 
ADD COLUMN `condition` VARCHAR(255) NULL 
AFTER `specs`;

-- Verify the column was added
DESCRIBE `products`;

