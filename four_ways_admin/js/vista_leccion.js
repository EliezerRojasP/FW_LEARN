async function cargarVistaPrevia() {
    let usuario = await verificarSesion();
    if (!usuario) return;
    try {
        let parametros = new URLSearchParams(window.location.search);
        let id = Number(parametros.get('id'));
        let lecciones = await consultarApi('admin.php?recurso=lecciones');
        let leccion = lecciones.find(function (leccion) { return Number(leccion.id_leccion) === id; });
        if (!leccion) { mostrarMensaje('No se encontró la lección.', true); return; }
        document.getElementById('titulo_vista').textContent = leccion.titulo;
        document.getElementById('volver_editor').href = 'lecciones.html?id=' + id;
        let datos = separarContenido(leccion.contenido);
        let nombres = ['contenido', 'ejemplo', 'practica', 'explicacion'];
        nombres.forEach(function (nombre, i) {
            document.getElementById('texto_' + nombre).textContent = datos.partes[i];
            document.getElementById('vista_' + nombre).hidden = datos.partes[i] === '';
        });
    } catch (error) {
        mostrarMensaje(error.message, true);
    }
}

cargarVistaPrevia();
