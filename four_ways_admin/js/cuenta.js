async function cargarCuenta() {
    let usuario = await verificarSesion();
    if (!usuario) return;
    document.getElementById('nombre_completo').textContent = usuario.nombre + ' ' + usuario.apellido;
    document.getElementById('usuario_cuenta').textContent = usuario.usuario;
    document.getElementById('nombre_cuenta').textContent = usuario.nombre;
    document.getElementById('apellido_cuenta').textContent = usuario.apellido;
    document.getElementById('api_cuenta').textContent = API_URL;
}

cargarCuenta();
