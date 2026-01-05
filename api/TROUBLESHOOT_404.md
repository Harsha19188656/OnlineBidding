# 🔍 Troubleshooting 404 Error - Forgot Password API

## ✅ File Status

The `forgot-password.php` file exists at:
```
C:\xampp\htdocs\onlinebidding\api\forgot-password.php
```

---

## 🔗 Expected URL

Your app is calling:
```
http://10.148.199.81/onlinebidding/api/forgot-password.php
```

---

## 🔍 Troubleshooting Steps

### 1. Verify Apache is Running

- Open XAMPP Control Panel
- Make sure **Apache** is running (green status)
- If not running, click "Start"

### 2. Test URL in Browser

Open in browser:
```
http://localhost/onlinebidding/api/forgot-password.php
```

**Expected:** Should show JSON error (not 404) because POST data is missing, but file should exist.

**If 404:** File might be in wrong location or Apache document root is different.

### 3. Check File Location

Verify file exists:
```
C:\xampp\htdocs\onlinebidding\api\forgot-password.php
```

### 4. Test with Postman/curl

**POST Request:**
```
URL: http://localhost/onlinebidding/api/forgot-password.php
Method: POST
Headers:
  Content-Type: application/json
Body:
{
  "email": "harsha168656@gmail.com"
}
```

### 5. Check Apache Error Log

**Location:** `C:\xampp\apache\logs\error.log`

Look for recent errors related to forgot-password.php

### 6. Verify Base URL in App

**File:** `app/src/main/java/com/example/onlinebidding/api/RetrofitInstance.kt`

**Current:** `http://10.148.199.81/onlinebidding/`

**For Emulator:** Should be `http://10.0.2.2/onlinebidding/`

**For Physical Device:** Should be your computer's IP (current: `10.148.199.81`)

---

## ✅ Quick Test

1. **Open browser:**
   ```
   http://localhost/onlinebidding/api/forgot-password.php
   ```

2. **Expected Response:**
   ```json
   {"success":false,"error":"Method not allowed"}
   ```
   
   This means the file exists! (The error is because we're using GET instead of POST)

3. **If 404:** 
   - Check XAMPP document root (usually `C:\xampp\htdocs\`)
   - Verify `onlinebidding` folder exists
   - Verify `api` folder exists inside `onlinebidding`
   - Check file permissions

---

## 🎯 Common Issues

### Issue 1: Wrong Base URL
- **For Emulator:** Use `http://10.0.2.2/onlinebidding/`
- **For Physical Device:** Use `http://YOUR_COMPUTER_IP/onlinebidding/`

### Issue 2: Apache Not Running
- Start Apache from XAMPP Control Panel

### Issue 3: File Not Found
- Verify file path: `C:\xampp\htdocs\onlinebidding\api\forgot-password.php`
- Check spelling of folder names

### Issue 4: .htaccess Blocking
- Check if `.htaccess` file exists in `onlinebidding` folder
- Temporarily rename it to test

---

## ✅ Verification Checklist

- [ ] Apache is running in XAMPP
- [ ] File exists at: `C:\xampp\htdocs\onlinebidding\api\forgot-password.php`
- [ ] Browser test: `http://localhost/onlinebidding/api/forgot-password.php` (should show JSON error, not 404)
- [ ] Base URL in app matches your setup (emulator vs physical device)
- [ ] No firewall blocking port 80

---

## 📞 Next Steps

1. Test URL in browser first
2. Check Apache error logs
3. Verify base URL matches your device (emulator vs physical)
4. Try the app again

