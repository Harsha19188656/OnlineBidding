# 📊 How to View Bids with Candidate Name and Model Name

## ✅ Quick View in phpMyAdmin

### Method 1: Use the VIEW (Easiest)

1. **Open phpMyAdmin**: `http://localhost/phpmyadmin`
2. **Click on `bids_with_details`** in the left sidebar under "Views" section
3. **All bids will display** with:
   - Candidate Name
   - Amount
   - Product/Model Name
   - Category
   - Bid Date

**No SQL query needed!** Just click and view! ✅

---

### Method 2: Direct SQL Query

If you want to see bids in the `bids` table directly:

```sql
SELECT 
    b.id,
    b.auction_id,
    u.name as candidate_name,
    b.amount,
    p.title as product_name,
    p.category as product_category,
    b.created_at as bid_date
FROM bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
ORDER BY b.amount DESC, b.created_at DESC;
```

This shows:
- **candidate_name**: The name of the person who placed the bid
- **product_name**: The model/product name (e.g., "ASUS ROG Zephyrus G16")
- **amount**: The bid amount
- **bid_date**: When the bid was placed

---

## 🎯 What Gets Stored

When you place a bid:

1. **Bid Amount** → Stored in `bids.amount`
2. **Candidate Name** → Stored via `bids.user_id` → `users.name`
3. **Model Name** → Stored via `bids.auction_id` → `auctions.product_id` → `products.title`

**Example:**
- Bid Amount: ₹220,000
- Candidate Name: Admin User (from users table, user_id = 25)
- Model Name: ASUS ROG Zephyrus G16 (from products table via auction)

---

## 📋 View Highest Bid for Each Auction

To see the highest bid for each auction:

```sql
SELECT 
    a.id as auction_id,
    p.title as product_name,
    u.name as highest_bidder,
    b.amount as highest_bid,
    b.created_at as bid_date
FROM auctions a
LEFT JOIN products p ON a.product_id = p.id
LEFT JOIN bids b ON a.id = b.auction_id
LEFT JOIN users u ON b.user_id = u.id
WHERE b.amount = (
    SELECT MAX(amount) 
    FROM bids 
    WHERE auction_id = a.id
)
ORDER BY b.amount DESC;
```

---

## ✅ Verification

After placing a bid:

1. **Check in phpMyAdmin**:
   - Go to `bids_with_details` VIEW
   - Or run the SQL query above
   - You should see:
     - Your name (candidate_name)
     - The bid amount
     - The product/model name

2. **Check in App**:
   - Open Bid Comments screen
   - Your bid should appear with your name
   - Highest bid should be marked as "Top Bid"

---

## 🔍 Current Bids in Database

To see all current bids:

```sql
SELECT * FROM bids_with_details ORDER BY amount DESC;
```

This shows all bids sorted by amount (highest first), with names and product names included!


