<?php
header('Content-Type: application/json');
require __DIR__ . '/../db.php';

$data = json_decode(file_get_contents('php://input'), true);
$email = $data['email'] ?? '';
$password = $data['password'] ?? '';
$name = $data['name'] ?? '';
$phone = $data['phone'] ?? '';
$dob = $data['dob'] ?? '';
$gender = $data['gender'] ?? '';

if (!$email || !$password || !$name) {
    http_response_code(400);
    echo json_encode(["status" => false, "success" => false, "error" => "Missing required fields"]);
    exit;
}

$stmt = $mysqli->prepare("SELECT id FROM users WHERE email=?");
$stmt->bind_param("s", $email);
$stmt->execute();
if ($stmt->fetch()) {
    http_response_code(409);
    echo json_encode(["status" => false, "success" => false, "error" => "Email already exists"]);
    exit;
}

$hash = password_hash($password, PASSWORD_BCRYPT);
$insert = $mysqli->prepare("INSERT INTO users (email, password_hash, name, phone, dob, gender) VALUES (?,?,?,?,?,?)");
$insert->bind_param("ssssss", $email, $hash, $name, $phone, $dob, $gender);
$insert->execute();

if ($insert->affected_rows > 0) {
    echo json_encode(["status" => true, "success" => true, "message" => "Registration successful"]);
} else {
    http_response_code(500);
    echo json_encode(["status" => false, "success" => false, "error" => "Registration failed"]);
}

