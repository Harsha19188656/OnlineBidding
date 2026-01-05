# Comprehensive Bid API Test Script
# This will test the bid API and show you exactly what's happening

Write-Host "=== BID API DIAGNOSTIC TEST ===" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost/onlinebidding/api"
$auctionId = 7
$userId = 25

# Step 1: Test API accessibility
Write-Host "Step 1: Testing API accessibility..." -ForegroundColor Yellow
try {
    $testUrl = "$baseUrl/debug_bid_storage.php"
    $debugResponse = Invoke-RestMethod -Uri $testUrl -Method GET -ErrorAction Stop
    Write-Host "✅ API is accessible" -ForegroundColor Green
    Write-Host "   Database connection: $($debugResponse.checks.database_connection)" -ForegroundColor Green
    Write-Host "   Bids table: $($debugResponse.checks.bids_table_exists)" -ForegroundColor Green
    Write-Host "   Total bids: $($debugResponse.checks.total_bids)" -ForegroundColor Green
    
    if ($debugResponse.checks.auction_7 -is [hashtable]) {
        Write-Host "   Auction 7 current price: ₹$($debugResponse.checks.auction_7.current_price)" -ForegroundColor Green
        Write-Host "   Auction 7 status: $($debugResponse.checks.auction_7.status)" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️ Auction 7: $($debugResponse.checks.auction_7)" -ForegroundColor Yellow
    }
    
    if ($debugResponse.checks.user_25 -is [hashtable]) {
        Write-Host "   User 25: $($debugResponse.checks.user_25.name) ($($debugResponse.checks.user_25.email))" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️ User 25: $($debugResponse.checks.user_25)" -ForegroundColor Yellow
    }
    
    Write-Host ""
} catch {
    Write-Host "❌ Cannot access API: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   Make sure:" -ForegroundColor Yellow
    Write-Host "   1. XAMPP Apache is running" -ForegroundColor Yellow
    Write-Host "   2. API files are in C:\xampp\htdocs\onlinebidding\api\" -ForegroundColor Yellow
    Write-Host "   3. Try: http://localhost/onlinebidding/api/debug_bid_storage.php in browser" -ForegroundColor Yellow
    exit
}

# Step 2: Get current auction price
Write-Host "Step 2: Getting current auction price..." -ForegroundColor Yellow
try {
    $auctionResponse = Invoke-RestMethod -Uri "$baseUrl/auctions/details.php?id=$auctionId" -Method GET -ErrorAction Stop
    if ($auctionResponse.success) {
        $currentPrice = $auctionResponse.auction.current_price
        Write-Host "✅ Current price: ₹$currentPrice" -ForegroundColor Green
        Write-Host ""
        
        # Step 3: Calculate next bid
        $nextBid = [math]::Round($currentPrice + 10000, 0)
        Write-Host "Step 3: Placing bid of ₹$nextBid (must be > ₹$currentPrice)..." -ForegroundColor Yellow
        
        # Step 4: Place bid
        $body = @{
            auction_id = $auctionId
            amount = $nextBid
            user_id = $userId
        } | ConvertTo-Json
        
        Write-Host "   Request body: $body" -ForegroundColor Gray
        
        $response = Invoke-RestMethod -Uri "$baseUrl/bids/place.php" `
            -Method POST `
            -ContentType "application/json" `
            -Body $body `
            -ErrorAction Stop
        
        Write-Host ""
        if ($response.success) {
            Write-Host "✅✅✅ SUCCESS! Bid placed!" -ForegroundColor Green
            Write-Host "   Bid ID: $($response.bid_id)" -ForegroundColor Green
            Write-Host "   New price: ₹$($response.current_price)" -ForegroundColor Green
            Write-Host ""
            Write-Host "Step 4: Verifying bid was stored..." -ForegroundColor Yellow
            
            # Step 5: Verify bid was stored
            Start-Sleep -Seconds 1
            $verifyResponse = Invoke-RestMethod -Uri "$baseUrl/debug_bid_storage.php" -Method GET
            $recentBids = $verifyResponse.checks.recent_bids
            
            if ($recentBids.Count -gt 0) {
                $latestBid = $recentBids[0]
                Write-Host "✅ Latest bid in database:" -ForegroundColor Green
                Write-Host "   ID: $($latestBid.id)" -ForegroundColor Green
                Write-Host "   Amount: ₹$($latestBid.amount)" -ForegroundColor Green
                Write-Host "   Auction ID: $($latestBid.auction_id)" -ForegroundColor Green
                Write-Host "   User ID: $($latestBid.user_id)" -ForegroundColor Green
                Write-Host "   Created: $($latestBid.created_at)" -ForegroundColor Green
                
                if ($latestBid.id -eq $response.bid_id) {
                    Write-Host ""
                    Write-Host "✅✅✅ CONFIRMED: Bid is stored in database!" -ForegroundColor Green
                } else {
                    Write-Host ""
                    Write-Host "⚠️ WARNING: Bid ID mismatch. Check database manually." -ForegroundColor Yellow
                }
            } else {
                Write-Host "⚠️ No bids found in database" -ForegroundColor Yellow
            }
        } else {
            Write-Host "❌ FAILED: $($response.error)" -ForegroundColor Red
        }
    } else {
        Write-Host "❌ Failed to get auction: $($auctionResponse.error)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "   Response: $responseBody" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Check PHP error log: C:\xampp\apache\logs\error.log" -ForegroundColor Yellow
Write-Host "2. Check database: SELECT * FROM bids ORDER BY id DESC LIMIT 5;" -ForegroundColor Yellow
Write-Host "3. Check Android Logcat for app-side errors" -ForegroundColor Yellow

