# 🔧 Update Database Table Structure

## ✅ Two Ways to Update Table

### Option 1: Automatic Update (Recommended)

**Open in browser:**
```
http://localhost/onlinebidding/api/update_table_structure.php
```

**This will:**
- ✅ Create table if it doesn't exist
- ✅ Add missing columns automatically
- ✅ Add missing indexes automatically
- ✅ Show current structure
- ✅ Verify everything is correct

**No manual SQL needed!**

---

### Option 2: Manual SQL Update

**Open phpMyAdmin:**
1. Go to: http://localhost/phpmyadmin
2. Select database: `onlinebidding`
3. Click "SQL" tab
4. Copy and paste the SQL from: `alter_password_reset_tokens_table.sql`
5. Click "Go"

**Or run SQL file:**
```sql
-- Run this in phpMyAdmin or MySQL
USE onlinebidding;

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    email VARCHAR(255) NOT NULL,
    otp VARCHAR(6) NOT NULL,
    expires_at DATETIME NOT NULL,
    used TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_otp (otp),
    INDEX idx_expires (expires_at),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 📋 Required Table Structure

**Columns:**
- `id` - INT AUTO_INCREMENT PRIMARY KEY
- `user_id` - INT NOT NULL
- `email` - VARCHAR(255) NOT NULL
- `otp` - VARCHAR(6) NOT NULL
- `expires_at` - DATETIME NOT NULL
- `used` - TINYINT(1) DEFAULT 0
- `created_at` - TIMESTAMP DEFAULT CURRENT_TIMESTAMP

**Indexes:**
- `idx_email` on `email`
- `idx_otp` on `otp`
- `idx_expires` on `expires_at`
- `idx_user_id` on `user_id`

**Foreign Key:**
- `user_id` references `users(id)` ON DELETE CASCADE

---

## ✅ After Updating

1. **Verify structure:**
   ```
   http://localhost/onlinebidding/api/verify_table_structure.php
   ```
2. **Request new OTP** from app
3. **Test OTP verification**

---

## 🧪 Quick Test

After updating, test with:
```
http://localhost/onlinebidding/api/debug_otp_verification.php?email=harsha168656@gmail.com&otp=669122
```

---

**Use Option 1 (automatic) - it's easier and safer!**

