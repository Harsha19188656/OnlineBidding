# Test Payment API
# This will help you debug payment issues

Write-Host "=== Testing Payment API ===" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost/onlinebidding/api"

# Test data
$body = @{
    user_id = 25
    amount = 70
    payment_method = "phonepe"
    upi_id = "harsha@paytm"
} | ConvertTo-Json

Write-Host "Request:" -ForegroundColor Yellow
Write-Host $body -ForegroundColor Gray
Write-Host ""

try {
    Write-Host "Sending request..." -ForegroundColor Yellow
    $response = Invoke-RestMethod -Uri "$baseUrl/payments/create.php" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body `
        -ErrorAction Stop
    
    Write-Host ""
    Write-Host "✅✅✅ SUCCESS!" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Green
    $response | ConvertTo-Json
    Write-Host ""
    Write-Host "Payment ID: $($response.payment_id)" -ForegroundColor Green
    Write-Host "Transaction ID: $($response.transaction_id)" -ForegroundColor Green
    Write-Host "Status: $($response.status)" -ForegroundColor Green
    Write-Host ""
    Write-Host "✅ Payment was saved to database!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Check database:" -ForegroundColor Yellow
    Write-Host "SELECT * FROM payments ORDER BY id DESC LIMIT 1;" -ForegroundColor Gray
    
} catch {
    Write-Host ""
    Write-Host "❌❌❌ ERROR!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body:" -ForegroundColor Red
        Write-Host $responseBody -ForegroundColor Red
        Write-Host ""
        
        # Try to parse JSON error
        try {
            $errorJson = $responseBody | ConvertFrom-Json
            Write-Host "Error Message: $($errorJson.error)" -ForegroundColor Red
            Write-Host ""
            
            if ($errorJson.error -like "*table*" -or $errorJson.error -like "*doesn't exist*") {
                Write-Host "🔧 FIX: Payments table doesn't exist!" -ForegroundColor Yellow
                Write-Host "   Run this SQL in phpMyAdmin:" -ForegroundColor Yellow
                Write-Host "   File: api/create_payments_table.sql" -ForegroundColor Yellow
            }
        } catch {
            Write-Host "Could not parse error response" -ForegroundColor Red
        }
    }
    
    Write-Host ""
    Write-Host "Troubleshooting:" -ForegroundColor Yellow
    Write-Host "1. Check if payments table exists: SHOW TABLES LIKE 'payments';" -ForegroundColor Yellow
    Write-Host "2. Check if user 25 exists: SELECT id FROM users WHERE id = 25;" -ForegroundColor Yellow
    Write-Host "3. Check PHP error log: C:\xampp\apache\logs\error.log" -ForegroundColor Yellow
    Write-Host "4. Make sure MySQL is running in XAMPP" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Cyan

