# 🔍 Google Sign-In Navigation Debug Guide

## Issue: Not Navigating After Selecting Google Account

If selecting a Google account doesn't navigate to the main screen, follow these steps:

---

## ✅ Step 1: Check Web Client ID Configuration

**Most Common Issue!**

1. Open: `app/src/main/java/com/example/onlinebidding/utils/GoogleSignInHelper.kt`
2. Find line 23: `val webClientId = "YOUR_WEB_CLIENT_ID"`
3. **Replace** with your actual Web Client ID from Google Cloud Console

**If Web Client ID is not configured:**
- The ID token will be `null`
- You'll see error: "Failed to get ID token from Google account"
- Navigation won't happen

**How to get Web Client ID:**
- See: `api/GOOGLE_SIGNIN_SETUP.md`

---

## ✅ Step 2: Check Logcat for Debug Logs

I've added extensive logging. Check Android Studio Logcat:

**Look for these logs:**

1. **When button is clicked:**
   ```
   LoginScreen: Google Sign-Up button clicked
   ```

2. **When account is selected:**
   ```
   GoogleSignIn: ID Token received, length: XXX
   LoginScreen: Google Sign-In success, calling viewModel
   ```

3. **When API is called:**
   ```
   AuthViewModel: Google Sign-In started, ID Token length: XXX
   AuthViewModel: Calling API: google-signin.php
   AuthViewModel: API Response - Code: 200, Success: true
   AuthViewModel: Response Body: success=true, token=..., user=email@example.com
   AuthViewModel: Google Sign-In successful! Setting state with token and role: user
   AuthViewModel: State updated. Token set: true
   ```

4. **When navigation should happen:**
   ```
   LoginScreen: Token detected in login screen, navigating...
   AppNavHost: Token received: ..., Role: user, Email: email@example.com
   AppNavHost: Navigating to interest
   ```

**If you see errors:**
- Check the error message in Logcat
- Common errors are listed below

---

## ✅ Step 3: Verify Backend API is Working

1. **Check if backend is running:**
   - XAMPP Apache should be running
   - Database should be accessible

2. **Test the API directly:**
   - Open browser: `http://localhost/onlinebidding/api/google-signin.php`
   - Should show: `{"success":false,"error":"Method not allowed"}` (because it's POST only)
   - If you see a different error, there's a backend issue

3. **Check backend logs:**
   - XAMPP error logs: `C:\xampp\apache\logs\error.log`
   - PHP error logs: `C:\xampp\php\logs\php_error_log`

---

## ✅ Step 4: Check Network Connection

**Verify API base URL:**

1. Open: `app/src/main/java/com/example/onlinebidding/api/RetrofitInstance.kt`
2. Check `BASE_URL` matches your setup:
   - **Emulator:** `http://10.0.2.2/onlinebidding/`
   - **Physical Device:** `http://YOUR_IP/onlinebidding/`

3. **Test connection:**
   - Try regular login first
   - If regular login works, network is fine

---

## ✅ Step 5: Common Errors & Solutions

### Error: "Developer error - check Google Sign-In configuration"
**Solution:** Web Client ID is not set or incorrect
- Set Web Client ID in `GoogleSignInHelper.kt`
- Make sure it's the **Web Client ID**, not Android Client ID

### Error: "Failed to get ID token from Google account"
**Solution:** Web Client ID is not configured
- Set Web Client ID in `GoogleSignInHelper.kt`

### Error: "Cannot connect to server"
**Solution:** Backend is not accessible
- Check XAMPP is running
- Verify BASE_URL in `RetrofitInstance.kt`
- Check firewall settings

### Error: "Google Sign-In failed: 500"
**Solution:** Backend error
- Check backend error logs
- Verify database connection
- Check `api/google-signin.php` file exists

### No Error, But Not Navigating
**Solution:** Check Logcat for:
- Is token being set? Look for: "State updated. Token set: true"
- Is LaunchedEffect triggering? Look for: "Token detected in login screen"
- If token is set but not navigating, check navigation routes exist

---

## ✅ Step 6: Manual Test

**Test the flow step by step:**

1. **Click "Sign in with Google"**
   - Should see account selection dialog
   - ✅ If not: Web Client ID issue

2. **Select an account**
   - Should see loading indicator
   - ✅ If not: Check Logcat for errors

3. **Check Logcat**
   - Look for all the debug logs above
   - Find where it stops/fails

4. **Check if token is set**
   - Look for: "State updated. Token set: true"
   - ✅ If false: API response issue

5. **Check if navigation triggers**
   - Look for: "Token detected in login screen, navigating..."
   - ✅ If not: LaunchedEffect issue

---

## 🔧 Quick Fixes

### Fix 1: Ensure Web Client ID is Set
```kotlin
// In GoogleSignInHelper.kt, line 23
val webClientId = "123456789-abcdefghijklmnop.apps.googleusercontent.com" // Your actual ID
```

### Fix 2: Verify Backend File Exists
- File should be at: `C:\xampp\htdocs\onlinebidding\api\google-signin.php`
- Copy from project if missing

### Fix 3: Check Database Connection
- Verify database `onlinebidding` exists
- Verify `users` table exists
- Test with regular login first

---

## 📱 Testing Checklist

- [ ] Web Client ID is configured
- [ ] Backend API file exists
- [ ] XAMPP is running
- [ ] Database is accessible
- [ ] Network connection works (test with regular login)
- [ ] Logcat shows token is received
- [ ] Logcat shows navigation is triggered
- [ ] No errors in Logcat

---

## 🆘 Still Not Working?

1. **Share Logcat output:**
   - Filter by: "GoogleSignIn", "AuthViewModel", "LoginScreen", "AppNavHost"
   - Copy all relevant logs

2. **Check backend response:**
   - Add logging in `api/google-signin.php`
   - Check what's being returned

3. **Verify navigation routes:**
   - Make sure "interest" route exists
   - Make sure "admin_dashboard" route exists

---

**Need more help?** Check the logs and share the error messages you see!

