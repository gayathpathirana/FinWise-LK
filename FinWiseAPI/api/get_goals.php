
<?php

header("Content-Type: application/json");

require_once "../db.php";

$user_id = intval($_GET["user_id"] ?? 0);

if ($user_id <= 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Valid user ID is required."
    ]);
    exit;
}

$stmt = $conn->prepare(
    "SELECT goal_id, goal_name, target_amount,
            current_savings, target_months, created_at
     FROM financial_goals
     WHERE user_id = ?
     ORDER BY created_at DESC"
);

$stmt->bind_param("i", $user_id);
$stmt->execute();

$result = $stmt->get_result();

$goals = [];

while ($row = $result->fetch_assoc()) {
    $goals[] = $row;
}

echo json_encode([
    "status" => "success",
    "goals" => $goals
]);

$stmt->close();
$conn->close();

?>
