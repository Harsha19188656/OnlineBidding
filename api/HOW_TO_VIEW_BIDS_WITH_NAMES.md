# 📊 How to View Bids with Candidate Names and Product Names

## ✅ Solution: Use SQL VIEW

I've created a **VIEW** called `bids_with_details` that shows all bid information including:
- ✅ Candidate Name
- ✅ Amount
- ✅ Product Name (Model)
- ✅ Product Category
- ✅ Bid Date

---

## 🚀 Quick Steps:

### Step 1: Create the VIEW (One-time setup)

1. Open phpMyAdmin: `http://localhost/phpmyadmin`
2. Select `onlinebidding` database
3. Click **SQL** tab
4. Copy and paste this SQL:

```sql
DROP VIEW IF EXISTS bids_with_details;

CREATE VIEW bids_with_details AS
SELECT 
    b.id as bid_id,
    b.auction_id,
    u.name as candidate_name,
    u.email as candidate_email,
    b.amount,
    p.title as product_name,
    p.category as product_category,
    a.current_price as auction_current_price,
    a.status as auction_status,
    b.created_at as bid_date
FROM bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
ORDER BY b.created_at DESC;
```

5. Click **Go**

---

### Step 2: View Bids with Names (Every time)

Now you can simply run:

```sql
SELECT * FROM bids_with_details;
```

**Or see just the important columns:**

```sql
SELECT 
    candidate_name,
    amount,
    product_name,
    product_category,
    bid_date
FROM bids_with_details
ORDER BY bid_date DESC;
```

---

## 📋 What You'll See:

| bid_id | candidate_name | amount | product_name | product_category | bid_date |
|--------|----------------|--------|--------------|------------------|----------|
| 2 | Admin User | 210000.00 | ASUS ROG Zephyrus G16 | laptop | 2025-12-31 10:12:21 |

---

## ✅ How Multiple Bids Work:

**When you place multiple bids:**
1. ✅ Each bid is stored as a **separate row** in the `bids` table
2. ✅ Same product can have multiple bids (different amounts)
3. ✅ Same candidate can bid multiple times
4. ✅ All bids are stored with:
   - Candidate name (from users table)
   - Amount
   - Product name (from products table)
   - Date/time

**Example - Multiple bids for same product:**
- Bid 1: Admin User, ₹210,000, ASUS ROG Zephyrus G16
- Bid 2: Admin User, ₹220,000, ASUS ROG Zephyrus G16  ← New bid stored!
- Bid 3: Admin User, ₹230,000, ASUS ROG Zephyrus G16  ← Another new bid!

All will show in the view! ✅

---

## 🔍 Alternative: Direct Query (Without VIEW)

If you prefer not to use a VIEW, use this query directly:

```sql
SELECT 
    b.id,
    u.name as candidate_name,
    b.amount,
    p.title as product_name,
    p.category,
    b.created_at
FROM bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
ORDER BY b.created_at DESC;
```

---

## ✅ Summary:

1. ✅ **All bids are stored** - Each bid creates a new row
2. ✅ **Multiple bids for same product** - Stored separately
3. ✅ **Candidate name** - Available via JOIN with users table
4. ✅ **Product name** - Available via JOIN with products table
5. ✅ **Use the VIEW** - Easy way to see everything together

**Everything is working correctly! Just use the VIEW to see names with amounts!** 🎉




