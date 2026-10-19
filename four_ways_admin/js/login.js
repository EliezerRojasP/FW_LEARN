async function iniciarSesion(evento) {
    evento.preventDefault();
    let boton = document.getElementById('ingresar');
    boton.disabled = true;
    mostrarMensaje('');

    let datos = {
        usuario: document.getElementById('usuario').value.trim(),
        password: document.getElementById('password').value
    };

    try {
        let respuesta = await consultarApi('login.php', 'POST', datos);
        if (respuesta.usuario.rol !== 'ADMIN') {
            // Revoca la sesión recién creada si pertenece a un alumno.
            sessionStorage.setItem('fw_admin_token', respuesta.token);
            try {
                await consultarApi('logout.php', 'POST', {});
            } catch (error) {
                // El token no quedará guardado en la web.
            }
            sessionStorage.removeItem('fw_admin_token');
            mostrarMensaje('Esta web es solo para administradores.', true);
            return;
        }
        sessionStorage.setItem('fw_admin_token', respuesta.token);
        window.location.href = 'panel.html';
    } catch (error) {
        mostrarMensaje(error.message, true);
    } finally {
        boton.disabled = false;
    }
}

function mostrarPassword() {
    let campo = document.getElementById('password');
    let boton = document.getElementById('ver_password');
    if (campo.type === 'password') {
        campo.type = 'text';
        boton.textContent = 'Ocultar';
    } else {
        campo.type = 'password';
        boton.textContent = 'Mostrar';
    }
}
