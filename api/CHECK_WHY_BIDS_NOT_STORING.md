# 🔍 Check Why Bids Are Not Storing

## Step 1: Run Debug Script

Open in browser:
```
http://localhost/onlinebidding/api/debug_bid_storage.php
```

This will show you:
- ✅ Database connection status
- ✅ If tables exist
- ✅ Current bid count
- ✅ Recent bids
- ✅ Auction 7 status
- ✅ User 25 status

---

## Step 2: Check PHP Error Logs

The API now logs all bid attempts. Check your PHP error log:

**XAMPP Error Log Location:**
```
C:\xampp\apache\logs\error.log
```

**Look for these messages:**
- `BID REQUEST: {...}` - Shows what data was received
- `BID INSERTED: ID=X` - Bid was inserted successfully
- `BID COMMITTED: Bid ID=X` - Transaction committed
- `BID INSERT FAILED: ...` - Insert failed
- `AUCTION UPDATE FAILED: ...` - Update failed

---

## Step 3: Test API Directly

Run this PowerShell command:

```powershell
$body = @{
    auction_id = 7
    amount = 220000
    user_id = 25
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost/onlinebidding/api/bids/place.php" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body
    
    Write-Host "Response:" -ForegroundColor Green
    $response | ConvertTo-Json
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Response: $($_.Exception.Response)" -ForegroundColor Red
}
```

---

## Step 4: Check Android Studio Logcat

1. Open **Android Studio**
2. Open **Logcat** tab
3. Filter by: `BidComments` or `AuctionDetails`
4. Look for:
   - `💰 Placing bid: ₹XXX for auction X` - Request sent
   - `✅ Bid placed successfully!` - Success
   - `❌ Bid rejected by API: [error]` - Rejected with reason
   - `❌ HTTP Error XXX: [error]` - HTTP error
   - `❌ Network error: [message]` - Network issue

---

## Step 5: Check Database Directly

In phpMyAdmin, run:

```sql
-- Check if bid was inserted
SELECT * FROM bids ORDER BY id DESC LIMIT 5;

-- Check auction current price
SELECT id, current_price, status FROM auctions WHERE id = 7;

-- Check if transaction was rolled back
SELECT COUNT(*) FROM bids WHERE auction_id = 7;
```

---

## Common Issues & Solutions

### Issue 1: Network Error
**Symptom:** "Network error: Unable to resolve host"
**Solution:** 
- Check IP address in `RetrofitInstance.kt`
- For emulator: Use `http://10.0.2.2/onlinebidding/`
- For physical device: Use your computer's WiFi IP

### Issue 2: Database Error
**Symptom:** "Database error. Please try again later."
**Solution:**
- Check MySQL is running in XAMPP
- Check database name is `onlinebidding`
- Check tables exist

### Issue 3: Transaction Rollback
**Symptom:** Bid appears to succeed but doesn't show in database
**Solution:**
- Check PHP error log for rollback reasons
- Check foreign key constraints
- Check if auction/user exists

### Issue 4: API Not Reached
**Symptom:** No logs in PHP error log
**Solution:**
- Check API file path: `C:\xampp\htdocs\onlinebidding\api\bids\place.php`
- Check Apache is running
- Test: `http://localhost/onlinebidding/api/debug_bid_storage.php`

---

## Quick Diagnostic Checklist

- [ ] MySQL is running in XAMPP
- [ ] Apache is running in XAMPP
- [ ] Database `onlinebidding` exists
- [ ] Tables `bids`, `auctions`, `users` exist
- [ ] Auction ID 7 exists and is active
- [ ] User ID 25 exists
- [ ] API file exists: `C:\xampp\htdocs\onlinebidding\api\bids\place.php`
- [ ] IP address in app matches your setup
- [ ] Checked PHP error log
- [ ] Checked Android Logcat
- [ ] Tested API directly with PowerShell

---

## Still Not Working?

1. **Share the debug output:**
   - Run `debug_bid_storage.php` and share the JSON output
   - Share relevant lines from PHP error log
   - Share Logcat output from Android Studio

2. **Test the exact request:**
   - Use the PowerShell script above
   - Share the exact response you get

3. **Check file permissions:**
   - Make sure PHP can write to error log
   - Check file permissions on API files

