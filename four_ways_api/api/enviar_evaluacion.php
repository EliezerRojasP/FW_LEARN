<?php
require __DIR__.'/bootstrap.php';require __DIR__.'/progreso_lib.php';method('POST');$u=user();$d=body();
$attempt=txt($d,'id_intento',64);
if(!preg_match('/^[a-f0-9]{64}$/D',$attempt))respond(null,'Intento inválido.',422);
$answers=$d['respuestas'] ?? null;
if(!is_array($answers) || !array_is_list($answers) || count($answers)>500)respond(null,'Respuestas inválidas.',422);
$map=[];
foreach($answers as $a){
 if(!is_array($a))respond(null,'Respuesta inválida.',422);
 $pid=integer($a,'id_pregunta');$aid=integer($a,'id_alternativa');
 if(isset($map[$pid]))respond(null,'Una pregunta está repetida.',422);$map[$pid]=$aid;
}
$conexion->begin_transaction();
$r=rows('SELECT *,expira<UTC_TIMESTAMP() AS vencida FROM evaluaciones WHERE id_intento=? AND id_usuario=? FOR UPDATE',[$attempt,$u['id_usuario']]);
if(!$r){$conexion->rollback();respond(null,'Intento no encontrado.',404);}
$attemptRow=$r[0];
// Reintentar después de una caída de conexión devuelve el resultado existente.
if($attemptRow['id_resultado']!==null){
 $result=rows('SELECT * FROM resultados WHERE id_resultado=?',[(int)$attemptRow['id_resultado']])[0];
 $result['revision']=json_decode($attemptRow['revision'],true);$result=resultProgress($result,$u['id_usuario'],(int)$attemptRow['id_leccion']);$conexion->commit();respond($result,'Resultado ya guardado.');
}
if((int)$attemptRow['vencida']===1){$conexion->rollback();respond(null,'La evaluación venció. Inicia una nueva.',409);}
$snapshot=json_decode($attemptRow['snapshot'],true,64,JSON_THROW_ON_ERROR);
if(count($map)!==count($snapshot)){$conexion->rollback();respond(null,'Responde todas las preguntas.',422);}
$correct=0;$review=[];
foreach($snapshot as $p){
 $aid=$map[$p['id']] ?? 0;$allowed=array_column($p['alternativas'],'id_alternativa');
 if(!in_array($aid,array_map('intval',$allowed),true)){$conexion->rollback();respond(null,'Una alternativa no pertenece a su pregunta.',422);}
 $right=array_values(array_filter($p['alternativas'],fn($a)=>(int)$a['es_correcta']===1))[0];
 $ok=$aid===(int)$right['id_alternativa'];if($ok)$correct++;
 $review[]=['enunciado'=>$p['enunciado'],'correcta'=>$ok,'respuesta_correcta'=>$right['texto']];
}
$total=count($snapshot);$percentage=round($correct*100.0/$total,2);$lesson=(int)$attemptRow['id_leccion'];
query('INSERT INTO resultados(id_usuario,id_leccion,puntaje,total_preguntas,porcentaje) VALUES(?,?,?,?,?)',[$u['id_usuario'],$lesson,$correct,$total,$percentage]);
$resultId=$conexion->insert_id;
query('INSERT INTO progreso(id_usuario,id_leccion,completada,mejor_puntaje) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE completada=GREATEST(completada,VALUES(completada)),mejor_puntaje=GREATEST(mejor_puntaje,VALUES(mejor_puntaje))',[$u['id_usuario'],$lesson,1,$percentage]);
query('UPDATE evaluaciones SET id_resultado=?,revision=? WHERE id_intento=?',[$resultId,json_encode($review,JSON_UNESCAPED_UNICODE|JSON_THROW_ON_ERROR),$attempt]);
$result=rows('SELECT * FROM resultados WHERE id_resultado=?',[$resultId])[0];$result['revision']=$review;
$result=resultProgress($result,$u['id_usuario'],$lesson);
$conexion->commit();respond($result,'Lección completada. Resultado y progreso guardados.');
