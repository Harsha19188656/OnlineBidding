# ⚡ Quick Fix - Gmail Authentication Error

## 🚨 Problem
OTP is showing on screen but **NOT coming to email**.

**Error:** `SMTP Error: Could not authenticate.`

## ✅ Solution (3 Steps - 5 Minutes)

### Step 1: Generate Gmail App Password

1. **Go to:** https://myaccount.google.com/apppasswords
2. **Select:**
   - App: **Mail**
   - Device: **Other (Custom name)**
   - Name: **Online Bidding**
3. **Click:** Generate
4. **Copy the 16-character password** (remove spaces!)

### Step 2: Update config_email.php

1. **Open:** `C:\xampp\htdocs\onlinebidding\api\config_email.php`
2. **Find this line:**
   ```php
   'smtp_password' => 'wymmgnwhbcofgbvq',
   ```
3. **Replace with your new App Password:**
   ```php
   'smtp_password' => 'your-new-app-password-here', // No spaces!
   ```
4. **Save the file**

### Step 3: Test Email

**Open in browser:**
```
http://localhost/onlinebidding/api/test_email_direct.php
```

**Expected:**
- ✅ Email sent successfully!
- Check inbox: harsha168656@gmail.com

---

## ⚠️ Important

- ❌ **Don't use:** `harsha@123` (regular password won't work)
- ✅ **Use:** App Password (16 characters, no spaces)
- 🔐 **Enable 2-Step Verification first** (required for App Passwords)

---

## ✅ Done!

After updating the App Password, your OTP emails will be sent to: **harsha168656@gmail.com**

**Get App Password:** https://myaccount.google.com/apppasswords

