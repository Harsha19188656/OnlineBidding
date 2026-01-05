# 🔧 Fix: "Failed to process payment" Error

## ❌ Common Causes

The error "Failed to process payment" usually happens because:

1. **Payments table doesn't exist** - Most common issue!
2. **Database connection error**
3. **User ID doesn't exist**
4. **Amount validation fails**
5. **Network/API connection issue**

---

## ✅ Step 1: Create Payments Table

**This is the most likely issue!** The payments table needs to exist first.

### In phpMyAdmin:

1. Open: `http://localhost/phpmyadmin`
2. Select `onlinebidding` database
3. Click **SQL** tab
4. Copy and paste this:

```sql
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
```

5. Click **Go**
6. You should see: "1 query executed successfully"

---

## ✅ Step 2: Verify Table Was Created

Run this query:
```sql
SHOW TABLES LIKE 'payments';
```

You should see the `payments` table listed.

---

## ✅ Step 3: Check User ID Exists

Make sure user ID 25 exists:
```sql
SELECT id, name FROM users WHERE id = 25;
```

If it doesn't exist, create it or use a different user ID.

---

## 🧪 Step 4: Test Payment API Directly

Test the API to see the exact error:

```powershell
$body = @{
    user_id = 25
    amount = 70
    payment_method = "phonepe"
    upi_id = "harsha@paytm"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost/onlinebidding/api/payments/create.php" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body
    
    Write-Host "✅ SUCCESS!" -ForegroundColor Green
    $response | ConvertTo-Json
} catch {
    Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response: $responseBody" -ForegroundColor Red
    }
}
```

---

## 🔍 Step 5: Check PHP Error Log

Check the PHP error log for detailed error messages:

**Location:** `C:\xampp\apache\logs\error.log`

Look for:
- `PAYMENT REQUEST: {...}` - Shows what was received
- `PAYMENT INSERTED: ...` - Payment was saved
- `Database error: ...` - Database issue
- `Payments table not found` - Table doesn't exist

---

## ✅ Step 6: Check Android Logcat

In Android Studio Logcat, filter by `UPIEntry`:

Look for:
- `✅ Payment saved: TXN...` - Success
- `❌ Payment failed: [error]` - Shows exact error
- `❌ HTTP Error XXX: [error]` - HTTP error
- `❌ Network error: [message]` - Network issue

---

## 🎯 Quick Fix Checklist

- [ ] Payments table exists (run `SHOW TABLES LIKE 'payments';`)
- [ ] User ID 25 exists (run `SELECT id FROM users WHERE id = 25;`)
- [ ] MySQL is running in XAMPP
- [ ] Apache is running in XAMPP
- [ ] API file exists: `C:\xampp\htdocs\onlinebidding\api\payments\create.php`
- [ ] Tested API directly with PowerShell
- [ ] Checked PHP error log
- [ ] Checked Android Logcat

---

## 💡 Most Common Fix

**90% of the time, the issue is:** The payments table doesn't exist!

**Solution:** Run the SQL from Step 1 above.

---

## 📋 Valid UPI ID Formats

Your UPI ID should be in this format:
- ✅ `harsha@paytm`
- ✅ `john@ybl`
- ✅ `jane@upi`
- ✅ `user123@paytm`

**Format:** `[username]@[provider]`

---

## ✅ After Fixing

Once the table is created, try again:
1. Enter UPI ID (e.g., `harsha@paytm`)
2. Click "Proceed to Pay"
3. Payment should save successfully
4. Check database: `SELECT * FROM payments ORDER BY id DESC;`

---

## 🎯 Still Not Working?

1. **Share the exact error message** from Logcat
2. **Share the API test response** from PowerShell
3. **Check if payments table exists** in phpMyAdmin
4. **Verify MySQL is running** in XAMPP

