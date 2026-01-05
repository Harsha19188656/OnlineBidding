# ✅ SQL Syntax Error Fixed

## ❌ Error Found

```
SQLSTATE[42000]: Syntax error or access violation: 1064 
You have an error in your SQL syntax; check the manual that corresponds to your MariaDB server version 
for the right syntax to use near 'current_time' at line 1
```

## 🔧 Issue

**Problem:** `current_time` is a **reserved keyword** in MySQL/MariaDB  
**Location:** `forgot-password.php` line 82

## ✅ Fix Applied

**Changed:**
```php
// BEFORE (Error)
SELECT NOW() as current_time

// AFTER (Fixed)
SELECT NOW() as db_now
```

**Also simplified:**
- Removed unnecessary time query
- Directly get expiration time from database
- Use `db_now` instead of `current_time` for logging

---

## ✅ Files Updated

1. ✅ `forgot-password.php` - Fixed SQL syntax error
2. ✅ Copied to server

---

## 🧪 Test Now

1. **Request OTP** from app
2. **Should work** without server error
3. **Check email** for OTP
4. **Enter OTP** within 5 minutes
5. **Reset password**

---

**The SQL syntax error is fixed! Try requesting OTP again.**

