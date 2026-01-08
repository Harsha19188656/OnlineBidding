# Backend API Summary

This document summarizes all the backend API endpoints created for the Online Bidding application.

## Database Tables Used

- **users** - User accounts (already created)
- **products** - Product information (already created)
- **auctions** - Auction listings linked to products
- **bids** - User bids on auctions
- **notifications** - User notifications (table exists, API not yet implemented)
- **payments** - Payment records (table exists, API not yet implemented)
- **sessions** - User sessions (table exists, API not yet implemented)

## API Endpoints Created

### 1. Auctions API

#### `GET /api/auctions/list.php`
List all active auctions with optional filtering.

**Query Parameters:**
- `category` (optional) - Filter by category: laptop, mobile, computer, monitor, tablet
- `search` (optional) - Search in title and description
- `limit` (optional, default: 20) - Number of results per page (1-100)
- `offset` (optional, default: 0) - Pagination offset

**Response:**
```json
{
  "success": true,
  "items": [
    {
      "product": {
        "id": 1,
        "title": "MacBook Pro",
        "description": "...",
        "category": "laptop",
        "image_url": "...",
        "specs": "...",
        "condition": "Excellent",
        "base_price": 200000.0,
        "created_at": "2024-01-01 10:00:00"
      },
      "auction": {
        "id": 1,
        "product_id": 1,
        "start_price": 150000.0,
        "current_price": 185000.0,
        "status": "active",
        "start_at": "2024-01-01 10:00:00",
        "end_at": "2024-01-08 10:00:00"
      },
      "name": "MacBook Pro",
      "specs": "...",
      "price": "₹1,85,000",
      "image_url": "..."
    }
  ],
  "count": 1,
  "total": 1
}
```

#### `GET /api/auctions/details.php`
Get detailed information about a specific auction.

**Query Parameters:**
- `id` (required) - Auction ID

**Response:**
```json
{
  "success": true,
  "product": { ... },
  "auction": { ... },
  "bids": [
    {
      "id": 1,
      "auction_id": 1,
      "user_id": 1,
      "amount": 185000.0,
      "created_at": "2024-01-01 11:00:00",
      "user_name": "John Doe"
    }
  ]
}
```

### 2. Bids API

#### `POST /api/bids/place.php`
Place a bid on an auction.

**Request Body:**
```json
{
  "auction_id": 1,
  "amount": 190000.0,
  "user_id": 1
}
```

**Response:**
```json
{
  "success": true,
  "current_price": 190000.0,
  "message": "Bid placed successfully"
}
```

**Validation:**
- Bid amount must be higher than current price
- Auction must be active
- User must exist

### 3. Admin Products API

#### `GET /api/admin/products/list.php`
List all products (admin view with auction info).

**Headers:**
- `Authorization` (optional for now - TODO: implement authentication)

**Query Parameters:**
- `category` (optional) - Filter by category
- `limit` (optional, default: 100) - Number of results
- `offset` (optional, default: 0) - Pagination offset

**Response:**
```json
{
  "success": true,
  "products": [
    {
      "id": 1,
      "title": "MacBook Pro",
      "description": "...",
      "category": "laptop",
      "image_url": "...",
      "specs": { "processor": "...", "ram": "..." },
      "condition_label": "Excellent",
      "base_price": 200000.0,
      "auction_id": 1,
      "start_price": 150000.0,
      "current_price": 185000.0,
      "auction_status": "active",
      "created_at": "2024-01-01 10:00:00"
    }
  ],
  "total": 1,
  "count": 1
}
```

#### `POST /api/admin/products/add.php`
Add a new product and create an auction for it.

**Headers:**
- `Authorization` (optional for now - TODO: implement authentication)

**Request Body:**
```json
{
  "title": "MacBook Pro 16\" M3 Max",
  "description": "Premium laptop",
  "category": "laptop",
  "image_url": "https://...",
  "specs": {
    "processor": "Apple M3 Max",
    "storage": "1TB SSD",
    "display": "16.2\" Liquid",
    "ram": "48GB Unified Memory"
  },
  "condition_label": "Excellent",
  "base_price": 200000.0,
  "start_price": 150000.0
}
```

**Response:**
```json
{
  "success": true,
  "message": "Product and auction created successfully",
  "product_id": 1,
  "auction_id": 1
}
```

