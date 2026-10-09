<?php
require __DIR__.'/bootstrap.php';require __DIR__.'/progreso_lib.php';method('GET');$u=user();
$courses=courseProgress($u['id_usuario']);$total=0;$completed=0;
foreach($courses as $course){$total+=$course['total_lecciones'];$completed+=$course['lecciones_completadas'];}
respond(['total_lecciones'=>$total,'lecciones_completadas'=>$completed,'porcentaje_avance'=>$total>0?round(100.0*$completed/$total,2):0.0,'cursos'=>$courses,'temas'=>topicProgress($u['id_usuario'])]);
