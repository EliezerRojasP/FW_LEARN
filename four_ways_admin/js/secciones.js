// Android guarda toda la lección en una sola columna: contenido.
// Los encabezados EJEMPLO, PRÁCTICA y EXPLICACIÓN separan las partes.
function separarContenido(texto) {
    let partes = ['', '', '', ''];
    let encabezados = ['', 'EJEMPLO', 'PRÁCTICA', 'EXPLICACIÓN'];
    let actual = 0;
    let lineas = String(texto || '').replaceAll('\r\n', '\n').split('\n');

    lineas.forEach(function (linea) {
        let titulo = linea.trim();
        let siguiente = -1;

        // Solo un encabezado escrito en MAYÚSCULAS inicia otra sección.
        if (titulo === titulo.toUpperCase()) {
            if (titulo === 'EJEMPLO' || titulo === 'EJEMPLOS' ||
                titulo.startsWith('EJEMPLO ') || titulo.startsWith('EJEMPLO:') ||
                titulo.startsWith('EJEMPLOS:') || titulo === 'EN TU PROYECTO') {
                siguiente = 1;
            } else if (titulo === 'PRÁCTICA' || titulo === 'PRACTICA' ||
                       titulo === 'PRÁCTICA:' || titulo === 'PRACTICA:' ||
                       titulo.startsWith('PRÁCTICA ') || titulo.startsWith('PRACTICA ')) {
                siguiente = 2;
            } else if (titulo === 'EXPLICACIÓN' || titulo === 'EXPLICACION' ||
                       titulo === 'EXPLICACIÓN:' || titulo === 'EXPLICACION:') {
                siguiente = 3;
            } else if (titulo === 'CONTENIDO') {
                siguiente = 0;
            }
        }

        if (siguiente >= 0 && siguiente !== actual && partes[siguiente] === '') {
            actual = siguiente;
            encabezados[siguiente] = titulo;
        } else {
            partes[actual] += linea + '\n';
        }
    });

    for (let i = 0; i < 4; i++) {
        partes[i] = partes[i].trim();
    }
    return { partes: partes, encabezados: encabezados };
}

function unirContenido(partes, encabezados) {
    let texto = '';
    for (let i = 0; i < 4; i++) {
        let parte = partes[i].trim();
        if (parte === '') continue;
        if (texto !== '') texto += '\n\n';
        if (i > 0) texto += encabezados[i] + '\n';
        texto += parte;
    }
    return texto;
}
