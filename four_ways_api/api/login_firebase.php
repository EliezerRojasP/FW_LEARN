<?php

require __DIR__ . '/bootstrap.php';
method('POST');

$config = require __DIR__ . '/../config/firebase.php';
$datos = body();
$idToken = txt($datos, 'id_token', 16384);

$partes = explode('.', $idToken);

if(count($partes)  !==3){
    respond(null, 'Token de Firebase invalido.', 401);
}

$contenido = base64_decode(strtr($partes[1], '-_', '+/'), true);
$identidad = json_decode($contenido ?: '', true);

if (
    !is_array($identidad) ||
    ($identidad['aud'] ?? '') !== $config['project_id'] ||
    ($identidad['iss'] ?? '') !==
        'https://securetoken.google.com/' . $config['project_id']
) {
    respond(null, 'El token no corresponde a este proyecto.', 401);
}

if (!function_exists('curl_init')) {
    respond(null, 'Falta habilitar cURL en PHP.', 500);
}

$url = 'https://identitytoolkit.googleapis.com/v1/accounts:lookup?key='
    . rawurlencode($config['api_key']);

$consulta = curl_init($url);

curl_setopt_array($consulta, [
    CURLOPT_POST => true,
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_HTTPHEADER => ['Content-Type: application/json'],
    CURLOPT_POSTFIELDS => json_encode(['idToken' => $idToken]),
    CURLOPT_CONNECTTIMEOUT => 5,
    CURLOPT_TIMEOUT => 10
]);

$respuesta = curl_exec($consulta);
$codigo = curl_getinfo($consulta, CURLINFO_HTTP_CODE);

if ($respuesta === false) {
    error_log('Conexion con Firebase: ' . curl_error($consulta));
    curl_close($consulta);
    respond(null, 'No se pudo conectar con Firebase.', 503);
}

curl_close($consulta);
$datosFirebase = json_decode($respuesta, true);

if ($codigo !== 200) {
    error_log(
        'Error Firebase: ' .
        ($datosFirebase['error']['message'] ?? 'Respuesta no valida')
    );

    $estado = in_array($codigo, [400, 401], true) ? 401 : 503;
    respond(null, 'No se pudo validar la sesion con Firebase.', $estado);
}

$cuenta = $datosFirebase['users'][0] ?? null;
$uid = $cuenta['localId'] ?? '';

if (
    !is_array($cuenta) ||
    !is_string($uid) ||
    $uid === '' ||
    ($cuenta['disabled'] ?? false) ||
    ($identidad['sub'] ?? '') !== $uid ||
    ($identidad['iat'] ?? 0) < (int) ($cuenta['validSince'] ?? 0)
) {
    respond(null, 'La sesion de Firebase no es valida.', 401);
}

$usuarios = rows(
    'SELECT u.id_usuario, u.id_rol, u.nombre, u.apellido,
            u.usuario, r.nombre AS rol
     FROM usuarios u
     JOIN roles r ON r.id_rol = u.id_rol
     WHERE u.firebase_uid = ? AND u.estado = 1',
    [$uid]
);

if (!$usuarios) {
    respond(null, 'La cuenta no esta vinculada a un usuario activo.', 403);
}

$usuario = $usuarios[0];
$usuario['id_usuario'] = (int) $usuario['id_usuario'];
$usuario['id_rol'] = (int) $usuario['id_rol'];


$token = bin2hex(random_bytes(32));

query('DELETE FROM sesiones WHERE expira < UTC_TIMESTAMP()');

query(
    'INSERT INTO sesiones(id_usuario, token_hash, expira)
     VALUES(?, ?, UTC_TIMESTAMP() + INTERVAL 7 DAY)',
    [$usuario['id_usuario'], hash('sha256', $token)]
);

respond([
    'token' => $token,
    'usuario' => $usuario
]);