<?php
    $con = mysqli_connect("localhost", "id7560856_sa", "Scoeat69?!", "id7560856_hunkdb");

    $name = $_POST["name"];
    $age = $_POST["age"];
    $username = $_POST["username"];
    $password = $_POST["password"];
    $statement = mysqli_prepare($con, "INSERT INTO user (name, username, age, password) VALUES (?, ?, ?, ?)");
    mysqli_stmt_bind_param($statement, "ssis", $name, $username, $age, $password);
    mysqli_stmt_execute($statement);

    $response = array();
    $response["success"] = true;

    echo json_encode($response);
?>