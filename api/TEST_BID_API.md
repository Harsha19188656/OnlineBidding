# 🧪 Test Bid API Directly

## Quick Test Commands

### 1. Test API Endpoint (Browser)
Open in browser:
```
http://localhost/onlinebidding/api/auctions/details.php?id=7
```

Should return JSON with bids array.

---

### 2. Test Place Bid (PowerShell)

```powershell
$body = @{
    auction_id = 7
    amount = 220000
    user_id = 25
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost/onlinebidding/api/bids/place.php" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

**Expected Response:**
```json
{
    "success": true,
    "current_price": 220000,
    "message": "Bid placed successfully"
}
```

---

### 3. Check Database After Test

Run in phpMyAdmin:
```sql
SELECT * FROM bids ORDER BY created_at DESC LIMIT 5;
```

Should see the new bid.

---

### 4. Check Current Price

```sql
SELECT id, current_price, status FROM auctions WHERE id = 7;
```

**Important:** Bid amount must be **higher** than `current_price`!

---

## Common Errors

### Error: "Bid amount must be higher than current price"
**Solution:** Increase the bid amount. Check current price first:
```sql
SELECT current_price FROM auctions WHERE id = 7;
```

### Error: "Auction not found"
**Solution:** Check if auction exists:
```sql
SELECT * FROM auctions WHERE id = 7;
```

### Error: "User not found"
**Solution:** Check if user_id 25 exists:
```sql
SELECT * FROM users WHERE id = 25;
```

### Error: "Auction is not active"
**Solution:** Check auction status:
```sql
SELECT id, status FROM auctions WHERE id = 7;
```
Status must be `'active'`.

---

## Test from App

1. Open app
2. Go to Laptop List
3. Click on a laptop (should load from API - check Logcat)
4. Click "Bid History"
5. **Check Logcat** - Should see:
   ```
   🔍 LaunchedEffect triggered - auctionId: 7, currentAuctionId: 7
   🔌 Loading bids for auction ID: 7
   ✅ Loaded X bids from API
   ```
6. Click "Add Bid"
7. Enter amount (must be higher than current price)
8. Submit
9. **Check Logcat** - Should see:
   ```
   🔍 addUserBid called - name: ..., amount: ..., currentAuctionId: 7
   💰 Placing bid: ₹220000 for auction 7
   ✅ Bid placed successfully!
   ```
10. Check phpMyAdmin - new bid should appear

---

## If Still Not Working

1. **Check XAMPP** - Apache and MySQL must be running
2. **Check API URL** - In `RetrofitInstance.kt`, `BASE_URL` should be:
   - Emulator: `http://10.0.2.2/onlinebidding/api/`
   - Physical device: `http://[YOUR_PC_IP]/onlinebidding/api/`
3. **Check Logcat** - Look for error messages
4. **Test API directly** - Use browser/PowerShell commands above




