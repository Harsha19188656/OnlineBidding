# 📱 What to Do in Your App to See Backend Changes

Your app code is **already ready**! You just need to set up the database and make sure everything is connected.

## ✅ What's Already Done in Your App

- ✅ API endpoints are defined in `ApiService.kt`
- ✅ Retrofit is configured in `RetrofitInstance.kt`
- ✅ Screens are using the API calls
- ✅ Backend PHP files are deployed to XAMPP

## 🚀 Steps to See Changes in Your App

### Step 1: Create Database Tables ⚠️ **REQUIRED**

You **MUST** create the `auctions` and `bids` tables first!

1. **Open phpMyAdmin**: `http://localhost/phpmyadmin`
2. **Select** `onlinebidding` database (left sidebar)
3. Click **SQL** tab (top menu)
4. **Copy and paste** this SQL:

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

5. Click **Go** button
6. You should see "2 queries executed successfully"

**OR** use the file: Open `api/create_tables.sql` and copy its contents.

---

### Step 2: Start XAMPP ⚠️ **REQUIRED**

1. Open **XAMPP Control Panel**
2. Start **Apache** (should turn green)
3. Start **MySQL** (should turn green)

**Both must be running!**

---

### Step 3: Check Your BASE_URL ⚠️ **IMPORTANT**

Open: `app/src/main/java/com/example/onlinebidding/api/RetrofitInstance.kt`

Check line 20:
```kotlin
private const val BASE_URL = "http://10.148.199.81/onlinebidding/"
```

**Choose the correct URL:**

- **If using Android Emulator**: Change to:
  ```kotlin
  private const val BASE_URL = "http://10.0.2.2/onlinebidding/"
  ```

- **If using Physical Device** (same WiFi):
  - Find your computer's IP: Run `ipconfig` in Command Prompt
  - Look for "IPv4 Address" (e.g., `172.20.10.2`)
  - Update to: `"http://172.20.10.2/onlinebidding/"`

**After changing, rebuild your app!**

---

### Step 4: Test API in Browser (Optional but Recommended)

Open browser and go to:
```
http://localhost/onlinebidding/api/auctions/list.php
```

**Expected:** JSON response like:
```json
{"success":true,"items":[],"count":0,"total":0}
```

If you see this, the API is working! ✅

---

### Step 5: Create Test Data (Optional)

To see auctions in your app, create some test data:

**In phpMyAdmin SQL tab**, run:

```sql
-- Create a test product (if you don't have one)
INSERT INTO `products` (`title`, `description`, `category`, `base_price`) 
VALUES 
('MacBook Pro 16" M3 Max', 'Premium laptop', 'laptop', 200000.00);

-- Get the product ID (check in phpMyAdmin or use LAST_INSERT_ID())
-- Then create an auction (replace 1 with your actual product_id)
INSERT INTO `auctions` (`product_id`, `start_price`, `current_price`, `status`, `start_at`, `end_at`) 
VALUES 
(1, 150000.00, 150000.00, 'active', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY));
```

**Or use the INSERT statements in `api/create_tables.sql`** (uncomment them).

---

### Step 6: Build and Run Your App! 🎉

1. **Open Android Studio**
2. **Build** your project (Build → Make Project)
3. **Run** on emulator or device
4. **Navigate** to any auction list screen (Laptops, Mobiles, etc.)
5. **Check Logcat** for API responses:
   - Filter by: `Retrofit` or `ApiService`
   - You should see HTTP requests and responses

---

## 🎯 What You Should See

### If Everything Works:

✅ **Auction List Screens** should load data from database
✅ **Auction Detail Screen** should show product and bid info
✅ **Place Bid** button should work and update prices
✅ **Logcat** shows successful API calls

### If You See Errors:

❌ **"404 Not Found"**: 
- Check XAMPP Apache is running
- Verify BASE_URL is correct

❌ **"Database error"**:
- Check MySQL is running
- Verify tables were created (Step 1)

❌ **"No auctions found"**:
- Create test data (Step 5)
- Check that auctions have `status = 'active'`

❌ **"Network error"**:
- Check BASE_URL matches your setup
- For emulator: use `10.0.2.2`
- For device: use your computer's IP (same WiFi)

---

## 📋 Quick Checklist

Before running your app:

- [ ] Created `auctions` table in database
- [ ] Created `bids` table in database  
- [ ] XAMPP Apache is running (green)
- [ ] XAMPP MySQL is running (green)
- [ ] BASE_URL is correct in `RetrofitInstance.kt`
- [ ] Tested API in browser (optional)
- [ ] Created test data (optional)
- [ ] Rebuilt app after changing BASE_URL

---

## 🔍 How to Verify It's Working

1. **Check Logcat** (Android Studio):
   - Filter: `Retrofit`
   - You should see HTTP requests to your backend
   - Response should be JSON

2. **Check Browser**:
   - Go to: `http://localhost/onlinebidding/api/auctions/list.php`
   - Should return JSON

3. **Check Database**:
   - Open phpMyAdmin
   - Check `auctions` table has data
   - Check `bids` table (if you placed bids)

---

## 💡 Summary

**Your app is already configured!** You just need to:

1. ✅ Create database tables (Step 1)
2. ✅ Start XAMPP (Step 2)  
3. ✅ Set correct BASE_URL (Step 3)
4. ✅ Create test data (Step 5 - optional)
5. ✅ Run your app! (Step 6)

That's it! 🚀


