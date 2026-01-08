# Forgot Password API Setup Guide

## Files Created:
1. `forgot-password.php` - Sends OTP to user's email
2. `verify-otp.php` - Verifies the OTP code

## Installation Steps:

### Step 1: Copy Files to XAMPP
Copy these files to your XAMPP backend directory:
- From: `api/forgot-password.php`
- To: `C:\xampp\htdocs\onlinebidding\api\forgot-password.php`

- From: `api/verify-otp.php`
- To: `C:\xampp\htdocs\onlinebidding\api\verify-otp.php`

### Step 2: Verify Database Connection
Make sure your database configuration matches:
- Host: `localhost`
- Database: `onlinebidding`
- Username: `root`
- Password: `` (empty)

If your database has different credentials, edit both PHP files and update lines 21-24.

### Step 3: Test the Endpoints

**Test forgot-password.php:**
```bash
curl -X POST http://10.148.199.81/onlinebidding/api/forgot-password.php \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'
```

**Test verify-otp.php:**
```bash
curl -X POST http://10.148.199.81/onlinebidding/api/verify-otp.php \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","otp":"123456"}'
```

### Step 4: Check Logs
If there are errors, check:
- Apache Error Log: `C:\xampp\apache\logs\error.log`
- PHP Error Log: Check your `php.ini` for error_log location

## Database Table:
The `password_reset_tokens` table will be created automatically when you first use the forgot password feature.

## Email Configuration:
The files use PHP's `mail()` function. For production, you may want to:
1. Configure SMTP in `php.ini`
2. Use a library like PHPMailer
3. Set up proper email server

## Troubleshooting:

**Error: "Forgot password endpoint not found"**
- Make sure files are copied to: `C:\xampp\htdocs\onlinebidding\api\`
- Check Apache is running
- Verify URL: `http://10.148.199.81/onlinebidding/api/forgot-password.php`

**Error: "Database error"**
- Check MySQL is running in XAMPP
- Verify database name is `onlinebidding`
- Check database credentials in PHP files

**OTP not received in email**
- Check PHP mail configuration
- Check spam folder
- Check error logs for email sending errors
- OTP is also logged in error log for testing




