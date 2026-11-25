
<?php

header("Content-Type: application/json");

require_once "../db.php";

$data = json_decode(file_get_contents("php://input"), true);

$user_id = intval($data["user_id"] ?? 0);
$goal_name = trim($data["goal_name"] ?? "");
$target_amount = floatval($data["target_amount"] ?? 0);
$current_savings = floatval($data["current_savings"] ?? 0);
$target_months = intval($data["target_months"] ?? 0);

if (
    $user_id <= 0 ||
    $goal_name === "" ||
    $target_amount <= 0 ||
    $current_savings < 0 ||
    $target_months <= 0 ||
    $current_savings > $target_amount
) {
    echo json_encode([
        "status" => "error",
        "message" => "Please enter valid goal details."
    ]);
    exit;
}

$stmt = $conn->prepare(
    "INSERT INTO financial_goals
    (user_id, goal_name, target_amount, current_savings, target_months)
    VALUES (?, ?, ?, ?, ?)"
);

$stmt->bind_param(
    "isddi",
    $user_id,
    $goal_name,
    $target_amount,
    $current_savings,
    $target_months
);

if ($stmt->execute()) {
    echo json_encode([
        "status" => "success",
        "message" => "Financial goal saved successfully!",
        "goal_id" => $stmt->insert_id
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Could not save financial goal."
    ]);
}

$stmt->close();
$conn->close();

?>
