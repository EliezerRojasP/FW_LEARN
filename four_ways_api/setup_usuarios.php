<?php
// Solo funciona desde la Shell de XAMPP. No crea usuarios desde el navegador.
if(PHP_SAPI!=='cli'){http_response_code(404);exit;}
require __DIR__.'/config/conexion.php';
$conexion->begin_transaction();
// Convierte las contraseñas importadas en texto plano, conservando las cuentas.
$rows=$conexion->query('SELECT id_usuario,password FROM usuarios')->fetch_all(MYSQLI_ASSOC);
foreach($rows as $u){if(password_get_info($u['password'])['algoName']==='unknown'){
 $hash=password_hash($u['password'],PASSWORD_DEFAULT);$s=$conexion->prepare('UPDATE usuarios SET password=? WHERE id_usuario=?');$s->bind_param('si',$hash,$u['id_usuario']);$s->execute();
}}
// Estas credenciales son de demostración local. No reemplaza cuentas existentes.
foreach([[1,'Administrador','Demo','admin','AdminFW2026!'],[2,'Alumno','Demo','alumno','AlumnoFW2026!']] as $u){
 [$role,$name,$last,$login,$pass]=$u;$hash=password_hash($pass,PASSWORD_DEFAULT);
 $s=$conexion->prepare('INSERT INTO usuarios(id_rol,nombre,apellido,usuario,password) SELECT ?,?,?,?,? WHERE NOT EXISTS(SELECT 1 FROM usuarios WHERE usuario=?)');
 $s->bind_param('isssss',$role,$name,$last,$login,$hash,$login);$s->execute();
}
$conexion->commit();echo "Contraseñas convertidas. Cuentas demo creadas si no existían.\n";
