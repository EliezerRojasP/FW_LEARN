// Mensajes recibidos desde Android. Guardar como js/mensajes.js
//
// Endpoints PHP que espera (carpeta de tu API):
//   GET  mensajes.php           -> data: [ { id, nombre, correo, pregunta, leido, respuesta, fecha }, ... ]
//   POST mensaje_leer.php       -> body: { id }                  (marca el mensaje como leído)
//   POST mensaje_responder.php  -> body: { id, respuesta }       (guarda la respuesta y lo marca contestado)

let mensajes = [];

function el(id) {
    return document.getElementById(id);
}

// Acepta nombres de campo alternativos por si tu PHP los devuelve distintos.
function normalizarMensaje(m) {
    return {
        id: Number(m.id_mensaje),
        nombre: [m.nombre, m.apellido].filter(Boolean).join(' ') || 'Alumno',
        correo: m.usuario ? '@' + m.usuario : '',
        pregunta: m.pregunta ?? '',
        leido: Number(m.leido ?? 0),
        respuesta: m.respuesta ?? '',
        fecha: m.fecha_envio ?? ''
    };
}

async function cargarMensajes() {
    el('actualizar').disabled = true;
    try {
        let datos = await consultarApi('mensajes.php');
        mensajes = datos.map(normalizarMensaje);
        mostrarMensaje('');
        filtrarMensajes();
    } catch (error) {
        mostrarMensaje(error.message, true);
        el('contador').textContent = 'No se pudieron cargar los mensajes.';
    } finally {
        el('actualizar').disabled = false;
    }
}

function filtrarMensajes() {
    let texto = el('buscar').value.trim().toLowerCase();
    let filtroRespuesta = el('filtro_respuesta').value;
    let filtroLectura = el('filtro_lectura').value;

    let lista = mensajes.filter(function (m) {
        let contestado = m.respuesta ? '1' : '0';
        let coincideTexto = !texto
            || m.nombre.toLowerCase().includes(texto)
            || m.pregunta.toLowerCase().includes(texto);
        return coincideTexto
            && (filtroRespuesta === 'todos' || filtroRespuesta === contestado)
            && (filtroLectura === 'todos' || filtroLectura === String(m.leido));
    });

    dibujarMensajes(lista);
    actualizarResumen();
}

function ponerEstado(span, texto, clase) {
    span.textContent = texto;
    span.className = clase ? 'estado ' + clase : 'estado';
}

function dibujarMensajes(lista) {
    let contenedor = el('lista_mensajes');
    let plantilla = el('plantilla_mensaje');
    contenedor.replaceChildren();

    lista.forEach(function (m) {
        let nodo = plantilla.content.firstElementChild.cloneNode(true);
        let campo = function (nombre) {
            return nodo.querySelector('[data-campo="' + nombre + '"]');
        };

        campo('inicial').textContent = m.nombre.charAt(0).toUpperCase();
        campo('nombre').textContent = m.nombre;
        campo('correo').textContent = m.correo;
        campo('pregunta').textContent = m.pregunta;
        campo('fecha').textContent = m.fecha;

        if (m.leido === 1) {
            ponerEstado(campo('estado_lectura'), 'Leído', 'inactivo');
        } else {
            ponerEstado(campo('estado_lectura'), 'Sin leer', 'nuevo');
            nodo.classList.add('sin-leer');
        }

        if (m.respuesta) {
            ponerEstado(campo('estado_respuesta'), 'Contestado', '');
            campo('respuesta').textContent = 'Respuesta: ' + m.respuesta;
            campo('respuesta').hidden = false;
        } else {
            ponerEstado(campo('estado_respuesta'), 'Sin contestar', 'pendiente');
        }

        nodo.querySelector('[data-accion="responder"]').onclick = function () {
            abrirRespuesta(m.id);
        };
        contenedor.appendChild(nodo);
    });

    el('vacio').hidden = lista.length > 0;
    el('contador').textContent = lista.length + ' de ' + mensajes.length + ' mensaje(s)';
}

function actualizarResumen() {
    el('total_mensajes').textContent = mensajes.length;
    el('total_sin_leer').textContent = mensajes.filter(function (m) { return m.leido !== 1; }).length;
    el('total_sin_contestar').textContent = mensajes.filter(function (m) { return !m.respuesta; }).length;
    el('total_contestados').textContent = mensajes.filter(function (m) { return m.respuesta; }).length;
}

async function abrirRespuesta(id) {
    let m = mensajes.find(function (x) { return x.id === id; });
    if (!m) {
        return;
    }

    if (m.leido !== 1) {
        try {
            await consultarApi('mensaje_leer.php', 'POST', { id: id });
            m.leido = 1;
        } catch (error) {
            mostrarMensaje(error.message, true);
        }
    }

    el('id_mensaje').value = m.id;
    el('resp_nombre').textContent = m.nombre;
    el('resp_correo').textContent = m.correo;
    el('resp_fecha').textContent = m.fecha;
    el('resp_pregunta').textContent = m.pregunta;
    el('respuesta').value = m.respuesta;
    ponerEstado(el('resp_estado_lectura'), 'Leído', 'inactivo');
    if (m.respuesta) {
        ponerEstado(el('resp_estado_respuesta'), 'Contestado', '');
    } else {
        ponerEstado(el('resp_estado_respuesta'), 'Sin contestar', 'pendiente');
    }

    hayCambios = false;
    el('lista').hidden = true;
    el('editor').hidden = false;
    el('respuesta').focus();
}

async function enviarRespuesta(evento) {
    evento.preventDefault();
    let id = Number(el('id_mensaje').value);
    let texto = el('respuesta').value.trim();
    if (!texto) {
        return;
    }

    el('guardar').disabled = true;
    try {
        await consultarApi('mensaje_responder.php', 'POST', { id: id, respuesta: texto });
        let m = mensajes.find(function (x) { return x.id === id; });
        m.respuesta = texto;
        m.leido = 1;
        hayCambios = false;
        mostrarMensaje('Respuesta enviada correctamente.');
        cancelarRespuesta();
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        el('guardar').disabled = false;
    }
}

function cancelarRespuesta() {
    if (hayCambios && !confirm('¿Salir sin guardar la respuesta?')) {
        return;
    }
    hayCambios = false;
    el('editor').hidden = true;
    el('lista').hidden = false;
    filtrarMensajes();
}

window.addEventListener('DOMContentLoaded', async function () {
    el('respuesta').addEventListener('input', function () { hayCambios = true; });
    let usuario = await verificarSesion();
    if (usuario) {
        cargarMensajes();
    }
});