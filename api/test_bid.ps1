# Test Bid API
Write-Host "Testing Bid API..." -ForegroundColor Yellow

$body = @{
    auction_id = 7
    amount = 220000
    user_id = 25
} | ConvertTo-Json

Write-Host "`nSending POST request to: http://localhost/onlinebidding/api/bids/place.php" -ForegroundColor Cyan
Write-Host "Body: $body`n" -ForegroundColor Gray

try {
    $response = Invoke-RestMethod -Uri "http://localhost/onlinebidding/api/bids/place.php" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body
    
    Write-Host "✅ SUCCESS!" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 10) -ForegroundColor Green
} catch {
    Write-Host "❌ ERROR!" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    if ($_.ErrorDetails) {
        Write-Host $_.ErrorDetails.Message -ForegroundColor Red
    }
}

Write-Host "`nCheck database:" -ForegroundColor Yellow
Write-Host "SELECT * FROM bids ORDER BY created_at DESC LIMIT 5;" -ForegroundColor Cyan


