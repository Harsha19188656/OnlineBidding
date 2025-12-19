<?php
header('Content-Type: application/json');
require __DIR__ . '/../../db.php';

$userId = require_auth($mysqli);
$data = json_decode(file_get_contents('php://input'), true);
$auctionId = intval($data['auction_id'] ?? 0);
$amount = floatval($data['amount'] ?? 0);

if ($auctionId <= 0 || $amount <= 0) {
    http_response_code(400);
    echo json_encode(["success" => false, "error" => "Missing auction or amount"]);
    exit;
}

$stmt = $mysqli->prepare("SELECT current_price, end_at, status FROM auctions WHERE id=?");
$stmt->bind_param("i", $auctionId);
$stmt->execute();
$stmt->bind_result($current, $endAt, $status);
if (!$stmt->fetch()) {
    http_response_code(404);
    echo json_encode(["success" => false, "error" => "Auction not found"]);
    exit;
}
if ($status !== 'live' || ($endAt && strtotime($endAt) < time())) {
    http_response_code(400);
    echo json_encode(["success" => false, "error" => "Auction not live"]);
    exit;
}
if ($amount <= $current) {
    http_response_code(400);
    echo json_encode(["success" => false, "error" => "Bid must be higher"]);
    exit;
}

$ins = $mysqli->prepare("INSERT INTO bids (auction_id, user_id, amount) VALUES (?,?,?)");
$ins->bind_param("iid", $auctionId, $userId, $amount);
$ins->execute();

$upd = $mysqli->prepare("UPDATE auctions SET current_price=? WHERE id=?");
$upd->bind_param("di", $amount, $auctionId);
$upd->execute();

echo json_encode(["success" => true, "current_price" => $amount]);

