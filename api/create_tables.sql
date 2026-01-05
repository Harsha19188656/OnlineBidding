-- SQL Script to Create Required Database Tables
-- Run this in phpMyAdmin or MySQL command line
-- Make sure you're using the 'onlinebidding' database

USE onlinebidding;

-- Create auctions table if it doesn't exist
CREATE TABLE IF NOT EXISTS `auctions` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `product_id` INT NOT NULL,
  `start_price` DECIMAL(10,2) NOT NULL,
  `current_price` DECIMAL(10,2) NOT NULL,
  `status` VARCHAR(20) DEFAULT 'active' COMMENT 'active, ended, cancelled',
  `start_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `end_at` DATETIME NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE,
  INDEX `idx_status` (`status`),
  INDEX `idx_product` (`product_id`),
  INDEX `idx_end_at` (`end_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create bids table if it doesn't exist
CREATE TABLE IF NOT EXISTS `bids` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `auction_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`auction_id`) REFERENCES `auctions`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_auction` (`auction_id`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_created` (`created_at`),
  INDEX `idx_amount` (`amount` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Optional: Insert sample data for testing
-- Uncomment and modify as needed

/*
-- Sample Product
INSERT INTO `products` (`title`, `description`, `category`, `image_url`, `base_price`, `created_at`) 
VALUES 
('MacBook Pro 16" M3 Max', 'Premium laptop with Apple M3 Max chip, 48GB RAM, 1TB SSD', 'laptop', 'https://example.com/macbook.jpg', 200000.00, NOW())
ON DUPLICATE KEY UPDATE title=title;

-- Sample Auction (adjust product_id if needed)
SET @product_id = LAST_INSERT_ID();
INSERT INTO `auctions` (`product_id`, `start_price`, `current_price`, `status`, `start_at`, `end_at`) 
VALUES 
(@product_id, 150000.00, 150000.00, 'active', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY))
ON DUPLICATE KEY UPDATE product_id=product_id;
*/


