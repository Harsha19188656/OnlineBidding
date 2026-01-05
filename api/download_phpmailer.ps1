# PowerShell script to download PHPMailer
# Run this script in the api folder

Write-Host "Downloading PHPMailer..." -ForegroundColor Cyan

$apiPath = "C:\xampp\htdocs\onlinebidding\api"
$phpmailerUrl = "https://github.com/PHPMailer/PHPMailer/archive/refs/tags/v6.9.0.zip"
$zipFile = "$apiPath\PHPMailer.zip"
$extractPath = "$apiPath\PHPMailer"

# Create api directory if it doesn't exist
if (-not (Test-Path $apiPath)) {
    Write-Host "Creating directory: $apiPath" -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $apiPath -Force | Out-Null
}

# Download PHPMailer
try {
    Write-Host "Downloading from: $phpmailerUrl" -ForegroundColor Yellow
    Invoke-WebRequest -Uri $phpmailerUrl -OutFile $zipFile -UseBasicParsing
    Write-Host "Download complete!" -ForegroundColor Green
} catch {
    Write-Host "Error downloading PHPMailer: $_" -ForegroundColor Red
    Write-Host "Please download manually from: https://github.com/PHPMailer/PHPMailer/releases" -ForegroundColor Yellow
    exit 1
}

# Extract ZIP file
try {
    Write-Host "Extracting PHPMailer..." -ForegroundColor Yellow
    if (Test-Path $extractPath) {
        Remove-Item -Path $extractPath -Recurse -Force
    }
    Expand-Archive -Path $zipFile -DestinationPath $apiPath -Force
    
    # Move files from PHPMailer-6.9.0 to PHPMailer
    $versionedPath = "$apiPath\PHPMailer-6.9.0"
    if (Test-Path $versionedPath) {
        Move-Item -Path $versionedPath -Destination $extractPath -Force
    }
    
    Write-Host "Extraction complete!" -ForegroundColor Green
} catch {
    Write-Host "Error extracting PHPMailer: $_" -ForegroundColor Red
    exit 1
}

# Clean up ZIP file
if (Test-Path $zipFile) {
    Remove-Item -Path $zipFile -Force
}

Write-Host ""
Write-Host "✅ PHPMailer installed successfully!" -ForegroundColor Green
Write-Host "Location: $extractPath" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Configure email settings in: config_email.php" -ForegroundColor White
Write-Host "2. For Gmail: Generate App Password at https://myaccount.google.com/apppasswords" -ForegroundColor White
Write-Host "3. Test forgot password functionality" -ForegroundColor White
Write-Host ""

