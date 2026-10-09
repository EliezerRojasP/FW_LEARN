<?php
// Consultas compartidas: el avance cuenta lecciones terminadas, no respuestas correctas.
function addPercent(array $row): array {
 $row['total_lecciones']=(int)$row['total_lecciones'];
 $row['lecciones_completadas']=(int)$row['lecciones_completadas'];
 $row['porcentaje_avance']=$row['total_lecciones']>0?round(100.0*$row['lecciones_completadas']/$row['total_lecciones'],2):0.0;
 return $row;
}
function courseProgress(int $uid): array {
 $data=rows("SELECT c.id_curso AS id,c.nombre,COALESCE(c.descripcion,'') AS descripcion,COUNT(l.id_leccion) AS total_lecciones,SUM(CASE WHEN p.completada=1 THEN 1 ELSE 0 END) AS lecciones_completadas FROM cursos c LEFT JOIN topicos t ON t.id_curso=c.id_curso AND t.estado=1 LEFT JOIN lecciones l ON l.id_topico=t.id_topico AND l.estado=1 LEFT JOIN progreso p ON p.id_leccion=l.id_leccion AND p.id_usuario=? WHERE c.estado=1 GROUP BY c.id_curso,c.nombre,c.descripcion ORDER BY c.id_curso",[$uid]);
 return array_map('addPercent',$data);
}
function topicProgress(int $uid,int $course=0): array {
 $data=rows("SELECT t.id_topico,t.id_curso,t.nombre,COALESCE(t.descripcion,'') AS descripcion,t.orden,t.estado,COUNT(l.id_leccion) AS total_lecciones,SUM(CASE WHEN p.completada=1 THEN 1 ELSE 0 END) AS lecciones_completadas FROM topicos t JOIN cursos c ON c.id_curso=t.id_curso LEFT JOIN lecciones l ON l.id_topico=t.id_topico AND l.estado=1 LEFT JOIN progreso p ON p.id_leccion=l.id_leccion AND p.id_usuario=? WHERE t.estado=1 AND c.estado=1 AND (?=0 OR t.id_curso=?) GROUP BY t.id_topico,t.id_curso,t.nombre,t.descripcion,t.orden,t.estado ORDER BY t.id_curso,t.orden,t.id_topico",[$uid,$course,$course]);
 return array_map('addPercent',$data);
}
function lessonProgress(int $uid,int $topic=0): array {
 $data=rows("SELECT l.*,COALESCE(p.completada,0) AS completada,COALESCE(p.mejor_puntaje,0) AS mejor_puntaje,CASE WHEN p.completada=1 THEN 100 ELSE 0 END AS porcentaje_avance,(SELECT COUNT(*) FROM preguntas q WHERE q.id_leccion=l.id_leccion AND q.estado=1) AS total_preguntas FROM lecciones l JOIN topicos t ON t.id_topico=l.id_topico JOIN cursos c ON c.id_curso=t.id_curso LEFT JOIN progreso p ON p.id_leccion=l.id_leccion AND p.id_usuario=? WHERE l.estado=1 AND t.estado=1 AND c.estado=1 AND (?=0 OR l.id_topico=?) ORDER BY t.id_curso,t.orden,l.orden,l.id_leccion",[$uid,$topic,$topic]);
 return $data;
}
function topicForLesson(int $uid,int $lesson): ?array {
 $r=rows("SELECT id_topico FROM lecciones WHERE id_leccion=?",[$lesson]);
 if(!$r)return null;
 foreach(topicProgress($uid) as $topic)if((int)$topic['id_topico']===(int)$r[0]['id_topico'])return $topic;
 return null;
}
function resultProgress(array $result,int $uid,int $lesson): array {
 $result['leccion_completada']=true;$result['porcentaje_finalizacion']=100.0;
 $result['aprobada']=(float)$result['porcentaje']>=70;
 $result['tema']=topicForLesson($uid,$lesson);
 return $result;
}
