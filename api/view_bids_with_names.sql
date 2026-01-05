-- SQL Query to View Bids with User Names and Auction Info
-- Run this in phpMyAdmin to see bids with user names

SELECT 
    b.id as bid_id,
    b.auction_id,
    b.user_id,
    u.name as user_name,
    u.email as user_email,
    b.amount,
    b.created_at as bid_date,
    p.title as product_name,
    p.category,
    a.current_price as auction_current_price,
    a.status as auction_status
FROM bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
ORDER BY b.created_at DESC;

-- Alternative: Simple view with just essential info
SELECT 
    b.id,
    u.name as user_name,
    b.amount,
    p.title as product_name,
    p.category,
    b.created_at
FROM bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
ORDER BY b.id DESC;


