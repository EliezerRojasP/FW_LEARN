<?php
require __DIR__.'/bootstrap.php';$u=admin();$resource=$_GET['recurso'] ?? '';
$definitions=[
 'cursos'=>['id_curso',['nombre','descripcion','estado']],
 'topicos'=>['id_topico',['id_curso','nombre','descripcion','orden','estado']],
 'lecciones'=>['id_leccion',['id_topico','titulo','contenido','orden','estado']],
 'preguntas'=>['id_pregunta',['id_leccion','enunciado','orden','estado']],
 'usuarios'=>['id_usuario',['id_rol','nombre','apellido','usuario','estado']]
];
if($resource==='estadisticas'){
 method('GET');respond([
  'alumnos'=>(int)rows('SELECT COUNT(*) AS n FROM usuarios WHERE id_rol=2 AND estado=1')[0]['n'],
  'cursos'=>(int)rows('SELECT COUNT(*) AS n FROM cursos WHERE estado=1')[0]['n'],
  'lecciones'=>(int)rows('SELECT COUNT(*) AS n FROM lecciones WHERE estado=1')[0]['n'],
  'intentos'=>(int)rows('SELECT COUNT(*) AS n FROM resultados')[0]['n'],
  'promedio'=>(float)(rows('SELECT AVG(porcentaje) AS n FROM resultados')[0]['n'] ?? 0)
 ]);
}
if($resource==='resultados'){
 method('GET');respond(rows('SELECT r.*,CONCAT(u.nombre,\' \',u.apellido) AS alumno,l.titulo FROM resultados r JOIN usuarios u ON u.id_usuario=r.id_usuario JOIN lecciones l ON l.id_leccion=r.id_leccion ORDER BY r.fecha_resultado DESC,r.id_resultado DESC LIMIT 200'));
}
if(!isset($definitions[$resource]))respond(null,'Recurso desconocido.',404);
[$pk,$fields]=$definitions[$resource];
$method=$_SERVER['REQUEST_METHOD'];
if($method==='GET'){
 $select=implode(',',array_merge([$pk],$fields));$data=rows("SELECT $select FROM $resource ORDER BY $pk");
 if($resource==='preguntas')foreach($data as &$p){$p['alternativas']=rows('SELECT id_alternativa,texto,es_correcta FROM alternativas WHERE id_pregunta=? ORDER BY id_alternativa',[(int)$p[$pk]]);}unset($p);
 respond($data);
}
if(!in_array($method,['POST','PUT','DELETE'],true))respond(null,'Método no permitido.',405);
$d=body();$id=$method==='POST'?0:integer($d,$pk);
if($method!=='POST' && !rows("SELECT $pk FROM $resource WHERE $pk=?",[$id]))respond(null,'Registro no encontrado.',404);
if($method==='DELETE'){
 if($resource==='usuarios' && $id===$u['id_usuario'])respond(null,'No puedes desactivar tu propia cuenta.',422);
 query("UPDATE $resource SET estado=0 WHERE $pk=?",[$id]);respond(null,'Registro desactivado. El historial se conserva.');
}
$values=[];
foreach($fields as $field){
 if($field==='estado')$values[$field]=integer($d,$field,0,1);
 elseif($field==='orden')$values[$field]=integer($d,$field,1,10000);
 elseif(str_starts_with($field,'id_'))$values[$field]=integer($d,$field);
 else {
  $limits=['nombre'=>$resource==='usuarios'?100:150,'apellido'=>100,'usuario'=>50,'titulo'=>200,'enunciado'=>4000,'descripcion'=>10000,'contenido'=>50000];
  $values[$field]=txt($d,$field,$limits[$field],in_array($field,['descripcion','contenido'],true));
 }
}
if($resource==='usuarios'){
 if(!in_array($values['id_rol'],[1,2],true))respond(null,'Rol inválido.',422);
 if($id===$u['id_usuario'] && ($values['estado']===0 || $values['id_rol']!==1))respond(null,'No puedes quitarte el acceso de administrador.',422);
 $pass=$d['password'] ?? '';
 if(!is_string($pass))respond(null,'Contraseña inválida.',422);
 if($method==='POST' || $pass!==''){
  if(strlen($pass)<8 || strlen($pass)>72)respond(null,'La contraseña debe tener entre 8 y 72 bytes.',422);
  $values['password']=password_hash($pass,PASSWORD_DEFAULT);
 }
}
$parent=['topicos'=>['cursos','id_curso'],'lecciones'=>['topicos','id_topico'],'preguntas'=>['lecciones','id_leccion']];
if(isset($parent[$resource])){
 [$table,$parentKey]=$parent[$resource];if(!rows("SELECT $parentKey FROM $table WHERE $parentKey=?",[$values[$parentKey]]))respond(null,'El registro padre no existe.',422);
}
$options=[];
if($resource==='preguntas'){
 $options=$d['alternativas'] ?? [];
 if(!is_array($options) || !array_is_list($options) || count($options)<2 || count($options)>6)respond(null,'La pregunta requiere de 2 a 6 alternativas.',422);
 $correct=0;$normalized=[];
 foreach($options as $a){if(!is_array($a))respond(null,'Alternativa inválida.',422);$ok=integer($a,'es_correcta',0,1);$correct+=$ok;$normalized[]=['texto'=>txt($a,'texto',500),'es_correcta'=>$ok];}
 if($correct!==1)respond(null,'Debe existir exactamente una alternativa correcta.',422);$options=$normalized;
}
$conexion->begin_transaction();
if($method==='POST'){
 $cols=implode(',',array_keys($values));$marks=implode(',',array_fill(0,count($values),'?'));
 query("INSERT INTO $resource($cols) VALUES($marks)",array_values($values));$id=$conexion->insert_id;
}else{
 $assign=implode(',',array_map(fn($f)=>"$f=?",array_keys($values)));query("UPDATE $resource SET $assign WHERE $pk=?",array_merge(array_values($values),[$id]));
}
if($resource==='preguntas'){
 query('DELETE FROM alternativas WHERE id_pregunta=?',[$id]);
 foreach($options as $a)query('INSERT INTO alternativas(id_pregunta,texto,es_correcta) VALUES(?,?,?)',[$id,$a['texto'],$a['es_correcta']]);
}
if($resource==='usuarios' && isset($values['password']))query('DELETE FROM sesiones WHERE id_usuario=?',[$id]);
$conexion->commit();respond(['id'=>$id],'Registro guardado.',$method==='POST'?201:200);
