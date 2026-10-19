let preguntas = [];
let lecciones = [];
let guardando = false;

async function iniciarPagina() {
    let usuario = await verificarSesion();
    if (!usuario) return;
    try {
        lecciones = await consultarApi('admin.php?recurso=lecciones');
        let selector = document.getElementById('id_leccion');
        selector.innerHTML = '';
        agregarOpcion(selector, '', 'Seleccionar lección');
        lecciones.forEach(function (leccion) {
            let nombre = leccion.titulo;
            if (Number(leccion.estado) === 0) nombre += ' (inactiva)';
            agregarOpcion(selector, leccion.id_leccion, nombre);
        });
        await listarPreguntas();
        document.getElementById('nuevo').disabled = false;
        let parametros = new URLSearchParams(window.location.search);
        if (parametros.get('id')) {
            editarPregunta(Number(parametros.get('id')));
        } else if (parametros.get('leccion')) {
            nuevaPregunta();
            selector.value = parametros.get('leccion');
        }
    } catch (error) {
        mostrarMensaje(error.message, true);
        document.getElementById('reintentar').hidden = false;
    }
}

async function listarPreguntas() {
    preguntas = await consultarApi('admin.php?recurso=preguntas');
    filtrarPreguntas();
}

function tituloLeccion(id) {
    let leccion = lecciones.find(function (leccion) { return Number(leccion.id_leccion) === Number(id); });
    return leccion ? leccion.titulo : 'Sin lección';
}

function filtrarPreguntas() {
    let buscar = document.getElementById('buscar').value.toLowerCase();
    let estado = document.getElementById('filtro_estado').value;
    let filtradas = preguntas.filter(function (pregunta) {
        let texto = pregunta.enunciado + ' ' + tituloLeccion(pregunta.id_leccion);
        let coincideTexto = texto.toLowerCase().includes(buscar);
        let coincideEstado = estado === 'todos' || Number(pregunta.estado) === Number(estado);
        return coincideTexto && coincideEstado;
    });
    let tabla = document.getElementById('tabla_registros');
    tabla.innerHTML = '';
    filtradas.forEach(function (pregunta) {
        let fila = document.createElement('tr');
        agregarCelda(fila, pregunta.enunciado);
        agregarCelda(fila, tituloLeccion(pregunta.id_leccion));
        agregarEstado(fila, pregunta.estado);
        agregarBoton(fila, 'Editar', function () { editarPregunta(pregunta.id_pregunta); });
        tabla.appendChild(fila);
    });
    document.getElementById('contador').textContent = filtradas.length + ' de ' + preguntas.length + ' registros';
}

function nuevaPregunta() {
    document.getElementById('formulario').reset();
    document.getElementById('id_registro').value = 0;
    document.getElementById('titulo_editor').textContent = 'Crear pregunta';
    document.getElementById('desactivar').hidden = true;
    document.getElementById('eliminar').hidden = true;
    document.getElementById('lista').hidden = true;
    document.getElementById('editor').hidden = false;
    document.getElementById('nuevo').hidden = true;
    mostrarAlternativas();
    mostrarMensaje('');
    hayCambios = false;
}

function editarPregunta(id) {
    let pregunta = preguntas.find(function (pregunta) { return Number(pregunta.id_pregunta) === Number(id); });
    if (!pregunta) { mostrarMensaje('No se encontró la pregunta.', true); return; }
    nuevaPregunta();
    document.getElementById('id_registro').value = pregunta.id_pregunta;
    document.getElementById('id_leccion').value = pregunta.id_leccion;
    document.getElementById('enunciado').value = pregunta.enunciado;
    document.getElementById('orden').value = pregunta.orden;
    document.getElementById('estado').checked = Number(pregunta.estado) === 1;
    document.getElementById('cantidad').value = pregunta.alternativas.length;
    pregunta.alternativas.forEach(function (alternativa, i) {
        document.getElementById('alternativa_' + i).value = alternativa.texto;
        if (Number(alternativa.es_correcta) === 1) {
            document.querySelector('input[name="correcta"][value="' + i + '"]').checked = true;
        }
    });
    mostrarAlternativas();
    document.getElementById('titulo_editor').textContent = 'Editar pregunta';
    document.getElementById('desactivar').hidden = false;
    document.getElementById('eliminar').hidden = false;
}

