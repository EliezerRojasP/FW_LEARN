<?php
require __DIR__.'/bootstrap.php'; method('POST'); $d=body();
$name=txt($d,'usuario',50);
$pass=$d['password'] ?? '';
if(!is_string($pass) || strlen($pass)>1024 || $pass==='') respond(null,'Contraseña inválida.',422);
// Limita intentos por IP y por usuario sin bloquear permanentemente una cuenta.
$ip=$_SERVER['REMOTE_ADDR'] ?? 'unknown';
$count=rows('SELECT COUNT(*) AS n FROM intentos_login WHERE fecha>UTC_TIMESTAMP()-INTERVAL 15 MINUTE AND (ip=? OR usuario=?)',[$ip,$name]);
if((int)$count[0]['n']>=20) respond(null,'Demasiados intentos. Espera 15 minutos.',429);
$r=rows('SELECT u.*,r.nombre AS rol FROM usuarios u JOIN roles r ON r.id_rol=u.id_rol WHERE u.usuario=? AND u.estado=1',[$name]);
$valid=$r && password_verify($pass,$r[0]['password']);
if(!$valid){query('INSERT INTO intentos_login(ip,usuario) VALUES(?,?)',[$ip,$name]);respond(null,'Usuario o contraseña incorrectos.',401);}
$u=$r[0];$token=bin2hex(random_bytes(32));
query('DELETE FROM sesiones WHERE expira<UTC_TIMESTAMP()');
query('INSERT INTO sesiones(id_usuario,token_hash,expira) VALUES(?,?,UTC_TIMESTAMP()+INTERVAL 7 DAY)',[(int)$u['id_usuario'],hash('sha256',$token)]);
unset($u['password']);$u['id_usuario']=(int)$u['id_usuario'];$u['id_rol']=(int)$u['id_rol'];
respond(['token'=>$token,'usuario'=>$u]);
