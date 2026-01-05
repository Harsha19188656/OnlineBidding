# 🚀 Quick Email Setup - Get Emails Working NOW!

## ⚠️ IMPORTANT: You MUST Download PHPMailer First!

The code is ready, but **PHPMailer library is missing**. Follow these steps:

---

## Step 1: Download PHPMailer (REQUIRED)

### Option A: Direct Download Link

1. **Download PHPMailer:**
   - Go to: https://github.com/PHPMailer/PHPMailer/archive/refs/tags/v6.9.0.zip
   - Or search: "PHPMailer release" on GitHub

2. **Extract the ZIP:**
   - Extract `PHPMailer-6.9.0.zip`
   - You'll see a folder: `PHPMailer-6.9.0`

3. **Copy to API folder:**
   - Copy the `PHPMailer-6.9.0` folder
   - Paste it here: `C:\xampp\htdocs\onlinebidding\api\`
   - **Rename it to:** `PHPMailer` (remove `-6.9.0`)

4. **Verify structure:**
   ```
   C:\xampp\htdocs\onlinebidding\api\PHPMailer\src\PHPMailer.php
   C:\xampp\htdocs\onlinebidding\api\PHPMailer\src\SMTP.php
   C:\xampp\htdocs\onlinebidding\api\PHPMailer\src\Exception.php
   ```

### Option B: Run Installation Script

1. **Run:**
   ```
   C:\xampp\htdocs\onlinebidding\api\INSTALL_PHPMailer.bat
   ```
2. Follow the on-screen instructions

---

## Step 2: Verify Email Configuration

Your email is already configured in `config_email.php`:
- ✅ Email: harsha168656@gmail.com
- ✅ App Password: Configured

**Verify file exists:**
```
C:\xampp\htdocs\onlinebidding\api\config_email.php
```

---

## Step 3: Test Email Sending

### Test 1: Quick PHP Test

1. **Open browser:**
   ```
   http://localhost/onlinebidding/api/test_email_send.php
   ```

2. **You should see:**
   - ✅ Email sent successfully!
   - Or error message if PHPMailer is missing

### Test 2: Via Your App

1. **Open your Android app**
2. **Go to Forgot Password**
3. **Enter:** harsha168656@gmail.com
4. **Check your email inbox** for OTP

---

## 🔍 Troubleshooting

### Error: "Class 'PHPMailer\PHPMailer\PHPMailer' not found"

**Problem:** PHPMailer not downloaded or wrong location

**Solution:**
1. Verify PHPMailer folder exists: `C:\xampp\htdocs\onlinebidding\api\PHPMailer\`
2. Check files exist:
   - `PHPMailer\src\PHPMailer.php`
   - `PHPMailer\src\SMTP.php`
   - `PHPMailer\src\Exception.php`
3. If missing, download from Step 1 above

### Error: "SMTP connect() failed"

**Problem:** Gmail authentication issue

**Solutions:**
1. **Verify App Password:**
   - Go to: https://myaccount.google.com/apppasswords
   - Make sure you're using the 16-character App Password
   - Remove spaces when pasting (e.g., `wymmgnwhbcofgbvq`)

2. **Check 2-Step Verification:**
   - Go to: https://myaccount.google.com/security
   - Make sure "2-Step Verification" is enabled

3. **Try different port:**
   - Edit `config_email.php`:
     ```php
     'smtp_port' => 465,      // Change from 587 to 465
     'smtp_secure' => 'ssl',  // Change from 'tls' to 'ssl'
     ```

### Email not received

**Check:**
1. ✅ Check spam/junk folder
2. ✅ Verify email address: harsha168656@gmail.com
3. ✅ Check PHP error log: `C:\xampp\apache\logs\error.log`
4. ✅ Look for: `✅ Email sent successfully` in logs

---

## ✅ Checklist

Before testing, verify:

- [ ] PHPMailer downloaded and extracted
- [ ] PHPMailer folder is at: `C:\xampp\htdocs\onlinebidding\api\PHPMailer\`
- [ ] Files exist: `PHPMailer\src\PHPMailer.php`, `SMTP.php`, `Exception.php`
- [ ] `config_email.php` has your credentials
- [ ] Gmail: 2-Step Verification enabled
- [ ] Gmail: App Password generated
- [ ] Apache is running in XAMPP

---

## 🎯 After Setup

Once PHPMailer is installed:

1. **Test:** `http://localhost/onlinebidding/api/test_email_send.php`
2. **Check email:** harsha168656@gmail.com
3. **Use app:** Forgot password feature will send emails automatically!

---

## 📞 Quick Summary

**What you need:**
1. ✅ Download PHPMailer (Step 1 above)
2. ✅ Verify config_email.php has your credentials (already done)
3. ✅ Test email sending

**Most common issue:** PHPMailer not downloaded - fix this first!


