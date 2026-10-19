// Administración de temas. El HTML contiene el formulario y la tabla.
let temas = [];
let guardando = false;

let cursos = [];

async function cargarCursos() {
    cursos = await consultarApi('admin.php?recurso=cursos');
    let selector = document.getElementById('id_curso');
    selector.innerHTML = '';
    agregarOpcion(selector, '', 'Seleccionar curso');
    cursos.forEach(function (curso) {
        let nombre = curso.nombre;
        if (Number(curso.estado) === 0) nombre += ' (inactivo)';
        agregarOpcion(selector, curso.id_curso, nombre);
    });
}

function nombreCurso(id) {
    let curso = cursos.find(function (curso) {
        return Number(curso.id_curso) === Number(id);
    });
    return curso ? curso.nombre : 'Sin curso';
}

async function iniciarPagina() {
    let usuario = await verificarSesion();
    if (!usuario) return;
    try {
        await cargarCursos();
        await listarTemas();
        document.getElementById('nuevo').disabled = false;
    } catch (error) {
        mostrarMensaje(error.message, true);
        document.getElementById('reintentar').hidden = false;
    }
}

async function listarTemas() {
    temas = await consultarApi('admin.php?recurso=topicos');
    filtrarTemas();
}

function filtrarTemas() {
    let texto = document.getElementById('buscar').value.toLowerCase();
    let estado = document.getElementById('filtro_estado').value;
    let filtrados = temas.filter(function (registro) {
        let contenido = registro.nombre + ' ' + nombreCurso(registro.id_curso);
        let coincideTexto = contenido.toLowerCase().includes(texto);
        let coincideEstado = estado === 'todos' || Number(registro.estado) === Number(estado);
        return coincideTexto && coincideEstado;
    });
    mostrarTemas(filtrados);
}

function mostrarTemas(registros) {
    let tabla = document.getElementById('tabla_registros');
    tabla.innerHTML = '';
    registros.forEach(function (registro) {
        let fila = document.createElement('tr');
        agregarCelda(fila, registro.nombre);
        agregarCelda(fila, nombreCurso(registro.id_curso));
        agregarEstado(fila, registro.estado);
        agregarBoton(fila, 'Editar', function () { editarTema(registro.id_topico); });
        tabla.appendChild(fila);
    });
    document.getElementById('contador').textContent = registros.length + ' de ' + temas.length + ' registros';
}

function nuevoTema() {
    document.getElementById('formulario').reset();
    document.getElementById('id_registro').value = 0;
    document.getElementById('titulo_editor').textContent = 'Crear tema';
    document.getElementById('desactivar').hidden = true;
    document.getElementById('eliminar').hidden = true;
    document.getElementById('lista').hidden = true;
    document.getElementById('editor').hidden = false;
    document.getElementById('nuevo').hidden = true;
    mostrarMensaje('');
    hayCambios = false;
}

function editarTema(id) {
    let registro = temas.find(function (registro) { return Number(registro.id_topico) === Number(id); });
    if (!registro) return;
    nuevoTema();
    document.getElementById('id_registro').value = registro.id_topico;
    document.getElementById('id_curso').value = registro.id_curso;
    document.getElementById('nombre').value = registro.nombre;
    document.getElementById('descripcion').value = registro.descripcion;
    document.getElementById('orden').value = registro.orden;
    document.getElementById('estado').checked = Number(registro.estado) === 1;
    document.getElementById('titulo_editor').textContent = 'Editar tema';
    document.getElementById('desactivar').hidden = false;
    document.getElementById('eliminar').hidden = false;
}

function cancelarEdicion() {
    if (guardando) return;
    if (hayCambios && !confirm('¿Descartar los cambios sin guardar?')) return;
    hayCambios = false;
    document.getElementById('lista').hidden = false;
    document.getElementById('editor').hidden = true;
    document.getElementById('nuevo').hidden = false;
}

async function guardarTema(evento) {
    evento.preventDefault();
    if (guardando) return;
    let id = Number(document.getElementById('id_registro').value);
    let datos = {
        id_curso: Number(document.getElementById('id_curso').value),
        nombre: document.getElementById('nombre').value.trim(),
        descripcion: document.getElementById('descripcion').value.trim(),
        orden: Number(document.getElementById('orden').value),
        estado: document.getElementById('estado').checked ? 1 : 0
    };
    let metodo = 'POST';
    if (id > 0) {
        metodo = 'PUT';
        datos.id_topico = id;
    }
    cambiarBotones(true);
    try {
        await consultarApi('admin.php?recurso=topicos', metodo, datos);
        hayCambios = false;
        document.getElementById('lista').hidden = false;
        document.getElementById('editor').hidden = true;
        document.getElementById('nuevo').hidden = false;
        mostrarMensaje('Registro guardado.');
        await listarTemas();
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        cambiarBotones(false);
    }
}

async function desactivarTema() {
    if (guardando || !confirm('¿Desactivar este registro? Se conservará el historial.')) return;
    let id = Number(document.getElementById('id_registro').value);
    cambiarBotones(true);
    try {
        await consultarApi('admin.php?recurso=topicos', 'DELETE', { id_topico: id });
        hayCambios = false;
        document.getElementById('editor').hidden = true;
        document.getElementById('lista').hidden = false;
        document.getElementById('nuevo').hidden = false;
        mostrarMensaje('Registro desactivado.');
        await listarTemas();
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        cambiarBotones(false);
    }
}

async function eliminarTema() {
    if (guardando) return;

    let id = Number(document.getElementById('id_registro').value);
    if (id <= 0) return;

    if (!confirm('¿Eliminar definitivamente este registro? Esta acción no se puede deshacer.')) {
        return;
    }

    cambiarBotones(true);

    try {
        await consultarApi(
            'admin_eliminar.php?recurso=topicos',
            'DELETE',
            { id_topico: id }
        );

        hayCambios = false;

        document.getElementById('editor').hidden = true;
        document.getElementById('lista').hidden = false;
        document.getElementById('nuevo').hidden = false;

        mostrarMensaje('Registro eliminado.');
        await listarTemas();
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

iniciarPagina();
