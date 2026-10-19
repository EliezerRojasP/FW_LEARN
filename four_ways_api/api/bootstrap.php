<?php

declare(strict_types=1);
header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');
set_exception_handler(function (Throwable $error): void {
    error_log((string) $error);
    $codigo = $error instanceof mysqli_sql_exception && $error->getCode() === 1062 ? 409 : 500;
    respond(null, $codigo === 409 ? 'Ya existe un registro con esos datos.' : 'Error del servidor. Revisa el registro de Apache.', $codigo);
});
require_once __DIR__ . '/../config/conexion.php';
function respond($data = null, string $message = '', int $status = 200): void
{
    http_response_code($status);
    echo json_encode(['success' => $status < 400, 'data' => $data, 'mensaje' => $message], JSON_UNESCAPED_UNICODE | JSON_THROW_ON_ERROR);
    exit;
}

function method(string $expected): void
{
    if ($_SERVER['REQUEST_METHOD'] !== $expected) {
        header('Allow: ' . $expected);
        respond(null, 'Metodo no permitido.', 405);
    }
}

function body(): array
{
    if ((int) ($_SERVER['CONTENT_LENGTH'] ?? 0) > 262144) {
        respond(null, 'Solicitud demasiado grande.', 413);
    }
    try {
        $valor = json_decode(file_get_contents('php://input'), true, 64, JSON_THROW_ON_ERROR);
    } catch (JsonException $error) {
        respond(null, 'JSON invalido.', 400);
    }
    if (!is_array($valor)) {
        respond(null, 'Se esperaba un objeto JSON.', 400);
    }
    return $valor;
}

function integer(array $datos, string $campo, int $minimo = 1, int $maximo = 2147483647): int
{
    $valor = filter_var($datos[$campo] ?? null, FILTER_VALIDATE_INT);
    if ($valor === false || $valor === null || $valor < $minimo || $valor > $maximo) {
        respond(null, "Valor invalido: $campo.", 422);
    }
    return $valor;
}

function txt(array $datos, string $campo, int $maximo, bool $optional = false): string
{
    if (!isset($datos[$campo]) && $optional) {
        return '';
    }
    if (!isset($datos[$campo]) || !is_string($datos[$campo])) {
        respond(null, "Texto invalido: $campo.", 422);
    }
    $valor = trim($datos[$campo]);
    if ((!$optional && $valor === '') || mb_strlen($valor) > $maximo) {
        respond(null, "Longitud invalida: $campo.", 422);
    }
    return $valor;
}

function query(string $sql, array $params = []): mysqli_stmt
{
    global $conexion;
    $consulta = $conexion->prepare($sql);
    if ($params) {
        $tipos = '';
        foreach ($params as $parametro) {
            if (is_int($parametro)) {
                $tipos .= 'i';
            } elseif (is_float($parametro)) {
                $tipos .= 'd';
            } else {
                $tipos .= 's';
            }
        }
        $consulta->bind_param($tipos, ...$params);
    }
    $consulta->execute();
    return $consulta;
}

function rows(string $sql, array $params = []): array
{
    return query($sql, $params)->get_result()->fetch_all(MYSQLI_ASSOC);
}

function bearer(): string
{
    $cabecera = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? '';
    if (!preg_match('/^Bearer ([a-f0-9]{64})$/D', $cabecera, $coincidencias)) {
        respond(null, 'Inicia sesion nuevamente.', 401);
    }
    return $coincidencias[1];
}

function user(): array
{
    $token = hash('sha256', bearer());
    $usuarios = rows('SELECT u.id_usuario,u.id_rol,u.nombre,u.apellido,u.usuario,r.nombre AS rol
        FROM sesiones s
        JOIN usuarios u ON u.id_usuario=s.id_usuario
        JOIN roles r ON r.id_rol=u.id_rol
        WHERE s.token_hash=? AND s.expira>UTC_TIMESTAMP() AND u.estado=1', [$token]);
    if (!$usuarios) {
        respond(null, 'Tu sesion vencio. Inicia sesion nuevamente.', 401);
    }
    $usuario = $usuarios[0];
    $usuario['id_usuario'] = (int) $usuario['id_usuario'];
    $usuario['id_rol'] = (int) $usuario['id_rol'];
    return $usuario;
}

function admin(): array
{
    $usuario = user();
    if ($usuario['rol'] !== 'ADMIN') {
        respond(null, 'Esta operacion requiere un administrador.', 403);
    }
    return $usuario;
}

function activeLesson(int $id): array
{
    $lecciones = rows('SELECT l.*
        FROM lecciones l
        JOIN topicos t ON t.id_topico=l.id_topico
        JOIN cursos c ON c.id_curso=t.id_curso
        WHERE l.id_leccion=? AND l.estado=1 AND t.estado=1 AND c.estado=1', [$id]);
    if (!$lecciones) {
        respond(null, 'La leccion no esta disponible.', 404);
    }
    return $lecciones[0];
}
