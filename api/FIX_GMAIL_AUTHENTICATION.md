# 🔧 FIX: Gmail Authentication Failed

## ❌ Problem Found

**Error:** `SMTP Error: Could not authenticate.`
**Gmail Response:** `535-5.7.8 Username and Password not accepted`

**Current App Password:** `umgutxhfvfon` (12 chars) - **REJECTED BY GMAIL**

---

## ✅ Solution: Generate New Gmail App Password

### Step 1: Enable 2-Step Verification (If Not Already)

1. Go to: https://myaccount.google.com/security
2. Find **"2-Step Verification"**
3. If not enabled, click **"Get Started"** and follow steps
4. **You MUST enable this first!**

### Step 2: Generate New App Password

1. Go to: https://myaccount.google.com/apppasswords
2. You may need to sign in again
3. Select:
   - **App:** Mail
   - **Device:** Other (Custom name)
   - **Name:** Online Bidding
4. Click **"Generate"**
5. **Copy the 16-character password** (e.g., `abcd efgh ijkl mnop`)
6. **Remove ALL spaces** (e.g., `abcdefghijklmnop`)

### Step 3: Update config_email.php

1. **Open:** `api/config_email.php`
2. **Find line 20:**
   ```php
   'smtp_password' => 'umgutxhfvfon',
   ```
3. **Replace with your new App Password:**
   ```php
   'smtp_password' => 'your-new-16-character-app-password-here', // No spaces!
   ```
4. **Save the file**

### Step 4: Copy to Server

```powershell
Copy-Item "api\config_email.php" "C:\xampp\htdocs\onlinebidding\api\config_email.php" -Force
```

### Step 5: Test

Open in browser:
```
http://localhost/onlinebidding/api/test_otp_direct.php?email=harsha168656@gmail.com
```

**Expected:**
- ✅ Email sent successfully!
- ✅ Check inbox: harsha168656@gmail.com

---

## 🔍 Why This Happened

Gmail App Passwords can expire or become invalid if:
- 2-Step Verification was disabled/re-enabled
- App Password was revoked
- Account security settings changed
- Too many failed login attempts

**Solution:** Generate a fresh App Password.

---

## ✅ After Fixing

1. **Test email:** `http://localhost/onlinebidding/api/test_otp_direct.php?email=harsha168656@gmail.com`
2. **Try from app:** Forgot Password → Enter email → Send OTP
3. **Check inbox:** OTP should arrive in 10-30 seconds

---

**The App Password is the issue - generate a new one and update config_email.php!**

