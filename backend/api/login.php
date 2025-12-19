<?php
header('Content-Type: application/json');
require __DIR__ . '/../db.php';

$data = json_decode(file_get_contents('php://input'), true);
$email = $data['email'] ?? '';
$password = $data['password'] ?? '';

if (!$email || !$password) {
    http_response_code(400);
    echo json_encode(["success" => false, "error" => "Missing fields"]);
    exit;
}

$stmt = $mysqli->prepare("SELECT id, password_hash FROM users WHERE email=?");
$stmt->bind_param("s", $email);
$stmt->execute();
$stmt->bind_result($uid, $hash);
if ($stmt->fetch() && password_verify($password, $hash)) {
    $token = bin2hex(random_bytes(32));
    $exp = date('Y-m-d H:i:s', time() + 86400);
    $insert = $mysqli->prepare("INSERT INTO sessions (user_id, token, expires_at) VALUES (?,?,?)");
    $insert->bind_param("iss", $uid, $token, $exp);
    $insert->execute();
    echo json_encode(["success" => true, "token" => $token, "user_id" => $uid]);
} else {
    http_response_code(401);
    echo json_encode(["success" => false, "error" => "Invalid credentials"]);
}

