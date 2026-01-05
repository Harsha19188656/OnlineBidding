# 🔐 Fix Gmail Authentication Error

## ⚠️ Current Error

```
SMTP Error: Could not authenticate.
```

This means your Gmail App Password is **incorrect or expired**.

---

## ✅ Solution: Generate New Gmail App Password

### Step 1: Enable 2-Step Verification (If Not Already Enabled)

1. Go to: https://myaccount.google.com/security
2. Find **"2-Step Verification"**
3. Click **"Get Started"** and follow steps
4. **You MUST enable this first!**

### Step 2: Generate App Password

1. Go to: https://myaccount.google.com/apppasswords
2. You'll see a dropdown - select:
   - **App:** Mail
   - **Device:** Other (Custom name)
   - Enter name: **"Online Bidding"**
3. Click **"Generate"**
4. **Copy the 16-character password** (looks like: `abcd efgh ijkl mnop`)
5. **Remove all spaces** when using it (e.g., `abcdefghijklmnop`)

### Step 3: Update config_email.php

1. Open: `C:\xampp\htdocs\onlinebidding\api\config_email.php`
2. Update the password line:
   ```php
   'smtp_password' => 'your-new-16-character-app-password-here', // No spaces!
   ```
3. Save the file

### Step 4: Test Email

Open in browser:
```
http://localhost/onlinebidding/api/test_email_direct.php
```

**You should see:**
- ✅ Email sent successfully!
- Check inbox: harsha168656@gmail.com

---

## ❌ Why Regular Password Doesn't Work

- **Gmail blocks regular passwords** (`harsha@123`) for security
- **Only App Passwords work** with SMTP
- App Passwords are 16 characters (no spaces)
- You can revoke them anytime

---

## 🔍 Current Status

- ✅ PHPMailer installed
- ✅ Code working
- ❌ **Gmail authentication failing** (need new App Password)

---

## ✅ After Getting App Password

1. Update `config_email.php` with new password
2. Test: `http://localhost/onlinebidding/api/test_email_direct.php`
3. Try forgot password in app - emails will work!

---

**Generate App Password here:** https://myaccount.google.com/apppasswords

