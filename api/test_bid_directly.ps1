# Test Bid API Directly
# This script helps you test if bids are being stored

Write-Host "=== Testing Bid API ===" -ForegroundColor Cyan
Write-Host ""

# Configuration
$baseUrl = "http://localhost/onlinebidding/api"
$auctionId = 7
$userId = 25

# Step 1: Check current auction price
Write-Host "Step 1: Checking current auction price..." -ForegroundColor Yellow
try {
    $auctionResponse = Invoke-RestMethod -Uri "$baseUrl/auctions/details.php?id=$auctionId" -Method GET
    if ($auctionResponse.success) {
        $currentPrice = $auctionResponse.auction.current_price
        Write-Host "✅ Current price: ₹$currentPrice" -ForegroundColor Green
        Write-Host ""
        
        # Step 2: Calculate next bid (must be higher)
        $nextBid = [math]::Round($currentPrice + 10000, 0)
        Write-Host "Step 2: Placing bid of ₹$nextBid (must be higher than ₹$currentPrice)..." -ForegroundColor Yellow
        
        # Step 3: Place bid
        $body = @{
            auction_id = $auctionId
            amount = $nextBid
            user_id = $userId
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "$baseUrl/bids/place.php" `
            -Method POST `
            -ContentType "application/json" `
            -Body $body
        
        if ($response.success) {
            Write-Host "✅ SUCCESS! Bid placed!" -ForegroundColor Green
            Write-Host "   Bid ID: $($response.bid_id)" -ForegroundColor Green
            Write-Host "   New price: ₹$($response.current_price)" -ForegroundColor Green
            Write-Host ""
            Write-Host "Check phpMyAdmin to verify the bid was stored!" -ForegroundColor Cyan
        } else {
            Write-Host "❌ FAILED: $($response.error)" -ForegroundColor Red
        }
    } else {
        Write-Host "❌ Failed to get auction details: $($auctionResponse.error)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Make sure:" -ForegroundColor Yellow
    Write-Host "1. XAMPP MySQL is running" -ForegroundColor Yellow
    Write-Host "2. API files are in C:\xampp\htdocs\onlinebidding\api\" -ForegroundColor Yellow
    Write-Host "3. Database 'onlinebidding' exists" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Cyan

