<?php

require __DIR__ . '/bootstrap.php';
require __DIR__ . '/progreso_lib.php';
method('GET');
$usuario = user();
$cursos = courseProgress($usuario['id_usuario']);
$total = 0;
$completadas = 0;
foreach ($cursos as $curso) {
    $total += $curso['total_lecciones'];
    $completadas += $curso['lecciones_completadas'];
}
respond(['total_lecciones' => $total, 'lecciones_completadas' => $completadas, 'porcentaje_avance' => $total > 0 ? round(100.0 * $completadas / $total, 2) : 0.0, 'cursos' => $cursos, 'temas' => topicProgress($usuario['id_usuario'])]);
