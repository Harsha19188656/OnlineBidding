# 📧 PHPMailer Setup Guide - Complete Working Solution

## 🎯 Step 1: Download PHPMailer

### Option A: Using Composer (Recommended)
```bash
cd C:\xampp\htdocs\onlinebidding\api
composer require phpmailer/phpmailer
```

### Option B: Manual Download
1. Go to: https://github.com/PHPMailer/PHPMailer/releases
2. Download the latest ZIP file (e.g., `PHPMailer-6.9.0.zip`)
3. Extract it
4. Copy the `PHPMailer` folder to: `C:\xampp\htdocs\onlinebidding\api\PHPMailer`

**Required files structure:**
```
api/
├── PHPMailer/
│   ├── src/
│   │   ├── PHPMailer.php
│   │   ├── SMTP.php
│   │   └── Exception.php
│   └── ...
├── forgot-password.php
└── config_email.php
```

---

## 🎯 Step 2: Email Configuration

Choose your email provider:

### Option 1: Gmail (Recommended for Testing)

**Gmail Setup:**
1. Go to: https://myaccount.google.com/security
2. Enable **2-Step Verification**
3. Go to: https://myaccount.google.com/apppasswords
4. Generate an **App Password** for "Mail"
5. Copy the 16-character password (you'll use this in config)

**Gmail SMTP Settings:**
- SMTP Host: `smtp.gmail.com`
- SMTP Port: `587` (TLS) or `465` (SSL)
- Username: Your Gmail address
- Password: App Password (16 characters)

### Option 2: Other Email Providers

**Outlook/Hotmail:**
- SMTP Host: `smtp-mail.outlook.com`
- Port: `587`
- Security: TLS

**Yahoo:**
- SMTP Host: `smtp.mail.yahoo.com`
- Port: `587`
- Security: TLS

---

## 🎯 Step 3: Configure Email Settings

Edit `api/config_email.php` with your email credentials (file created below).

---

## ✅ Test Email

After setup, test the forgot password feature - emails should send successfully!

