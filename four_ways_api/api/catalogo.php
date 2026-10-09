<?php
require_once __DIR__.'/bootstrap.php';require_once __DIR__.'/progreso_lib.php';method('GET');$u=user();
switch($resource){
 case 'cursos': $data=courseProgress($u['id_usuario']);break;
 case 'topicos': $data=topicProgress($u['id_usuario'],integer($_GET,'curso_id'));break;
 case 'lecciones': $data=lessonProgress($u['id_usuario'],integer($_GET,'topico_id'));break;
 case 'leccion':
  $id=integer($_GET,'id');activeLesson($id);$data=null;
  foreach(lessonProgress($u['id_usuario']) as $lesson)if((int)$lesson['id_leccion']===$id){$data=$lesson;break;}
  break;
 default: respond(null,'Recurso desconocido.',404);
}
respond($data);
