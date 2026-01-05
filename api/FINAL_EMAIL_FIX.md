# 🎯 FINAL FIX - Get OTP Emails Working!

## ✅ Current Status

- ✅ PHPMailer installed
- ✅ Code configured correctly
- ✅ Email: harsha168656@gmail.com
- ❌ **Gmail App Password is WRONG** (authentication failing)

---

## 🔧 Fix Authentication (5 Minutes)

### Step 1: Get New Gmail App Password

1. **Go to:** https://myaccount.google.com/apppasswords
2. **Select:**
   - App: **Mail**
   - Device: **Other (Custom name)**
   - Name: **Online Bidding**
3. **Click:** Generate
4. **Copy the password** (16 characters, remove spaces!)

### Step 2: Update Password

1. **Open:** `C:\xampp\htdocs\onlinebidding\api\config_email.php`
2. **Change line 20:**
   ```php
   'smtp_password' => 'your-new-app-password-here', // Replace wymmgnwhbcofgbvq
   ```
3. **Save**

### Step 3: Test

**Open:** `http://localhost/onlinebidding/api/test_email_direct.php`

**You should see:**
- ✅ Email sent successfully!
- Check inbox: **harsha168656@gmail.com**

---

## ✅ Verification

**OTP emails will be sent to:** harsha168656@gmail.com

**Code confirms:**
- Line 143 in `forgot-password.php`: `$mail->addAddress($email, $user['name']);`
- This uses the email from the form (harsha168656@gmail.com)

---

## 🎯 After Fixing App Password

1. **Try forgot password in your app**
2. **Enter:** harsha168656@gmail.com
3. **You'll receive OTP email!**

---

**The only issue is the App Password - fix that and emails will work!**

