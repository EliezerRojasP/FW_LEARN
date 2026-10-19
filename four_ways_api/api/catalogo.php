<?php
require_once __DIR__ . '/bootstrap.php';
require_once __DIR__ . '/progreso_lib.php';
method('GET');
$usuario = user();
$registros = [];

switch ($resource ?? '') {
    case 'cursos':
        $registros = courseProgress($usuario['id_usuario']);
        break;

    case 'topicos':
        $idCurso = integer($_GET, 'curso_id');
        $registros = topicProgress($usuario['id_usuario'], $idCurso);
        break;

    case 'lecciones':
        $idTopico = integer($_GET, 'topico_id');
        $registros = lessonProgress($usuario['id_usuario'], $idTopico);
        break;

    case 'leccion':
        $id = integer($_GET, 'id');
        activeLesson($id);
        $registros = null;
        foreach (lessonProgress($usuario['id_usuario']) as $leccion) {
            if ((int) $leccion['id_leccion'] === $id) {
                $registros = $leccion;
                break;
            }
        }
        break;

    default:
        respond(null, 'Recurso desconocido.', 404);
}

respond($registros);
