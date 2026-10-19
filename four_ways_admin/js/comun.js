// Funciones pequeñas que se repiten en las páginas.
let hayCambios = false;

function mostrarMensaje(texto, esError = false) {
    let mensaje = document.getElementById('mensaje');
    mensaje.textContent = texto;
    mensaje.className = esError ? 'mensaje error' : 'mensaje';
    mensaje.hidden = !texto;
}

async function verificarSesion() {
    if (!sessionStorage.getItem('fw_admin_token')) {
        window.location.href = 'index.html';
        return null;
    }
    try {
        let usuario = await consultarApi('me.php');
        if (usuario.rol !== 'ADMIN') {
            try {
                await consultarApi('logout.php', 'POST', {});
            } catch (error) {
                // La cuenta no se conservará en esta web.
            }
            sessionStorage.removeItem('fw_admin_token');
            window.location.href = 'index.html';
            return null;
        }
        document.getElementById('nombre_admin').textContent = usuario.nombre;
        document.getElementById('aplicacion').hidden = false;
        return usuario;
    } catch (error) {
        mostrarMensaje(error.message, true);
        document.getElementById('reintentar').hidden = false;
        return null;
    }
}

async function cerrarSesion() {
    if (hayCambios && !confirm('¿Salir sin guardar los cambios?')) {
        return;
    }
    try {
        await consultarApi('logout.php', 'POST', {});
        hayCambios = false;
        sessionStorage.removeItem('fw_admin_token');
        window.location.href = 'index.html';
    } catch (error) {
        mostrarMensaje(error.message, true);
    }
}

// textContent muestra los datos como texto y evita interpretarlos como HTML.
function agregarCelda(fila, texto) {
    let celda = document.createElement('td');
    celda.textContent = texto;
    fila.appendChild(celda);
    return celda;
}

function agregarEstado(fila, estado) {
    let celda = document.createElement('td');
    let etiqueta = document.createElement('span');
    etiqueta.textContent = Number(estado) === 1 ? 'Activo' : 'Inactivo';
    etiqueta.className = Number(estado) === 1 ? 'estado' : 'estado inactivo';
    celda.appendChild(etiqueta);
    fila.appendChild(celda);
}

function agregarBoton(fila, texto, accion) {
    let celda = document.createElement('td');
    let boton = document.createElement('button');
    boton.textContent = texto;
    boton.className = 'boton-pequeno';
    boton.type = 'button';
    boton.onclick = accion;
    celda.appendChild(boton);
    fila.appendChild(celda);
}

function agregarOpcion(selector, valor, texto) {
    let opcion = document.createElement('option');
    opcion.value = valor;
    opcion.textContent = texto;
    selector.appendChild(opcion);
}

function porcentaje(numero) {
    return Number(numero || 0).toFixed(1) + '%';
}

window.addEventListener('beforeunload', function (evento) {
    if (hayCambios) {
        evento.preventDefault();
        evento.returnValue = '';
    }
});
