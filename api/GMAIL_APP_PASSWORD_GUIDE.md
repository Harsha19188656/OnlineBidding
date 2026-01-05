# 📧 Gmail App Password Setup Guide

## ⚠️ IMPORTANT: Gmail Requires App Password

**You cannot use your regular Gmail password (`harsha@123`) for sending emails via SMTP.**

Gmail requires an **App Password** for third-party applications like PHPMailer.

---

## 🔑 How to Get Gmail App Password

### Step 1: Enable 2-Step Verification

1. Go to: https://myaccount.google.com/security
2. Find **"2-Step Verification"**
3. Click **"Get Started"** or **"Turn On"**
4. Follow the steps to enable 2-Step Verification

### Step 2: Generate App Password

1. Go to: https://myaccount.google.com/apppasswords
2. Select:
   - **App:** Mail
   - **Device:** Other (Custom name)
   - Enter: **"Online Bidding"**
3. Click **"Generate"**
4. **Copy the 16-character password** (e.g., `abcd efgh ijkl mnop`)
5. **Remove spaces** when using it (e.g., `abcdefghijklmnop`)

---

## ✅ Current Configuration

Your email config (`config_email.php`) currently uses:
- **Email:** harsha168656@gmail.com
- **App Password:** wymmgnwhbcofgbvq (already configured)

**If you want to use a NEW App Password:**

1. Generate a new App Password (follow steps above)
2. Update `config_email.php`:
   ```php
   'smtp_password' => 'your-new-app-password-here', // No spaces!
   ```
3. Save the file

---

## 🧪 Test Email

After setup, test email sending:
```
http://localhost/onlinebidding/api/test_email_send.php
```

---

## ❌ Why Regular Password Doesn't Work

- Gmail blocks regular passwords for security
- Only App Passwords work with SMTP
- App Passwords are specific to each application
- You can revoke them anytime if needed

---

## ✅ You're All Set!

PHPMailer is installed and email is configured. Your OTP emails will be sent to: **harsha168656@gmail.com**

