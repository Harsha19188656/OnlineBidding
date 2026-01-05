# ✅ Real-Time Bid Storage: Name, Model, Amount

## 🎯 What Gets Stored When You Place a Bid

When you click "Add Bid" in the Bid Comments screen, the following information is **automatically stored in real-time** to the `bids` table:

### ✅ Stored Directly in `bids` Table:
- **Amount** (₹) - The bid amount you entered
- **User ID** - Your user ID (links to your name)
- **Auction ID** - Links to the product/model
- **Created At** - Timestamp when bid was placed

### ✅ Available via Database Relationships:
- **Name** - From `users` table (via `user_id`)
- **Model/Device** - From `products` table (via `auction_id` → `product_id`)

---

## 📊 How to View Bids with Name, Model, and Amount

### Method 1: Use the SQL Query (Recommended)

1. **Open phpMyAdmin**: `http://localhost/phpmyadmin`
2. **Select** `onlinebidding` database
3. **Click SQL** tab
4. **Copy and paste this query**:

```sql
SELECT 
    b.id as bid_id,
    u.name as candidate_name,
    p.title as device_model,
    p.category as device_category,
    b.amount,
    b.created_at as bid_time
FROM bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
ORDER BY b.created_at DESC;
```

5. **Click "Go"**

**You'll see:**
| bid_id | candidate_name | device_model | device_category | amount | bid_time |
|--------|----------------|--------------|-----------------|--------|----------|
| 5 | Jaya | ASUS ROG Zephyrus G16 | laptop | 100000.00 | 2025-01-01 14:30:00 |
| 4 | Neha Verma | iPhone 15 Pro | mobile | 95000.00 | 2025-01-01 14:25:00 |

---

### Method 2: Use the VIEW (Easier)

If you've created the `bids_with_details` view:

```sql
SELECT 
    candidate_name,
    product_name as device_model,
    amount,
    bid_date
FROM bids_with_details
ORDER BY bid_date DESC;
```

---

## 🔄 Real-Time Updates

**Bids are stored immediately** when you:
1. Enter your name
2. Enter the bid amount
3. Click "Add Bid"

**To see new bids in real-time:**
- Refresh the SQL query in phpMyAdmin
- Or run the query again

---

## 📋 What Happens When You Place a Bid

### Step-by-Step Process:

1. **You enter bid** in the app (name, amount)
2. **App sends request** to `api/bids/place.php`
3. **API validates**:
   - Bid amount > current price
   - User exists
   - Auction is active
4. **API stores bid** in `bids` table:
   ```sql
   INSERT INTO bids (auction_id, user_id, amount, created_at) 
   VALUES (7, 25, 100000.00, NOW())
   ```
5. **API updates auction** current price
6. **API logs** the bid with name and model:
   ```
   BID INSERTED: ID=5 | Name: Jaya | Model: ASUS ROG Zephyrus G16 | Amount: ₹100000 | Auction: 7
   ```
7. **Bid appears** in database immediately

---

## ✅ Verify Bids Are Storing

### Quick Check:

```sql
-- Count total bids
SELECT COUNT(*) as total_bids FROM bids;

-- See latest 5 bids
SELECT 
    b.id,
    u.name,
    p.title as model,
    b.amount,
    b.created_at
FROM bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
ORDER BY b.id DESC
LIMIT 5;
```

---

## 🎯 Example: Viewing Bids for a Specific Device

To see all bids for a specific product (e.g., "ASUS ROG Zephyrus G16"):

```sql
SELECT 
    u.name as candidate_name,
    b.amount,
    b.created_at as bid_time
FROM bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
WHERE p.title = 'ASUS ROG Zephyrus G16'
ORDER BY b.amount DESC;
```

---

## 📝 Summary

✅ **Name** - Stored via `user_id` (from `users` table)  
✅ **Model** - Stored via `auction_id` → `product_id` (from `products` table)  
✅ **Amount** - Stored directly in `bids.amount`  
✅ **Real-Time** - Bids are saved immediately when placed  

**All information is available in the database and can be viewed using SQL JOIN queries!**

---

## 🔍 Check PHP Error Log

To see bid storage in real-time, check:
**Location**: `C:\xampp\apache\logs\error.log`

Look for:
```
BID INSERTED: ID=5 | Name: Jaya | Model: ASUS ROG Zephyrus G16 | Amount: ₹100000 | Auction: 7
```

This confirms the bid was stored with all information!

