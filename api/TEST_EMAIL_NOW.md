# 🧪 Test Email Now

## ✅ Updated App Password

**New App Password:** `umgutxhfvfon` (spaces removed)

**Updated in:** `api/config_email.php`

---

## 🧪 Test Email Sending

### Option 1: Test via Browser
Open in browser:
```
http://localhost/onlinebidding/api/test_email_send.php
```

You should see:
- ✅ Email sent successfully!
- Check inbox: **harsha168656@gmail.com**

### Option 2: Test via App
1. Open app → Forgot Password
2. Enter: `harsha168656@gmail.com`
3. Click "Send OTP"
4. **Check email inbox** (not app screen!)
5. OTP should arrive in 10-30 seconds

---

## 🔍 If Email Still Not Received

### Check Error Logs
```powershell
Get-Content "C:\xampp\apache\logs\error.log" -Tail 30 | Select-String -Pattern "Email|OTP|PHPMailer|SMTP"
```

### Verify App Password
1. Go to: https://myaccount.google.com/apppasswords
2. Make sure 2-Step Verification is enabled
3. Generate new App Password if needed
4. Update `config_email.php` with new password (no spaces!)

### Check Gmail Settings
- Check spam/junk folder
- Make sure Gmail account is active
- Verify no security blocks on account

---

## ✅ Files Updated

1. ✅ `api/config_email.php` - New App Password: `umgutxhfvfon`
2. ✅ `api/forgot-password.php` - OTP removed from response
3. ✅ Copied to server: `C:\xampp\htdocs\onlinebidding\api\`

---

**Test now:** http://localhost/onlinebidding/api/test_email_send.php

