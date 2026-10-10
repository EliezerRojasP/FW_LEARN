# FW_LEARN

Proyecto de Android con API en PHP y base de datos para XAMPP.

## Para hacerlo funcionar

1. Descarguen el proyecto y copien `four_ways_api` a `C:\xampp\htdocs\`.
2. En XAMPP inicien Apache y MySQL. Entren a `http://localhost/phpmyadmin/` e importen la base desde `database`: usen el SQL combinado **o** los archivos `01`, `02`, `03` y `04` en ese orden. No importen las dos opciones.
3. En **XAMPP Shell** ejecuten:
   ```bat
   "C:\xampp\php\php.exe" "C:\xampp\htdocs\four_ways_api\setup_usuarios.php"
   ```
4. En Android Studio abran `FW_LEARN` (la carpeta que tiene `settings.gradle.kts`). En su `gradle.properties` configuren una de estas direcciones:
   ```properties
   # Emulador
   FW_API_BASE_URL=http://10.0.2.2/four_ways_api/api/
   # Celular (reemplacen IP_DE_TU_PC por la IPv4 de la PC)
   FW_API_BASE_URL=http://IP_DE_TU_PC/four_ways_api/api/
   ```
   Dejen **solo una** línea `FW_API_BASE_URL` activa. Para usar el celular, conéctenlo a la misma red que la PC. No quiten la `/` del final.
5. Sincronicen Gradle y ejecuten la app. Si Android Studio lo pide, instalen SDK API 36.1 y usen JDK 17.

Cuentas de prueba: `admin` / `AdminFW2026!` y `alumno` / `AlumnoFW2026!`.

Apache y MySQL deben seguir encendidos mientras usan la app.

## Capturas del proyecto

![Acceso y cursos de programación](docs/capturas/acceso-cursos.png)

![Lección, evaluación y progreso](docs/capturas/leccion-progreso.png)

![Administración de usuarios y lecciones](docs/capturas/administracion.png)