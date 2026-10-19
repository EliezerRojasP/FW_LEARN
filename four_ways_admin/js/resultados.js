let resultados = [];

async function iniciarPagina() {
    let usuario = await verificarSesion();
    if (usuario) await listarResultados();
}

async function listarResultados() {
    document.getElementById('actualizar').disabled = true;
    try {
        resultados = await consultarApi('admin.php?recurso=resultados');
        filtrarResultados();
        mostrarMensaje('');
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        document.getElementById('actualizar').disabled = false;
    }
}

function filtrarResultados() {
    let buscar = document.getElementById('buscar').value.toLowerCase();
    let filtrados = resultados.filter(function (resultado) {
        return (resultado.alumno + ' ' + resultado.titulo).toLowerCase().includes(buscar);
    });
    let tabla = document.getElementById('tabla_resultados');
    tabla.innerHTML = '';
    filtrados.forEach(function (resultado) {
        let fila = document.createElement('tr');
        agregarCelda(fila, resultado.alumno);
        agregarCelda(fila, resultado.titulo);
        agregarCelda(fila, resultado.puntaje + ' / ' + resultado.total_preguntas);
        agregarCelda(fila, porcentaje(resultado.porcentaje));
        agregarCelda(fila, resultado.fecha_resultado);
        tabla.appendChild(fila);
    });
    document.getElementById('contador').textContent = filtrados.length + ' de ' + resultados.length + ' resultados';
}

iniciarPagina();
