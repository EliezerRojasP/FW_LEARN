<?php
require __DIR__.'/bootstrap.php';method('POST');$u=user();$id=integer(body(),'id_leccion');activeLesson($id);
$questions=rows('SELECT id_pregunta,enunciado,orden FROM preguntas WHERE id_leccion=? AND estado=1 ORDER BY orden,id_pregunta',[$id]);
if(!$questions) respond(null,'Esta lección todavía no tiene evaluación.',409);
$private=[];$public=[];
foreach($questions as $p){
 $alts=rows('SELECT id_alternativa,texto,es_correcta FROM alternativas WHERE id_pregunta=? ORDER BY id_alternativa',[(int)$p['id_pregunta']]);
 if(count($alts)<2 || count(array_filter($alts,fn($a)=>(int)$a['es_correcta']===1))!==1) respond(null,'La evaluación necesita revisión del administrador.',409);
 shuffle($alts);$pid=(int)$p['id_pregunta'];
 $private[]=['id'=>$pid,'enunciado'=>$p['enunciado'],'alternativas'=>$alts];
 $public[]=['id'=>$pid,'enunciado'=>$p['enunciado'],'alternativas'=>array_map(fn($a)=>['id'=>(int)$a['id_alternativa'],'texto'=>$a['texto']],$alts)];
}
$attempt=bin2hex(random_bytes(32));
query('INSERT INTO evaluaciones(id_intento,id_usuario,id_leccion,snapshot,expira) VALUES(?,?,?,?,UTC_TIMESTAMP()+INTERVAL 2 HOUR)',[$attempt,$u['id_usuario'],$id,json_encode($private,JSON_UNESCAPED_UNICODE|JSON_THROW_ON_ERROR)]);
respond(['id_intento'=>$attempt,'id_leccion'=>$id,'preguntas'=>$public], 'Evaluación lista.',201);
