# 🔍 Complete OTP Email Diagnostic Guide

## ✅ Files Created

1. **`diagnose_otp_email.php`** - Complete diagnostic tool
2. **`CHECK_OTP_IN_DB.php`** - Check OTPs in database
3. Enhanced logging in `forgot-password.php`
4. Debug mode enabled in `config_email.php`

---

## 🧪 Step 1: Run Diagnostic

Open in browser:
```
http://localhost/onlinebidding/api/diagnose_otp_email.php
```

This will check:
- ✅ PHPMailer installation
- ✅ Email configuration
- ✅ Database connection
- ✅ Table structure
- ✅ Recent OTPs
- ✅ Test email sending

---

## 📊 Step 2: Check OTPs in Database

Open in browser:
```
http://localhost/onlinebidding/api/CHECK_OTP_IN_DB.php
```

This will show:
- Table structure
- All OTPs for harsha168656@gmail.com
- Latest valid OTP
- Recent OTPs

---

## 📝 Step 3: Check Log Files

### Email Debug Log
```powershell
Get-Content "C:\xampp\htdocs\onlinebidding\api\email_debug.log" -Tail 50
```

### Email Success Log
```powershell
Get-Content "C:\xampp\htdocs\onlinebidding\api\email_success.log" -Tail 20
```

### Email Error Log
```powershell
Get-Content "C:\xampp\htdocs\onlinebidding\api\email_error.log" -Tail 50
```

### Apache Error Log
```powershell
Get-Content "C:\xampp\apache\logs\error.log" -Tail 50 | Select-String -Pattern "Email|OTP|PHPMailer|SMTP|forgot"
```

---

## 🔍 Common Issues & Solutions

### Issue 1: OTP Generated but Email Not Sent

**Check:**
1. Run `diagnose_otp_email.php` - see if test email works
2. Check `email_error.log` for specific error
3. Verify Gmail App Password is correct

**Solution:**
- Generate new App Password: https://myaccount.google.com/apppasswords
- Update `config_email.php` with new password (no spaces!)

---

### Issue 2: SMTP Authentication Failed

**Error:** `SMTP Error: Could not authenticate`

**Solution:**
1. Verify 2-Step Verification is enabled on Gmail
2. Generate new App Password
3. Make sure password has no spaces
4. Update `config_email.php`

---

### Issue 3: OTP Not in Database

**Check:**
- Run `CHECK_OTP_IN_DB.php`
- Verify user exists: `SELECT * FROM users WHERE email = 'harsha168656@gmail.com'`

**Solution:**
- Make sure user exists in database
- Check database connection in `forgot-password.php`

---

### Issue 4: Email Goes to Spam

**Solution:**
- Check spam/junk folder
- Mark as "Not Spam"
- Add sender to contacts

---

## 🧪 Test Complete Flow

1. **Open app** → Forgot Password
2. **Enter:** harsha168656@gmail.com
3. **Click:** Send OTP
4. **Check database:** `http://localhost/onlinebidding/api/CHECK_OTP_IN_DB.php`
5. **Check logs:** `email_debug.log`, `email_error.log`
6. **Check email inbox** (and spam folder)

---

## 📋 Current Configuration

- **Email:** harsha168656@gmail.com
- **App Password:** umgutxhfvfon
- **SMTP:** smtp.gmail.com:587 (TLS)
- **Debug Mode:** Enabled (2 = client and server messages)

---

## ✅ Next Steps

1. **Run diagnostic:** `http://localhost/onlinebidding/api/diagnose_otp_email.php`
2. **Check OTPs:** `http://localhost/onlinebidding/api/CHECK_OTP_IN_DB.php`
3. **Check logs:** See Step 3 above
4. **Test email:** Try sending OTP from app
5. **Review errors:** Check `email_error.log` for specific issues

---

**All diagnostic tools are ready! Run them to identify the exact issue.**

