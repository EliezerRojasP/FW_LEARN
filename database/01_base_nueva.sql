CREATE DATABASE IF NOT EXISTS db_fw_learn_v2 CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE db_fw_learn_v2;
-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 09-10-2026 a las 09:09:10
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `db_fw_learn`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `alternativas`
--

CREATE TABLE `alternativas` (
  `id_alternativa` int(11) NOT NULL,
  `id_pregunta` int(11) NOT NULL,
  `texto` varchar(500) NOT NULL,
  `es_correcta` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `alternativas`
--

INSERT INTO `alternativas` (`id_alternativa`, `id_pregunta`, `texto`, `es_correcta`) VALUES
(1, 1, 'Un conjunto de instrucciones que puede ejecutar una computadora.', 1),
(2, 1, 'Un componente físico de una computadora.', 0),
(3, 1, 'Una conexión a Internet.', 0),
(4, 1, 'Un archivo de imagen.', 0),
(5, 2, 'Resolver problemas mediante instrucciones.', 1),
(6, 2, 'Solamente diseñar páginas web.', 0),
(7, 2, 'Reemplazar el hardware.', 0),
(8, 2, 'Crear conexiones físicas.', 0),
(9, 3, 'Un lenguaje utilizado para escribir instrucciones que una computadora puede interpretar o ejecutar.', 1),
(10, 3, 'Un dispositivo de almacenamiento.', 0),
(11, 3, 'Un sistema operativo.', 0),
(12, 3, 'Una base de datos.', 0),
(13, 4, 'Un espacio identificado que permite almacenar un valor.', 1),
(14, 4, 'Una pantalla.', 0),
(15, 4, 'Un servidor físico.', 0),
(16, 4, 'Un cable de red.', 0),
(17, 5, 'Entero.', 1),
(18, 5, 'Monitor.', 0),
(19, 5, 'Teclado.', 0),
(20, 5, 'Servidor.', 0),
(21, 6, 'Para ejecutar instrucciones dependiendo de una condición.', 1),
(22, 6, 'Para almacenar imágenes.', 0),
(23, 6, 'Para conectarse a Internet.', 0),
(24, 6, 'Para crear una base de datos automáticamente.', 0),
(25, 7, 'Para repetir instrucciones una cantidad determinada de veces.', 1),
(26, 7, 'Para eliminar una base de datos.', 0),
(27, 7, 'Para crear usuarios.', 0),
(28, 7, 'Para diseñar una página web.', 0),
(29, 8, 'Un bloque de código diseñado para realizar una tarea específica.', 1),
(30, 8, 'Un tipo de hardware.', 0),
(31, 8, 'Un navegador web.', 0),
(32, 8, 'Una tabla de base de datos.', 0),
(33, 9, 'Un sistema organizado para almacenar y gestionar información.', 1),
(34, 9, 'Un lenguaje de programación.', 0),
(35, 9, 'Un dispositivo físico.', 0),
(36, 9, 'Un navegador.', 0),
(37, 10, 'SELECT.', 1),
(38, 10, 'DELETE solamente.', 0),
(39, 10, 'CREATE solamente.', 0),
(40, 10, 'START.', 0),
(41, 11, 'HTML.', 1),
(42, 11, 'SQL.', 0),
(43, 11, 'PHP solamente.', 0),
(44, 11, 'MySQL solamente.', 0),
(45, 12, 'Para definir estilos y presentación visual de una página web.', 1),
(46, 12, 'Para almacenar datos.', 0),
(47, 12, 'Para crear tablas SQL.', 0),
(48, 12, 'Para administrar usuarios de Android.', 0),
(49, 13, 'Una secuencia ordenada de pasos para resolver un problema.', 1),
(50, 13, 'Un componente físico.', 0),
(51, 13, 'Una base de datos.', 0),
(52, 13, 'Un lenguaje de marcado.', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cursos`
--

CREATE TABLE `cursos` (
  `id_curso` int(11) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `cursos`
--

INSERT INTO `cursos` (`id_curso`, `nombre`, `descripcion`, `estado`, `fecha_creacion`) VALUES
(1, 'Fundamentos de Programación', 'Aprende los conceptos fundamentales para comenzar a programar.', 1, '2026-10-05 03:09:41'),
(2, 'Bases de Datos', 'Aprende a organizar, consultar y administrar información.', 1, '2026-10-05 03:09:41'),
(3, 'Desarrollo Web', 'Conoce los fundamentos para crear aplicaciones web modernas.', 1, '2026-10-05 03:09:41'),
(4, 'Algoritmos', 'Desarrolla habilidades para resolver problemas mediante algoritmos.', 1, '2026-10-05 03:09:41');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `lecciones`
--

CREATE TABLE `lecciones` (
  `id_leccion` int(11) NOT NULL,
  `id_topico` int(11) NOT NULL,
  `titulo` varchar(200) NOT NULL,
  `contenido` text DEFAULT NULL,
  `orden` int(11) NOT NULL DEFAULT 1,
  `estado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `lecciones`
--

INSERT INTO `lecciones` (`id_leccion`, `id_topico`, `titulo`, `contenido`, `orden`, `estado`) VALUES
(1, 1, '¿Qué es programar?', 'Introducción a la programación y al pensamiento computacional.', 1, 1),
(2, 1, 'Lenguajes de programación', 'Conceptos generales sobre lenguajes de programación.', 2, 1),
(3, 2, 'Variables', 'Concepto y utilización de variables.', 1, 1),
(4, 2, 'Tipos de datos', 'Principales tipos de datos utilizados en programación.', 2, 1),
(5, 3, 'Sentencia IF', 'Uso de estructuras condicionales.', 1, 1),
(6, 3, 'IF - ELSE', 'Uso de alternativas dentro de un programa.', 2, 1),
(7, 4, 'Ciclo FOR', 'Repetición de instrucciones mediante FOR.', 1, 1),
(8, 4, 'Ciclo WHILE', 'Repetición de instrucciones mediante WHILE.', 2, 1),
(9, 5, 'Concepto de función', 'Qué son las funciones y para qué sirven.', 1, 1),
(10, 5, 'Parámetros y retorno', 'Uso de parámetros y valores de retorno.', 2, 1),
(11, 6, 'Conceptos básicos de bases de datos', 'Introducción a las bases de datos.', 1, 1),
(12, 7, 'Entidades y relaciones', 'Conceptos del modelo entidad relación.', 1, 1),
(13, 8, 'SELECT', 'Consulta de información utilizando SQL.', 1, 1),
(14, 9, 'Conceptos básicos de desarrollo web', 'Introducción al desarrollo web.', 1, 1),
(15, 10, 'Estructura HTML', 'Elementos principales de HTML.', 1, 1),
(16, 11, 'Estilos CSS', 'Conceptos básicos de CSS.', 1, 1),
(17, 12, '¿Qué es un algoritmo?', 'Conceptos fundamentales de algoritmos.', 1, 1),
(18, 13, 'Diagramas de flujo', 'Representación gráfica de algoritmos.', 1, 1),
(19, 14, 'Eficiencia algorítmica', 'Conceptos básicos de complejidad.', 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `preguntas`
--

CREATE TABLE `preguntas` (
  `id_pregunta` int(11) NOT NULL,
  `id_leccion` int(11) NOT NULL,
  `enunciado` text NOT NULL,
  `orden` int(11) NOT NULL DEFAULT 1,
  `estado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `preguntas`
--

INSERT INTO `preguntas` (`id_pregunta`, `id_leccion`, `enunciado`, `orden`, `estado`) VALUES
(1, 1, '¿Qué es un programa?', 1, 1),
(2, 1, '¿Cuál es uno de los objetivos de la programación?', 2, 1),
(3, 2, '¿Qué es un lenguaje de programación?', 1, 1),
(4, 3, '¿Qué es una variable?', 1, 1),
(5, 4, '¿Cuál de los siguientes representa un tipo de dato?', 1, 1),
(6, 5, '¿Para qué sirve una estructura IF?', 1, 1),
(7, 7, '¿Para qué sirve un ciclo FOR?', 1, 1),
(8, 9, '¿Qué es una función?', 1, 1),
(9, 11, '¿Qué es una base de datos?', 1, 1),
(10, 13, '¿Qué comando SQL permite consultar datos?', 1, 1),
(11, 15, '¿Qué lenguaje se utiliza para estructurar páginas web?', 1, 1),
(12, 16, '¿Para qué se utiliza CSS?', 1, 1),
(13, 17, '¿Qué es un algoritmo?', 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `progreso`
--

CREATE TABLE `progreso` (
  `id_progreso` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `id_leccion` int(11) NOT NULL,
  `completada` tinyint(1) NOT NULL DEFAULT 0,
  `mejor_puntaje` decimal(5,2) NOT NULL DEFAULT 0.00,
  `fecha_actualizacion` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `progreso`
--

INSERT INTO `progreso` (`id_progreso`, `id_usuario`, `id_leccion`, `completada`, `mejor_puntaje`, `fecha_actualizacion`) VALUES
(1, 1, 1, 1, 75.00, '2026-10-05 04:23:46');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `resultados`
--

CREATE TABLE `resultados` (
  `id_resultado` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `id_leccion` int(11) NOT NULL,
  `puntaje` decimal(5,2) NOT NULL DEFAULT 0.00,
  `total_preguntas` int(11) NOT NULL DEFAULT 0,
  `porcentaje` decimal(5,2) NOT NULL DEFAULT 0.00,
  `fecha_resultado` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `resultados`
--

INSERT INTO `resultados` (`id_resultado`, `id_usuario`, `id_leccion`, `puntaje`, `total_preguntas`, `porcentaje`, `fecha_resultado`) VALUES
(1, 1, 1, 75.00, 4, 75.00, '2026-10-05 04:22:31');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `id_rol` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id_rol`, `nombre`) VALUES
(1, 'ADMIN'),
(2, 'ALUMNO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `topicos`
--

CREATE TABLE `topicos` (
  `id_topico` int(11) NOT NULL,
  `id_curso` int(11) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `orden` int(11) NOT NULL DEFAULT 1,
  `estado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `topicos`
--

INSERT INTO `topicos` (`id_topico`, `id_curso`, `nombre`, `descripcion`, `orden`, `estado`) VALUES
(1, 1, 'Introducción a la programación', 'Conceptos básicos de programación y resolución de problemas.', 1, 1),
(2, 1, 'Variables y tipos de datos', 'Aprende a trabajar con variables y diferentes tipos de datos.', 2, 1),
(3, 1, 'Condicionales', 'Aprende a tomar decisiones dentro de un programa.', 3, 1),
(4, 1, 'Estructuras repetitivas', 'Aprende a utilizar ciclos para repetir instrucciones.', 4, 1),
(5, 1, 'Funciones', 'Aprende a crear y utilizar funciones.', 5, 1),
(6, 2, 'Introducción a las bases de datos', 'Conceptos fundamentales de bases de datos.', 1, 1),
(7, 2, 'Modelo entidad relación', 'Aprende a representar entidades y relaciones.', 2, 1),
(8, 2, 'SQL', 'Aprende los fundamentos del lenguaje SQL.', 3, 1),
(9, 3, 'Introducción al desarrollo web', 'Conceptos básicos del desarrollo web.', 1, 1),
(10, 3, 'HTML', 'Aprende a estructurar páginas web.', 2, 1),
(11, 3, 'CSS', 'Aprende a diseñar y dar estilo a páginas web.', 3, 1),
(12, 4, 'Conceptos de algoritmos', 'Introducción a los algoritmos y resolución de problemas.', 1, 1),
(13, 4, 'Diagramas de flujo', 'Representación gráfica de algoritmos.', 2, 1),
(14, 4, 'Complejidad', 'Conceptos básicos de eficiencia algorítmica.', 3, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL,
  `id_rol` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `usuario` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `id_rol`, `nombre`, `apellido`, `usuario`, `password`, `estado`, `fecha_registro`) VALUES
(1, 2, 'Aaron', 'Pacheco', 'alumno1', '123456', 1, '2026-10-05 04:17:40');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `alternativas`
--
ALTER TABLE `alternativas`
  ADD PRIMARY KEY (`id_alternativa`),
  ADD KEY `fk_alternativa_pregunta` (`id_pregunta`);

--
-- Indices de la tabla `cursos`
--
ALTER TABLE `cursos`
  ADD PRIMARY KEY (`id_curso`);

--
-- Indices de la tabla `lecciones`
--
ALTER TABLE `lecciones`
  ADD PRIMARY KEY (`id_leccion`),
  ADD KEY `fk_leccion_topico` (`id_topico`);

--
-- Indices de la tabla `preguntas`
--
ALTER TABLE `preguntas`
  ADD PRIMARY KEY (`id_pregunta`),
  ADD KEY `fk_pregunta_leccion` (`id_leccion`);

--
-- Indices de la tabla `progreso`
--
ALTER TABLE `progreso`
  ADD PRIMARY KEY (`id_progreso`),
  ADD UNIQUE KEY `uk_progreso_usuario_leccion` (`id_usuario`,`id_leccion`),
  ADD KEY `fk_progreso_leccion` (`id_leccion`);

--
-- Indices de la tabla `resultados`
--
ALTER TABLE `resultados`
  ADD PRIMARY KEY (`id_resultado`),
  ADD KEY `fk_resultado_usuario` (`id_usuario`),
  ADD KEY `fk_resultado_leccion` (`id_leccion`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id_rol`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `topicos`
--
ALTER TABLE `topicos`
  ADD PRIMARY KEY (`id_topico`),
  ADD KEY `fk_topico_curso` (`id_curso`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `usuario` (`usuario`),
  ADD KEY `fk_usuario_rol` (`id_rol`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `alternativas`
--
ALTER TABLE `alternativas`
  MODIFY `id_alternativa` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT de la tabla `cursos`
--
ALTER TABLE `cursos`
  MODIFY `id_curso` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `lecciones`
--
ALTER TABLE `lecciones`
  MODIFY `id_leccion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de la tabla `preguntas`
--
ALTER TABLE `preguntas`
  MODIFY `id_pregunta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `progreso`
--
ALTER TABLE `progreso`
  MODIFY `id_progreso` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `resultados`
--
ALTER TABLE `resultados`
  MODIFY `id_resultado` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `id_rol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `topicos`
--
ALTER TABLE `topicos`
  MODIFY `id_topico` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `alternativas`
--
ALTER TABLE `alternativas`
  ADD CONSTRAINT `fk_alternativa_pregunta` FOREIGN KEY (`id_pregunta`) REFERENCES `preguntas` (`id_pregunta`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `lecciones`
--
ALTER TABLE `lecciones`
  ADD CONSTRAINT `fk_leccion_topico` FOREIGN KEY (`id_topico`) REFERENCES `topicos` (`id_topico`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `preguntas`
--
ALTER TABLE `preguntas`
  ADD CONSTRAINT `fk_pregunta_leccion` FOREIGN KEY (`id_leccion`) REFERENCES `lecciones` (`id_leccion`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `progreso`
--
ALTER TABLE `progreso`
  ADD CONSTRAINT `fk_progreso_leccion` FOREIGN KEY (`id_leccion`) REFERENCES `lecciones` (`id_leccion`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_progreso_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `resultados`
--
ALTER TABLE `resultados`
  ADD CONSTRAINT `fk_resultado_leccion` FOREIGN KEY (`id_leccion`) REFERENCES `lecciones` (`id_leccion`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_resultado_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `topicos`
--
ALTER TABLE `topicos`
  ADD CONSTRAINT `fk_topico_curso` FOREIGN KEY (`id_curso`) REFERENCES `cursos` (`id_curso`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
