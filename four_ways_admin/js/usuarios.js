// Administración de usuarios. El HTML contiene el formulario y la tabla.
let usuarios = [];
let guardando = false;
let idAdministrador = 0;

async function iniciarPagina() {
    let usuario = await verificarSesion();
    if (!usuario) return;
    idAdministrador = usuario.id_usuario;
    try {
        await listarUsuarios();
        document.getElementById('nuevo').disabled = false;
    } catch (error) {
        mostrarMensaje(error.message, true);
        document.getElementById('reintentar').hidden = false;
    }
}

async function listarUsuarios() {
    usuarios = await consultarApi('admin.php?recurso=usuarios');
    filtrarUsuarios();
}

function filtrarUsuarios() {
    let texto = document.getElementById('buscar').value.toLowerCase();
    let estado = document.getElementById('filtro_estado').value;
    let filtrados = usuarios.filter(function (registro) {
        let contenido = registro.usuario + ' ' + (Number(registro.id_rol) === 1 ? 'Administrador' : 'Alumno') + ' · ' + registro.nombre + ' ' + registro.apellido;
        let coincideTexto = contenido.toLowerCase().includes(texto);
        let coincideEstado = estado === 'todos' || Number(registro.estado) === Number(estado);
        return coincideTexto && coincideEstado;
    });
    mostrarUsuarios(filtrados);
}

function mostrarUsuarios(registros) {
    let tabla = document.getElementById('tabla_registros');
    tabla.innerHTML = '';
    registros.forEach(function (registro) {
        let fila = document.createElement('tr');
        agregarCelda(fila, registro.usuario);
        agregarCelda(fila, (Number(registro.id_rol) === 1 ? 'Administrador' : 'Alumno') + ' · ' + registro.nombre + ' ' + registro.apellido);
        agregarEstado(fila, registro.estado);
        agregarBoton(fila, 'Editar', function () { editarUsuario(registro.id_usuario); });
        tabla.appendChild(fila);
    });
    document.getElementById('contador').textContent = registros.length + ' de ' + usuarios.length + ' registros';
}

function nuevoUsuario() {
    document.getElementById('formulario').reset();
    document.getElementById('id_registro').value = 0;
    document.getElementById('password').required = true;
    document.getElementById('titulo_editor').textContent = 'Crear usuario';
    document.getElementById('desactivar').hidden = true;
    document.getElementById('eliminar').hidden = true;
    document.getElementById('lista').hidden = true;
    document.getElementById('editor').hidden = false;
    document.getElementById('nuevo').hidden = true;
    mostrarMensaje('');
    hayCambios = false;
}

function editarUsuario(id) {
    let registro = usuarios.find(function (registro) { return Number(registro.id_usuario) === Number(id); });
    if (!registro) return;
    nuevoUsuario();
    document.getElementById('id_registro').value = registro.id_usuario;
    document.getElementById('nombre').value = registro.nombre;
    document.getElementById('apellido').value = registro.apellido;
    document.getElementById('usuario').value = registro.usuario;
    document.getElementById('password').value = '';
    document.getElementById('id_rol').value = registro.id_rol;
    document.getElementById('password').required = false;
    document.getElementById('estado').checked = Number(registro.estado) === 1;
    document.getElementById('titulo_editor').textContent = 'Editar usuario';
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

async function guardarUsuario(evento) {
    evento.preventDefault();
    if (guardando) return;
    let id = Number(document.getElementById('id_registro').value);
    let datos = {
        nombre: document.getElementById('nombre').value.trim(),
        apellido: document.getElementById('apellido').value.trim(),
        usuario: document.getElementById('usuario').value.trim(),
        password: document.getElementById('password').value,
        id_rol: Number(document.getElementById('id_rol').value),
        estado: document.getElementById('estado').checked ? 1 : 0
    };
    let cantidadBytes = new TextEncoder().encode(datos.password).length;
    if ((id === 0 || datos.password !== '') && (cantidadBytes < 8 || cantidadBytes > 72)) {
        mostrarMensaje('La contraseña debe tener entre 8 y 72 bytes.', true);
        return;
    }
    let metodo = 'POST';
    if (id > 0) {
        metodo = 'PUT';
        datos.id_usuario = id;
    }
    cambiarBotones(true);
    try {
        await consultarApi('admin.php?recurso=usuarios', metodo, datos);
        hayCambios = false;
        if (id === Number(idAdministrador)) {
            document.getElementById('nombre_admin').textContent = datos.nombre;
            if (datos.password !== '') {
                sessionStorage.removeItem('fw_admin_token');
                window.location.href = 'index.html';
                return;
            }
        }
        document.getElementById('lista').hidden = false;
        document.getElementById('editor').hidden = true;
        document.getElementById('nuevo').hidden = false;
        mostrarMensaje('Registro guardado.');
        await listarUsuarios();
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        cambiarBotones(false);
    }
}

async function desactivarUsuario() {
    if (guardando || !confirm('¿Desactivar este registro? Se conservará el historial.')) return;
    let id = Number(document.getElementById('id_registro').value);
    cambiarBotones(true);
    try {
        await consultarApi('admin.php?recurso=usuarios', 'DELETE', { id_usuario: id });
        hayCambios = false;
        document.getElementById('editor').hidden = true;
        document.getElementById('lista').hidden = false;
        document.getElementById('nuevo').hidden = false;
        mostrarMensaje('Registro desactivado.');
        await listarUsuarios();
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        cambiarBotones(false);
    }
}

async function eliminarUsuario() {
    if (guardando) return;

    let id = Number(document.getElementById('id_registro').value);
    if (id <= 0) return;

    if (!confirm('¿Eliminar definitivamente este registro? Esta acción no se puede deshacer.')) {
        return;
    }

    cambiarBotones(true);

    try {
        await consultarApi(
            'admin_eliminar.php?recurso=usuarios',
            'DELETE',
            { id_usuario: id }
        );

        hayCambios = false;

        document.getElementById('editor').hidden = true;
        document.getElementById('lista').hidden = false;
        document.getElementById('nuevo').hidden = false;

        mostrarMensaje('Registro eliminado.');
        await listarUsuarios();
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
