<?php

// Consultas compartidas: el avance cuenta lecciones terminadas, no respuestas correctas.
function addPercent(array $registro): array {
    $registro['total_lecciones'] = (int) $registro['total_lecciones'];
    $registro['lecciones_completadas'] = (int) $registro['lecciones_completadas'];
    $registro['porcentaje_avance'] = $registro['total_lecciones'] > 0 ? round(100.0 * $registro['lecciones_completadas'] / $registro['total_lecciones'], 2): 0.0;
    return $registro;
}

function courseProgress(int $idUsuario): array {
    $registros = rows("SELECT c.id_curso AS id,c.nombre,COALESCE(c.descripcion,'') AS descripcion,COUNT(l.id_leccion) AS total_lecciones,SUM(CASE WHEN p.completada=1 THEN 1 ELSE 0 END) AS lecciones_completadas
        FROM cursos c
        LEFT JOIN topicos t ON t.id_curso=c.id_curso AND t.estado=1
        LEFT JOIN lecciones l ON l.id_topico=t.id_topico AND l.estado=1
        LEFT JOIN progreso p ON p.id_leccion=l.id_leccion AND p.id_usuario=?
        WHERE c.estado=1
        GROUP BY c.id_curso,c.nombre,c.descripcion
        ORDER BY c.id_curso", [$idUsuario]);
    foreach ($registros as &$registro) {
        $registro = addPercent($registro);
    }
    unset($registro);
    return $registros;
}

function topicProgress(int $idUsuario, int $idCurso = 0): array {
    $registros = rows("SELECT t.id_topico,t.id_curso,t.nombre,COALESCE(t.descripcion,'') AS descripcion,t.orden,t.estado,COUNT(l.id_leccion) AS total_lecciones,SUM(CASE WHEN p.completada=1 THEN 1 ELSE 0 END) AS lecciones_completadas
        FROM topicos t
        JOIN cursos c ON c.id_curso=t.id_curso
        LEFT JOIN lecciones l ON l.id_topico=t.id_topico AND l.estado=1
        LEFT JOIN progreso p ON p.id_leccion=l.id_leccion AND p.id_usuario=?
        WHERE t.estado=1 AND c.estado=1 AND (?=0 OR t.id_curso=?)
        GROUP BY t.id_topico,t.id_curso,t.nombre,t.descripcion,t.orden,t.estado
        ORDER BY t.id_curso,t.orden,t.id_topico", [$idUsuario, $idCurso, $idCurso]);
    foreach ($registros as &$registro) {
        $registro = addPercent($registro);
    }
    unset($registro);
    return $registros;
}

function lessonProgress(int $idUsuario, int $idTopico = 0): array {
    $registros = rows("SELECT l.*,COALESCE(p.completada,0) AS completada,COALESCE(p.mejor_puntaje,0) AS mejor_puntaje,CASE WHEN p.completada=1 THEN 100 ELSE 0 END AS porcentaje_avance,(SELECT COUNT(*)
        FROM preguntas q
        WHERE q.id_leccion=l.id_leccion AND q.estado=1) AS total_preguntas
        FROM lecciones l
        JOIN topicos t ON t.id_topico=l.id_topico
        JOIN cursos c ON c.id_curso=t.id_curso
        LEFT JOIN progreso p ON p.id_leccion=l.id_leccion AND p.id_usuario=?
        WHERE l.estado=1 AND t.estado=1 AND c.estado=1 AND (?=0 OR l.id_topico=?)
        ORDER BY t.id_curso,t.orden,l.orden,l.id_leccion", [$idUsuario, $idTopico, $idTopico]);
    return $registros;
}

function topicForLesson(int $idUsuario, int $idLeccion): ?array {
    $lecciones = rows("SELECT id_topico
        FROM lecciones
        WHERE id_leccion=?", [$idLeccion]);
    if (!$lecciones) {
        return null;
    }
    foreach (topicProgress($idUsuario) as $topico) {
        if ((int) $topico['id_topico'] === (int) $lecciones[0]['id_topico']) {
            return $topico;
        }
    }
    return null;
}

function resultProgress(array $resultado, int $idUsuario, int $idLeccion): array {
    $resultado['leccion_completada'] = true;
    $resultado['porcentaje_finalizacion'] = 100.0;
    $resultado['aprobada'] = (float) $resultado['porcentaje'] >= 70;
    $resultado['tema'] = topicForLesson($idUsuario, $idLeccion);
    return $resultado;
}
