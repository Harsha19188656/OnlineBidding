# ✅ Verify Email Setup - OTP to harsha168656@gmail.com

## 📧 Current Configuration

**Email:** harsha168656@gmail.com  
**App Password:** wymmgnwhbcofgbvq (may be expired/incorrect)  
**Status:** ❌ Authentication failing

---

## ⚠️ Problem

**Error:** `SMTP Error: Could not authenticate.`

This means your Gmail App Password is **incorrect or expired**.

---

## ✅ Solution: Generate New Gmail App Password

### Step 1: Enable 2-Step Verification (If Not Enabled)

1. Go to: https://myaccount.google.com/security
2. Find **"2-Step Verification"**
3. Click **"Get Started"** and follow steps
4. **You MUST enable this first!**

### Step 2: Generate App Password

1. Go to: https://myaccount.google.com/apppasswords
2. Select:
   - **App:** Mail
   - **Device:** Other (Custom name)
   - **Name:** Online Bidding
3. Click **"Generate"**
4. **Copy the 16-character password** (e.g., `abcd efgh ijkl mnop`)
5. **Remove all spaces** (e.g., `abcdefghijklmnop`)

### Step 3: Update config_email.php

1. **Open:** `C:\xampp\htdocs\onlinebidding\api\config_email.php`
2. **Find this line:**
   ```php
   'smtp_password' => 'wymmgnwhbcofgbvq',
   ```
3. **Replace with your new App Password:**
   ```php
   'smtp_password' => 'your-new-16-character-app-password', // No spaces!
   ```
4. **Save the file**

### Step 4: Test Email

**Open in browser:**
```
http://localhost/onlinebidding/api/test_email_direct.php
```

**Expected:**
- ✅ Email sent successfully!
- Check inbox: **harsha168656@gmail.com**

---

## ✅ Verify OTP Email Destination

The `forgot-password.php` is configured to send OTP to:
- **Email:** `$email` (from user input)
- **In your case:** harsha168656@gmail.com

**Code location:** `api/forgot-password.php` line 143:
```php
$mail->addAddress($email, $user['name']);
```

This means OTP will be sent to **whatever email the user enters** in the forgot password form.

---

## 🧪 Test Complete Flow

1. **Generate new App Password** (Step 2 above)
2. **Update config_email.php** (Step 3 above)
3. **Test email:** `http://localhost/onlinebidding/api/test_email_direct.php`
4. **Try forgot password in app:**
   - Enter: harsha168656@gmail.com
   - **You'll receive OTP email!**

---

## ✅ Checklist

- [ ] 2-Step Verification enabled on Gmail
- [ ] New App Password generated
- [ ] App Password updated in config_email.php (no spaces!)
- [ ] Test email works: `http://localhost/onlinebidding/api/test_email_direct.php`
- [ ] OTP emails are being sent to harsha168656@gmail.com

---

## 🎯 After Fixing

Once you update the App Password:
- ✅ OTP will be sent to: **harsha168656@gmail.com**
- ✅ You'll receive emails in your inbox
- ✅ Check spam folder if not in inbox

---

**Generate App Password:** https://myaccount.google.com/apppasswords

