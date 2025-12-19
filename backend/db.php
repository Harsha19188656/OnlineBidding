<?php
// Simple mysqli connection helper. Update credentials for your local setup.
$db_host = "localhost";
$db_user = "root";
$db_pass = ""; // set your password if any
$db_name = "onlinebidding";

$mysqli = new mysqli($db_host, $db_user, $db_pass, $db_name);
if ($mysqli->connect_errno) {
    http_response_code(500);
    echo json_encode(["success" => false, "error" => "DB connection failed"]);
    exit;
}
$mysqli->set_charset("utf8mb4");

function require_auth($mysqli) {
    $headers = getallheaders();
    $auth = $headers['Authorization'] ?? $headers['authorization'] ?? '';
    if (!str_starts_with($auth, 'Bearer ')) {
        http_response_code(401);
        echo json_encode(["success" => false, "error" => "Missing token"]);
        exit;
    }
    $token = substr($auth, 7);
    $stmt = $mysqli->prepare("SELECT user_id FROM sessions WHERE token=? AND expires_at > NOW()");
    $stmt->bind_param("s", $token);
    $stmt->execute();
    $stmt->bind_result($user_id);
    if ($stmt->fetch()) {
        return $user_id;
    }
    http_response_code(401);
    echo json_encode(["success" => false, "error" => "Invalid or expired token"]);
    exit;
}

