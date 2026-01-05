# 📧 Complete PHPMailer Email Setup Guide

## ✅ Quick Setup (5 Minutes)

### Step 1: Download PHPMailer

**Option A: Using PowerShell Script (Easiest)**
```powershell
cd C:\xampp\htdocs\onlinebidding\api
.\download_phpmailer.ps1
```

**Option B: Manual Download**
1. Go to: https://github.com/PHPMailer/PHPMailer/releases
2. Download latest ZIP (e.g., `PHPMailer-6.9.0.zip`)
3. Extract to: `C:\xampp\htdocs\onlinebidding\api\PHPMailer`

**Required Structure:**
```
api/
├── PHPMailer/
│   └── src/
│       ├── PHPMailer.php
│       ├── SMTP.php
│       └── Exception.php
├── config_email.php
└── forgot-password.php
```

---

### Step 2: Configure Gmail (Recommended)

**For Gmail, you need an App Password:**

1. **Enable 2-Step Verification:**
   - Go to: https://myaccount.google.com/security
   - Enable "2-Step Verification"

2. **Generate App Password:**
   - Go to: https://myaccount.google.com/apppasswords
   - Select "Mail" and "Other (Custom name)"
   - Enter: "Online Bidding"
   - Click "Generate"
   - **Copy the 16-character password** (e.g., `abcd efgh ijkl mnop`)

3. **Edit `config_email.php`:**
   ```php
   'smtp_username' => 'your-email@gmail.com',      // Your Gmail
   'smtp_password' => 'abcdefghijklmnop',          // App Password (no spaces)
   'from_email' => 'your-email@gmail.com',
   'reply_to_email' => 'your-email@gmail.com',
   ```

---

### Step 3: Test Email

1. **Test forgot password:**
   - Use your app's forgot password feature
   - Enter a registered email
   - Check email inbox for OTP

2. **Check logs:**
   - Location: `C:\xampp\apache\logs\error.log`
   - Look for: `✅ Email sent successfully` or `❌ Email sending failed`

---

## 📋 Configuration Options

### Gmail Settings (config_email.php)
```php
'smtp_host' => 'smtp.gmail.com',
'smtp_port' => 587,                    // Use 587 for TLS
'smtp_secure' => 'tls',                // or 'ssl' for port 465
'smtp_auth' => true,
'smtp_username' => 'your-email@gmail.com',
'smtp_password' => 'your-app-password', // 16-char App Password
```

### Outlook/Hotmail Settings
```php
'smtp_host' => 'smtp-mail.outlook.com',
'smtp_port' => 587,
'smtp_secure' => 'tls',
'smtp_username' => 'your-email@outlook.com',
'smtp_password' => 'your-password',
```

### Yahoo Settings
```php
'smtp_host' => 'smtp.mail.yahoo.com',
'smtp_port' => 587,
'smtp_secure' => 'tls',
'smtp_username' => 'your-email@yahoo.com',
'smtp_password' => 'your-app-password', // Yahoo also requires App Password
```

---

## 🔍 Troubleshooting

### Error: "Class 'PHPMailer\PHPMailer\PHPMailer' not found"

**Solution:**
- Check PHPMailer folder exists: `C:\xampp\htdocs\onlinebidding\api\PHPMailer\src\`
- Verify files: `PHPMailer.php`, `SMTP.php`, `Exception.php`

### Error: "SMTP connect() failed"

**Possible causes:**
1. Wrong SMTP settings
2. Firewall blocking port 587
3. Gmail: Not using App Password (using regular password)
4. 2-Step Verification not enabled

**Solutions:**
- Verify `smtp_host` and `smtp_port`
- For Gmail: Use App Password, not regular password
- Try port 465 with SSL instead of 587 with TLS
- Check firewall/antivirus settings

### Error: "Authentication failed"

**Solution:**
- Gmail: Make sure you're using App Password (16 characters)
- Remove spaces from App Password
- Verify username is correct email address
- Check if 2-Step Verification is enabled

### Email not received

**Check:**
1. Check spam/junk folder
2. Verify email address is correct
3. Check PHP error log: `C:\xampp\apache\logs\error.log`
4. Enable debug mode in `config_email.php`:
   ```php
   'debug' => 2,  // Shows detailed SMTP communication
   ```

---

## 🧪 Test Email Sending

Create a test file: `api/test_email.php`

```php
<?php
require_once __DIR__ . '/PHPMailer/src/Exception.php';
require_once __DIR__ . '/PHPMailer/src/PHPMailer.php';
require_once __DIR__ . '/PHPMailer/src/SMTP.php';

use PHPMailer\PHPMailer\PHPMailer;

$emailConfig = require __DIR__ . '/config_email.php';

$mail = new PHPMailer(true);
$mail->isSMTP();
$mail->Host = $emailConfig['smtp_host'];
$mail->SMTPAuth = $emailConfig['smtp_auth'];
$mail->Username = $emailConfig['smtp_username'];
$mail->Password = $emailConfig['smtp_password'];
$mail->SMTPSecure = $emailConfig['smtp_secure'];
$mail->Port = $emailConfig['smtp_port'];
$mail->SMTPDebug = 2; // Show debug info

$mail->setFrom($emailConfig['from_email'], $emailConfig['from_name']);
$mail->addAddress('test@example.com'); // Your test email

$mail->isHTML(true);
$mail->Subject = 'Test Email';
$mail->Body = '<h1>Test Email</h1><p>If you receive this, email is working!</p>';

try {
    $mail->send();
    echo "✅ Email sent successfully!";
} catch (Exception $e) {
    echo "❌ Error: " . $mail->ErrorInfo;
}
?>
```

Access: `http://localhost/onlinebidding/api/test_email.php`

---

## ✅ Verification Checklist

- [ ] PHPMailer downloaded and extracted
- [ ] `config_email.php` configured with your email credentials
- [ ] Gmail: 2-Step Verification enabled
- [ ] Gmail: App Password generated and copied
- [ ] `config_email.php` updated with App Password
- [ ] Tested forgot password - email received
- [ ] OTP stored in `password_reset_tokens` table
- [ ] No errors in PHP error log

---

## 🎯 Production Notes

**Before going to production:**

1. **Set development mode to false:**
   ```php
   $DEVELOPMENT_MODE = false; // In forgot-password.php
   ```

2. **Disable debug mode:**
   ```php
   'debug' => false, // In config_email.php
   ```

3. **Secure config file:**
   - Don't commit `config_email.php` to public repositories
   - Use environment variables for production
   - Keep credentials secure

---

## 🚀 You're Done!

Your email system is now fully functional with PHPMailer! Users will receive OTP emails when they request password reset.

