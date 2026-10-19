<?php
require __DIR__ . '/bootstrap.php';

$alumno = user();
if ($alumno['rol'] !== 'ALUMNO') {
    respond(null, 'Solo los alumnos pueden enviar mensajes.', 403);
}
method('POST');

$datos = body();
$pregunta = txt($datos, 'pregunta', 2000);

// Límite simple contra abuso: máximo 10 mensajes por hora.
$enviados = (int) rows(
    'SELECT COUNT(*) AS n FROM mensajes WHERE id_usuario = ? AND fecha_envio > (NOW() - INTERVAL 1 HOUR)',
    [$alumno['id_usuario']]
)[0]['n'];
if ($enviados >= 10) {
    respond(null, 'Enviaste muchos mensajes. Intenta nuevamente más tarde.', 429);
}

$consulta = query(
    'INSERT INTO mensajes (id_usuario, pregunta) VALUES (?, ?)',
    [$alumno['id_usuario'], $pregunta]
);
respond(['id_mensaje' => (int) $consulta->insert_id], 'Mensaje enviado.', 201);