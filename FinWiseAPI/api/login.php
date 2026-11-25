
<?php

header("Content-Type: application/json");

require_once "../db.php";

$data = json_decode(file_get_contents("php://input"), true);

$email = trim($data["email"] ?? "");
$password = $data["password"] ?? "";

if (empty($email) || empty($password)) {
    echo json_encode([
        "status" => "error",
        "message" => "Email and password are required."
    ]);
    exit;
}

$stmt = $conn->prepare(
    "SELECT user_id, full_name, email, password_hash
     FROM users WHERE email = ?"
);

$stmt->bind_param("s", $email);
$stmt->execute();

$result = $stmt->get_result();

if ($user = $result->fetch_assoc()) {

    if (password_verify($password, $user["password_hash"])) {

        echo json_encode([
            "status" => "success",
            "message" => "Login successful!",
            "user" => [
                "user_id" => $user["user_id"],
                "full_name" => $user["full_name"],
                "email" => $user["email"]
            ]
        ]);

    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Incorrect email or password."
        ]);
    }

} else {
    echo json_encode([
        "status" => "error",
        "message" => "Incorrect email or password."
    ]);
}

$stmt->close();
$conn->close();

?>
