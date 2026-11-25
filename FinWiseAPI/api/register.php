
<?php

header("Content-Type: application/json");

require_once "../db.php";

// Get data from Android app
$data = json_decode(file_get_contents("php://input"), true);

$full_name = trim($data["full_name"] ?? "");
$email = trim($data["email"] ?? "");
$password = $data["password"] ?? "";

// Validate inputs
if (empty($full_name) || empty($email) || empty($password)) {
    echo json_encode([
        "status" => "error",
        "message" => "All fields are required."
    ]);
    exit;
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid email address."
    ]);
    exit;
}

if (strlen($password) < 8) {
    echo json_encode([
        "status" => "error",
        "message" => "Password must contain at least 8 characters."
    ]);
    exit;
}

// Hash password
$password_hash = password_hash($password, PASSWORD_DEFAULT);

// Insert user
$stmt = $conn->prepare(
    "INSERT INTO users (full_name, email, password_hash)
     VALUES (?, ?, ?)"
);

$stmt->bind_param("sss", $full_name, $email, $password_hash);

if ($stmt->execute()) {
    echo json_encode([
        "status" => "success",
        "message" => "Registration successful!"
    ]);
} else {
    if ($conn->errno == 1062) {
        echo json_encode([
            "status" => "error",
            "message" => "Email already registered."
        ]);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Registration failed."
        ]);
    }
}

$stmt->close();
$conn->close();

?>
