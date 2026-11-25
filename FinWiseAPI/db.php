
<?php

$host = "127.0.0.1";
$port = 3306;
$username = "root";
$password = "YOUR_DATABASE_PASSWORD";
$database = "finwise_db";

// Connect to MySQL
$conn = new mysqli(
    $host,
    $username,
    $password,
    $database,
    $port
);

// Check connection
if ($conn->connect_error) {
    http_response_code(500);
    die(json_encode([
        "status" => "error",
        "message" => "Database connection failed."
    ]));
}

// Set character encoding
$conn->set_charset("utf8mb4");

?>
