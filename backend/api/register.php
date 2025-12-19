<?php
header('Content-Type: application/json');
require __DIR__ . '/../db.php';

$data = json_decode(file_get_contents('php://input'), true);
$email = $data['email'] ?? '';
$password = $data['password'] ?? '';
$name = $data['name'] ?? '';

if (!$email || !$password || !$name) {
    http_response_code(400);
    echo json_encode(["success" => false, "error" => "Missing fields"]);
    exit;
}

$stmt = $mysqli->prepare("SELECT id FROM users WHERE email=?");
$stmt->bind_param("s", $email);
$stmt->execute();
if ($stmt->fetch()) {
    http_response_code(409);
    echo json_encode(["success" => false, "error" => "Email already exists"]);
    exit;
}

$hash = password_hash($password, PASSWORD_BCRYPT);
$insert = $mysqli->prepare("INSERT INTO users (email, password_hash, name) VALUES (?,?,?)");
$insert->bind_param("sss", $email, $hash, $name);
$insert->execute();

echo json_encode(["success" => true, "message" => "Registered"]);

