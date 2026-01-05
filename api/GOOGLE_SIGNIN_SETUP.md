# 🔐 Google Sign-In Setup Guide

## Overview
This guide explains how to set up Google Sign-In for the Online Bidding app.

---

## 📱 Android App Setup

### Step 1: Get Google Web Client ID

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable **Google Sign-In API**:
   - Navigate to **APIs & Services** > **Library**
   - Search for "Google Sign-In API"
   - Click **Enable**

4. Create OAuth 2.0 Credentials:
   - Go to **APIs & Services** > **Credentials**
   - Click **Create Credentials** > **OAuth client ID**
   - Application type: **Web application**
   - Name: "Online Bidding Web Client"
   - Authorized redirect URIs: (leave empty for now)
   - Click **Create**
   - **Copy the Client ID** (looks like: `123456789-abcdefghijklmnop.apps.googleusercontent.com`)

5. Create Android OAuth Client (optional but recommended):
   - Click **Create Credentials** > **OAuth client ID** again
   - Application type: **Android**
   - Name: "Online Bidding Android"
   - Package name: `com.example.onlinebidding`
   - SHA-1 certificate fingerprint: (get from your keystore)
   - Click **Create**

### Step 2: Update Android Code

1. Open `app/src/main/java/com/example/onlinebidding/utils/GoogleSignInHelper.kt`
2. Replace `"YOUR_WEB_CLIENT_ID"` with your actual Web Client ID:
   ```kotlin
   .requestIdToken("123456789-abcdefghijklmnop.apps.googleusercontent.com")
   ```

### Step 3: Get SHA-1 Certificate Fingerprint (For Android OAuth Client)

**For Debug Build:**
```bash
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

**For Release Build:**
```bash
keytool -list -v -keystore your-release-key.keystore -alias your-key-alias
```

Copy the SHA-1 fingerprint and add it to your Android OAuth client in Google Cloud Console.

---

## 🔧 Backend API Setup

The backend API (`api/google-signin.php`) is already set up and ready to use.

### How It Works:

1. **Token Verification**: The API decodes the Google ID token (JWT) to extract user information
2. **User Lookup**: Checks if user exists by email
3. **Auto-Registration**: If user doesn't exist, creates a new account automatically
4. **Login**: If user exists, logs them in and returns auth token

### Production Note:

For production, you should verify the Google ID token with Google's servers:
```
GET https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=TOKEN
```

The current implementation uses simple JWT decoding for development. Update `google-signin.php` to verify tokens with Google's API in production.

---

## ✅ Testing

1. Run the app
2. Go to Login screen
3. Click "Sign in with Google"
4. Select your Google account
5. You should be automatically logged in or registered

---

## 🐛 Troubleshooting

### Error: "Developer error - check Google Sign-In configuration"
- Make sure you've added the correct Web Client ID in `GoogleSignInHelper.kt`
- Verify the OAuth client is created in Google Cloud Console

### Error: "Sign-in was cancelled"
- User cancelled the sign-in process (not an error)

### Error: "Network error"
- Check internet connection
- Verify Google Sign-In API is enabled in Google Cloud Console

### Error: "Failed to get ID token"
- Make sure you're requesting `requestIdToken()` in `GoogleSignInOptions`
- Verify the Web Client ID is correct

---

## 📝 Files Modified

- ✅ `api/google-signin.php` - Backend API endpoint
- ✅ `app/src/main/java/com/example/onlinebidding/utils/GoogleSignInHelper.kt` - Google Sign-In helper
- ✅ `app/src/main/java/com/example/onlinebidding/api/ApiService.kt` - Added Google Sign-In API method
- ✅ `app/src/main/java/com/example/onlinebidding/ui/viewmodel/AuthViewModel.kt` - Added Google Sign-In method
- ✅ `app/src/main/java/com/example/onlinebidding/navigation/AppNavHost.kt` - Connected Google Sign-In to LoginPage
- ✅ `app/src/main/java/com/example/onlinebidding/screens/login/LoginPage.kt` - Added Google Sign-In button

---

## 🚀 Next Steps

1. Get your Web Client ID from Google Cloud Console
2. Update `GoogleSignInHelper.kt` with your Client ID
3. Test Google Sign-In in the app
4. (Optional) Add SHA-1 fingerprint for Android OAuth client
5. (Production) Update backend to verify tokens with Google's API

---

**Need Help?** Check Google Sign-In documentation: https://developers.google.com/identity/sign-in/android/start

