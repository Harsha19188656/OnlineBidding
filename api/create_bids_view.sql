-- Create a VIEW to easily see bids with candidate names and product names
-- This makes it easy to view all bid information in one place

USE onlinebidding;

-- Drop the view if it already exists
DROP VIEW IF EXISTS bids_with_details;

-- Create the view
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

-- Now you can simply query:
-- SELECT * FROM bids_with_details;

-- Or see just name and amount:
-- SELECT candidate_name, amount, product_name, bid_date FROM bids_with_details;




