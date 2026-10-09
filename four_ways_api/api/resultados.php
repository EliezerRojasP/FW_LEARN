<?php
require __DIR__.'/bootstrap.php';method('GET');$u=user();
respond(rows('SELECT r.*,l.titulo FROM resultados r JOIN lecciones l ON l.id_leccion=r.id_leccion WHERE r.id_usuario=? ORDER BY r.fecha_resultado DESC,r.id_resultado DESC LIMIT 100',[$u['id_usuario']]));
