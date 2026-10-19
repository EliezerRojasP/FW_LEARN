<?php
require __DIR__ . '/bootstrap.php';

$administrador = admin();
method('POST');

$datos = body();
$id = integer($datos, 'id');
$respuesta = txt($datos, 'respuesta', 4000);

if (!rows('SELECT id_mensaje FROM mensajes WHERE id_mensaje = ?', [$id])) {
    respond(null, 'Mensaje no encontrado.', 404);
}

query(
    'UPDATE mensajes
     SET respuesta = ?, id_admin = ?, fecha_respuesta = NOW(), leido = 1, respuesta_vista = 0
     WHERE id_mensaje = ?',
    [$respuesta, $administrador['id_usuario'], $id]
);
respond(null, 'Respuesta guardada.');