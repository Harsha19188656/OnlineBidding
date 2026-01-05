# 🚀 How to See Changes in Your App

Follow these steps to deploy and test your new backend APIs:

## Step 1: Deploy PHP Files to XAMPP

1. **Open Command Prompt** and navigate to your project's `api` folder:
   ```bash
   cd C:\Users\ADMIN\AndroidStudioProjects\onlinebidding2\api
   ```

2. **Run the copy script**:
   ```bash
   copy_to_xampp.bat
   ```

   This will automatically:
   - Create necessary directories in XAMPP
   - Copy all PHP files to `C:\xampp\htdocs\onlinebidding\api\`

3. **Verify files were copied**:
   Check that these directories exist:
   - `C:\xampp\htdocs\onlinebidding\api\auctions\`
   - `C:\xampp\htdocs\onlinebidding\api\bids\`
   - `C:\xampp\htdocs\onlinebidding\api\admin\products\`

## Step 2: Start XAMPP Services

1. **Open XAMPP Control Panel**
2. **Start Apache** (click "Start" - should turn green)
3. **Start MySQL** (click "Start" - should turn green)

## Step 3: Verify Database Tables Exist

1. **Open phpMyAdmin**: `http://localhost/phpmyadmin`
2. **Select `onlinebidding` database**
3. **Check these tables exist**:
   - ✅ `users` (should already exist)
   - ✅ `products` (should already exist)
   - ❓ `auctions` - **Need to create if missing**
   - ❓ `bids` - **Need to create if missing**

### If `auctions` table doesn't exist, run this SQL:

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
```

### If `bids` table doesn't exist, run this SQL:

```sql
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
  INDEX `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## Step 4: Test API Endpoints in Browser

### Test Auction List Endpoint:
Open browser and go to:
```
http://localhost/onlinebidding/api/auctions/list.php
```

**Expected:** JSON response (even if empty array is OK)

### Test with Category Filter:
```
http://localhost/onlinebidding/api/auctions/list.php?category=laptop
```

### Test Auction Details:
```
http://localhost/onlinebidding/api/auctions/details.php?id=1
```

**Note:** You'll get "Auction not found" if no auctions exist yet - that's OK!

## Step 5: Create Test Data (Optional but Recommended)

To see data in your app, create a test auction. Run this SQL in phpMyAdmin:

```sql
-- First, make sure you have at least one product
-- If not, create one:
INSERT INTO `products` (`title`, `description`, `category`, `image_url`, `base_price`, `created_at`) 
VALUES 
('MacBook Pro 16" M3 Max', 'Premium laptop with Apple M3 Max chip', 'laptop', 'https://example.com/image.jpg', 200000.00, NOW());

-- Get the product_id (let's assume it's 1, or check the last insert ID)
-- Then create an auction:
INSERT INTO `auctions` (`product_id`, `start_price`, `current_price`, `status`, `start_at`, `end_at`) 
VALUES 
(LAST_INSERT_ID(), 150000.00, 150000.00, 'active', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY));
```

Or use phpMyAdmin interface to insert data manually.

## Step 6: Check Your App's Base URL

1. **Open**: `app/src/main/java/com/example/onlinebidding/api/RetrofitInstance.kt`

2. **Check the BASE_URL**:
   ```kotlin
   private const val BASE_URL = "http://10.148.199.81/onlinebidding/"
   ```

3. **Choose the correct URL based on your setup**:
   - **Android Emulator**: Use `"http://10.0.2.2/onlinebidding/"`
   - **Physical Device (same WiFi)**: Use your computer's IP (e.g., `"http://172.20.10.2/onlinebidding/"`)
   
   **To find your IP**:
   - Open Command Prompt
   - Type: `ipconfig`
   - Look for "IPv4 Address" under your active network adapter

4. **Update if needed** and rebuild the app.

## Step 7: Run Your Android App

1. **Build and Run** your Android app in Android Studio
2. **Navigate to auction screens** (Laptop List, Mobile List, etc.)
3. **Check Logcat** for API responses:
   - Filter by "Retrofit" or "ApiService"
   - You should see HTTP requests and responses

## Step 8: Test Features in App

### Test Auction Listing:
- Go to any device list screen (Laptops, Mobiles, etc.)
- Should see auctions loaded from database

### Test Auction Details:
- Click on any auction item
- Should show product details, current price, and bid history

### Test Placing Bid:
- Open an auction detail screen
- Click "Place Bid"
- Enter an amount higher than current price
- Should update the current price

## Troubleshooting

### ❌ "404 Not Found" or "Endpoint not found"
- ✅ Check XAMPP Apache is running
- ✅ Verify files were copied to correct location
- ✅ Check file permissions

### ❌ "Database error" or "Connection failed"
- ✅ Check MySQL is running in XAMPP
- ✅ Verify database name is `onlinebidding`
- ✅ Check username is `root` and password is empty (default XAMPP)

### ❌ "No auctions found" (but tables exist)
- ✅ Create test data using SQL above
- ✅ Check that auctions have `status = 'active'`

### ❌ App can't connect to backend
- ✅ Check BASE_URL in `RetrofitInstance.kt`
- ✅ For emulator: use `10.0.2.2`
- ✅ For physical device: use your computer's IP (same WiFi network)
- ✅ Check phone/emulator can ping the server

### ❌ "Network error" in Logcat
- ✅ Check XAMPP Apache is running
- ✅ Check firewall isn't blocking port 80
- ✅ Verify BASE_URL is correct
- ✅ Try accessing endpoint in browser first

## Quick Test Checklist

- [ ] PHP files copied to XAMPP
- [ ] Apache is running (green in XAMPP)
- [ ] MySQL is running (green in XAMPP)
- [ ] `auctions` table exists
- [ ] `bids` table exists
- [ ] Test endpoint works in browser: `http://localhost/onlinebidding/api/auctions/list.php`
- [ ] BASE_URL is correct in `RetrofitInstance.kt`
- [ ] App rebuilt after BASE_URL change
- [ ] Test data created (at least 1 product and 1 auction)

## Next Steps

Once everything is working:
1. ✅ You can list auctions by category
2. ✅ You can view auction details
3. ✅ You can place bids
4. ✅ Admin can manage products

For notifications, payments, and sessions - these can be added later if needed!


