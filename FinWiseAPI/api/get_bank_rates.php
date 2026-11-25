
<?php

header("Content-Type: application/json");

require_once "../db.php";

$sql = "SELECT rate_id, bank_name, product_type,
               interest_rate, period_months,
               effective_date, source_url, last_updated
        FROM bank_rates
        ORDER BY bank_name ASC";

$result = $conn->query($sql);

$rates = [];

while ($row = $result->fetch_assoc()) {
    $rates[] = $row;
}

echo json_encode([
    "status" => "success",
    "rates" => $rates
]);

$conn->close();

?>
