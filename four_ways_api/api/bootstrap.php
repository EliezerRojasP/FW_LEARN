<?php
declare(strict_types=1);
header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');
set_exception_handler(function(Throwable $e): void {
    error_log((string)$e);
    $code = $e instanceof mysqli_sql_exception && $e->getCode() === 1062 ? 409 : 500;
    respond(null, $code === 409 ? 'Ya existe un registro con esos datos.' : 'Error del servidor. Revisa el registro de Apache.', $code);
});
require_once __DIR__ . '/../config/conexion.php';
function respond($data = null, string $message = '', int $status = 200): void {
    http_response_code($status);
    echo json_encode(['success'=>$status < 400, 'data'=>$data, 'mensaje'=>$message], JSON_UNESCAPED_UNICODE | JSON_THROW_ON_ERROR);
    exit;
}
function method(string $expected): void {
    if ($_SERVER['REQUEST_METHOD'] !== $expected) { header('Allow: '.$expected); respond(null,'Método no permitido.',405); }
}
function body(): array {
    if ((int)($_SERVER['CONTENT_LENGTH'] ?? 0) > 262144) respond(null,'Solicitud demasiado grande.',413);
    try { $value=json_decode(file_get_contents('php://input'),true,64,JSON_THROW_ON_ERROR); }
    catch(JsonException $e) { respond(null,'JSON inválido.',400); }
    if (!is_array($value)) respond(null,'Se esperaba un objeto JSON.',400);
    return $value;
}
function integer(array $a,string $key,int $min=1,int $max=2147483647): int {
    $v=filter_var($a[$key] ?? null,FILTER_VALIDATE_INT);
    if ($v === false || $v === null || $v < $min || $v > $max) respond(null,"Valor inválido: $key.",422);
    return $v;
}
function txt(array $a,string $key,int $max,bool $optional=false): string {
    if (!isset($a[$key]) && $optional) return '';
    if (!isset($a[$key]) || !is_string($a[$key])) respond(null,"Texto inválido: $key.",422);
    $v=trim($a[$key]);
    if ((!$optional && $v === '') || mb_strlen($v) > $max) respond(null,"Longitud inválida: $key.",422);
    return $v;
}
function query(string $sql,array $params=[]): mysqli_stmt {
    global $conexion;
    $s=$conexion->prepare($sql);
    if ($params) {
        $types=''; foreach($params as $p) $types.=is_int($p)?'i':(is_float($p)?'d':'s');
        $s->bind_param($types,...$params);
    }
    $s->execute(); return $s;
}
function rows(string $sql,array $params=[]): array { return query($sql,$params)->get_result()->fetch_all(MYSQLI_ASSOC); }
function bearer(): string {
    $h=$_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? '';
    if (!preg_match('/^Bearer ([a-f0-9]{64})$/D',$h,$m)) respond(null,'Inicia sesión nuevamente.',401);
    return $m[1];
}
function user(): array {
    $token=hash('sha256',bearer());
    $found=rows('SELECT u.id_usuario,u.id_rol,u.nombre,u.apellido,u.usuario,r.nombre AS rol FROM sesiones s JOIN usuarios u ON u.id_usuario=s.id_usuario JOIN roles r ON r.id_rol=u.id_rol WHERE s.token_hash=? AND s.expira>UTC_TIMESTAMP() AND u.estado=1',[$token]);
    if (!$found) respond(null,'Tu sesión venció. Inicia sesión nuevamente.',401);
    $u=$found[0];$u['id_usuario']=(int)$u['id_usuario'];$u['id_rol']=(int)$u['id_rol'];return $u;
}
function admin(): array { $u=user(); if($u['rol']!=='ADMIN') respond(null,'Esta operación requiere un administrador.',403);return $u; }
function activeLesson(int $id): array {
    $r=rows('SELECT l.* FROM lecciones l JOIN topicos t ON t.id_topico=l.id_topico JOIN cursos c ON c.id_curso=t.id_curso WHERE l.id_leccion=? AND l.estado=1 AND t.estado=1 AND c.estado=1',[$id]);
    if(!$r) respond(null,'La lección no está disponible.',404);return $r[0];
}
