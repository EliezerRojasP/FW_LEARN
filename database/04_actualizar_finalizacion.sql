USE db_fw_learn_v2;
-- Una evaluación enviada y guardada termina la lección, independientemente de la nota.
-- Conserva la mejor nota y actualiza resultados existentes de la versión anterior.
INSERT INTO progreso(id_usuario,id_leccion,completada,mejor_puntaje)
SELECT id_usuario,id_leccion,1,MAX(porcentaje) FROM resultados GROUP BY id_usuario,id_leccion
ON DUPLICATE KEY UPDATE completada=1,mejor_puntaje=GREATEST(mejor_puntaje,VALUES(mejor_puntaje));
