<?php

require __DIR__ . '/bootstrap.php';
method('GET');
$usuario = user();
respond(rows('SELECT p.*,l.titulo
        FROM progreso p
        JOIN lecciones l ON l.id_leccion=p.id_leccion
        WHERE p.id_usuario=?
        ORDER BY p.fecha_actualizacion DESC', [$usuario['id_usuario']]));
