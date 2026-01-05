-- Update all existing auctions to start at ₹5000
-- Run this in phpMyAdmin SQL tab

USE onlinebidding;

-- Update auctions that don't have any bids yet (current_price = start_price)
-- Set both start_price and current_price to 5000
UPDATE auctions 
SET start_price = 5000.00, 
    current_price = 5000.00
WHERE current_price = start_price 
  AND status = 'active';

-- For auctions that already have bids, only update start_price
-- (current_price should remain as the highest bid)
UPDATE auctions 
SET start_price = 5000.00
WHERE current_price > start_price 
  AND status = 'active';

-- Verify the changes
SELECT 
    id,
    product_id,
    start_price,
    current_price,
    status
FROM auctions
ORDER BY id;


