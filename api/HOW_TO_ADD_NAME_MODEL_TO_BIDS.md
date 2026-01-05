# ✅ Add Name and Model to Bids Table

## 🎯 What This Does

This adds two new columns to the `bids` table:
- **`candidate_name`** - Stores the user's name directly
- **`device_model`** - Stores the product/model name directly

Now when you view the `bids` table in phpMyAdmin, you'll see:
- ✅ Candidate Name
- ✅ Device Model  
- ✅ Amount
- ✅ All in one table!

---

## 🚀 Step-by-Step Instructions

### Step 1: Open phpMyAdmin

1. Go to: `http://localhost/phpmyadmin`
2. Select `onlinebidding` database (left sidebar)

### Step 2: Run the SQL Script

1. Click **SQL** tab (top menu)
2. Copy and paste this SQL:

```sql
USE onlinebidding;

-- Add candidate_name column
ALTER TABLE `bids` 
ADD COLUMN `candidate_name` VARCHAR(255) NULL AFTER `user_id`;

-- Add device_model column
ALTER TABLE `bids` 
ADD COLUMN `device_model` VARCHAR(255) NULL AFTER `candidate_name`;

-- Add indexes for faster queries
ALTER TABLE `bids` 
ADD INDEX `idx_candidate_name` (`candidate_name`),
ADD INDEX `idx_device_model` (`device_model`);
```

3. Click **Go**

You should see: **"3 queries executed successfully"**

### Step 3: Update Existing Bids

If you have existing bids, update them with name and model:

1. Click **SQL** tab again
2. Copy and paste:

```sql
UPDATE bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
SET 
    b.candidate_name = u.name,
    b.device_model = p.title
WHERE b.candidate_name IS NULL OR b.device_model IS NULL;
```

3. Click **Go**

### Step 4: Verify

1. Click on **`bids`** table in left sidebar
2. You should now see:
   - `id`
   - `auction_id`
   - `user_id`
   - **`candidate_name`** ✅ (NEW!)
   - **`device_model`** ✅ (NEW!)
   - `amount`
   - `created_at`

---

## ✅ What Happens Next

**From now on**, when you place a bid:
1. ✅ Name will be stored in `candidate_name` column
2. ✅ Model will be stored in `device_model` column
3. ✅ Amount will be stored in `amount` column
4. ✅ All visible directly in the `bids` table!

---

## 📋 Example: What You'll See

**Before:**
| id | auction_id | user_id | amount | created_at |
|----|------------|---------|--------|------------|
| 2 | 7 | 25 | 210000.00 | 2025-12-31 10:12:21 |

**After:**
| id | auction_id | user_id | **candidate_name** | **device_model** | amount | created_at |
|----|------------|---------|-------------------|------------------|--------|------------|
| 2 | 7 | 25 | **Admin User** | **ASUS ROG Zephyrus G16** | 210000.00 | 2025-12-31 10:12:21 |

---

## 🔄 Real-Time Updates

After running the SQL script:
- ✅ New bids will automatically include name and model
- ✅ Old bids will be updated with name and model
- ✅ You can see everything directly in the `bids` table

---

## ⚠️ If You Get Errors

**Error: "Duplicate column name"**
- The columns already exist! Skip Step 2.

**Error: "Table doesn't exist"**
- Make sure you selected `onlinebidding` database first.

**Error: "Unknown column"**
- Make sure you ran Step 2 before Step 3.

---

## 🎯 Quick Test

After adding columns, place a new bid in your app, then check:

```sql
SELECT * FROM bids ORDER BY id DESC LIMIT 1;
```

You should see the new bid with `candidate_name` and `device_model` filled in!

