# 🔧 FIX Gmail App Password - Step by Step

## ❌ Current Error
```
535-5.7.8 Username and Password not accepted
SMTP Error: Could not authenticate
```

**Current Password:** `umgutxhfvfon` - **REJECTED BY GMAIL**

---

## ✅ SOLUTION (Follow These Steps Exactly)

### Step 1: Verify 2-Step Verification is Enabled

1. Go to: https://myaccount.google.com/security
2. Scroll to **"2-Step Verification"**
3. If it says **"Off"**, click it and enable it
4. **You MUST have 2-Step Verification enabled to use App Passwords!**

### Step 2: Generate New App Password

1. Go to: https://myaccount.google.com/apppasswords
   - If you see "App passwords aren't available", enable 2-Step Verification first
2. You may need to sign in again
3. At the bottom, under **"Select app"**, choose: **Mail**
4. Under **"Select device"**, choose: **Other (Custom name)**
5. Type: **Online Bidding**
6. Click **"Generate"**
7. **A 16-character password will appear** (e.g., `abcd efgh ijkl mnop`)
8. **Copy it immediately** (you can't see it again!)
9. **Remove ALL spaces** (e.g., `abcdefghijklmnop`)

### Step 3: Update config_email.php

1. **Open:** `api/config_email.php` in your IDE
2. **Find line 20:**
   ```php
   'smtp_password' => 'umgutxhfvfon',
   ```
3. **Replace with your new App Password (NO SPACES!):**
   ```php
   'smtp_password' => 'abcdefghijklmnop', // Your new 16-char App Password
   ```
4. **Save the file** (Ctrl+S)

### Step 4: Copy to Server

Run this in PowerShell:
```powershell
Copy-Item "api\config_email.php" "C:\xampp\htdocs\onlinebidding\api\config_email.php" -Force
```

Or manually copy:
- **From:** `C:\Users\ADMIN\AndroidStudioProjects\onlinebidding2\api\config_email.php`
- **To:** `C:\xampp\htdocs\onlinebidding\api\config_email.php`

### Step 5: Test Again

Open in browser:
```
http://localhost/onlinebidding/api/test_otp_direct.php?email=harsha168656@gmail.com
```

**Expected Result:**
- ✅ Email sent successfully!
- ✅ Check inbox: harsha168656@gmail.com
- ✅ OTP should be in the email

---

## 🔍 Troubleshooting

### If "App passwords aren't available":
- Enable 2-Step Verification first: https://myaccount.google.com/security

### If password still doesn't work:
1. Make sure you removed ALL spaces from the App Password
2. Make sure it's exactly 16 characters (no more, no less)
3. Try generating a new one
4. Make sure you copied it to the server location

### If email goes to spam:
- Check spam/junk folder
- Mark as "Not Spam"
- Add sender to contacts

---

## ✅ Verification Checklist

- [ ] 2-Step Verification enabled on Gmail
- [ ] New App Password generated (16 chars)
- [ ] All spaces removed from password
- [ ] Updated `api/config_email.php` (line 20)
- [ ] Copied to server: `C:\xampp\htdocs\onlinebidding\api\config_email.php`
- [ ] Test email works: `test_otp_direct.php`
- [ ] OTP emails arriving in inbox

---

**Generate New App Password:** https://myaccount.google.com/apppasswords

