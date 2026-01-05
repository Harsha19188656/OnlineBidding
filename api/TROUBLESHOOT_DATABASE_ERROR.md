# 🔧 Fix "Database error. Please try again later."

You're getting this error because the database connection is failing. Here's how to fix it:

## ✅ Step 1: Check MySQL is Running

1. Open **XAMPP Control Panel**
2. Check **MySQL** - it should show **"Running"** (green)
3. If it says "Stopped", click **Start**

## ✅ Step 2: Check Database Exists

1. Open phpMyAdmin: `http://localhost/phpmyadmin`
2. Look in the left sidebar for **`onlinebidding`** database
3. If you don't see it, you need to create it first

### Create Database (if missing):
1. Click **"New"** in left sidebar
2. Database name: `onlinebidding`
3. Collation: `utf8mb4_general_ci`
4. Click **Create**

## ✅ Step 3: Create Required Tables

The error happens because `auctions` table doesn't exist!

1. In phpMyAdmin, select **`onlinebidding`** database
2. Click **SQL** tab
3. Copy and paste this:

```sql
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
```

4. Click **Go**
5. You should see: "2 queries executed successfully"

## ✅ Step 4: Check Database Credentials

The PHP files use these default XAMPP settings:
- Host: `localhost`
- Database: `onlinebidding`
- Username: `root`
- Password: `` (empty)

If your MySQL has a password, you need to update the PHP files.

## ✅ Step 5: Test Again

After creating tables, refresh:
```
http://localhost/onlinebidding/api/auctions/list.php
```

**Expected Result:**
```json
{"success":true,"items":[],"count":0,"total":0}
```

This means it's working! (Empty array is OK if you haven't added data yet)

## 🚨 Common Issues

### Error: "Table 'onlinebidding.auctions' doesn't exist"
→ **Solution:** Run Step 3 (Create Tables)

### Error: "Access denied for user 'root'@'localhost'"
→ **Solution:** Your MySQL has a password. Update PHP files with correct password.

### Error: "Unknown database 'onlinebidding'"
→ **Solution:** Run Step 2 (Create Database)

### MySQL won't start in XAMPP
→ **Solution:** 
- Check if port 3306 is being used by another program
- Check XAMPP error log
- Try stopping and starting again

## 💡 Quick Fix Summary

**Most likely issue:** Tables don't exist!

1. ✅ Make sure MySQL is running
2. ✅ Open phpMyAdmin
3. ✅ Select `onlinebidding` database
4. ✅ Run the CREATE TABLE SQL statements
5. ✅ Test the API again

That's it! 🚀


