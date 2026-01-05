-- View Bids with Name, Model, and Amount in Real-Time
-- This query shows all bids with user name and product model
-- Run this in phpMyAdmin to see bids as they are placed

USE onlinebidding;

-- Main query to view all bids with details
SELECT 
    b.id as bid_id,
    u.name as candidate_name,
    p.title as device_model,
    p.category as device_category,
    b.amount,
    b.created_at as bid_time,
    a.id as auction_id,
    a.current_price as current_auction_price
FROM bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
ORDER BY b.created_at DESC;

-- Alternative: View only latest 10 bids
-- SELECT 
--     b.id as bid_id,
--     u.name as candidate_name,
--     p.title as device_model,
--     b.amount,
--     b.created_at as bid_time
-- FROM bids b
-- LEFT JOIN users u ON b.user_id = u.id
-- LEFT JOIN auctions a ON b.auction_id = a.id
-- LEFT JOIN products p ON a.product_id = p.id
-- ORDER BY b.created_at DESC
-- LIMIT 10;

-- View bids for a specific auction
-- Replace 7 with your auction_id
-- SELECT 
--     b.id as bid_id,
--     u.name as candidate_name,
--     p.title as device_model,
--     b.amount,
--     b.created_at as bid_time
-- FROM bids b
-- LEFT JOIN users u ON b.user_id = u.id
-- LEFT JOIN auctions a ON b.auction_id = a.id
-- LEFT JOIN products p ON a.product_id = p.id
-- WHERE b.auction_id = 7
-- ORDER BY b.amount DESC;

