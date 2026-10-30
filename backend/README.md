# Servidor local de Finanzas UDB

Inicia `iniciar-servidor.cmd` desde la raíz del proyecto y deja la ventana abierta. Necesita Node.js 22.13 o superior, sin dependencias adicionales. Las cuentas y movimientos se conservan en `.data/finanzas.sqlite`, ignorado por Git.

## Categorías propias

En la app, abre **+ → Nueva categoría**. Escribe un nombre de hasta 60 caracteres, elige **GASTO** o **INGRESO** y pulsa **Crear categoría**. La categoría se guarda en tu cuenta y queda seleccionada para registrar el movimiento. No se repiten nombres dentro de un mismo tipo; dos cuentas pueden usar los mismos nombres.

La API usa `POST /api/categories?userId=...`, con la sesión Bearer del usuario y un cuerpo JSON como:

```json
{"nombre":"Educación","tipo":"GASTO"}
```

Devuelve la categoría creada, con su identificador y tipo, y queda disponible en `GET /api/categories`. Si se conecta la app con otro backend, ese servidor también debe implementar esta ruta.

## Verificación

Desde la raíz del proyecto:

```powershell
node --test backend/server.test.cjs
```
