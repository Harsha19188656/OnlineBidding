-- Add name and model columns to bids table
-- This allows you to see candidate name and device model directly in the bids table

USE onlinebidding;

-- Add candidate_name column
ALTER TABLE `bids` 
ADD COLUMN `candidate_name` VARCHAR(255) NULL AFTER `user_id`;

-- Add device_model column
ALTER TABLE `bids` 
ADD COLUMN `device_model` VARCHAR(255) NULL AFTER `candidate_name`;

-- Add index for faster queries
ALTER TABLE `bids` 
ADD INDEX `idx_candidate_name` (`candidate_name`),
ADD INDEX `idx_device_model` (`device_model`);

-- Update existing bids with name and model data
UPDATE bids b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN auctions a ON b.auction_id = a.id
LEFT JOIN products p ON a.product_id = p.id
SET 
    b.candidate_name = u.name,
    b.device_model = p.title
WHERE b.candidate_name IS NULL OR b.device_model IS NULL;

-- Verify the changes
SELECT 
    id,
    auction_id,
    user_id,
    candidate_name,
    device_model,
    amount,
    created_at
FROM bids
ORDER BY id DESC
LIMIT 10;

