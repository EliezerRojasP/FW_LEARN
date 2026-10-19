-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 20-10-2026 a las 00:41:55
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
-- Base de datos: `db_fw_learn_v2`
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
(1, 1, 'Un componente físico.', 0),
(2, 1, 'Un cable de red.', 0),
(3, 1, 'Solo una imagen.', 0),
(4, 1, 'Un conjunto de instrucciones que puede ejecutar una computadora.', 1),
(5, 2, '5', 0),
(6, 2, '83', 0),
(7, 2, '24', 1),
(8, 2, '11', 0),
(9, 3, 'Para almacenar exclusivamente fotos.', 0),
(10, 3, 'Para expresar instrucciones con reglas definidas.', 1),
(11, 3, 'Para aumentar físicamente la memoria.', 0),
(12, 3, 'Para reemplazar el teclado.', 0),
(13, 4, 'Almacenar y utilizar un valor identificado por un nombre.', 1),
(14, 4, 'Crear hardware.', 0),
(15, 4, 'Cambiar la velocidad de Internet.', 0),
(16, 4, 'Eliminar todas las instrucciones.', 0),
(17, 5, 'Boolean', 0),
(18, 5, 'String', 0),
(19, 5, 'TextView', 0),
(20, 5, 'Int', 1),
(21, 6, 'Solo cuando la condición es falsa.', 0),
(22, 6, 'Antes de evaluar la condición.', 0),
(23, 6, 'Cuando su condición es verdadera.', 1),
(24, 6, 'Siempre.', 0),
(25, 7, 'Detiene el servidor.', 0),
(26, 7, 'Recorre elementos o repite instrucciones.', 1),
(27, 7, 'Elimina siempre una tabla.', 0),
(28, 7, 'Crea una cuenta automáticamente.', 0),
(29, 8, 'Un bloque de código que realiza una tarea.', 1),
(30, 8, 'Un componente físico.', 0),
(31, 8, 'Un navegador.', 0),
(32, 8, 'Una contraseña.', 0),
(33, 9, 'Un cable.', 0),
(34, 9, 'Un navegador.', 0),
(35, 9, 'Un monitor.', 0),
(36, 9, 'Un conjunto organizado de información.', 1),
(37, 10, 'DELETE', 0),
(38, 10, 'UPDATE', 0),
(39, 10, 'SELECT', 1),
(40, 10, 'DROP', 0),
(41, 11, 'MariaDB', 0),
(42, 11, 'HTML', 1),
(43, 11, 'SQL', 0),
(44, 11, 'CSS exclusivamente', 0),
(45, 12, 'Para definir la presentación visual.', 1),
(46, 12, 'Para almacenar usuarios.', 0),
(47, 12, 'Para consultar MariaDB.', 0),
(48, 12, 'Para autenticar por sí solo.', 0),
(49, 13, 'Un componente físico.', 0),
(50, 13, 'Un lenguaje de marcado.', 0),
(51, 13, 'Una tabla necesariamente.', 0),
(52, 13, 'Una secuencia precisa de pasos para resolver un problema.', 1),
(53, 14, 'Mostrar únicamente el resultado.', 0),
(54, 14, 'Cambiar el monitor.', 0),
(55, 14, 'Multiplicar precio por cantidad.', 1),
(56, 14, 'Leer únicamente el precio.', 0),
(57, 15, 'Una dirección IP', 0),
(58, 15, 'Kotlin', 1),
(59, 15, 'HTML solamente', 0),
(60, 15, 'CSS solamente', 0),
(61, 16, 'Hola, Ana', 1),
(62, 16, 'Hola, nombre', 0),
(63, 16, 'Ana, Hola', 0),
(64, 16, 'No muestra nada.', 0),
(65, 17, 'val', 0),
(66, 17, 'println', 0),
(67, 17, 'package', 0),
(68, 17, 'var', 1),
(69, 18, '5', 0),
(70, 18, '15', 0),
(71, 18, '8', 1),
(72, 18, '3', 0),
(73, 19, 'StringBuilder', 0),
(74, 19, 'Boolean', 1),
(75, 19, 'Double', 0),
(76, 19, 'Int', 0),
(77, 20, 'null', 1),
(78, 20, '0 siempre', 0),
(79, 20, '123', 0),
(80, 20, 'true', 0),
(81, 21, '=', 0),
(82, 21, '+=', 0),
(83, 21, '++', 0),
(84, 21, '==', 1),
(85, 22, 'Siempre', 0),
(86, 22, 'Solo en Android.', 0),
(87, 22, 'No', 1),
(88, 22, 'Sí', 0),
(89, 23, 'Ninguno aunque exista else.', 0),
(90, 23, 'else', 1),
(91, 23, 'Ambos bloques siempre.', 0),
(92, 23, 'Solo if', 0),
(93, 24, 'Aprueba.', 1),
(94, 24, 'Desaprueba.', 0),
(95, 24, 'Produce un error.', 0),
(96, 24, 'No se puede comparar.', 0),
(97, 25, 'Basta que una sea verdadera.', 0),
(98, 25, 'Niega las condiciones.', 0),
(99, 25, 'Concatena dos números.', 0),
(100, 25, 'Ambas deben ser verdaderas.', 1),
(101, 26, '4', 0),
(102, 26, '1', 0),
(103, 26, '3', 1),
(104, 26, '2', 0),
(105, 27, '123', 0),
(106, 27, '6', 1),
(107, 27, '3', 0),
(108, 27, '5', 0),
(109, 28, 'Mientras la condición sea verdadera.', 1),
(110, 28, 'Mientras sea falsa.', 0),
(111, 28, 'Exactamente una vez siempre.', 0),
(112, 28, 'Después de cerrar la aplicación.', 0),
(113, 29, '1', 0),
(114, 29, '3', 0),
(115, 29, 'Infinitas.', 0),
(116, 29, '0', 1),
(117, 30, 'Se crea una base de datos.', 0),
(118, 30, 'El lenguaje cambia.', 0),
(119, 30, 'Un bucle infinito.', 1),
(120, 30, 'La variable se elimina.', 0),
(121, 31, 'className', 0),
(122, 31, 'fun', 1),
(123, 31, 'SELECT', 0),
(124, 31, 'style', 0),
(125, 32, 'Llamar saludar().', 1),
(126, 32, 'Solo escribir su declaración.', 0),
(127, 32, 'Escribir su nombre en un comentario.', 0),
(128, 32, 'Cambiar un color.', 0),
(129, 33, 'Para cambiar el monitor.', 0),
(130, 33, 'Para borrar la función.', 0),
(131, 33, 'Para ocultar todo resultado.', 0),
(132, 33, 'Para recibir datos de entrada.', 1),
(133, 34, '2', 0),
(134, 34, '15', 0),
(135, 34, '8', 1),
(136, 34, '35', 0),
(137, 35, 'Crea una variable global.', 0),
(138, 35, 'Devuelve un resultado al llamador.', 1),
(139, 35, 'Muestra texto siempre.', 0),
(140, 35, 'Repite el bloque.', 0),
(141, 36, 'Un registro.', 1),
(142, 36, 'Todas las tablas.', 0),
(143, 36, 'Un servidor completo.', 0),
(144, 36, 'Una contraseña obligatoria.', 0),
(145, 37, 'Para dar formato al texto.', 0),
(146, 37, 'Para iniciar Apache.', 0),
(147, 37, 'Para repetir columnas.', 0),
(148, 37, 'Para identificar de forma única un registro.', 1),
(149, 38, 'Ninguna.', 0),
(150, 38, 'Solo muchos a cero.', 0),
(151, 38, 'Uno a muchos.', 1),
(152, 38, 'Uno a uno obligatoriamente.', 0),
(153, 39, 'Solo en el nombre del curso.', 0),
(154, 39, 'En topicos como clave foránea.', 1),
(155, 39, 'En un color XML.', 0),
(156, 39, 'En el teclado.', 0),
(157, 40, 'progreso', 1),
(158, 40, 'roles solamente', 0),
(159, 40, 'cursos solamente', 0),
(160, 40, 'alternativas solamente', 0),
(161, 41, 'ORDER BY solamente', 0),
(162, 41, 'INSERT', 0),
(163, 41, 'CREATE', 0),
(164, 41, 'WHERE', 1),
(165, 42, 'Crea una contraseña.', 0),
(166, 42, 'Actualiza todas las filas.', 0),
(167, 42, 'Ordena por nombre de forma ascendente.', 1),
(168, 42, 'Borra los nombres.', 0),
(169, 43, 'Define únicamente colores CSS.', 0),
(170, 43, 'Procesa solicitudes y consulta la base de datos.', 1),
(171, 43, 'Solo dibuja botones Android.', 0),
(172, 43, 'Reemplaza la batería.', 0),
(173, 44, 'JSON', 1),
(174, 44, 'Solo imágenes PNG.', 0),
(175, 44, 'Un archivo APK.', 0),
(176, 44, 'Audio MP3.', 0),
(177, 45, 'Dentro del APK.', 0),
(178, 45, 'En cada pregunta visible.', 0),
(179, 45, 'En el nombre del usuario.', 0),
(180, 45, 'En el servidor PHP.', 1),
(181, 46, 'head', 0),
(182, 46, 'title siempre', 0),
(183, 46, 'p', 1),
(184, 46, 'h1 siempre', 0),
(185, 47, 'font-size', 0),
(186, 47, 'href', 1),
(187, 47, 'color', 0),
(188, 47, 'id_curso', 0),
(189, 48, 'color', 1),
(190, 48, 'SELECT', 0),
(191, 48, 'href', 0),
(192, 48, 'id_topico', 0),
(193, 49, 'Todas las tablas SQL.', 0),
(194, 49, 'Solo enlaces externos.', 0),
(195, 49, 'Un archivo APK.', 0),
(196, 49, 'Elementos con la clase tarjeta.', 1),
(197, 50, '18', 0),
(198, 50, '12', 0),
(199, 50, '15', 1),
(200, 50, '45', 0),
(201, 51, 'La contraseña de todos los alumnos.', 0),
(202, 51, 'Entradas, pasos y salida esperada.', 1),
(203, 51, 'Solo el color del botón.', 0),
(204, 51, 'Solo el nombre del archivo.', 0),
(205, 52, 'Rombo', 1),
(206, 52, 'Rectángulo solamente', 0),
(207, 52, 'Óvalo solamente', 0),
(208, 52, 'Una tabla SQL.', 0),
(209, 53, 'El color del texto.', 0),
(210, 53, 'La memoria del celular.', 0),
(211, 53, 'El rol del usuario.', 0),
(212, 53, 'El recorrido de los pasos.', 1),
(213, 54, 'Una relación muchos a muchos.', 0),
(214, 54, 'Una contraseña.', 0),
(215, 54, 'Un proceso.', 1),
(216, 54, 'Solo el fin.', 0),
(217, 55, 'O(0)', 0),
(218, 55, 'O(n)', 1),
(219, 55, 'O(1) siempre', 0),
(220, 55, 'O(n²) siempre', 0),
(221, 56, 'O(n²)', 1),
(222, 56, 'O(1)', 0),
(223, 56, 'O(n) siempre', 0),
(224, 56, 'O(log n) siempre', 0),
(225, 57, 'El tiempo exacto en segundos.', 0),
(226, 57, 'El color del código.', 0),
(227, 57, 'La contraseña de la base.', 0),
(228, 57, 'Cómo crece el trabajo al aumentar la entrada.', 1),
(241, 59, 'HOla', 1),
(242, 59, 'holaa', 0),
(243, 59, 'holaaa', 0),
(244, 59, 'holaaa', 0);

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
-- Estructura de tabla para la tabla `evaluaciones`
--

