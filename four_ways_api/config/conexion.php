<?php
// XAMPP local. Cambia estos valores si configuraste otra cuenta de MariaDB.
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
$conexion = new mysqli('localhost', 'root', '', 'db_fw_learn_v2', 3306);
$conexion->set_charset('utf8mb4');
$conexion->query("SET time_zone = '+00:00'");