**Note:** Automatically creates an active auction with 7-day duration.

#### `POST /api/admin/products/update.php`
Update an existing product.

**Headers:**
- `Authorization` (optional for now - TODO: implement authentication)

**Request Body:**
```json
{
  "product_id": 1,
  "title": "Updated Title",
  "description": "Updated description",
  "category": "laptop",
  "image_url": "https://...",
  "specs": { ... },
  "condition_label": "Good",
  "base_price": 180000.0
}
```

**Response:**
```json
{
  "success": true,
  "message": "Product updated successfully",
  "product_id": 1
}
```

**Note:** All fields except `product_id` are optional. Only provided fields will be updated.

#### `POST /api/admin/products/delete.php`
Delete a product and its associated auction and bids.

**Headers:**
- `Authorization` (optional for now - TODO: implement authentication)

**Request Body:**
```json
{
  "product_id": 1
}
```

**Response:**
```json
{
  "success": true,
  "message": "Product deleted successfully",
  "product_id": 1
}
```

**Note:** Cascades deletion to related auctions and bids.

## Database Schema Requirements

Make sure your database has the following tables with these columns:

### `auctions` table
- `id` (INT, PRIMARY KEY, AUTO_INCREMENT)
- `product_id` (INT, FOREIGN KEY to products.id)
- `start_price` (DECIMAL)
- `current_price` (DECIMAL)
- `status` (VARCHAR) - 'active', 'ended', 'cancelled'
- `start_at` (DATETIME)
- `end_at` (DATETIME)
- `created_at` (TIMESTAMP)

### `bids` table
- `id` (INT, PRIMARY KEY, AUTO_INCREMENT)
- `auction_id` (INT, FOREIGN KEY to auctions.id)
- `user_id` (INT, FOREIGN KEY to users.id)
- `amount` (DECIMAL)
- `created_at` (TIMESTAMP)

### `products` table (should already exist)
- `id` (INT, PRIMARY KEY, AUTO_INCREMENT)
- `title` (VARCHAR)
- `description` (TEXT, nullable)
- `category` (VARCHAR) - 'laptop', 'mobile', 'computer', 'monitor', 'tablet'
- `image_url` (VARCHAR, nullable)
- `specs` (TEXT/JSON, nullable) - JSON string or text
- `condition` (VARCHAR, nullable)
- `base_price` (DECIMAL)
- `created_at` (TIMESTAMP)

## Installation

1. Copy all PHP files to your XAMPP `htdocs/onlinebidding/api/` directory
2. Run `copy_to_xampp.bat` to automatically copy all files
3. Make sure the directory structure matches:
   ```
   api/
   ├── auctions/
   │   ├── list.php
   │   └── details.php
   ├── bids/
   │   └── place.php
   ├── admin/
   │   └── products/
   │       ├── list.php
   │       ├── add.php
   │       ├── update.php
   │       └── delete.php
   ├── forgot-password.php
   ├── verify-otp.php
   └── reset-password.php
   ```

## Testing

You can test the endpoints using:
- Postman
- cURL
- Your Android app (Retrofit)

Example cURL commands:

```bash
# List auctions
curl "http://localhost/onlinebidding/api/auctions/list.php?category=laptop&limit=10"

# Get auction details
curl "http://localhost/onlinebidding/api/auctions/details.php?id=1"

# Place a bid
curl -X POST "http://localhost/onlinebidding/api/bids/place.php" \
  -H "Content-Type: application/json" \
  -d '{"auction_id":1,"amount":190000,"user_id":1}'
```

## TODO / Future Enhancements

1. **Authentication**: Implement proper admin authentication using JWT tokens
2. **Notifications API**: Create endpoints for user notifications
3. **Payments API**: Create endpoints for payment processing
4. **Sessions API**: Implement session management
5. **Bid History**: Add endpoint to get user's bid history
6. **Auction Status Updates**: Automatically update auction status when end_at time passes
7. **Email Notifications**: Send emails when bids are placed or auctions end
8. **Image Upload**: Add endpoint for uploading product images

## Notes

- All endpoints return JSON responses
- CORS is enabled for all endpoints (adjust in production)
- Error handling is implemented with try-catch blocks
- Database transactions are used where appropriate (bids, product creation/deletion)
- Input validation is performed on all endpoints
- Error messages are logged to PHP error log for debugging




