<?php
require __DIR__ . '/bootstrap.php';

$administrador = admin();
$recurso = $_GET['recurso'] ?? '';
$metodo = $_SERVER['REQUEST_METHOD'];

if ($recurso === 'estadisticas') {
    method('GET');
    respond([
        'alumnos' => (int) rows('SELECT COUNT(*) AS n FROM usuarios WHERE id_rol = 2 AND estado = 1')[0]['n'],
        'cursos' => (int) rows('SELECT COUNT(*) AS n FROM cursos WHERE estado = 1')[0]['n'],
        'lecciones' => (int) rows('SELECT COUNT(*) AS n FROM lecciones WHERE estado = 1')[0]['n'],
        'intentos' => (int) rows('SELECT COUNT(*) AS n FROM resultados')[0]['n'],
        'promedio' => (float) (rows('SELECT AVG(porcentaje) AS n FROM resultados')[0]['n'] ?? 0)
    ]);
}

if ($recurso === 'resultados') {
    method('GET');
    respond(rows("SELECT r.*, CONCAT(u.nombre, ' ', u.apellido) AS alumno, l.titulo
        FROM resultados r
        JOIN usuarios u ON u.id_usuario = r.id_usuario
        JOIN lecciones l ON l.id_leccion = r.id_leccion
        ORDER BY r.fecha_resultado DESC, r.id_resultado DESC LIMIT 200"));
}

if (!in_array($recurso, ['cursos', 'topicos', 'lecciones', 'preguntas', 'usuarios'], true)) {
    respond(null, 'Recurso desconocido.', 404);
}

// Listar los registros.
if ($metodo === 'GET') {
    $registros = [];

    switch ($recurso) {
        case 'cursos':
            $registros = rows('SELECT id_curso, nombre, descripcion, estado FROM cursos ORDER BY id_curso');
            break;

        case 'topicos':
            $registros = rows('SELECT id_topico, id_curso, nombre, descripcion, orden, estado FROM topicos ORDER BY id_topico');
            break;

        case 'lecciones':
            $registros = rows('SELECT id_leccion, id_topico, titulo, contenido, orden, estado FROM lecciones ORDER BY id_leccion');
            break;

        case 'preguntas':
            $registros = rows('SELECT id_pregunta, id_leccion, enunciado, orden, estado FROM preguntas ORDER BY id_pregunta');
            foreach ($registros as &$pregunta) {
                $pregunta['alternativas'] = rows(
                    'SELECT id_alternativa, texto, es_correcta FROM alternativas WHERE id_pregunta = ? ORDER BY id_alternativa',
                    [(int) $pregunta['id_pregunta']]
                );
            }
            unset($pregunta);
            break;

        case 'usuarios':
            $registros = rows('SELECT id_usuario, id_rol, nombre, apellido, usuario, estado FROM usuarios ORDER BY id_usuario');
            break;
    }
    respond($registros);
}

if (!in_array($metodo, ['POST', 'PUT', 'DELETE'], true)) {
    respond(null, 'Metodo no permitido.', 405);
}

$datos = body();
$id = 0;
$registro = [];
$registros = [];

if ($metodo !== 'POST') {
    switch ($recurso) {
        case 'cursos':
            $id = integer($datos, 'id_curso');
            $registro = rows('SELECT id_curso FROM cursos WHERE id_curso = ?', [$id]);
            break;

        case 'topicos':
            $id = integer($datos, 'id_topico');
            $registro = rows('SELECT id_topico FROM topicos WHERE id_topico = ?', [$id]);
            break;

        case 'lecciones':
            $id = integer($datos, 'id_leccion');
            $registro = rows('SELECT id_leccion FROM lecciones WHERE id_leccion = ?', [$id]);
            break;

        case 'preguntas':
            $id = integer($datos, 'id_pregunta');
            $registro = rows('SELECT id_pregunta FROM preguntas WHERE id_pregunta = ?', [$id]);
            break;

        case 'usuarios':
            $id = integer($datos, 'id_usuario');
            $registro = rows('SELECT id_usuario FROM usuarios WHERE id_usuario = ?', [$id]);
            break;
    }
    if (!$registro) {
        respond(null, 'Registro no encontrado.', 404);
    }
}

// Esta ruta desactiva el registro para conservar su historial.
if ($metodo === 'DELETE') {
    if ($recurso === 'usuarios' && $id === $administrador['id_usuario']) {
        respond(null, 'No puedes desactivar tu propia cuenta.', 422);
    }

    switch ($recurso) {
        case 'cursos':
            query('UPDATE cursos SET estado = 0 WHERE id_curso = ?', [$id]);
            break;

        case 'topicos':
            query('UPDATE topicos SET estado = 0 WHERE id_topico = ?', [$id]);
            break;

        case 'lecciones':
            query('UPDATE lecciones SET estado = 0 WHERE id_leccion = ?', [$id]);
            break;

        case 'preguntas':
            query('UPDATE preguntas SET estado = 0 WHERE id_pregunta = ?', [$id]);
            break;

        case 'usuarios':
            query('UPDATE usuarios SET estado = 0 WHERE id_usuario = ?', [$id]);
            break;
    }
    respond(null, 'Registro desactivado. El historial se conserva.');
}

$id_curso = 0;
$id_topico = 0;
$id_leccion = 0;
$id_rol = 0;
$nombre = '';
$apellido = '';
$usuario = '';
$descripcion = '';
$titulo = '';
$contenido = '';
$enunciado = '';
$orden = 1;
$estado = 1;
$cambiarPassword = false;
$passwordGuardado = '';
$alternativasValidadas = [];

// Validar los datos antes de guardarlos.
switch ($recurso) {
    case 'cursos':
        $nombre = txt($datos, 'nombre', 150);
        $descripcion = txt($datos, 'descripcion', 10000, true);
        $estado = integer($datos, 'estado', 0, 1);
        break;

    case 'topicos':
        $id_curso = integer($datos, 'id_curso');
        $nombre = txt($datos, 'nombre', 150);
        $descripcion = txt($datos, 'descripcion', 10000, true);
        $orden = integer($datos, 'orden', 1, 10000);
        $estado = integer($datos, 'estado', 0, 1);

        if (!rows('SELECT id_curso FROM cursos WHERE id_curso = ?', [$id_curso])) {
            respond(null, 'El registro padre no existe.', 422);
        }
        break;

    case 'lecciones':
        $id_topico = integer($datos, 'id_topico');
        $titulo = txt($datos, 'titulo', 200);
        $contenido = txt($datos, 'contenido', 50000, true);
        $orden = integer($datos, 'orden', 1, 10000);
        $estado = integer($datos, 'estado', 0, 1);

        if (!rows('SELECT id_topico FROM topicos WHERE id_topico = ?', [$id_topico])) {
            respond(null, 'El registro padre no existe.', 422);
        }
        break;

    case 'preguntas':
        $id_leccion = integer($datos, 'id_leccion');
        $enunciado = txt($datos, 'enunciado', 4000);
        $orden = integer($datos, 'orden', 1, 10000);
        $estado = integer($datos, 'estado', 0, 1);

        if (!rows('SELECT id_leccion FROM lecciones WHERE id_leccion = ?', [$id_leccion])) {
            respond(null, 'El registro padre no existe.', 422);
        }

        $alternativas = $datos['alternativas'] ?? [];
        if (!is_array($alternativas) || !array_is_list($alternativas) || count($alternativas) < 2 || count($alternativas) > 6) {
            respond(null, 'La pregunta requiere de 2 a 6 alternativas.', 422);
        }

        $correctas = 0;
        $alternativasValidadas = [];
        foreach ($alternativas as $alternativa) {
            if (!is_array($alternativa)) {
                respond(null, 'Alternativa invalida.', 422);
            }
            $texto = txt($alternativa, 'texto', 500);
            $esCorrecta = integer($alternativa, 'es_correcta', 0, 1);
            $correctas += $esCorrecta;
            $alternativasValidadas[] = ['texto' => $texto, 'es_correcta' => $esCorrecta];
        }
        if ($correctas !== 1) {
            respond(null, 'Debe existir exactamente una alternativa correcta.', 422);
        }
        break;

    case 'usuarios':
        $id_rol = integer($datos, 'id_rol');
        $nombre = txt($datos, 'nombre', 100);
        $apellido = txt($datos, 'apellido', 100);
        $usuario = txt($datos, 'usuario', 50);
        $estado = integer($datos, 'estado', 0, 1);

        if (!in_array($id_rol, [1, 2], true)) {
            respond(null, 'Rol invalido.', 422);
        }
        if ($id === $administrador['id_usuario'] && ($estado === 0 || $id_rol !== 1)) {
            respond(null, 'No puedes quitarte el acceso de administrador.', 422);
        }

        $password = $datos['password'] ?? '';
        if (!is_string($password)) {
            respond(null, 'Contraseña invalida.', 422);
        }
        $cambiarPassword = $metodo === 'POST' || $password !== '';
        if ($cambiarPassword) {
            if (strlen($password) < 8 || strlen($password) > 72) {
                respond(null, 'La contraseña debe tener entre 8 y 72 bytes.', 422);
            }
            $passwordGuardado = password_hash($password, PASSWORD_DEFAULT);
        }
        break;
}

// La transaccion permite guardar todos los cambios juntos.
$conexion->begin_transaction();
try {
    switch ($recurso) {
        case 'cursos':
            if ($metodo === 'POST') {
                query(
                    'INSERT INTO cursos (nombre, descripcion, estado) VALUES (?, ?, ?)',
                    [$nombre, $descripcion, $estado]
                );
                $id = (int) $conexion->insert_id;
            } else {
                query(
                    'UPDATE cursos SET nombre = ?, descripcion = ?, estado = ? WHERE id_curso = ?',
                    [$nombre, $descripcion, $estado, $id]
                );
            }
            break;

        case 'topicos':
            if ($metodo === 'POST') {
                query(
                    'INSERT INTO topicos (id_curso, nombre, descripcion, orden, estado) VALUES (?, ?, ?, ?, ?)',
                    [$id_curso, $nombre, $descripcion, $orden, $estado]
                );
                $id = (int) $conexion->insert_id;
            } else {
                query(
                    'UPDATE topicos SET id_curso = ?, nombre = ?, descripcion = ?, orden = ?, estado = ? WHERE id_topico = ?',
                    [$id_curso, $nombre, $descripcion, $orden, $estado, $id]
                );
            }
            break;

        case 'lecciones':
            if ($metodo === 'POST') {
                query(
                    'INSERT INTO lecciones (id_topico, titulo, contenido, orden, estado) VALUES (?, ?, ?, ?, ?)',
                    [$id_topico, $titulo, $contenido, $orden, $estado]
                );
                $id = (int) $conexion->insert_id;
            } else {
                query(
                    'UPDATE lecciones SET id_topico = ?, titulo = ?, contenido = ?, orden = ?, estado = ? WHERE id_leccion = ?',
                    [$id_topico, $titulo, $contenido, $orden, $estado, $id]
                );
            }
            break;

        case 'preguntas':
            if ($metodo === 'POST') {
                query(
                    'INSERT INTO preguntas (id_leccion, enunciado, orden, estado) VALUES (?, ?, ?, ?)',
                    [$id_leccion, $enunciado, $orden, $estado]
                );
                $id = (int) $conexion->insert_id;
            } else {
                query(
                    'UPDATE preguntas SET id_leccion = ?, enunciado = ?, orden = ?, estado = ? WHERE id_pregunta = ?',
                    [$id_leccion, $enunciado, $orden, $estado, $id]
                );
            }
            query('DELETE FROM alternativas WHERE id_pregunta = ?', [$id]);
            foreach ($alternativasValidadas as $alternativa) {
                query(
                    'INSERT INTO alternativas (id_pregunta, texto, es_correcta) VALUES (?, ?, ?)',
                    [$id, $alternativa['texto'], $alternativa['es_correcta']]
                );
            }
            break;

        case 'usuarios':
            if ($metodo === 'POST') {
                query(
                    'INSERT INTO usuarios (id_rol, nombre, apellido, usuario, estado, password) VALUES (?, ?, ?, ?, ?, ?)',
                    [$id_rol, $nombre, $apellido, $usuario, $estado, $passwordGuardado]
                );
                $id = (int) $conexion->insert_id;
            } elseif ($cambiarPassword) {
                query(
                    'UPDATE usuarios SET id_rol = ?, nombre = ?, apellido = ?, usuario = ?, estado = ?, password = ? WHERE id_usuario = ?',
                    [$id_rol, $nombre, $apellido, $usuario, $estado, $passwordGuardado, $id]
                );
            } else {
                query(
                    'UPDATE usuarios SET id_rol = ?, nombre = ?, apellido = ?, usuario = ?, estado = ? WHERE id_usuario = ?',
                    [$id_rol, $nombre, $apellido, $usuario, $estado, $id]
                );
            }
            if ($cambiarPassword) {
                query('DELETE FROM sesiones WHERE id_usuario = ?', [$id]);
            }
            break;
    }
    $conexion->commit();
} catch (Throwable $error) {
    $conexion->rollback();
    throw $error;
}

respond(['id' => $id], 'Registro guardado.', $metodo === 'POST' ? 201 : 200);
