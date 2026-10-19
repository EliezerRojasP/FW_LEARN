// Envía una solicitud a PHP y devuelve los datos de su respuesta JSON.
async function consultarApi(ruta, metodo = 'GET', datos = null) {
    let token = sessionStorage.getItem('fw_admin_token');
    let opciones = {
        method: metodo,
        headers: { 'Accept': 'application/json' },
        cache: 'no-store'
    };

    if (token) {
        opciones.headers.Authorization = 'Bearer ' + token;
    }
    if (datos !== null) {
        opciones.headers['Content-Type'] = 'application/json';
        opciones.body = JSON.stringify(datos);
    }

    let respuesta;
    try {
        respuesta = await fetch(API_URL + ruta, opciones);
    } catch (error) {
        throw new Error('No se pudo conectar. Revisa Apache, la ruta de la API y el puerto.');
    }

    let resultado;
    try {
        resultado = await respuesta.json();
    } catch (error) {
        throw new Error('La API no devolvió JSON. Revisa la ruta y Apache. HTTP ' + respuesta.status);
    }

    if (!respuesta.ok || !resultado.success) {
        let error = new Error(resultado.mensaje || 'No se pudo completar la solicitud.');
        error.estado = respuesta.status;
        if (respuesta.status === 401 && ruta !== 'login.php') {
            sessionStorage.removeItem('fw_admin_token');
            window.location.href = 'index.html';
        }
        throw error;
    }
    return resultado.data;
}
