# 📊 How to View Bids with Name and Model in phpMyAdmin

## ✅ Easiest Method: Use the VIEW

1. **Open phpMyAdmin**: `http://localhost/phpmyadmin`
2. **In the left sidebar**, find `onlinebidding` database
3. **Expand "Views" section**
4. **Click on `bids_with_details`**
5. **All bids will display automatically** with:
   - ✅ Candidate Name
   - ✅ Amount
   - ✅ Product/Model Name
   - ✅ Category
   - ✅ Bid Date

**No SQL query needed!** Just click and view! 🎉

---

## 📋 What You'll See

| bid_id | auction_id | candidate_name | amount | product_name | product_category | bid_date |
|--------|------------|----------------|--------|--------------|------------------|----------|
| 2 | 7 | Admin User | 210000.00 | ASUS ROG Zephyrus G16 | laptop | 2025-12-31 10:12:21 |

---

## 🔍 Alternative: Direct SQL Query

If you want to query the `bids` table directly:

1. **Click on `bids` table** in left sidebar
2. **Click "SQL" tab**
3. **Paste this query**:

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

4. **Click "Go"**

---

## ✅ What Gets Stored When You Place a Bid

When you place a bid **more than ₹5000**:

1. **Bid Amount** → Stored in `bids.amount` column
2. **Candidate Name** → Retrieved from `users.name` (via `bids.user_id`)
3. **Model Name** → Retrieved from `products.title` (via `auctions.product_id`)

**Example:**
- You place bid: ₹10,000
- Your name: "Admin User" (from users table)
- Model: "ASUS ROG Zephyrus G16" (from products table)
- All stored in `bids` table with proper relationships!

---

## 🎯 View Highest Bid for Each Model

To see the highest bid for each product/model:

```sql
SELECT 
    p.title as product_name,
    u.name as highest_bidder,
    MAX(b.amount) as highest_bid,
    b.created_at as bid_date
FROM bids b
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
LEFT JOIN users u ON b.user_id = u.id
GROUP BY a.product_id
ORDER BY highest_bid DESC;
```

---

## 📝 Summary

- ✅ **Bids > ₹5000** can be placed
- ✅ **Candidate name** is stored (via user_id → users table)
- ✅ **Model name** is stored (via auction_id → auctions → products table)
- ✅ **Highest bid** is tracked in `auctions.current_price`
- ✅ **View all bids** using `bids_with_details` VIEW in phpMyAdmin

**Just click on `bids_with_details` in the left sidebar - that's it!** 🎉


