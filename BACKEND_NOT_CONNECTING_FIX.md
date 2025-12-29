# 🔧 Fix: Backend Not Connecting for Laptops and Mobiles

## Quick Diagnostic Steps

### Step 1: Test Backend Status
Open in browser: **http://localhost/onlinebidding/test_all_backend.php**

This will show you:
- ✅ Database connection status
- ✅ Data availability (laptops, mobiles)
- ✅ API endpoint status
- ✅ What's missing

### Step 2: Check XAMPP Services
1. Open **XAMPP Control Panel**
2. **Apache** should be green "Running"
3. **MySQL** should be green "Running"
4. If not, click "Start" for both

### Step 3: Run Complete Setup
Open in browser: **http://localhost/onlinebidding/setup_complete.php**

This will:
- Create database (if missing)
- Create tables (if missing)
- Insert laptop data
- Insert mobile data
- Create auctions

### Step 4: Test APIs Directly
**Laptop API:**
- http://localhost/onlinebidding/api/auctions/list.php?category=laptop
- Should return JSON with laptops

**Mobile API:**
- http://localhost/onlinebidding/api/auctions/list.php?category=mobile
- Should return JSON with mobiles

### Step 5: Check Android App Configuration

**File:** `app/src/main/java/com/example/onlinebidding/api/RetrofitInstance.kt`

**Check BASE_URL:**
- **For Emulator**: `"http://10.0.2.2/onlinebidding/"`
- **For Physical Device**: `"http://YOUR_IP/onlinebidding/"`
  - Find your IP: Open CMD, type `ipconfig`
  - Look for IPv4 Address under Wi-Fi
  - Example: `"http://10.148.199.81/onlinebidding/"`

### Step 6: Check Network (Physical Device Only)
- Phone and computer must be on **same WiFi network**
- Test from phone browser: http://YOUR_IP/onlinebidding/api/auctions/list.php?category=laptop
- If it doesn't load, network/firewall issue

---

## Most Common Issues

### Issue 1: XAMPP Not Running
**Solution:** Start Apache and MySQL in XAMPP Control Panel

### Issue 2: No Data in Database
**Solution:** Run `setup_complete.php` in browser

### Issue 3: Wrong IP Address
**Solution:** Update BASE_URL in RetrofitInstance.kt to match your computer's IP

### Issue 4: Different WiFi Networks
**Solution:** Connect phone and computer to same WiFi

### Issue 5: Firewall Blocking
**Solution:** Allow Apache/port 80 through Windows Firewall

---

## Quick Fix Checklist

Run these in order:

1. [ ] Start XAMPP (Apache + MySQL)
2. [ ] Run: http://localhost/onlinebidding/test_all_backend.php
3. [ ] If data missing, run: http://localhost/onlinebidding/setup_complete.php
4. [ ] Test APIs in browser (should return JSON)
5. [ ] Check BASE_URL in RetrofitInstance.kt
6. [ ] Rebuild and run Android app

---

**Start with Step 1 and Step 3 - these fix 90% of connection issues!** 🚀

