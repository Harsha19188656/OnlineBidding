# 🚀 QUICK FIX - "Forgot password endpoint not found"

## ✅ Files Are Now Copied!
The PHP files have been automatically copied to XAMPP.

## 🔧 Do These Steps NOW:

### Step 1: Start XAMPP Apache
1. Open **XAMPP Control Panel**
2. Find **Apache** in the list
3. If it shows "Stopped" (red), click **Start**
4. Wait until it shows **Running** (green)

### Step 2: Test in Browser
Open your browser and go to:
```
http://localhost/onlinebidding/api/forgot-password.php
```

**Expected Result:** You should see:
```json
{"success":false,"error":"Method not allowed"}
```
This is GOOD! It means the file exists and Apache is serving it.

If you see "404 Not Found" or "File not found", the file path is wrong.

### Step 3: Check Your IP Address
The app uses: `http://10.148.199.81/onlinebidding/`

**To find your actual IP:**
1. Open Command Prompt
2. Type: `ipconfig`
3. Look for "IPv4 Address" under your active network adapter
4. If it's different from `10.148.199.81`, update `RetrofitInstance.kt`

### Step 4: If Using Android Emulator
Change the BASE_URL in `RetrofitInstance.kt` to:
```kotlin
private const val BASE_URL = "http://10.0.2.2/onlinebidding/"
```

### Step 5: Restart Your App
After making changes, rebuild and restart your Android app.

## 🧪 Test the Endpoint:
Run this in Command Prompt (if curl is available):
```bash
curl -X POST http://localhost/onlinebidding/api/forgot-password.php -H "Content-Type: application/json" -d "{\"email\":\"test@example.com\"}"
```

## 📝 Still Not Working?
1. Check Apache error log: `C:\xampp\apache\logs\error.log`
2. Make sure port 80 is not blocked by firewall
3. Try accessing: `http://127.0.0.1/onlinebidding/api/forgot-password.php`
4. Verify MySQL is also running in XAMPP




