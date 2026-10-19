async function iniciarPagina() {
    let usuario = await verificarSesion();
    if (usuario) await cargarEstadisticas();
}

async function cargarEstadisticas() {
    document.getElementById('actualizar').disabled = true;
    try {
        let datos = await consultarApi('admin.php?recurso=estadisticas');
        document.getElementById('total_alumnos').textContent = datos.alumnos;
        document.getElementById('total_cursos').textContent = datos.cursos;
        document.getElementById('total_lecciones').textContent = datos.lecciones;
        document.getElementById('total_intentos').textContent = datos.intentos;
        document.getElementById('promedio').textContent = porcentaje(datos.promedio);
        let valor = Math.max(0, Math.min(100, Number(datos.promedio)));
        document.getElementById('barra_promedio').style.width = valor + '%';
        mostrarMensaje('');
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        document.getElementById('actualizar').disabled = false;
    }
}

iniciarPagina();
