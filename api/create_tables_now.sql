USE onlinebidding;

CREATE TABLE IF NOT EXISTS `auctions` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `product_id` INT NOT NULL,
  `start_price` DECIMAL(10,2) NOT NULL,
  `current_price` DECIMAL(10,2) NOT NULL,
  `status` VARCHAR(20) DEFAULT 'active',
  `start_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `end_at` DATETIME NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE,
  INDEX `idx_status` (`status`),
  INDEX `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `bids` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `auction_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`auction_id`) REFERENCES `auctions`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_auction` (`auction_id`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


