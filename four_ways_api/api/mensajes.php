<?php
require __DIR__ . '/bootstrap.php';

admin();
method('GET');

$mensajes = rows("SELECT m.id_mensaje, m.id_usuario, u.nombre, u.apellido, u.usuario,
        m.pregunta, m.leido, m.respuesta, m.fecha_envio, m.fecha_respuesta
    FROM mensajes m
    JOIN usuarios u ON u.id_usuario = m.id_usuario
    ORDER BY m.leido ASC, m.fecha_envio DESC, m.id_mensaje DESC
    LIMIT 500");

foreach ($mensajes as &$mensaje) {
    $mensaje['id_mensaje'] = (int) $mensaje['id_mensaje'];
    $mensaje['id_usuario'] = (int) $mensaje['id_usuario'];
    $mensaje['leido'] = (int) $mensaje['leido'];
}
unset($mensaje);

respond($mensajes);