CREATE TABLE `evaluaciones` (
  `id_intento` char(64) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `id_leccion` int(11) NOT NULL,
  `snapshot` mediumtext NOT NULL,
  `revision` mediumtext DEFAULT NULL,
  `expira` datetime NOT NULL,
  `id_resultado` int(11) DEFAULT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `evaluaciones`
--

INSERT INTO `evaluaciones` (`id_intento`, `id_usuario`, `id_leccion`, `snapshot`, `revision`, `expira`, `id_resultado`, `fecha_creacion`) VALUES
('06b8632cc6858e2f0e49daae9833cea24bb0c1f9cfc53a04105ea0e0ce998526', 1, 13, '[{\"id\":10,\"enunciado\":\"¿Qué instrucción consulta datos?\",\"alternativas\":[{\"id_alternativa\":39,\"texto\":\"SELECT\",\"es_correcta\":1},{\"id_alternativa\":38,\"texto\":\"UPDATE\",\"es_correcta\":0},{\"id_alternativa\":37,\"texto\":\"DELETE\",\"es_correcta\":0},{\"id_alternativa\":40,\"texto\":\"DROP\",\"es_correcta\":0}]},{\"id\":41,\"enunciado\":\"¿Qué cláusula filtra registros?\",\"alternativas\":[{\"id_alternativa\":161,\"texto\":\"ORDER BY solamente\",\"es_correcta\":0},{\"id_alternativa\":163,\"texto\":\"CREATE\",\"es_correcta\":0},{\"id_alternativa\":162,\"texto\":\"INSERT\",\"es_correcta\":0},{\"id_alternativa\":164,\"texto\":\"WHERE\",\"es_correcta\":1}]},{\"id\":42,\"enunciado\":\"¿Qué hace ORDER BY nombre ASC?\",\"alternativas\":[{\"id_alternativa\":166,\"texto\":\"Actualiza todas las filas.\",\"es_correcta\":0},{\"id_alternativa\":167,\"texto\":\"Ordena por nombre de forma ascendente.\",\"es_correcta\":1},{\"id_alternativa\":168,\"texto\":\"Borra los nombres.\",\"es_correcta\":0},{\"id_alternativa\":165,\"texto\":\"Crea una contraseña.\",\"es_correcta\":0}]}]', '[{\"enunciado\":\"¿Qué instrucción consulta datos?\",\"correcta\":true,\"respuesta_correcta\":\"SELECT\"},{\"enunciado\":\"¿Qué cláusula filtra registros?\",\"correcta\":false,\"respuesta_correcta\":\"WHERE\"},{\"enunciado\":\"¿Qué hace ORDER BY nombre ASC?\",\"correcta\":true,\"respuesta_correcta\":\"Ordena por nombre de forma ascendente.\"}]', '2026-10-11 23:39:50', 6, '2026-10-11 21:39:50'),
('5c570831bd5cb67d16f7d400c7ddf9dc67ed1eb52dc3d8d143c920f7c54c221d', 1, 5, '[{\"id\":6,\"enunciado\":\"¿Cuándo se ejecuta el bloque de un if?\",\"alternativas\":[{\"id_alternativa\":21,\"texto\":\"Solo cuando la condición es falsa.\",\"es_correcta\":0},{\"id_alternativa\":23,\"texto\":\"Cuando su condición es verdadera.\",\"es_correcta\":1},{\"id_alternativa\":22,\"texto\":\"Antes de evaluar la condición.\",\"es_correcta\":0},{\"id_alternativa\":24,\"texto\":\"Siempre.\",\"es_correcta\":0}]},{\"id\":21,\"enunciado\":\"¿Qué operador compara igualdad en Kotlin?\",\"alternativas\":[{\"id_alternativa\":84,\"texto\":\"==\",\"es_correcta\":1},{\"id_alternativa\":82,\"texto\":\"+=\",\"es_correcta\":0},{\"id_alternativa\":81,\"texto\":\"=\",\"es_correcta\":0},{\"id_alternativa\":83,\"texto\":\"++\",\"es_correcta\":0}]},{\"id\":22,\"enunciado\":\"Con edad = 17, ¿es verdadera edad >= 18?\",\"alternativas\":[{\"id_alternativa\":86,\"texto\":\"Solo en Android.\",\"es_correcta\":0},{\"id_alternativa\":85,\"texto\":\"Siempre\",\"es_correcta\":0},{\"id_alternativa\":88,\"texto\":\"Sí\",\"es_correcta\":0},{\"id_alternativa\":87,\"texto\":\"No\",\"es_correcta\":1}]}]', NULL, '2026-10-12 06:07:18', NULL, '2026-10-12 04:07:18'),
('6658aa354749b6f3a8e7059a00c2ae40a139a5e002d59328c5d9eaad8df930fb', 1, 16, '[{\"id\":12,\"enunciado\":\"¿Para qué se utiliza CSS?\",\"alternativas\":[{\"id_alternativa\":47,\"texto\":\"Para consultar MariaDB.\",\"es_correcta\":0},{\"id_alternativa\":45,\"texto\":\"Para definir la presentación visual.\",\"es_correcta\":1},{\"id_alternativa\":46,\"texto\":\"Para almacenar usuarios.\",\"es_correcta\":0},{\"id_alternativa\":48,\"texto\":\"Para autenticar por sí solo.\",\"es_correcta\":0}]},{\"id\":48,\"enunciado\":\"¿Qué propiedad cambia el color del texto?\",\"alternativas\":[{\"id_alternativa\":189,\"texto\":\"color\",\"es_correcta\":1},{\"id_alternativa\":191,\"texto\":\"href\",\"es_correcta\":0},{\"id_alternativa\":190,\"texto\":\"SELECT\",\"es_correcta\":0},{\"id_alternativa\":192,\"texto\":\"id_topico\",\"es_correcta\":0}]},{\"id\":49,\"enunciado\":\"¿Qué selecciona .tarjeta?\",\"alternativas\":[{\"id_alternativa\":196,\"texto\":\"Elementos con la clase tarjeta.\",\"es_correcta\":1},{\"id_alternativa\":194,\"texto\":\"Solo enlaces externos.\",\"es_correcta\":0},{\"id_alternativa\":195,\"texto\":\"Un archivo APK.\",\"es_correcta\":0},{\"id_alternativa\":193,\"texto\":\"Todas las tablas SQL.\",\"es_correcta\":0}]}]', '[{\"enunciado\":\"¿Para qué se utiliza CSS?\",\"correcta\":true,\"respuesta_correcta\":\"Para definir la presentación visual.\"},{\"enunciado\":\"¿Qué propiedad cambia el color del texto?\",\"correcta\":true,\"respuesta_correcta\":\"color\"},{\"enunciado\":\"¿Qué selecciona .tarjeta?\",\"correcta\":true,\"respuesta_correcta\":\"Elementos con la clase tarjeta.\"}]', '2026-10-18 03:56:56', 7, '2026-10-18 01:56:56'),
('6e85c23cac32d42bd6302dab0637c7e1dbf251ffac9257cd1cc451c041772ce6', 1, 4, '[{\"id\":5,\"enunciado\":\"¿Qué tipo representa un número entero en Kotlin?\",\"alternativas\":[{\"id_alternativa\":18,\"texto\":\"String\",\"es_correcta\":0},{\"id_alternativa\":19,\"texto\":\"TextView\",\"es_correcta\":0},{\"id_alternativa\":20,\"texto\":\"Int\",\"es_correcta\":1},{\"id_alternativa\":17,\"texto\":\"Boolean\",\"es_correcta\":0}]},{\"id\":19,\"enunciado\":\"¿Qué tipo usarías para verdadero o falso?\",\"alternativas\":[{\"id_alternativa\":76,\"texto\":\"Int\",\"es_correcta\":0},{\"id_alternativa\":73,\"texto\":\"StringBuilder\",\"es_correcta\":0},{\"id_alternativa\":74,\"texto\":\"Boolean\",\"es_correcta\":1},{\"id_alternativa\":75,\"texto\":\"Double\",\"es_correcta\":0}]},{\"id\":20,\"enunciado\":\"¿Qué devuelve \\\"abc\\\".toIntOrNull()?\",\"alternativas\":[{\"id_alternativa\":80,\"texto\":\"true\",\"es_correcta\":0},{\"id_alternativa\":78,\"texto\":\"0 siempre\",\"es_correcta\":0},{\"id_alternativa\":77,\"texto\":\"null\",\"es_correcta\":1},{\"id_alternativa\":79,\"texto\":\"123\",\"es_correcta\":0}]}]', '[{\"enunciado\":\"¿Qué tipo representa un número entero en Kotlin?\",\"correcta\":true,\"respuesta_correcta\":\"Int\"},{\"enunciado\":\"¿Qué tipo usarías para verdadero o falso?\",\"correcta\":true,\"respuesta_correcta\":\"Boolean\"},{\"enunciado\":\"¿Qué devuelve \\\"abc\\\".toIntOrNull()?\",\"correcta\":true,\"respuesta_correcta\":\"null\"}]', '2026-10-10 07:42:36', 5, '2026-10-10 05:42:36'),
('a9059f42abe736d94c44429e7a1aa617d6f9d3d674ad8aa358a72d962e5c2230', 1, 3, '[{\"id\":4,\"enunciado\":\"¿Qué permite una variable?\",\"alternativas\":[{\"id_alternativa\":16,\"texto\":\"Eliminar todas las instrucciones.\",\"es_correcta\":0},{\"id_alternativa\":14,\"texto\":\"Crear hardware.\",\"es_correcta\":0},{\"id_alternativa\":15,\"texto\":\"Cambiar la velocidad de Internet.\",\"es_correcta\":0},{\"id_alternativa\":13,\"texto\":\"Almacenar y utilizar un valor identificado por un nombre.\",\"es_correcta\":1}]},{\"id\":17,\"enunciado\":\"¿Qué declaración permite reasignar el valor en Kotlin?\",\"alternativas\":[{\"id_alternativa\":67,\"texto\":\"package\",\"es_correcta\":0},{\"id_alternativa\":66,\"texto\":\"println\",\"es_correcta\":0},{\"id_alternativa\":68,\"texto\":\"var\",\"es_correcta\":1},{\"id_alternativa\":65,\"texto\":\"val\",\"es_correcta\":0}]},{\"id\":18,\"enunciado\":\"Si puntos vale 5 y ejecutas puntos = puntos + 3, ¿cuánto vale?\",\"alternativas\":[{\"id_alternativa\":70,\"texto\":\"15\",\"es_correcta\":0},{\"id_alternativa\":71,\"texto\":\"8\",\"es_correcta\":1},{\"id_alternativa\":69,\"texto\":\"5\",\"es_correcta\":0},{\"id_alternativa\":72,\"texto\":\"3\",\"es_correcta\":0}]}]', '[{\"enunciado\":\"¿Qué permite una variable?\",\"correcta\":true,\"respuesta_correcta\":\"Almacenar y utilizar un valor identificado por un nombre.\"},{\"enunciado\":\"¿Qué declaración permite reasignar el valor en Kotlin?\",\"correcta\":true,\"respuesta_correcta\":\"var\"},{\"enunciado\":\"Si puntos vale 5 y ejecutas puntos = puntos + 3, ¿cuánto vale?\",\"correcta\":true,\"respuesta_correcta\":\"8\"}]', '2026-10-10 00:26:59', 3, '2026-10-09 22:26:59'),
('da18d4ae61858332054d9ecafb02c9c92ddfaa95dd8a0f4223226b2fa5985d36', 1, 2, '[{\"id\":3,\"enunciado\":\"¿Para qué sirve un lenguaje de programación?\",\"alternativas\":[{\"id_alternativa\":10,\"texto\":\"Para expresar instrucciones con reglas definidas.\",\"es_correcta\":1},{\"id_alternativa\":11,\"texto\":\"Para aumentar físicamente la memoria.\",\"es_correcta\":0},{\"id_alternativa\":9,\"texto\":\"Para almacenar exclusivamente fotos.\",\"es_correcta\":0},{\"id_alternativa\":12,\"texto\":\"Para reemplazar el teclado.\",\"es_correcta\":0}]},{\"id\":15,\"enunciado\":\"¿Cuál de estos lenguajes se usa en tu proyecto Android?\",\"alternativas\":[{\"id_alternativa\":57,\"texto\":\"Una dirección IP\",\"es_correcta\":0},{\"id_alternativa\":60,\"texto\":\"CSS solamente\",\"es_correcta\":0},{\"id_alternativa\":58,\"texto\":\"Kotlin\",\"es_correcta\":1},{\"id_alternativa\":59,\"texto\":\"HTML solamente\",\"es_correcta\":0}]},{\"id\":16,\"enunciado\":\"Si nombre = \\\"Ana\\\", ¿qué muestra println(\\\"Hola, $nombre\\\")?\",\"alternativas\":[{\"id_alternativa\":63,\"texto\":\"Ana, Hola\",\"es_correcta\":0},{\"id_alternativa\":64,\"texto\":\"No muestra nada.\",\"es_correcta\":0},{\"id_alternativa\":62,\"texto\":\"Hola, nombre\",\"es_correcta\":0},{\"id_alternativa\":61,\"texto\":\"Hola, Ana\",\"es_correcta\":1}]}]', '[{\"enunciado\":\"¿Para qué sirve un lenguaje de programación?\",\"correcta\":true,\"respuesta_correcta\":\"Para expresar instrucciones con reglas definidas.\"},{\"enunciado\":\"¿Cuál de estos lenguajes se usa en tu proyecto Android?\",\"correcta\":true,\"respuesta_correcta\":\"Kotlin\"},{\"enunciado\":\"Si nombre = \\\"Ana\\\", ¿qué muestra println(\\\"Hola, $nombre\\\")?\",\"correcta\":true,\"respuesta_correcta\":\"Hola, Ana\"}]', '2026-10-09 19:32:18', 2, '2026-10-09 17:32:18'),
('e52fe51d4888aa62e5001daa85a2dd9ec2bfd8d7d7cae61e7281d8cb9b347a17', 4, 1, '[{\"id\":1,\"enunciado\":\"¿Qué es un programa?\",\"alternativas\":[{\"id_alternativa\":1,\"texto\":\"Un componente físico.\",\"es_correcta\":0},{\"id_alternativa\":2,\"texto\":\"Un cable de red.\",\"es_correcta\":0},{\"id_alternativa\":4,\"texto\":\"Un conjunto de instrucciones que puede ejecutar una computadora.\",\"es_correcta\":1},{\"id_alternativa\":3,\"texto\":\"Solo una imagen.\",\"es_correcta\":0}]},{\"id\":2,\"enunciado\":\"Si precio = 8 y cantidad = 3, ¿qué produce precio * cantidad?\",\"alternativas\":[{\"id_alternativa\":6,\"texto\":\"83\",\"es_correcta\":0},{\"id_alternativa\":5,\"texto\":\"5\",\"es_correcta\":0},{\"id_alternativa\":7,\"texto\":\"24\",\"es_correcta\":1},{\"id_alternativa\":8,\"texto\":\"11\",\"es_correcta\":0}]},{\"id\":14,\"enunciado\":\"¿Cuál es el procesamiento al calcular el precio total?\",\"alternativas\":[{\"id_alternativa\":55,\"texto\":\"Multiplicar precio por cantidad.\",\"es_correcta\":1},{\"id_alternativa\":54,\"texto\":\"Cambiar el monitor.\",\"es_correcta\":0},{\"id_alternativa\":56,\"texto\":\"Leer únicamente el precio.\",\"es_correcta\":0},{\"id_alternativa\":53,\"texto\":\"Mostrar únicamente el resultado.\",\"es_correcta\":0}]}]', '[{\"enunciado\":\"¿Qué es un programa?\",\"correcta\":true,\"respuesta_correcta\":\"Un conjunto de instrucciones que puede ejecutar una computadora.\"},{\"enunciado\":\"Si precio = 8 y cantidad = 3, ¿qué produce precio * cantidad?\",\"correcta\":true,\"respuesta_correcta\":\"24\"},{\"enunciado\":\"¿Cuál es el procesamiento al calcular el precio total?\",\"correcta\":true,\"respuesta_correcta\":\"Multiplicar precio por cantidad.\"}]', '2026-10-10 00:37:00', 4, '2026-10-09 22:37:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `intentos_login`
--

CREATE TABLE `intentos_login` (
  `id` bigint(20) NOT NULL,
  `ip` varchar(45) NOT NULL,
  `usuario` varchar(50) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `intentos_login`
--

INSERT INTO `intentos_login` (`id`, `ip`, `usuario`, `fecha`) VALUES
(1, '127.0.0.1', 'alumno', '2026-10-09 17:24:13'),
(2, '127.0.0.1', 'alumno1', '2026-10-09 17:28:15'),
(3, '127.0.0.1', 'alumno1', '2026-10-09 17:28:37'),
(4, '::1', 'admin', '2026-10-19 05:43:03'),
(5, '::1', 'admin', '2026-10-19 05:43:08'),
(6, '::1', 'admin', '2026-10-19 05:43:08'),
(7, '::1', 'admin', '2026-10-19 05:43:09');

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
(1, 1, '¿Qué es programar?', 'Programar consiste en expresar una solución mediante instrucciones precisas que una computadora puede ejecutar. Un programa recibe datos, los procesa y produce un resultado.\n\nEJEMPLO EN KOTLIN\nval precio = 15.0\nval cantidad = 2\nval total = precio * cantidad\nprintln(total) // 30.0\n\nLa entrada es precio y cantidad. El procesamiento es la multiplicación. La salida es 30.0. El orden de las instrucciones importa: una variable debe estar disponible antes de usarla.\n\nPRÁCTICA\nUn producto cuesta 8 soles y compras 3. ¿Cuál será el total?\n\nEXPLICACIÓN\n8 * 3 = 24 soles. El programa automatiza una regla definida; no adivina la operación.', 1, 1),
(2, 1, 'Lenguajes de programación', 'Un lenguaje de programación define una sintaxis y reglas para expresar instrucciones. Kotlin se usa en aplicaciones Android; SQL permite trabajar con datos; HTML describe la estructura de documentos web y no es un lenguaje de programación de propósito general.\n\nEJEMPLO EN KOTLIN\nval nombre = \"Eliezer\"\nprintln(\"Hola, $nombre\")\n\nval declara una referencia que no puede reasignarse. println muestra texto. $nombre inserta el valor de la variable dentro de la cadena.\n\nPRÁCTICA\n¿Qué mostraría el ejemplo si nombre fuera \"Ana\"?\n\nEXPLICACIÓN\nMostraría Hola, Ana. Aprender un lenguaje requiere comprender sus reglas y practicar la resolución de problemas, además de recordar palabras clave.', 2, 1),
(3, 2, 'Variables', 'Una variable relaciona un nombre con un valor. En Kotlin, val impide reasignar la referencia y var permite cambiarla.\n\nEJEMPLO\nval nombre = \"Ana\"\nvar edad = 20\nedad = edad + 1\nprintln(edad) // 21\n\nIntentar nombre = \"Luis\" produce un error porque nombre se declaró con val. Que una referencia sea val no significa que un objeto mutable guardado en ella se vuelva inmutable.\n\nPRÁCTICA\nvar puntos = 5\npuntos = puntos + 3\n¿Qué valor queda guardado?\n\nEXPLICACIÓN\npuntos pasa a valer 8. La expresión de la derecha se calcula antes de realizar la asignación.', 1, 1),
(4, 2, 'Tipos de datos', 'El tipo de dato describe qué valores puede representar una variable y qué operaciones admite. En Kotlin, Int representa enteros, Double números de punto flotante, Boolean verdadero o falso y String texto.\n\nEJEMPLO\nval edad: Int = 23\nval altura: Double = 1.70\nval activo: Boolean = true\nval usuario: String = \"alumno\"\n\nUna cadena que contiene dígitos sigue siendo texto: \"23\" no es un Int. Para convertir una entrada utiliza toIntOrNull(), que devuelve null si la conversión falla, y valida el resultado antes de usarlo.\n\nPRÁCTICA\n¿Qué tipo elegirías para una respuesta de sí o no?\n\nEXPLICACIÓN\nBoolean, porque solo tiene dos valores: true y false.', 2, 1),
(5, 3, 'Sentencia IF', 'Una condición produce true o false. if ejecuta un bloque cuando la condición es verdadera.\n\nEJEMPLO\nval edad = 23\nif (edad >= 18) {\n    println(\"Mayor de edad\")\n}\n\nLos operadores >, <, >=, <=, == y != permiten comparar. En Kotlin, = asigna y == compara igualdad. No los confundas.\n\nPRÁCTICA\n¿Qué ocurre si edad vale 17 en el ejemplo?\n\nEXPLICACIÓN\nEl bloque no se ejecuta porque 17 >= 18 es falso. Un if sin else puede no ejecutar ninguna acción cuando la condición es falsa.', 1, 1),
(6, 3, 'IF - ELSE', 'if-else permite elegir entre dos caminos. Si la condición es verdadera se ejecuta if; de lo contrario se ejecuta else.\n\nEJEMPLO\nval nota = 65\nif (nota >= 70) {\n    println(\"Aprobado\")\n} else {\n    println(\"Sigue practicando\")\n}\n\nPuedes combinar condiciones con && (y), || (o) y ! (negación). Primero define claramente la regla de negocio.\n\nPRÁCTICA\n¿Qué mensaje aparece con nota = 70?\n\nEXPLICACIÓN\nAprobado, porque >= incluye el valor límite 70. En esta app, una lección se aprueba al obtener al menos 70%.', 2, 1),
(7, 4, 'Ciclo FOR', 'Un bucle repite instrucciones. for resulta útil para recorrer rangos y colecciones.\n\nEJEMPLO\nfor (numero in 1..3) {\n    println(numero)\n}\n\nLa salida es 1, 2, 3. El rango 1..3 incluye ambos extremos. También puedes recorrer una lista con for (curso in cursos).\n\nPRÁCTICA\nvar suma = 0\nfor (n in 1..3) { suma += n }\n¿Cuánto vale suma al terminar?\n\nEXPLICACIÓN\nVale 6: primero suma 1, luego 2 y finalmente 3. Identifica siempre el número de iteraciones y qué cambia en cada una.', 1, 1),
(8, 4, 'Ciclo WHILE', 'while repite un bloque mientras su condición sea verdadera. Evalúa la condición antes de cada iteración, por lo que puede ejecutarse cero veces.\n\nEJEMPLO\nvar contador = 0\nwhile (contador < 3) {\n    println(contador)\n    contador++\n}\n\nLa salida es 0, 1, 2. contador++ aumenta el valor en uno. Si omites ese cambio, la condición podría seguir siendo verdadera indefinidamente.\n\nPRÁCTICA\n¿Qué ocurre si contador comienza en 3?\n\nEXPLICACIÓN\nNo se ejecuta el bloque: 3 < 3 es falso. Verifica que el bucle tenga una forma de terminar.', 2, 1),
(9, 5, 'Concepto de función', 'Una función agrupa instrucciones para realizar una tarea. Permite reutilizar código, separar responsabilidades y dar un nombre a una operación.\n\nEJEMPLO\nfun saludar() {\n    println(\"Hola\")\n}\nsaludar()\n\nDeclarar saludar no ejecuta su cuerpo; llamarla con saludar() sí lo hace. Una función puede recibir parámetros y devolver un valor.\n\nPRÁCTICA\nSi llamas saludar() dos veces, ¿cuántas veces aparece Hola?\n\nEXPLICACIÓN\nDos veces. Conviene escribir funciones que hagan una tarea clara en lugar de concentrar todo el programa en un bloque enorme.', 1, 1),
(10, 5, 'Parámetros y retorno', 'Los parámetros son entradas de una función. El valor de retorno es su resultado.\n\nEJEMPLO\nfun sumar(a: Int, b: Int): Int {\n    return a + b\n}\nval resultado = sumar(4, 6)\nprintln(resultado) // 10\n\nEn la declaración, a y b son parámetros. En la llamada, 4 y 6 son argumentos. : Int después de los paréntesis indica el tipo devuelto.\n\nPRÁCTICA\n¿Qué devuelve sumar(3, 5)?\n\nEXPLICACIÓN\nDevuelve 8. return entrega el valor al lugar que llamó la función; no es lo mismo que mostrarlo con println.', 2, 1),
(11, 6, 'Conceptos básicos de bases de datos', 'Una base de datos organiza información para consultarla y actualizarla. En una base relacional, las tablas contienen filas (registros) y columnas (atributos).\n\nEJEMPLO\nLa tabla cursos puede tener id_curso, nombre y descripcion. Una fila representa un curso; la columna nombre guarda su nombre.\n\nUna clave primaria identifica de forma única una fila. Una clave foránea relaciona un registro con otro, por ejemplo lecciones.id_topico con topicos.id_topico.\n\nPRÁCTICA\n¿Dónde guardarías el nombre de cada alumno?\n\nEXPLICACIÓN\nEn una columna de la tabla usuarios. Evita duplicar información innecesariamente y utiliza las relaciones para conectar datos.', 1, 1),
(12, 7, 'Entidades y relaciones', 'Una entidad representa algo sobre lo que guardamos datos, como Curso, Tema o Lección. Una relación expresa cómo se conectan.\n\nEJEMPLO\nUn curso tiene muchos temas. Cada tema pertenece a un curso. Esta es una relación uno a muchos.\n\nSe implementa colocando id_curso en topicos como clave foránea. No necesitas repetir todos los datos del curso en cada tema.\n\nPRÁCTICA\nUn alumno completa muchas lecciones y una lección puede ser completada por muchos alumnos. ¿Cómo lo modelas?\n\nEXPLICACIÓN\nCon una tabla intermedia como progreso, que incluye id_usuario e id_leccion. La restricción única de esa pareja evita duplicar el progreso de una misma lección para un alumno.', 1, 1),
(13, 8, 'SELECT', 'SELECT consulta información sin modificarla. FROM indica la tabla, WHERE filtra registros y ORDER BY ordena los resultados.\n\nEJEMPLO SQL\nSELECT nombre\nFROM cursos\nWHERE estado = 1\nORDER BY nombre ASC;\n\nObtiene los nombres de cursos activos y los ordena alfabéticamente. SELECT * obtiene todas las columnas, pero conviene pedir las necesarias.\n\nPRÁCTICA\n¿Cómo consultarías las lecciones del tema 2?\n\nEXPLICACIÓN\nSELECT titulo FROM lecciones WHERE id_topico = 2;\nEn PHP, los valores recibidos del usuario deben pasarse mediante consultas preparadas; no concatenarse directamente al SQL.', 1, 1),
(14, 9, 'Conceptos básicos de desarrollo web', 'Una aplicación web combina presentación, comportamiento y procesamiento. El frontend es la parte con la que interactúa el usuario. El backend procesa solicitudes y accede a los datos.\n\nEN TU PROYECTO\nAndroid es el cliente. Retrofit envía solicitudes HTTP. Apache ejecuta PHP. PHP consulta MariaDB y devuelve JSON. La aplicación interpreta esa respuesta y actualiza la pantalla.\n\nPRÁCTICA\n¿Debe Android conectarse directamente a MariaDB usando la contraseña de la base?\n\nEXPLICACIÓN\nNo. La API debe validar la sesión, los permisos y los datos. Las credenciales de MariaDB permanecen en el servidor. JSON es un formato de intercambio de datos; no una base de datos.', 1, 1),
(15, 10, 'Estructura HTML', 'HTML describe la estructura de una página web. Los elementos se representan mediante etiquetas. Un documento suele tener html, head y body.\n\nEJEMPLO\n<!doctype html>\n<html lang=\"es\">\n<head><title>Mi curso</title></head>\n<body>\n  <h1>Programación</h1>\n  <p>Aprende los fundamentos.</p>\n</body>\n</html>\n\nh1 representa un encabezado principal; p, un párrafo. head contiene metadatos, mientras body contiene el contenido del documento.\n\nPRÁCTICA\n¿Qué etiqueta usarías para un enlace?\n\nEXPLICACIÓN\n<a href=\"https://ejemplo.com\">Visitar</a>. El atributo href indica el destino.', 1, 1),
(16, 11, 'Estilos CSS', 'CSS define la presentación de un documento HTML. Un selector elige elementos y las declaraciones establecen propiedades.\n\nEJEMPLO\nh1 {\n  color: purple;\n  font-size: 28px;\n}\n\nEl selector h1 elige los encabezados de ese tipo. color cambia el color del texto y font-size su tamaño. Una clase como .tarjeta permite aplicar estilos a elementos con class=\"tarjeta\".\n\nPRÁCTICA\n¿Cómo cambiarías el fondo de una tarjeta?\n\nEXPLICACIÓN\n.tarjeta { background-color: #f7f7f7; }\nElige colores con contraste suficiente para que el texto se lea. CSS cambia la presentación, no sustituye las consultas SQL ni la validación del servidor.', 1, 1),
(17, 12, '¿Qué es un algoritmo?', 'Un algoritmo es una secuencia precisa y finita de pasos para resolver un problema. Puede describirse con palabras, pseudocódigo, diagramas o código.\n\nEJEMPLO: PROMEDIO\n1. Leer tres notas.\n2. Sumar las notas.\n3. Dividir la suma entre tres.\n4. Mostrar el promedio.\n\nPRÁCTICA\nLas notas son 12, 15 y 18. ¿Cuál es el promedio?\n\nEXPLICACIÓN\n(12 + 15 + 18) / 3 = 15. Define las entradas, el procesamiento y la salida. También considera casos límite, como listas vacías o datos inválidos, antes de programar.', 1, 1),
(18, 13, 'Diagramas de flujo', 'Un diagrama de flujo representa visualmente los pasos de un algoritmo. Las flechas indican el recorrido.\n\nSÍMBOLOS HABITUALES\nÓvalo: inicio o fin.\nRectángulo: proceso, como una operación.\nRombo: decisión, con salidas como sí y no.\nParalelogramo: entrada o salida de datos.\n\nEJEMPLO\nInicio → Leer nota → ¿nota >= 70?\nSi sí: mostrar Aprobado.\nSi no: mostrar Sigue practicando.\nAmbos caminos terminan en Fin.\n\nPRÁCTICA\n¿Qué símbolo usarías para preguntar si una contraseña es válida?\n\nEXPLICACIÓN\nUn rombo, porque la condición produce alternativas. Un buen diagrama permite seguir cada camino sin saltos ambiguos.', 1, 1),
(19, 14, 'Eficiencia algorítmica', 'La complejidad describe cómo crece el trabajo de un algoritmo al aumentar el tamaño de la entrada. No es una medición exacta en segundos.\n\nEJEMPLOS\nO(1): acceder a un elemento por índice en un arreglo.\nO(n): recorrer una lista de n elementos una vez.\nO(n²): comparar cada elemento con todos los demás mediante dos recorridos completos anidados.\n\nPRÁCTICA\nSi buscas un nombre revisando una lista uno por uno, ¿qué pasa al duplicar la cantidad de nombres?\n\nEXPLICACIÓN\nEn el peor caso, el número de revisiones crece aproximadamente al doble: O(n). También considera memoria, acceso a red y claridad. Una notación menor no garantiza que un programa sea más rápido para cualquier entrada.', 1, 1),
(20, 8, 'Parece que si paso la prueba', '', 1, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mensajes`
--

CREATE TABLE `mensajes` (
  `id_mensaje` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `pregunta` text NOT NULL,
  `leido` tinyint(1) NOT NULL DEFAULT 0,
  `respuesta` text DEFAULT NULL,
  `id_admin` int(11) DEFAULT NULL,
  `fecha_envio` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_respuesta` datetime DEFAULT NULL,
  `respuesta_vista` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `mensajes`
--

INSERT INTO `mensajes` (`id_mensaje`, `id_usuario`, `pregunta`, `leido`, `respuesta`, `id_admin`, `fecha_envio`, `fecha_respuesta`, `respuesta_vista`) VALUES
(1, 1, 'Hola admin necesito ayuda', 1, 'Hola si dime como puedo ayudarlo?', 2, '2026-10-19 06:16:42', '2026-10-19 06:17:18', 1);

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
(2, 1, 'Si precio = 8 y cantidad = 3, ¿qué produce precio * cantidad?', 2, 1),
(3, 2, '¿Para qué sirve un lenguaje de programación?', 1, 1),
(4, 3, '¿Qué permite una variable?', 1, 1),
(5, 4, '¿Qué tipo representa un número entero en Kotlin?', 1, 1),
(6, 5, '¿Cuándo se ejecuta el bloque de un if?', 1, 1),
(7, 7, '¿Qué hace un ciclo for?', 1, 1),
(8, 9, '¿Qué es una función?', 1, 1),
(9, 11, '¿Qué es una base de datos?', 1, 1),
(10, 13, '¿Qué instrucción consulta datos?', 1, 1),
(11, 15, '¿Qué lenguaje estructura una página web?', 1, 1),
(12, 16, '¿Para qué se utiliza CSS?', 1, 1),
(13, 17, '¿Qué es un algoritmo?', 1, 1),
(14, 1, '¿Cuál es el procesamiento al calcular el precio total?', 3, 1),
(15, 2, '¿Cuál de estos lenguajes se usa en tu proyecto Android?', 2, 1),
(16, 2, 'Si nombre = \"Ana\", ¿qué muestra println(\"Hola, $nombre\")?', 3, 1),
(17, 3, '¿Qué declaración permite reasignar el valor en Kotlin?', 2, 1),
(18, 3, 'Si puntos vale 5 y ejecutas puntos = puntos + 3, ¿cuánto vale?', 3, 1),
(19, 4, '¿Qué tipo usarías para verdadero o falso?', 2, 1),
(20, 4, '¿Qué devuelve \"abc\".toIntOrNull()?', 3, 1),
(21, 5, '¿Qué operador compara igualdad en Kotlin?', 2, 1),
(22, 5, 'Con edad = 17, ¿es verdadera edad >= 18?', 3, 1),
(23, 6, '¿Qué bloque se ejecuta si la condición de if es falsa?', 1, 1),
(24, 6, 'Si se aprueba con nota >= 70, ¿qué ocurre con nota = 70?', 2, 1),
(25, 6, '¿Qué significa && entre dos condiciones?', 3, 1),
(26, 7, '¿Cuántas veces se ejecuta for (n in 1..3)?', 2, 1),
(27, 7, '¿Cuánto suma 1 + 2 + 3?', 3, 1),
(28, 8, '¿Cuándo continúa un while?', 1, 1),
(29, 8, 'Con contador = 3, ¿cuántas veces se ejecuta while (contador < 3)?', 2, 1),
(30, 8, '¿Qué puede pasar si la condición nunca deja de ser verdadera?', 3, 1),
(31, 9, '¿Qué palabra declara una función en Kotlin?', 2, 1),
(32, 9, '¿Qué ejecuta el cuerpo de saludar?', 3, 1),
(33, 10, '¿Para qué sirven los parámetros?', 1, 1),
(34, 10, '¿Qué devuelve sumar(3, 5) si la función retorna a + b?', 2, 1),
(35, 10, '¿Qué hace return?', 3, 1),
(36, 11, '¿Qué representa normalmente una fila?', 2, 1),
(37, 11, '¿Para qué sirve una clave primaria?', 3, 1),
(38, 12, 'Si un curso tiene muchos temas, ¿qué relación existe?', 1, 1),
(39, 12, '¿Dónde se coloca id_curso para vincular un tema con su curso?', 2, 1),
(40, 12, '¿Qué tabla relaciona alumnos y lecciones completadas en tu proyecto?', 3, 1),
(41, 13, '¿Qué cláusula filtra registros?', 2, 1),
(42, 13, '¿Qué hace ORDER BY nombre ASC?', 3, 1),
(43, 14, '¿Qué hace el backend en este proyecto?', 1, 1),
(44, 14, '¿Qué formato usa esta API para responder?', 2, 1),
(45, 14, '¿Dónde deben permanecer las credenciales de MariaDB?', 3, 1),
(46, 15, '¿Qué etiqueta representa un párrafo?', 2, 1),
(47, 15, '¿Qué atributo indica el destino de un enlace?', 3, 1),
(48, 16, '¿Qué propiedad cambia el color del texto?', 2, 1),
(49, 16, '¿Qué selecciona .tarjeta?', 3, 1),
(50, 17, '¿Cuál es el promedio de 12, 15 y 18?', 2, 1),
(51, 17, '¿Qué conviene definir antes de escribir el código?', 3, 1),
(52, 18, '¿Qué símbolo representa habitualmente una decisión?', 1, 1),
(53, 18, '¿Qué indican las flechas?', 2, 1),
(54, 18, '¿Qué representa habitualmente un rectángulo?', 3, 1),
(55, 19, 'Recorrer n elementos una vez tiene normalmente complejidad…', 1, 1),
(56, 19, 'Dos recorridos completos anidados sobre n elementos tienen normalmente…', 2, 1),
(57, 19, '¿Qué describe la notación de complejidad?', 3, 1),
(59, 1, 'Como se dice? Como estas?', 1, 0);

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
(1, 1, 1, 1, 75.00, '2026-10-05 04:23:46'),
(3, 1, 2, 1, 100.00, '2026-10-09 17:32:41'),
(4, 1, 3, 1, 100.00, '2026-10-09 22:27:19'),
(5, 4, 1, 1, 100.00, '2026-10-09 22:37:31'),
(6, 1, 4, 1, 100.00, '2026-10-10 05:42:52'),
(7, 1, 13, 1, 66.67, '2026-10-11 21:40:02'),
(8, 1, 16, 1, 100.00, '2026-10-18 01:57:19');

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
(1, 1, 1, 3.00, 4, 75.00, '2026-10-05 04:22:31'),
(2, 1, 2, 3.00, 3, 100.00, '2026-10-09 17:32:41'),
(3, 1, 3, 3.00, 3, 100.00, '2026-10-09 22:27:19'),
(4, 4, 1, 3.00, 3, 100.00, '2026-10-09 22:37:31'),
(5, 1, 4, 3.00, 3, 100.00, '2026-10-10 05:42:52'),
(6, 1, 13, 2.00, 3, 66.67, '2026-10-11 21:40:02'),
(7, 1, 16, 3.00, 3, 100.00, '2026-10-18 01:57:19');

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
-- Estructura de tabla para la tabla `sesiones`
--

CREATE TABLE `sesiones` (
  `token_hash` char(64) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `expira` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `sesiones`
--

INSERT INTO `sesiones` (`token_hash`, `id_usuario`, `expira`) VALUES
('1822fbe3d344516688bcfc005541ba13d2161672828cc2845edb67fc6a357613', 1, '2026-10-26 06:16:16'),
('2bb0b80b23fee5d67bab30c02c2dae93bee28d523cb5f695bd629135e9e21ffd', 2, '2026-10-26 06:04:00'),
('3bf04b3f58bd20531a01aef1c05ea9241b27cb8d11678714f62e5de06edda1bc', 2, '2026-10-26 06:08:50');

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
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp(),
  `firebase_uid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `id_rol`, `nombre`, `apellido`, `usuario`, `password`, `estado`, `fecha_registro`, `firebase_uid`) VALUES
(1, 2, 'Aaron', 'Pacheco', 'alumno1', '$2y$10$9hSy.0GGiMsUCZMVsfSvsu7XxTbzXpKBltFaD0ePMUjSy.QCx4ojK', 1, '2026-10-05 04:17:40', 'gDTZlTpPICV5jZyGt6ARo1ERFHj1'),
(2, 1, 'Administrador', 'Demo', 'admin', '$2y$10$Hg3fW0GUlG4vPXQN4kiPW.bD9i7bEr0M6wURzlvl03gnMeVBJHyXO', 1, '2026-10-09 17:31:37', 'eVRXoWiHy7eER2BsQMC4YOr3D9P2'),
(3, 2, 'Alumno', 'Demo', 'alumno', '$2y$10$XocnjWWU2SufgjqbqtF2HurAXcZXpCKR3AhSWbNXmM4lx4JWSPvb2', 1, '2026-10-09 17:31:37', NULL),
(4, 2, 'Eliezer', 'Rojas', 'alumn2', '$2y$10$7F1rOxdcVjSo0Y5nuZMyqubM4MePLTdhcll4Mtzg7oWtSRZ9chn.q', 1, '2026-10-09 22:35:03', NULL);

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
-- Indices de la tabla `evaluaciones`
--
ALTER TABLE `evaluaciones`
  ADD PRIMARY KEY (`id_intento`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `id_leccion` (`id_leccion`),
  ADD KEY `id_resultado` (`id_resultado`);

--
-- Indices de la tabla `intentos_login`
--
ALTER TABLE `intentos_login`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fecha` (`fecha`),
  ADD KEY `ip` (`ip`,`fecha`),
  ADD KEY `usuario` (`usuario`,`fecha`);

--
-- Indices de la tabla `lecciones`
--
ALTER TABLE `lecciones`
  ADD PRIMARY KEY (`id_leccion`),
  ADD KEY `fk_leccion_topico` (`id_topico`);

--
-- Indices de la tabla `mensajes`
--
ALTER TABLE `mensajes`
  ADD PRIMARY KEY (`id_mensaje`),
  ADD KEY `idx_mensaje_usuario` (`id_usuario`),
  ADD KEY `idx_mensaje_admin` (`id_admin`),
  ADD KEY `idx_mensaje_estado` (`leido`,`fecha_envio`);

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
-- Indices de la tabla `sesiones`
--
ALTER TABLE `sesiones`
  ADD PRIMARY KEY (`token_hash`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `expira` (`expira`);

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
  ADD UNIQUE KEY `firebase_uid` (`firebase_uid`),
  ADD KEY `fk_usuario_rol` (`id_rol`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `alternativas`
--
ALTER TABLE `alternativas`
  MODIFY `id_alternativa` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=245;

--
-- AUTO_INCREMENT de la tabla `cursos`
--
ALTER TABLE `cursos`
  MODIFY `id_curso` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `intentos_login`
--
ALTER TABLE `intentos_login`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `lecciones`
--
ALTER TABLE `lecciones`
  MODIFY `id_leccion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT de la tabla `mensajes`
--
ALTER TABLE `mensajes`
  MODIFY `id_mensaje` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `preguntas`
--
ALTER TABLE `preguntas`
  MODIFY `id_pregunta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=60;

--
-- AUTO_INCREMENT de la tabla `progreso`
--
ALTER TABLE `progreso`
  MODIFY `id_progreso` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `resultados`
--
ALTER TABLE `resultados`
  MODIFY `id_resultado` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `id_rol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `topicos`
--
ALTER TABLE `topicos`
  MODIFY `id_topico` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `alternativas`
--
ALTER TABLE `alternativas`
  ADD CONSTRAINT `fk_alternativa_pregunta` FOREIGN KEY (`id_pregunta`) REFERENCES `preguntas` (`id_pregunta`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `evaluaciones`
--
ALTER TABLE `evaluaciones`
  ADD CONSTRAINT `evaluaciones_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `evaluaciones_ibfk_2` FOREIGN KEY (`id_leccion`) REFERENCES `lecciones` (`id_leccion`) ON DELETE CASCADE,
  ADD CONSTRAINT `evaluaciones_ibfk_3` FOREIGN KEY (`id_resultado`) REFERENCES `resultados` (`id_resultado`) ON DELETE SET NULL;

--
-- Filtros para la tabla `lecciones`
--
ALTER TABLE `lecciones`
  ADD CONSTRAINT `fk_leccion_topico` FOREIGN KEY (`id_topico`) REFERENCES `topicos` (`id_topico`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `mensajes`
--
ALTER TABLE `mensajes`
  ADD CONSTRAINT `fk_mensaje_admin` FOREIGN KEY (`id_admin`) REFERENCES `usuarios` (`id_usuario`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_mensaje_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE ON UPDATE CASCADE;

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
-- Filtros para la tabla `sesiones`
--
ALTER TABLE `sesiones`
  ADD CONSTRAINT `sesiones_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE;

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
