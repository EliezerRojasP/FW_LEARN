let lecciones = [];
let cursos = [];
let temas = [];
let preguntas = [];
let encabezados = ['', 'EJEMPLO', 'PRÁCTICA', 'EXPLICACIÓN'];
let guardando = false;

async function iniciarPagina() {
    let usuario = await verificarSesion();
    if (!usuario) return;
    try {
        cursos = await consultarApi('admin.php?recurso=cursos');
        temas = await consultarApi('admin.php?recurso=topicos');
        preguntas = await consultarApi('admin.php?recurso=preguntas');
        cargarCursos();
        await listarLecciones();
        document.getElementById('nuevo').disabled = false;
        let parametros = new URLSearchParams(window.location.search);
        if (parametros.get('id')) editarLeccion(Number(parametros.get('id')));
    } catch (error) {
        mostrarMensaje(error.message, true);
        document.getElementById('reintentar').hidden = false;
    }
}

function cargarCursos() {
    let selector = document.getElementById('id_curso');
    selector.innerHTML = '';
    agregarOpcion(selector, '', 'Seleccionar curso');
    cursos.forEach(function (curso) {
        let nombre = curso.nombre;
        if (Number(curso.estado) === 0) nombre += ' (inactivo)';
        agregarOpcion(selector, curso.id_curso, nombre);
    });
}

function cargarTemas() {
    let idCurso = Number(document.getElementById('id_curso').value);
    let selector = document.getElementById('id_topico');
    selector.innerHTML = '';
    agregarOpcion(selector, '', 'Seleccionar tema');
    temas.forEach(function (tema) {
        if (Number(tema.id_curso) === idCurso) {
            let nombre = tema.nombre;
            if (Number(tema.estado) === 0) nombre += ' (inactivo)';
            agregarOpcion(selector, tema.id_topico, nombre);
        }
    });
}

async function listarLecciones() {
    lecciones = await consultarApi('admin.php?recurso=lecciones');
    filtrarLecciones();
}

function nombreTema(id) {
    let tema = temas.find(function (tema) { return Number(tema.id_topico) === Number(id); });
    return tema ? tema.nombre : 'Sin tema';
}

function filtrarLecciones() {
    let buscar = document.getElementById('buscar').value.toLowerCase();
    let estado = document.getElementById('filtro_estado').value;
    let filtradas = lecciones.filter(function (leccion) {
        let texto = leccion.titulo + ' ' + nombreTema(leccion.id_topico);
        let coincideTexto = texto.toLowerCase().includes(buscar);
        let coincideEstado = estado === 'todos' || Number(leccion.estado) === Number(estado);
        return coincideTexto && coincideEstado;
    });
    let tabla = document.getElementById('tabla_registros');
    tabla.innerHTML = '';
    filtradas.forEach(function (leccion) {
        let fila = document.createElement('tr');
        agregarCelda(fila, leccion.titulo);
        agregarCelda(fila, nombreTema(leccion.id_topico));
        agregarEstado(fila, leccion.estado);
        agregarBoton(fila, 'Editar', function () { editarLeccion(leccion.id_leccion); });
        tabla.appendChild(fila);
    });
    document.getElementById('contador').textContent = filtradas.length + ' de ' + lecciones.length + ' registros';
}

function nuevaLeccion() {
    document.getElementById('formulario').reset();
    document.getElementById('id_registro').value = 0;
    document.getElementById('titulo_editor').textContent = 'Crear lección';
    document.getElementById('desactivar').hidden = true;
    document.getElementById('eliminar').hidden = true;
    document.getElementById('lista').hidden = true;
    document.getElementById('editor').hidden = false;
    document.getElementById('nuevo').hidden = true;
    document.getElementById('preguntas_asociadas').textContent = '';
    document.getElementById('crear_pregunta').hidden = true;
    document.getElementById('aviso_preguntas').hidden = false;
    document.getElementById('vista_previa').hidden = true;
    encabezados = ['', 'EJEMPLO', 'PRÁCTICA', 'EXPLICACIÓN'];
    cargarTemas();
    mostrarSeccion('contenido');
    mostrarMensaje('');
    hayCambios = false;
}

function editarLeccion(id) {
    let leccion = lecciones.find(function (leccion) { return Number(leccion.id_leccion) === Number(id); });
    if (!leccion) {
        mostrarMensaje('No se encontró la lección.', true);
        return;
    }
    nuevaLeccion();
    document.getElementById('id_registro').value = leccion.id_leccion;
    let tema = temas.find(function (tema) { return Number(tema.id_topico) === Number(leccion.id_topico); });
    document.getElementById('id_curso').value = tema ? tema.id_curso : '';
    cargarTemas();
    document.getElementById('id_topico').value = leccion.id_topico;
    document.getElementById('titulo').value = leccion.titulo;
    document.getElementById('orden').value = leccion.orden;
    document.getElementById('estado').checked = Number(leccion.estado) === 1;
    let secciones = separarContenido(leccion.contenido);
    document.getElementById('contenido').value = secciones.partes[0];
    document.getElementById('ejemplo').value = secciones.partes[1];
    document.getElementById('practica').value = secciones.partes[2];
    document.getElementById('explicacion').value = secciones.partes[3];
    encabezados = secciones.encabezados;
    document.getElementById('titulo_editor').textContent = 'Editar lección';
    document.getElementById('desactivar').hidden = false;
    document.getElementById('eliminar').hidden = false;
    document.getElementById('vista_previa').href = 'vista_leccion.html?id=' + id;
    document.getElementById('vista_previa').hidden = false;
    mostrarPreguntas(id);
}

