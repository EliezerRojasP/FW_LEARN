<?php
require __DIR__ . '/bootstrap.php';

method('DELETE');

$administrador = admin();
$recurso = $_GET['recurso'] ?? '';
$datos = body();

if (!in_array($recurso, [
    'cursos',
    'topicos',
    'lecciones',
    'preguntas',
    'usuarios'
], true)) {
    respond(null, 'Recurso desconocido.', 404);
}

$id = 0;

switch ($recurso) {
    case 'cursos':
        $id = integer($datos, 'id_curso');
        break;

    case 'topicos':
        $id = integer($datos, 'id_topico');
        break;

    case 'lecciones':
        $id = integer($datos, 'id_leccion');
        break;

    case 'preguntas':
        $id = integer($datos, 'id_pregunta');
        break;

    case 'usuarios':
        $id = integer($datos, 'id_usuario');
        break;
}

if (
    $recurso === 'usuarios' &&
    $id === (int) $administrador['id_usuario']
) {
    respond(null, 'No puedes eliminar tu propia cuenta.', 422);
}

$conexion->begin_transaction();

try {
    $registro = [];

    switch ($recurso) {
        case 'cursos':
            $registro = rows(
                'SELECT id_curso FROM cursos
                 WHERE id_curso = ? FOR UPDATE',
                [$id]
            );
            break;

        case 'topicos':
            $registro = rows(
                'SELECT id_topico FROM topicos
                 WHERE id_topico = ? FOR UPDATE',
                [$id]
            );
            break;

        case 'lecciones':
            $registro = rows(
                'SELECT id_leccion FROM lecciones
                 WHERE id_leccion = ? FOR UPDATE',
                [$id]
            );
            break;

        case 'preguntas':
            $registro = rows(
                'SELECT id_pregunta, id_leccion FROM preguntas
                 WHERE id_pregunta = ? FOR UPDATE',
                [$id]
            );
            break;

        case 'usuarios':
            $registro = rows(
                'SELECT id_usuario FROM usuarios
                 WHERE id_usuario = ? FOR UPDATE',
                [$id]
            );
            break;
    }

    if (!$registro) {
        $conexion->rollback();
        respond(null, 'Registro no encontrado.', 404);
    }

    switch ($recurso) {
        case 'cursos':
            $temas = rows(
                'SELECT id_topico FROM topicos
                 WHERE id_curso = ?
                 ORDER BY id_topico FOR UPDATE',
                [$id]
            );

            foreach ($temas as $tema) {
                borrarTema($tema['id_topico']);
            }

            query(
                'DELETE FROM cursos WHERE id_curso = ?',
                [$id]
            );
            break;

        case 'topicos':
            borrarTema($id);
            break;

        case 'lecciones':
            borrarLeccion($id);
            break;

        case 'preguntas':
            $idLeccion = $registro[0]['id_leccion'];

            rows(
                'SELECT id_leccion FROM lecciones
                 WHERE id_leccion = ? FOR UPDATE',
                [$idLeccion]
            );

            if (tieneHistorial($idLeccion)) {
                throw new DomainException(
                    'La lección tiene historial de alumnos. Puedes desactivar la pregunta.'
                );
            }

            query(
                'DELETE FROM alternativas WHERE id_pregunta = ?',
                [$id]
            );

            query(
                'DELETE FROM preguntas WHERE id_pregunta = ?',
                [$id]
            );
            break;

        case 'usuarios':
            if (
                rows(
                    'SELECT id_usuario FROM progreso
                     WHERE id_usuario = ? LIMIT 1',
                    [$id]
                ) ||
                rows(
                    'SELECT id_usuario FROM resultados
                     WHERE id_usuario = ? LIMIT 1',
                    [$id]
                ) ||
                rows(
                    'SELECT id_usuario FROM evaluaciones
                     WHERE id_usuario = ? LIMIT 1',
                    [$id]
                )
            ) {
                throw new DomainException(
                    'El usuario tiene historial. Puedes desactivarlo.'
                );
            }

            query(
                'DELETE FROM sesiones WHERE id_usuario = ?',
                [$id]
            );

            query(
                'DELETE FROM usuarios WHERE id_usuario = ?',
                [$id]
            );
            break;
    }

    $conexion->commit();

} catch (DomainException $error) {
    $conexion->rollback();
    respond(null, $error->getMessage(), 409);

} catch (Throwable $error) {
    $conexion->rollback();

    if (
        $error instanceof mysqli_sql_exception &&
        $error->getCode() === 1451
    ) {
        respond(
            null,
            'Otra relacion de la base de datos impide eliminar este registro.',
            409
        );
    }

    throw $error;
}

respond(['id' => $id], 'Registro y contenido asociado eliminados.');

function tieneHistorial(int $idLeccion): bool {
    if (rows(
        'SELECT id_leccion FROM progreso
         WHERE id_leccion = ? LIMIT 1',
        [$idLeccion]
    )) {
        return true;
    }

    if (rows(
        'SELECT id_leccion FROM resultados
         WHERE id_leccion = ? LIMIT 1',
        [$idLeccion]
    )) {
        return true;
    }

    if (rows(
        'SELECT id_leccion FROM evaluaciones
         WHERE id_leccion = ? LIMIT 1',
        [$idLeccion]
    )) {
        return true;
    }

    return false;
}

function borrarLeccion(int $idLeccion): void {
    rows(
        'SELECT id_leccion FROM lecciones
         WHERE id_leccion = ? FOR UPDATE',
        [$idLeccion]
    );

    if (tieneHistorial($idLeccion)) {
        throw new DomainException(
            'El contenido tiene historial de alumnos. Puedes desactivarlo.'
        );
    }

    query(
        'DELETE FROM alternativas
         WHERE id_pregunta IN (
             SELECT id_pregunta FROM preguntas
             WHERE id_leccion = ?
         )',
        [$idLeccion]
    );

    query(
        'DELETE FROM preguntas WHERE id_leccion = ?',
        [$idLeccion]
    );

    query(
        'DELETE FROM lecciones WHERE id_leccion = ?',
        [$idLeccion]
    );
}

function borrarTema(int $idTopico): void{
    $lecciones = rows(
        'SELECT id_leccion FROM lecciones
         WHERE id_topico = ?
         ORDER BY id_leccion FOR UPDATE',
        [$idTopico]
    );

    foreach ($lecciones as $leccion) {
        borrarLeccion($leccion['id_leccion']);
    }

    query(
        'DELETE FROM topicos WHERE id_topico = ?',
        [$idTopico]
    );
}