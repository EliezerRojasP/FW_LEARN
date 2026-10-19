<?php

require __DIR__ . '/bootstrap.php';
method('POST');
$usuario = user();
$id = integer(body(), 'id_leccion');
activeLesson($id);
$preguntas = rows('SELECT id_pregunta,enunciado,orden
        FROM preguntas
        WHERE id_leccion=? AND estado=1
        ORDER BY orden,id_pregunta', [$id]);
if (!$preguntas) {
    respond(null, 'Esta lección todavía no tiene evaluación.', 409);
}
$preguntasPrivadas = [];
$preguntasPublicas = [];
foreach ($preguntas as $pregunta) {
    $alternativas = rows('SELECT id_alternativa,texto,es_correcta
        FROM alternativas
        WHERE id_pregunta=?
        ORDER BY id_alternativa', [(int) $pregunta['id_pregunta']]);
    $correctas = 0;
    foreach ($alternativas as $alternativa) {
        if ((int) $alternativa['es_correcta'] === 1) {
            $correctas++;
        }
    }
    if (count($alternativas) < 2 || $correctas !== 1) {
        respond(null, 'La evaluación necesita revisión del administrador.', 409);
    }
    shuffle($alternativas);
    $idPregunta = (int) $pregunta['id_pregunta'];
    $preguntasPrivadas[] = ['id' => $idPregunta, 'enunciado' => $pregunta['enunciado'], 'alternativas' => $alternativas];
    $alternativasPublicas = [];
    foreach ($alternativas as $alternativa) {
        $alternativasPublicas[] = ['id' => (int) $alternativa['id_alternativa'], 'texto' => $alternativa['texto']];
    }
    $preguntasPublicas[] = ['id' => $idPregunta, 'enunciado' => $pregunta['enunciado'], 'alternativas' => $alternativasPublicas];
}
$intento = bin2hex(random_bytes(32));
query('INSERT INTO evaluaciones(id_intento,id_usuario,id_leccion,snapshot,expira) VALUES(?,?,?,?,UTC_TIMESTAMP()+INTERVAL 2 HOUR)', [$intento, $usuario['id_usuario'], $id, json_encode($preguntasPrivadas, JSON_UNESCAPED_UNICODE | JSON_THROW_ON_ERROR)]);
respond(['id_intento' => $intento, 'id_leccion' => $id, 'preguntas' => $preguntasPublicas], 'Evaluación lista.', 201);
