<?php

require __DIR__ . '/bootstrap.php';
require __DIR__ . '/progreso_lib.php';
method('POST');
$usuario = user();
$datos = body();
$intento = txt($datos, 'id_intento', 64);
if (!preg_match('/^[a-f0-9]{64}$/D', $intento)) {
    respond(null, 'Intento inválido.', 422);
}
$respuestas = $datos['respuestas'] ?? null;
if (!is_array($respuestas) || !array_is_list($respuestas) || count($respuestas) > 500) {
    respond(null, 'Respuestas inválidas.', 422);
}
$respuestasPorPregunta = [];
foreach ($respuestas as $respuesta) {
    if (!is_array($respuesta)) {
        respond(null, 'Respuesta inválida.', 422);
    }
    $idPregunta = integer($respuesta, 'id_pregunta');
    $idAlternativa = integer($respuesta, 'id_alternativa');
    if (isset($respuestasPorPregunta[$idPregunta])) {
        respond(null, 'Una pregunta está repetida.', 422);
    }
    $respuestasPorPregunta[$idPregunta] = $idAlternativa;
}
$conexion->begin_transaction();
$intentos = rows('SELECT *,expira<UTC_TIMESTAMP() AS vencida
        FROM evaluaciones
        WHERE id_intento=? AND id_usuario=? FOR UPDATE', [$intento, $usuario['id_usuario']]);
if (!$intentos) {
    $conexion->rollback();
    respond(null, 'Intento no encontrado.', 404);
}
$evaluacion = $intentos[0];
if ($evaluacion['id_resultado'] !== null) {
    $resultado = rows('SELECT *
        FROM resultados
        WHERE id_resultado=?', [(int) $evaluacion['id_resultado']])[0];
    $resultado['revision'] = json_decode($evaluacion['revision'], true);
    $resultado = resultProgress($resultado, $usuario['id_usuario'], (int) $evaluacion['id_leccion']);
    $conexion->commit();
    respond($resultado, 'Resultado ya guardado.');
}
if ((int) $evaluacion['vencida'] === 1) {
    $conexion->rollback();
    respond(null, 'La evaluación venció. Inicia una nueva.', 409);
}
$preguntas = json_decode($evaluacion['snapshot'], true, 64, JSON_THROW_ON_ERROR);
if (count($respuestasPorPregunta) !== count($preguntas)) {
    $conexion->rollback();
    respond(null, 'Responde todas las preguntas.', 422);
}
$correctas = 0;
$revision = [];
foreach ($preguntas as $pregunta) {
    $idAlternativa = $respuestasPorPregunta[$pregunta['id']] ?? 0;
    $permitidas = array_column($pregunta['alternativas'], 'id_alternativa');
    if (!in_array($idAlternativa, array_map('intval', $permitidas), true)) {
        $conexion->rollback();
        respond(null, 'Una alternativa no pertenece a su pregunta.', 422);
    }
    $alternativaCorrecta = null;
    foreach ($pregunta['alternativas'] as $alternativa) {
        if ((int) $alternativa['es_correcta'] === 1) {
            $alternativaCorrecta = $alternativa;
            break;
        }
    }
    if ($alternativaCorrecta === null) {
        $conexion->rollback();
        respond(null, 'La evaluación necesita revisión del administrador.', 409);
    }
    $esCorrecta = $idAlternativa === (int) $alternativaCorrecta['id_alternativa'];
    if ($esCorrecta) {
        $correctas++;
    }
    $revision[] = ['enunciado' => $pregunta['enunciado'], 'correcta' => $esCorrecta, 'respuesta_correcta' => $alternativaCorrecta['texto']];
}
$total = count($preguntas);
$porcentaje = round($correctas * 100.0 / $total, 2);
$idLeccion = (int) $evaluacion['id_leccion'];
query('INSERT INTO resultados(id_usuario,id_leccion,puntaje,total_preguntas,porcentaje) VALUES(?,?,?,?,?)', [$usuario['id_usuario'], $idLeccion, $correctas, $total, $porcentaje]);
$idResultado = $conexion->insert_id;
query('INSERT INTO progreso(id_usuario,id_leccion,completada,mejor_puntaje) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE completada=GREATEST(completada,VALUES(completada)),mejor_puntaje=GREATEST(mejor_puntaje,VALUES(mejor_puntaje))', [$usuario['id_usuario'], $idLeccion, 1, $porcentaje]);
query('UPDATE evaluaciones SET id_resultado=?,revision=? WHERE id_intento=?', [$idResultado, json_encode($revision, JSON_UNESCAPED_UNICODE | JSON_THROW_ON_ERROR), $intento]);
$resultado = rows('SELECT *
        FROM resultados
        WHERE id_resultado=?', [$idResultado])[0];
$resultado['revision'] = $revision;
$resultado = resultProgress($resultado, $usuario['id_usuario'], $idLeccion);
$conexion->commit();
respond($resultado, 'Leccion completada. Resultado y progreso guardados.');
