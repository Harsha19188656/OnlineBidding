<?php
header('Content-Type: application/json');
require __DIR__ . '/../../db.php';

$id = intval($_GET['id'] ?? 0);
if ($id <= 0) {
    http_response_code(400);
    echo json_encode(["success" => false, "error" => "Missing id"]);
    exit;
}

$stmt = $mysqli->prepare("SELECT a.id as auction_id, a.product_id, a.start_price, a.current_price, a.status, a.start_at, a.end_at,
                                 p.title, p.description, p.category, p.image_url, p.specs, p.condition_label, p.base_price
                          FROM auctions a
                          JOIN products p ON p.id = a.product_id
                          WHERE a.id=?");
$stmt->bind_param("i", $id);
$stmt->execute();
$result = $stmt->get_result();
if (!($row = $result->fetch_assoc())) {
    http_response_code(404);
    echo json_encode(["success" => false, "error" => "Not found"]);
    exit;
}

$bidsStmt = $mysqli->prepare("SELECT id, user_id, amount, created_at FROM bids WHERE auction_id=? ORDER BY amount DESC LIMIT 20");
$bidsStmt->bind_param("i", $id);
$bidsStmt->execute();
$bidsRes = $bidsStmt->get_result();
$bids = [];
while ($b = $bidsRes->fetch_assoc()) {
    $bids[] = [
        "id" => intval($b["id"]),
        "auction_id" => $id,
        "user_id" => intval($b["user_id"]),
        "amount" => floatval($b["amount"]),
        "created_at" => $b["created_at"]
    ];
}

echo json_encode([
    "success" => true,
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
    ],
    "bids" => $bids
]);

