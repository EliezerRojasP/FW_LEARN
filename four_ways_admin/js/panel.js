async function cargarPanel() {
    let usuario = await verificarSesion();
    if (!usuario) return;
    document.getElementById('saludo').textContent = usuario.nombre;

    try {
        let resumen = await consultarApi('admin.php?recurso=estadisticas');
        let preguntas = await consultarApi('admin.php?recurso=preguntas');
        let lecciones = await consultarApi('admin.php?recurso=lecciones');
        let activas = preguntas.filter(function (pregunta) { return Number(pregunta.estado) === 1; });

        document.getElementById('total_alumnos').textContent = resumen.alumnos;
        document.getElementById('total_cursos').textContent = resumen.cursos;
        document.getElementById('total_lecciones').textContent = resumen.lecciones;
        document.getElementById('total_preguntas').textContent = activas.length;

        let tabla = document.getElementById('ultimas_lecciones');
        tabla.innerHTML = '';
        let ultimas = lecciones.slice(-5).reverse();
        ultimas.forEach(function (leccion) {
            let fila = document.createElement('tr');
            agregarCelda(fila, leccion.titulo);
            agregarEstado(fila, leccion.estado);
            let celda = document.createElement('td');
            let enlace = document.createElement('a');
            enlace.className = 'boton-pequeno';
            enlace.textContent = 'Editar';
            enlace.href = 'lecciones.html?id=' + leccion.id_leccion;
            celda.appendChild(enlace);
            fila.appendChild(celda);
            tabla.appendChild(fila);
        });
        if (ultimas.length === 0) {
            let fila = document.createElement('tr');
            let celda = agregarCelda(fila, 'Aún no hay lecciones.');
            celda.colSpan = 3;
            tabla.appendChild(fila);
        }
    } catch (error) {
        mostrarMensaje(error.message, true);
        document.getElementById('reintentar').hidden = false;
    }
}

cargarPanel();
