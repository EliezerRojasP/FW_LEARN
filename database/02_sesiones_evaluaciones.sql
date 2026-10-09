USE db_fw_learn_v2;
CREATE TABLE IF NOT EXISTS sesiones(
 token_hash CHAR(64) PRIMARY KEY,
 id_usuario INT NOT NULL,
 expira DATETIME NOT NULL,
 FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
 INDEX(expira)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS evaluaciones(
 id_intento CHAR(64) PRIMARY KEY,
 id_usuario INT NOT NULL,
 id_leccion INT NOT NULL,
 snapshot MEDIUMTEXT NOT NULL,
 revision MEDIUMTEXT DEFAULT NULL,
 expira DATETIME NOT NULL,
 id_resultado INT DEFAULT NULL,
 fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
 FOREIGN KEY(id_leccion) REFERENCES lecciones(id_leccion) ON DELETE CASCADE,
 FOREIGN KEY(id_resultado) REFERENCES resultados(id_resultado) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS intentos_login(
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 ip VARCHAR(45) NOT NULL,
 usuario VARCHAR(50) NOT NULL,
 fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 INDEX(fecha),INDEX(ip,fecha),INDEX(usuario,fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- Normaliza el registro de ejemplo: puntaje representa respuestas correctas.
UPDATE resultados SET puntaje=ROUND(porcentaje*total_preguntas/100) WHERE puntaje>total_preguntas;
