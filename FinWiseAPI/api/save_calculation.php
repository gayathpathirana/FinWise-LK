
<?php

header("Content-Type: application/json");

require_once "../db.php";

$data = json_decode(file_get_contents("php://input"), true);

$user_id = intval($data["user_id"] ?? 0);
$type = $data["type"] ?? "";

if ($user_id <= 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Valid user ID is required."
    ]);
    exit;
}

$tables = [
    "fd" => [
        "table" => "fd_records",
        "fields" => ["deposit_amount", "interest_rate", "period_months", "interest_amount", "maturity_amount"],
        "types" => "idd dd"
    ],
    "loan" => [
        "table" => "loan_records",
        "fields" => ["loan_amount", "interest_rate", "period_years", "monthly_emi", "total_interest", "total_payment"],
        "types" => "idd ddd"
    ],
    "salary" => [
        "table" => "salary_records",
        "fields" => ["gross_salary", "employee_epf", "estimated_apit", "net_salary"],
        "types" => "dddd"
    ],
    "investment" => [
        "table" => "investment_records",
        "fields" => ["initial_amount", "monthly_amount", "annual_return", "period_years", "total_invested", "estimated_profit", "final_value"],
        "types" => "ddd iddd"
    ]
];

if (!isset($tables[$type])) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid calculation type."
    ]);
    exit;
}

$config = $tables[$type];
$table = $config["table"];
$fields = $config["fields"];

$values = [];

foreach ($fields as $field) {
    if (!isset($data[$field]) || !is_numeric($data[$field])) {
        echo json_encode([
            "status" => "error",
            "message" => "Missing or invalid field: " . $field
        ]);
        exit;
    }

    $values[] = $data[$field];
}

$columns = implode(", ", array_merge(["user_id"], $fields));
$placeholders = implode(", ", array_fill(0, count($fields) + 1, "?"));

$sql = "INSERT INTO $table ($columns) VALUES ($placeholders)";

$stmt = $conn->prepare($sql);

$types = "i";

foreach ($fields as $field) {
    $types .= in_array($field, ["period_months", "period_years"]) ? "i" : "d";
}

$params = array_merge([$user_id], $values);
$stmt->bind_param($types, ...$params);

if ($stmt->execute()) {
    echo json_encode([
        "status" => "success",
        "message" => "Calculation saved successfully!"
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Could not save calculation."
    ]);
}

$stmt->close();
$conn->close();

?>
