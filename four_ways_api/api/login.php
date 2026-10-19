<?php

require __DIR__ . '/bootstrap.php';
method('POST');
$datos = body();
$nombreUsuario = txt($datos, 'usuario', 50);
$password = $datos['password'] ?? '';
if (!is_string($password) || strlen($password) > 1024 || $password === '') {
    respond(null, 'Contraseña invalida.', 422);
}
// Limita intentos por IP y por usuario sin bloquear permanentemente una cuenta.
$ip = $_SERVER['REMOTE_ADDR'] ?? 'unknown';
$intentos = rows('SELECT COUNT(*) AS n
        FROM intentos_login
        WHERE fecha>UTC_TIMESTAMP()-INTERVAL 15 MINUTE AND (ip=? OR usuario=?)', [$ip, $nombreUsuario]);
if ((int) $intentos[0]['n'] >= 20) {
    respond(null, 'Demasiados intentos. Espera 15 minutos.', 429);
}
$usuarios = rows('SELECT u.*,r.nombre AS rol
        FROM usuarios u
        JOIN roles r ON r.id_rol=u.id_rol
        WHERE u.usuario=? AND u.estado=1', [$nombreUsuario]);
$credencialesValidas = $usuarios && password_verify($password, $usuarios[0]['password']);
if (!$credencialesValidas) {
    query('INSERT INTO intentos_login(ip,usuario) VALUES(?,?)', [$ip, $nombreUsuario]);
    respond(null, 'Usuario o contraseña incorrectos.', 401);
}
$usuario = $usuarios[0];
$token = bin2hex(random_bytes(32));
query('DELETE FROM sesiones WHERE expira<UTC_TIMESTAMP()');
query('INSERT INTO sesiones(id_usuario,token_hash,expira) VALUES(?,?,UTC_TIMESTAMP()+INTERVAL 7 DAY)', [(int) $usuario['id_usuario'], hash('sha256', $token)]);
unset($usuario['password']);
$usuario['id_usuario'] = (int) $usuario['id_usuario'];
$usuario['id_rol'] = (int) $usuario['id_rol'];
respond(['token' => $token, 'usuario' => $usuario]);
