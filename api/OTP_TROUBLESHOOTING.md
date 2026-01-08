# 🔧 OTP Email Not Received - Troubleshooting Guide

## ⚠️ Common Issue: PHP mail() Function in XAMPP

PHP's `mail()` function **doesn't work by default** in XAMPP because there's no mail server configured. This is normal for local development!

## ✅ Quick Solutions

### Solution 1: Check the API Response (Development Mode)

The OTP is now **included in the API response** for development/testing:

1. **Check the API response** - The OTP will be in the JSON response:
   ```json
   {
     "success": true,
     "message": "OTP has been sent to your email address.",
     "otp": "123456",
     "debug": "Development mode: OTP included in response. Check email or use this OTP.",
     "mail_sent": false
   }
   ```

2. **Check Android Studio Logcat** - Look for logs like:
   ```
   OTP for user@example.com: 123456
   ```

3. **Check XAMPP Error Log**:
   - Location: `C:\xampp\apache\logs\error.log`
   - Search for: `OTP for`
   - You'll see: `OTP for user@example.com: 123456 (Expires: 2024-...)`

### Solution 2: Check Error Log File

1. Open: `C:\xampp\apache\logs\error.log`
2. Scroll to the bottom (most recent entries)
3. Look for lines containing: `OTP for`
4. The OTP will be displayed there

### Solution 3: Enable Development Mode (Already Done)

The `forgot-password.php` file now includes the OTP in the response when `$DEVELOPMENT_MODE = true`.

**To see the OTP:**
- Check the API response in your app's network logs
- Or check the error log file mentioned above

### Solution 4: Use Gmail SMTP (For Real Email Sending)

If you want actual emails to work, you need to configure SMTP. Here's how:

#### Option A: Use PHPMailer with Gmail

1. Download PHPMailer: https://github.com/PHPMailer/PHPMailer
2. Extract to: `C:\xampp\htdocs\onlinebidding\api\PHPMailer\`
3. Update `forgot-password.php` to use PHPMailer instead of `mail()`

#### Option B: Configure XAMPP Mail (Advanced)

1. Edit: `C:\xampp\php\php.ini`
2. Find `[mail function]` section
3. Configure SMTP settings:
   ```ini
   [mail function]
   SMTP = smtp.gmail.com
   smtp_port = 587
   sendmail_from = your-email@gmail.com
   sendmail_path = "\"C:\xampp\sendmail\sendmail.exe\" -t"
   ```
4. Edit: `C:\xampp\sendmail\sendmail.ini`
5. Configure Gmail SMTP settings

## 🧪 Testing Steps

### Step 1: Test API Endpoint

Open browser or use curl:
```bash
curl -X POST http://localhost/onlinebidding/api/forgot-password.php \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"your-email@example.com\"}"
```

**Expected Response (Development Mode):**
```json
{
  "success": true,
  "message": "OTP has been sent to your email address.",
  "otp": "123456",
  "debug": "Development mode: OTP included in response. Check email or use this OTP.",
  "mail_sent": false
}
```

### Step 2: Check Error Log

1. Open: `C:\xampp\apache\logs\error.log`
2. Look for: `OTP for your-email@example.com: 123456`

### Step 3: Use the OTP

Copy the OTP from the response or error log and use it in your app!

## 📝 Important Notes

1. **Development Mode**: The OTP is included in the API response for testing. 
   - **REMOVE THIS IN PRODUCTION!** Set `$DEVELOPMENT_MODE = false;` in `forgot-password.php`

2. **Email Won't Work Locally**: PHP's `mail()` function requires a mail server, which XAMPP doesn't have by default.

3. **For Production**: 
   - Use a proper SMTP service (Gmail, SendGrid, Mailgun, etc.)
   - Or use PHPMailer with SMTP configuration
   - Remove the OTP from API responses

## 🔍 Still Not Working?

1. **Check if OTP is being generated:**
   - Look in error log: `C:\xampp\apache\logs\error.log`
   - Search for "OTP for"

2. **Check if API is working:**
   - Test the endpoint in browser or Postman
   - Check the response JSON

3. **Check database:**
   - Verify the OTP is saved in `password_reset_tokens` table
   - Run: `SELECT * FROM password_reset_tokens ORDER BY created_at DESC LIMIT 1;`

4. **Check Apache is running:**
   - XAMPP Control Panel → Apache should be "Running" (green)

## 💡 Quick Fix for Testing

**Right now, the easiest way to get the OTP:**

1. Request password reset from your app
2. Check Android Studio Logcat for the API response
3. Or check: `C:\xampp\apache\logs\error.log`
4. Copy the OTP and use it!

The OTP is being generated and saved to the database - you just need to retrieve it from the response or logs since email isn't configured.




