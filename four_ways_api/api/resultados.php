<?php

require __DIR__ . '/bootstrap.php';
method('GET');
$usuario = user();
respond(rows('SELECT r.*,l.titulo
        FROM resultados r
        JOIN lecciones l ON l.id_leccion=r.id_leccion
        WHERE r.id_usuario=?
        ORDER BY r.fecha_resultado DESC,r.id_resultado DESC LIMIT 100', [$usuario['id_usuario']]));
