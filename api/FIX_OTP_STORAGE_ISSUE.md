# 🔧 Fix OTP Storage Issue

## ✅ Created Diagnostic Tools

I've created tools to verify and fix the OTP storage issue.

---

## 🧪 Step 1: Verify Table Structure

**Open in browser:**
```
http://localhost/onlinebidding/api/verify_table_structure.php
```

**This will:**
- ✅ Check if table exists
- ✅ Create table if missing
- ✅ Verify all required columns
- ✅ Test OTP insertion and retrieval
- ✅ Show recent OTPs

**If table is missing or has wrong structure, it will be fixed automatically!**

---

## 🧪 Step 2: Test OTP Generation

1. **Request new OTP** from app (Forgot Password)
2. **Check error logs:**
   ```powershell
   Get-Content "C:\xampp\apache\logs\error.log" -Tail 30 | Select-String -Pattern "OTP|password_reset"
   ```
3. **Look for:**
   - `✅ OTP inserted` - OTP was stored
   - `✅ OTP verified in database` - OTP confirmed in DB
   - `❌ ERROR: OTP was not stored` - Storage failed

---

## 🧪 Step 3: Debug OTP Verification

**Get OTP from email**, then:
```
http://localhost/onlinebidding/api/debug_otp_verification.php?email=harsha168656@gmail.com&otp=YOUR_OTP
```

**This will show:**
- All OTPs for your email
- Exact match test
- Why verification is failing

---

## 🔍 Common Issues

### Issue 1: Table Doesn't Exist

**Fix:**
- Run `verify_table_structure.php` - it will create the table automatically

### Issue 2: Wrong Column Types

**Fix:**
- Run `verify_table_structure.php` - it will show the structure
- If columns are wrong, the table needs to be recreated

### Issue 3: OTP Not Being Stored

**Check:**
- Error logs for "OTP inserted" message
- Database directly: `SELECT * FROM password_reset_tokens ORDER BY created_at DESC LIMIT 5`

### Issue 4: Foreign Key Constraint

**If user_id doesn't exist:**
- Make sure user exists: `SELECT * FROM users WHERE email = 'harsha168656@gmail.com'`

---

## 📋 Quick Fix Steps

1. **Run table verification:**
   ```
   http://localhost/onlinebidding/api/verify_table_structure.php
   ```
2. **Request new OTP** from app
3. **Check if OTP was stored:**
   ```
   http://localhost/onlinebidding/api/CHECK_OTP_IN_DB.php
   ```
4. **Test verification:**
   ```
   http://localhost/onlinebidding/api/debug_otp_verification.php?email=harsha168656@gmail.com&otp=YOUR_OTP
   ```

---

## ✅ Files Updated

1. ✅ `forgot-password.php` - Added OTP storage verification
2. ✅ `verify_table_structure.php` - Created (verifies and fixes table)
3. ✅ `debug_otp_verification.php` - Created (debugs verification)

---

**Run verify_table_structure.php first - it will fix any table issues automatically!**

