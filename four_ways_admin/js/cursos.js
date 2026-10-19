// Administración de cursos. El HTML contiene el formulario y la tabla.
let cursos = [];
let guardando = false;

async function iniciarPagina() {
    let usuario = await verificarSesion();
    if (!usuario) return;
    try {
        await listarCursos();
        document.getElementById('nuevo').disabled = false;
    } catch (error) {
        mostrarMensaje(error.message, true);
        document.getElementById('reintentar').hidden = false;
    }
}

async function listarCursos() {
    cursos = await consultarApi('admin.php?recurso=cursos');
    filtrarCursos();
}

function filtrarCursos() {
    let texto = document.getElementById('buscar').value.toLowerCase();
    let estado = document.getElementById('filtro_estado').value;
    let filtrados = cursos.filter(function (registro) {
        let contenido = registro.nombre + ' ' + registro.descripcion || 'Sin descripción';
        let coincideTexto = contenido.toLowerCase().includes(texto);
        let coincideEstado = estado === 'todos' || Number(registro.estado) === Number(estado);
        return coincideTexto && coincideEstado;
    });
    mostrarCursos(filtrados);
}

function mostrarCursos(registros) {
    let tabla = document.getElementById('tabla_registros');
    tabla.innerHTML = '';
    registros.forEach(function (registro) {
        let fila = document.createElement('tr');
        agregarCelda(fila, registro.nombre);
        agregarCelda(fila, registro.descripcion || 'Sin descripción');
        agregarEstado(fila, registro.estado);
        agregarBoton(fila, 'Editar', function () { editarCurso(registro.id_curso); });
        tabla.appendChild(fila);
    });
    document.getElementById('contador').textContent = registros.length + ' de ' + cursos.length + ' registros';
}

function nuevoCurso() {
    document.getElementById('formulario').reset();
    document.getElementById('id_registro').value = 0;
    document.getElementById('titulo_editor').textContent = 'Crear curso';
    document.getElementById('desactivar').hidden = true;
    document.getElementById('eliminar').hidden = true;
    document.getElementById('lista').hidden = true;
    document.getElementById('editor').hidden = false;
    document.getElementById('nuevo').hidden = true;
    mostrarMensaje('');
    hayCambios = false;
}

function editarCurso(id) {
    let registro = cursos.find(function (registro) { return Number(registro.id_curso) === Number(id); });
    if (!registro) return;
    nuevoCurso();
    document.getElementById('id_registro').value = registro.id_curso;
    document.getElementById('nombre').value = registro.nombre;
    document.getElementById('descripcion').value = registro.descripcion;
    document.getElementById('estado').checked = Number(registro.estado) === 1;
    document.getElementById('titulo_editor').textContent = 'Editar curso';
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

async function guardarCurso(evento) {
    evento.preventDefault();
    if (guardando) return;
    let id = Number(document.getElementById('id_registro').value);
    let datos = {
        nombre: document.getElementById('nombre').value.trim(),
        descripcion: document.getElementById('descripcion').value.trim(),
        estado: document.getElementById('estado').checked ? 1 : 0
    };
    let metodo = 'POST';
    if (id > 0) {
        metodo = 'PUT';
        datos.id_curso = id;
    }
    cambiarBotones(true);
    try {
        await consultarApi('admin.php?recurso=cursos', metodo, datos);
        hayCambios = false;
        document.getElementById('lista').hidden = false;
        document.getElementById('editor').hidden = true;
        document.getElementById('nuevo').hidden = false;
        mostrarMensaje('Registro guardado.');
        await listarCursos();
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        cambiarBotones(false);
    }
}

async function desactivarCurso() {
    if (guardando || !confirm('¿Desactivar este registro? Se conservará el historial.')) return;
    let id = Number(document.getElementById('id_registro').value);
    cambiarBotones(true);
    try {
        await consultarApi('admin.php?recurso=cursos', 'DELETE', { id_curso: id });
        hayCambios = false;
        document.getElementById('editor').hidden = true;
        document.getElementById('lista').hidden = false;
        document.getElementById('nuevo').hidden = false;
        mostrarMensaje('Registro desactivado.');
        await listarCursos();
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        cambiarBotones(false);
    }
}

async function eliminarCurso() {
    if (guardando) return; 
        
    let id = Number(document.getElementById('id_registro').value);
    if (id <= 0) return; 
        
    if(!confirm('¿Eliminar definitivamente este registro? Esta acción no se puede deshacer.')) {
        return;
    }

    cambiarBotones(true);
    try {
        await consultarApi(
            'admin_eliminar.php?recurso=cursos',
            'DELETE',
            {id_curso: id}
        );

        hayCambios = false;

        document.getElementById('editor').hidden = true;
        document.getElementById('lista').hidden = false;
        document.getElementById('nuevo').hidden = false;

        mostrarMensaje('Registro eliminado.');
        await listarCursos();
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
