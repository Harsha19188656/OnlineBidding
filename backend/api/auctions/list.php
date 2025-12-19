<?php
header('Content-Type: application/json');
require __DIR__ . '/../../db.php';

$category = $_GET['category'] ?? null;
$search = $_GET['search'] ?? null;
$limit = intval($_GET['limit'] ?? 20);
$offset = intval($_GET['offset'] ?? 0);

$sql = "SELECT a.id as auction_id, a.product_id, a.start_price, a.current_price, a.status, a.start_at, a.end_at,
               p.title, p.description, p.category, p.image_url, p.specs, p.condition_label, p.base_price
        FROM auctions a
        JOIN products p ON p.id = a.product_id
        WHERE 1=1";
$params = [];
$types = "";
if ($category) {
    $sql .= " AND p.category=?";
    $types .= "s";
    $params[] = $category;
}
if ($search) {
    $sql .= " AND p.title LIKE ?";
    $types .= "s";
    $params[] = "%" . $search . "%";
}
$sql .= " ORDER BY a.created_at DESC LIMIT ? OFFSET ?";
$types .= "ii";
$params[] = $limit;
$params[] = $offset;

$stmt = $mysqli->prepare($sql);
$stmt->bind_param($types, ...$params);
$stmt->execute();
$result = $stmt->get_result();
$items = [];
while ($row = $result->fetch_assoc()) {
    $items[] = [
        "product" => [
            "id" => intval($row["product_id"]),
            "title" => $row["title"],
            "description" => $row["description"],
            "category" => $row["category"],
            "image_url" => $row["image_url"],
            "specs" => $row["specs"],
            "condition" => $row["condition_label"],
            "base_price" => floatval($row["base_price"]),
            "created_at" => null
        ],
        "auction" => [
            "id" => intval($row["auction_id"]),
            "product_id" => intval($row["product_id"]),
            "start_price" => floatval($row["start_price"]),
            "current_price" => floatval($row["current_price"]),
            "status" => $row["status"],
            "start_at" => $row["start_at"],
            "end_at" => $row["end_at"]
        ]
    ];
}

echo json_encode(["success" => true, "items" => $items]);