function mostrarSeccion(nombre) {
    let nombres = ['contenido', 'ejemplo', 'practica'];
    nombres.forEach(function (seccion) {
        document.getElementById('seccion_' + seccion).hidden = seccion !== nombre;
        let boton = document.getElementById('boton_' + seccion);
        boton.className = seccion === nombre ? 'seleccionada' : '';
        boton.setAttribute('aria-pressed', seccion === nombre ? 'true' : 'false');
    });
}

function mostrarPreguntas(idLeccion) {
    let contenedor = document.getElementById('preguntas_asociadas');
    contenedor.textContent = '';
    let asociadas = preguntas.filter(function (pregunta) { return Number(pregunta.id_leccion) === Number(idLeccion); });
    asociadas.forEach(function (pregunta) {
        let enlace = document.createElement('a');
        enlace.className = 'acceso';
        enlace.textContent = pregunta.enunciado;
        enlace.href = 'preguntas.html?id=' + pregunta.id_pregunta;
        contenedor.appendChild(enlace);
    });
    if (asociadas.length === 0) contenedor.textContent = 'Aún no hay preguntas.';
    document.getElementById('crear_pregunta').href = 'preguntas.html?leccion=' + idLeccion;
    document.getElementById('crear_pregunta').hidden = false;
    document.getElementById('aviso_preguntas').hidden = true;
}

function cancelarEdicion() {
    if (guardando) return;
    if (hayCambios && !confirm('¿Descartar los cambios sin guardar?')) return;
    hayCambios = false;
    document.getElementById('lista').hidden = false;
    document.getElementById('editor').hidden = true;
    document.getElementById('nuevo').hidden = false;
}

async function guardarLeccion(evento) {
    evento.preventDefault();
    if (guardando) return;
    let id = Number(document.getElementById('id_registro').value);
    let partes = [
        document.getElementById('contenido').value,
        document.getElementById('ejemplo').value,
        document.getElementById('practica').value,
        document.getElementById('explicacion').value
    ];
    let texto = unirContenido(partes, encabezados);
    if (Array.from(texto).length > 50000) {
        mostrarMensaje('El contenido completo no debe superar los 50 000 caracteres.', true);
        return;
    }
    let datos = {
        id_topico: Number(document.getElementById('id_topico').value),
        titulo: document.getElementById('titulo').value.trim(),
        contenido: texto,
        orden: Number(document.getElementById('orden').value),
        estado: document.getElementById('estado').checked ? 1 : 0
    };
    let metodo = 'POST';
    if (id > 0) { metodo = 'PUT'; datos.id_leccion = id; }
    cambiarBotones(true);
    try {
        await consultarApi('admin.php?recurso=lecciones', metodo, datos);
        hayCambios = false;
        document.getElementById('editor').hidden = true;
        document.getElementById('lista').hidden = false;
        document.getElementById('nuevo').hidden = false;
        mostrarMensaje('Lección guardada. Vuelve a consultarla en Android para ver el cambio.');
        await listarLecciones();
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        cambiarBotones(false);
    }
}

async function desactivarLeccion() {
    if (guardando || !confirm('¿Desactivar esta lección? Se conservará el historial.')) return;
    let id = Number(document.getElementById('id_registro').value);
    cambiarBotones(true);
    try {
        await consultarApi('admin.php?recurso=lecciones', 'DELETE', { id_leccion: id });
        hayCambios = false;
        document.getElementById('editor').hidden = true;
        document.getElementById('lista').hidden = false;
        document.getElementById('nuevo').hidden = false;
        mostrarMensaje('Lección desactivada.');
        await listarLecciones();
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        cambiarBotones(false);
    }
}

async function eliminarLeccion() {
    if (guardando) return;

    let id = Number(document.getElementById('id_registro').value);
    if (id <= 0) return;

    if (!confirm('¿Eliminar definitivamente este registro? Esta acción no se puede deshacer.')) {
        return;
    }

    cambiarBotones(true);

    try {
        await consultarApi(
            'admin_eliminar.php?recurso=lecciones',
            'DELETE',
            { id_leccion: id }
        );

        hayCambios = false;

        document.getElementById('editor').hidden = true;
        document.getElementById('lista').hidden = false;
        document.getElementById('nuevo').hidden = false;

        mostrarMensaje('Registro eliminado.');
        await listarLecciones();
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
    document.getElementById('eliminar').disabled = ocupado;
    document.getElementById('cancelar').disabled = ocupado;
}

document.getElementById('id_curso').onchange = cargarTemas;
iniciarPagina();
