# 🧪 Test Bid Storage - Step by Step

## ✅ Quick Test:

### Step 1: Test API Directly

Run this in PowerShell or Command Prompt:

```powershell
$body = '{"auction_id":7,"amount":210000,"user_id":25}'
Invoke-WebRequest -Uri "http://localhost/onlinebidding/api/bids/place.php" -Method POST -ContentType "application/json" -Body $body -UseBasicParsing
```

**Or use curl:**
```bash
curl -X POST http://localhost/onlinebidding/api/bids/place.php -H "Content-Type: application/json" -d "{\"auction_id\":7,\"amount\":210000,\"user_id\":25}"
```

### Step 2: Check Database

```sql
SELECT * FROM bids;
```

### Step 3: Check with Names

```sql
SELECT 
    b.id,
    u.name as user_name,
    b.amount,
    p.title as product_name,
    b.created_at
FROM bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
ORDER BY b.id DESC;
```

---

## 🔍 Troubleshooting:

### If API returns error:

1. **"User not found"** → User ID 25 doesn't exist
   - Check: `SELECT id FROM users;`
   - Use a valid user ID

2. **"Auction not found"** → Auction ID doesn't exist
   - Check: `SELECT id FROM auctions;`
   - Use a valid auction ID

3. **"Bid amount must be higher"** → Amount too low
   - Check current price: `SELECT current_price FROM auctions WHERE id = 7;`
   - Use amount higher than current_price

4. **"Network error"** → XAMPP not running
   - Start Apache and MySQL in XAMPP

---

## 📱 From Android App:

1. Make sure user_id = 25 in the code (already fixed)
2. Place bid from laptop detail screen
3. Check Logcat for errors
4. Check database: `SELECT * FROM bids;`

---

## ✅ Expected Result:

After placing bid, you should see:
- API returns: `{"success":true,"current_price":210000,"message":"Bid placed successfully"}`
- Database shows new row in bids table
- Auction current_price updates to new bid amount




