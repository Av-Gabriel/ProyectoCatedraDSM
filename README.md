# Finanzas UDB

## Registro e inicio de sesión en tu PC

Este proyecto incluye ahora un servidor local en `backend/`, compatible con las rutas de la app Android. Usa Node.js 22.13 o superior y SQLite integrado; no necesita `npm install` ni instalar una base de datos aparte. Node.js ya está disponible en la PC donde se hizo la configuración.

1. Haz doble clic en **`iniciar-servidor.cmd`** desde la raíz del proyecto.
2. Espera el mensaje **Servidor Finanzas UDB listo** y deja la ventana abierta.
3. En Android Studio ejecuta `app` en el emulador.
4. Si usas Android 17 o superior, acepta el permiso de **dispositivos cercanos** que solicita la app. Ese permiso permite que Android contacte con la API de tu PC. Si lo rechazaste, la app ofrece volver a solicitarlo o abrir sus ajustes.
5. Crea tu cuenta con una contraseña de al menos 8 caracteres. Verás la confirmación y podrás iniciar sesión con ese mismo correo y contraseña.

Puedes comprobar que el servidor está activo abriendo `http://127.0.0.1:8081/api/health` en el navegador de tu PC. Debe devolver `status: ok`. Si el puerto ya está ocupado, comprueba si ya tienes una ventana del servidor abierta.

Las cuentas, categorías, sesiones y movimientos se guardan en **`backend/.data/finanzas.sqlite`**. Esa carpeta está ignorada por Git. Cerrar y volver a abrir el servidor conserva los datos; las sesiones vencen a las 24 horas y puedes volver a iniciar sesión. Cada cuenta recibe categorías de ingresos y gastos para agregar movimientos desde el botón **+**. Dashboard e Historial muestran los movimientos de tu cuenta; Presupuesto conserva su diseño con montos de ejemplo.

Este servidor sirve para desarrollo local. Cuando el equipo tenga disponible su backend original, basta con cambiar `API_BASE_URL` para volver a conectarse a él.

## Ejecutar la app en Android Studio

1. Abre esta carpeta en Android Studio y espera a que termine la sincronización de Gradle.
2. En **Settings > Build, Execution, Deployment > Build Tools > Gradle**, selecciona el **Embedded JDK** de Android Studio como Gradle JDK. Este proyecto usa Gradle 9 y no funciona con Java 8.
3. En SDK Manager instala Android SDK Platform 37 si Android Studio lo solicita.
4. Crea o inicia un emulador Android (API 26 o superior) desde Device Manager.
5. Selecciona `app` y pulsa **Run**.

Para registrarse, iniciar sesión y cargar datos, el servidor debe estar ejecutándose en el puerto `8081`.

## Conectar con la API local

La URL se configura con `API_BASE_URL` en el archivo local `local.properties` de la raíz del proyecto. Ese archivo está ignorado por Git y no afecta el repositorio del equipo.

Para un emulador Android con la API ejecutándose en esta misma PC, agrega al final de `local.properties`:

```properties
API_BASE_URL=http://10.0.2.2:8081/
```

Después pulsa **Sync Project with Gradle Files** y vuelve a ejecutar la app. `10.0.2.2` permite que el emulador llegue a los servicios de la PC anfitriona.

Para un teléfono físico conectado a la misma red que la PC, usa la IP local de la PC, por ejemplo:

```properties
API_BASE_URL=http://192.168.1.25:8081/
```

Reemplaza `192.168.1.25` por la IPv4 real de la PC donde corre el backend. El teléfono y la PC deben estar en la misma red. Para aceptar conexiones del teléfono, inicia el servidor desde PowerShell con:

```powershell
$env:HOST = '0.0.0.0'
node backend/server.cjs
```

Permite a Node.js el acceso a redes privadas si Windows lo solicita. El inicio normal con `iniciar-servidor.cmd` escucha solo en `127.0.0.1`, suficiente para el emulador de Android en esta PC.

## Verificar el servidor

```powershell
node --test backend/server.test.cjs
```

La prueba usa una base temporal y comprueba registro, correo duplicado, contraseña incorrecta, acceso sin sesión, aislamiento entre cuentas, categorías, guardado de movimientos y persistencia tras reiniciar el servidor.
