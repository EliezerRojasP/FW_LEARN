<?php
require __DIR__ . '/bootstrap.php';

$alumno = user();
if ($alumno['rol'] !== 'ALUMNO') {
    respond(null, 'Solo los alumnos pueden consultar sus mensajes.', 403);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $datos = body();
    $id = integer($datos, 'id_mensaje');
    query(
        'UPDATE mensajes SET respuesta_vista = 1 WHERE id_mensaje = ? AND id_usuario = ?',
        [$id, $alumno['id_usuario']]
    );
    respond(null, 'Respuesta marcada como vista.');
}

method('GET');
$mensajes = rows(
    'SELECT id_mensaje, pregunta, respuesta, fecha_envio, fecha_respuesta, respuesta_vista
     FROM mensajes
     WHERE id_usuario = ?
     ORDER BY fecha_envio DESC, id_mensaje DESC
     LIMIT 100',
    [$alumno['id_usuario']]
);

foreach ($mensajes as &$mensaje) {
    $mensaje['id_mensaje'] = (int) $mensaje['id_mensaje'];
    $mensaje['respuesta_vista'] = (int) $mensaje['respuesta_vista'];
}
unset($mensaje);

respond($mensajes);