-- Create payments table for storing payment records
-- Run this in phpMyAdmin or MySQL command line

USE onlinebidding;

CREATE TABLE IF NOT EXISTS `payments` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `auction_id` INT NULL COMMENT 'NULL for credit purchases, set for auction payments',
  `amount` DECIMAL(10,2) NOT NULL,
  `payment_method` VARCHAR(50) NOT NULL COMMENT 'phonepe, gpay, paytm, etc.',
  `upi_id` VARCHAR(255) NOT NULL,
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT 'pending, completed, failed, cancelled',
  `transaction_id` VARCHAR(255) NULL COMMENT 'Payment gateway transaction ID',
  `payment_date` TIMESTAMP NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`auction_id`) REFERENCES `auctions`(`id`) ON DELETE SET NULL,
  INDEX `idx_user` (`user_id`),
  INDEX `idx_auction` (`auction_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

