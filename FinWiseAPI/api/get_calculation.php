
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

$tables = [
    "fd" => "fd_records",
    "loan" => "loan_records",
    "salary" => "salary_records",
    "investment" => "investment_records"
];

$response = [];

foreach ($tables as $type => $table) {

    $stmt = $conn->prepare(
        "SELECT * FROM $table WHERE user_id = ? ORDER BY 1 DESC"
    );

    $stmt->bind_param("i", $user_id);
    $stmt->execute();

    $result = $stmt->get_result();

    $response[$type] = $result->fetch_all(MYSQLI_ASSOC);

    $stmt->close();
}

echo json_encode([
    "status" => "success",
    "calculations" => $response
]);

$conn->close();

?>
