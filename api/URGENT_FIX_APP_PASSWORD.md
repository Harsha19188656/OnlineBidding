# 🚨 URGENT: Gmail App Password Issue

## ❌ Current Status

**Error:** Gmail is rejecting your App Password
**Password:** `umgutxhfvfon` (12 chars) - **NOT WORKING**

---

## ✅ IMMEDIATE FIX (5 Minutes)

### 1. Generate New App Password

**Go to:** https://myaccount.google.com/apppasswords

**Steps:**
1. Sign in if needed
2. Select: **Mail** → **Other (Custom name)** → **Online Bidding**
3. Click **Generate**
4. **Copy the password** (16 characters)
5. **Remove all spaces!**

### 2. Update config_email.php

**File:** `api/config_email.php`

**Change line 20:**
```php
'smtp_password' => 'your-new-app-password-here', // Replace umgutxhfvfon
```

**Save the file**

### 3. Copy to Server

```powershell
Copy-Item "api\config_email.php" "C:\xampp\htdocs\onlinebidding\api\config_email.php" -Force
```

### 4. Test

```
http://localhost/onlinebidding/api/test_otp_direct.php?email=harsha168656@gmail.com
```

---

## 📋 Checklist

- [ ] 2-Step Verification enabled on Gmail
- [ ] New App Password generated (16 chars, no spaces)
- [ ] Updated `api/config_email.php`
- [ ] Copied to server
- [ ] Test email works
- [ ] OTP emails arriving

---

**Generate new App Password NOW:** https://myaccount.google.com/apppasswords

