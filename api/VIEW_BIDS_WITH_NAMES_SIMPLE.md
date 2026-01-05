# 📊 View Bids with Candidate Name and Amount

## ✅ Current Database Structure:

The `bids` table stores:
- `id` - Bid ID
- `auction_id` - Which auction
- `user_id` - User ID (number like 25)
- `amount` - Bid amount
- `created_at` - Date/time

**The name is in the `users` table**, so we need to JOIN them!

---

## 🔍 Quick Query to See Names with Amounts:

### Step 1: Open phpMyAdmin SQL Tab

1. Go to: `http://localhost/phpmyadmin`
2. Select `onlinebidding` database (left sidebar)
3. Click **SQL** tab

### Step 2: Copy and Paste This Query:

```sql
SELECT 
    b.id,
    u.name as candidate_name,
    b.amount,
    p.title as product_name,
    b.created_at
FROM bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
ORDER BY b.id DESC;
```

### Step 3: Click "Go"

**You'll see:**
- ✅ Bid ID
- ✅ **Candidate Name** (from users table)
- ✅ **Amount** (from bids table)
- ✅ Product Name
- ✅ Date

---

## 📋 Example Output:

| id | candidate_name | amount | product_name | created_at |
|----|----------------|--------|--------------|------------|
| 2 | Admin User | 210000.00 | ASUS ROG Zephyrus G16 | 2025-12-31 10:12:21 |

---

## 💡 Why Not Store Name Directly?

**Good Database Design:**
- ✅ Store `user_id` in bids table
- ✅ Store name in users table
- ✅ JOIN when you need to see the name

**Benefits:**
- If user changes name, it updates everywhere
- No duplicate data
- Follows database normalization rules

---

## 🎯 Simple Query (Just Name + Amount):

If you only want name and amount:

```sql
SELECT 
    u.name as candidate_name,
    b.amount
FROM bids b
LEFT JOIN users u ON b.user_id = u.id
ORDER BY b.amount DESC;
```

---

## ✅ Summary:

**The bids ARE being stored correctly!** 
- Amount: ✅ Stored in `bids.amount`
- User ID: ✅ Stored in `bids.user_id`
- Name: ✅ Available via JOIN with `users` table

Just use the JOIN query to see everything together! 🎉


