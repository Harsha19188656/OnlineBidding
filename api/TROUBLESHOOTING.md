# Troubleshooting "Forgot password endpoint not found" Error

## ✅ Files Have Been Copied
The PHP files have been automatically copied to:
- `C:\xampp\htdocs\onlinebidding\api\forgot-password.php`
- `C:\xampp\htdocs\onlinebidding\api\verify-otp.php`

## If You Still Get the Error, Check:

### 1. **XAMPP Apache is Running**
- Open XAMPP Control Panel
- Make sure Apache shows "Running" (green)
- If not, click "Start" next to Apache

### 2. **Test the Endpoint Directly**
Open your browser and test:
```
http://localhost/onlinebidding/api/forgot-password.php
```

You should see: `{"success":false,"error":"Method not allowed"}` (this is OK - it means the file exists!)

### 3. **Check File Permissions**
Make sure the files are readable:
- Right-click `C:\xampp\htdocs\onlinebidding\api\forgot-password.php`
- Properties → Security → Make sure "Users" have "Read" permission

### 4. **Verify URL in App**
The app uses: `http://10.148.199.81/onlinebidding/api/forgot-password.php`

Make sure:
- Your computer's IP is `10.148.199.81`
- Your phone/emulator can reach this IP
- If using emulator, change BASE_URL to `http://10.0.2.2/onlinebidding/`

### 5. **Check Apache Error Log**
If still not working, check:
```
C:\xampp\apache\logs\error.log
```

Look for any PHP errors related to forgot-password.php

### 6. **Test with curl (if available)**
```bash
curl -X POST http://10.148.199.81/onlinebidding/api/forgot-password.php -H "Content-Type: application/json" -d "{\"email\":\"test@example.com\"}"
```

### 7. **Restart Apache**
Sometimes Apache needs a restart:
- XAMPP Control Panel → Stop Apache → Start Apache

## Quick Fix Checklist:
- [ ] Apache is running in XAMPP
- [ ] Files exist at `C:\xampp\htdocs\onlinebidding\api\`
- [ ] Can access `http://localhost/onlinebidding/api/forgot-password.php` in browser
- [ ] IP address `10.148.199.81` is correct for your network
- [ ] Phone/emulator can reach your computer's IP
- [ ] No firewall blocking port 80