function mostrarAlternativas() {
    let cantidad = Number(document.getElementById('cantidad').value);
    for (let i = 0; i < 6; i++) {
        document.getElementById('fila_alternativa_' + i).hidden = i >= cantidad;
        document.getElementById('alternativa_' + i).required = i < cantidad;
    }
    let correcta = document.querySelector('input[name="correcta"]:checked');
    if (!correcta || Number(correcta.value) >= cantidad) {
        document.querySelector('input[name="correcta"][value="0"]').checked = true;
    }
}

function cancelarEdicion() {
    if (guardando) return;
    if (hayCambios && !confirm('¿Descartar los cambios sin guardar?')) return;
    hayCambios = false;
    document.getElementById('lista').hidden = false;
    document.getElementById('editor').hidden = true;
    document.getElementById('nuevo').hidden = false;
}

async function guardarPregunta(evento) {
    evento.preventDefault();
    if (guardando) return;
    let id = Number(document.getElementById('id_registro').value);
    let cantidad = Number(document.getElementById('cantidad').value);
    let seleccion = document.querySelector('input[name="correcta"]:checked');
    if (!seleccion || cantidad < 2 || cantidad > 6 || Number(seleccion.value) >= cantidad) {
        mostrarMensaje('Elige de 2 a 6 alternativas y una respuesta correcta.', true);
        return;
    }
    let alternativas = [];
    for (let i = 0; i < cantidad; i++) {
        let texto = document.getElementById('alternativa_' + i).value.trim();
        if (texto === '') { mostrarMensaje('Completa todas las alternativas.', true); return; }
        alternativas.push({ texto: texto, es_correcta: i === Number(seleccion.value) ? 1 : 0 });
    }
    let datos = {
        id_leccion: Number(document.getElementById('id_leccion').value),
        enunciado: document.getElementById('enunciado').value.trim(),
        orden: Number(document.getElementById('orden').value),
        estado: document.getElementById('estado').checked ? 1 : 0,
        alternativas: alternativas
    };
    let metodo = 'POST';
    if (id > 0) { metodo = 'PUT'; datos.id_pregunta = id; }
    cambiarBotones(true);
    try {
        await consultarApi('admin.php?recurso=preguntas', metodo, datos);
        hayCambios = false;
        document.getElementById('editor').hidden = true;
        document.getElementById('lista').hidden = false;
        document.getElementById('nuevo').hidden = false;
        mostrarMensaje('Pregunta guardada.');
        await listarPreguntas();
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        cambiarBotones(false);
    }
}

async function desactivarPregunta() {
    if (guardando || !confirm('¿Desactivar esta pregunta? Se conservará el historial.')) return;
    let id = Number(document.getElementById('id_registro').value);
    cambiarBotones(true);
    try {
        await consultarApi('admin.php?recurso=preguntas', 'DELETE', { id_pregunta: id });
        hayCambios = false;
        document.getElementById('editor').hidden = true;
        document.getElementById('lista').hidden = false;
        document.getElementById('nuevo').hidden = false;
        mostrarMensaje('Pregunta desactivada.');
        await listarPreguntas();
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        cambiarBotones(false);
    }
}

async function eliminarPregunta() {
    if (guardando) return;

    let id = Number(document.getElementById('id_registro').value);
    if (id <= 0) return;

    if (!confirm('¿Eliminar definitivamente este registro? Esta acción no se puede deshacer.')) {
        return;
    }

    cambiarBotones(true);

    try {
        await consultarApi(
            'admin_eliminar.php?recurso=preguntas',
            'DELETE',
            { id_pregunta: id }
        );

        hayCambios = false;

        document.getElementById('editor').hidden = true;
        document.getElementById('lista').hidden = false;
        document.getElementById('nuevo').hidden = false;

        mostrarMensaje('Registro eliminado.');
        await listarPreguntas();
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        cambiarBotones(false);
    }
}


function cambiarBotones(ocupado) {
    guardando = ocupado;
    document.getElementById('guardar').disabled = ocupado;
    document.getElementById('desactivar').disabled = ocupado;
    document.getElementById('cancelar').disabled = ocupado;
    document.getElementById('eliminar').disabled = ocupado;
}

iniciarPagina();
