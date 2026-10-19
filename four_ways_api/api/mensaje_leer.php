<?php
require __DIR__ . '/bootstrap.php';

admin();
method('POST');

$datos = body();
$id = integer($datos, 'id');

if (!rows('SELECT id_mensaje FROM mensajes WHERE id_mensaje = ?', [$id])) {
    respond(null, 'Mensaje no encontrado.', 404);
}

query('UPDATE mensajes SET leido = 1 WHERE id_mensaje = ?', [$id]);
respond(null, 'Mensaje marcado como leído.');