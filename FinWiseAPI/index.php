
<?php

header("Content-Type: application/json");

require_once "db.php";

echo json_encode([
    "status" => "success",
    "message" => "FinWise LK API Connected Successfully!",
    "database" => "finwise_db"
]);

?>
