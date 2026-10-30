'use strict';

const { createServer } = require('node:http');
const { mkdirSync } = require('node:fs');
const { dirname, join } = require('node:path');
const { randomBytes, scryptSync, timingSafeEqual, createHash } = require('node:crypto');
const { DatabaseSync } = require('node:sqlite');

class ApiError extends Error {
  constructor(status, message) { super(message); this.status = status; }
}

const defaultCategories = [
  ['Alimentación', 'GASTO', 'restaurant', '#2E7D63'],
  ['Transporte', 'GASTO', 'swap_horiz', '#4A6572'],
  ['Ocio', 'GASTO', 'tv', '#9A6EAA'],
  ['Compras', 'GASTO', 'shopping_bag', '#C88B45'],
  ['Salario', 'INGRESO', 'work', '#2E7D63'],
  ['Otros ingresos', 'INGRESO', 'swap_horiz', '#4A6572']
];

function text(value, field, { optional = false, max = 255 } = {}) {
  if (optional && value == null) return '';
  if (typeof value !== 'string' || (!optional && !value.trim()) || value.length > max) {
    throw new ApiError(400, `Revisa el campo ${field}.`);
  }
  return value.trim();
}

function email(value) {
  const result = text(value, 'correo').toLowerCase();
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(result)) throw new ApiError(400, 'Ingresa un correo válido.');
  return result;
}

async function readJson(request) {
  let size = 0;
  const chunks = [];
  for await (const chunk of request) {
    size += chunk.length;
    if (size > 16384) throw new ApiError(413, 'La solicitud es demasiado grande.');
    chunks.push(chunk);
  }
  try {
    const body = JSON.parse(Buffer.concat(chunks).toString('utf8'));
    if (!body || typeof body !== 'object' || Array.isArray(body)) throw new Error();
    return body;
  } catch { throw new ApiError(400, 'Envía los datos en formato JSON válido.'); }
}

function send(response, status, body) {
  response.writeHead(status, { 'Content-Type': 'application/json; charset=utf-8', 'Cache-Control': 'no-store' });
  response.end(JSON.stringify(body));
}

function tokenHash(token) { return createHash('sha256').update(token).digest('hex'); }

function createApiServer({ databasePath = join(__dirname, '.data', 'finanzas.sqlite') } = {}) {
  if (databasePath !== ':memory:') mkdirSync(dirname(databasePath), { recursive: true });
  const db = new DatabaseSync(databasePath);
  db.exec(`
    PRAGMA foreign_keys = ON;
    PRAGMA busy_timeout = 5000;
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY, nombre TEXT NOT NULL, apellido TEXT NOT NULL,
      nombreUsuario TEXT NOT NULL, correo TEXT NOT NULL UNIQUE,
      salt TEXT NOT NULL, passwordHash TEXT NOT NULL
    );
    CREATE TABLE IF NOT EXISTS sessions (
      tokenHash TEXT PRIMARY KEY, userId INTEGER NOT NULL REFERENCES users(id), expiresAt INTEGER NOT NULL
    );
    CREATE TABLE IF NOT EXISTS categories (
      id INTEGER PRIMARY KEY, nombre TEXT NOT NULL, tipo TEXT NOT NULL CHECK(tipo IN ('GASTO', 'INGRESO')),
      icono TEXT NOT NULL, color TEXT NOT NULL, activo INTEGER NOT NULL DEFAULT 1,
      userId INTEGER NOT NULL REFERENCES users(id)
    );
    CREATE TABLE IF NOT EXISTS transactions (
      id INTEGER PRIMARY KEY, cents INTEGER NOT NULL CHECK(cents > 0), fecha TEXT NOT NULL,
      descripcion TEXT NOT NULL, categoriaId INTEGER NOT NULL REFERENCES categories(id),
      userId INTEGER NOT NULL REFERENCES users(id)
    );
    CREATE INDEX IF NOT EXISTS transactions_user ON transactions(userId, fecha);
    CREATE INDEX IF NOT EXISTS categories_user ON categories(userId);
  `);

  const publicUser = (id) => db.prepare('SELECT id, nombre, apellido, correo FROM users WHERE id = ?').get(id);
  const publicTransaction = (id) => db.prepare(`
    SELECT t.id, t.cents / 100.0 AS monto, t.fecha, c.tipo, t.descripcion, t.categoriaId, t.userId
    FROM transactions t JOIN categories c ON c.id = t.categoriaId WHERE t.id = ?
  `).get(id);

  function authorize(request, url) {
    const token = /^Bearer ([A-Za-z0-9_-]+)$/.exec(request.headers.authorization || '')?.[1];
    const session = token && db.prepare('SELECT userId FROM sessions WHERE tokenHash = ? AND expiresAt > ?')
      .get(tokenHash(token), Date.now());
    if (!session) throw new ApiError(401, 'Inicia sesión para continuar.');
    const requestedId = url.searchParams.get('userId');
    if (requestedId !== null && requestedId !== String(session.userId)) {
      throw new ApiError(403, 'No tienes acceso a los datos de otro usuario.');
    }
    return session.userId;
  }

  const server = createServer(async (request, response) => {
    try {
      const url = new URL(request.url, 'http://localhost');
      const route = `${request.method} ${url.pathname}`;
      if (route === 'GET /api/health' || route === 'GET /') {
        return send(response, 200, { status: 'ok', service: 'finanzas-udb-local' });
      }
      if (route === 'POST /api/users') {
        const body = await readJson(request);
        const nombre = text(body.nombre, 'nombre');
        const apellido = text(body.apellido, 'apellido', { optional: true });
        const correo = email(body.correo);
        const username = text(body.nombreUsuario || correo.substring(0, correo.indexOf('@')), 'nombre de usuario');
        const password = body.contrasena;
        if (typeof password !== 'string' || password.length < 8 || password.length > 128 || !password.trim()) {
          throw new ApiError(400, 'La contraseña debe tener entre 8 y 128 caracteres.');
        }
        if (db.prepare('SELECT id FROM users WHERE correo = ?').get(correo)) {
          throw new ApiError(409, 'Ya existe una cuenta con ese correo.');
        }
        const salt = randomBytes(16).toString('hex');
        const passwordHash = scryptSync(password, salt, 64).toString('hex');
        db.exec('BEGIN');
        try {
          const { lastInsertRowid: id } = db.prepare(`
            INSERT INTO users (nombre, apellido, nombreUsuario, correo, salt, passwordHash) VALUES (?, ?, ?, ?, ?, ?)
          `).run(nombre, apellido, username, correo, salt, passwordHash);
          const insertCategory = db.prepare('INSERT INTO categories (nombre, tipo, icono, color, userId) VALUES (?, ?, ?, ?, ?)');
          for (const category of defaultCategories) insertCategory.run(...category, id);
          db.exec('COMMIT');
          return send(response, 201, publicUser(id));
        } catch (error) { db.exec('ROLLBACK'); throw error; }
      }
      if (route === 'POST /api/auth/login') {
        const body = await readJson(request);
        const correo = email(body.correo);
        const password = body.contrasena;
        if (typeof password !== 'string' || password.length > 128) throw new ApiError(400, 'Revisa la contraseña.');
        const user = db.prepare('SELECT * FROM users WHERE correo = ?').get(correo);
        if (!user || !timingSafeEqual(scryptSync(password, user.salt, 64), Buffer.from(user.passwordHash, 'hex'))) {
          throw new ApiError(401, 'Correo o contraseña incorrectos.');
        }
        const token = randomBytes(32).toString('base64url');
        db.prepare('DELETE FROM sessions WHERE expiresAt <= ?').run(Date.now());
        db.prepare('INSERT INTO sessions (tokenHash, userId, expiresAt) VALUES (?, ?, ?)')
          .run(tokenHash(token), user.id, Date.now() + 24 * 60 * 60 * 1000);
        return send(response, 200, { token, userId: user.id });
      }
      if (route === 'GET /api/users') return send(response, 200, [publicUser(authorize(request, url))]);
      if (route === 'GET /api/categories') {
        const userId = authorize(request, url);
        const categories = db.prepare('SELECT * FROM categories WHERE userId = ? ORDER BY id').all(userId);
        return send(response, 200, categories.map((category) => ({ ...category, activo: Boolean(category.activo) })));
      }
      if (route === 'GET /api/transactions') {
        const userId = authorize(request, url);
        return send(response, 200, db.prepare(`
          SELECT t.id, t.cents / 100.0 AS monto, t.fecha, c.tipo, t.descripcion, t.categoriaId, t.userId
          FROM transactions t JOIN categories c ON c.id = t.categoriaId
          WHERE t.userId = ? ORDER BY t.fecha DESC, t.id DESC
        `).all(userId));
      }
      if (route === 'POST /api/transactions') {
        const userId = authorize(request, url);
        const body = await readJson(request);
        const cents = Math.round(body.monto * 100);
        if (typeof body.monto !== 'number' || !Number.isFinite(body.monto) || cents <= 0 || !Number.isSafeInteger(cents) || cents > 100000000000) {
          throw new ApiError(400, 'Ingresa un monto positivo válido.');
        }
        const descripcion = text(body.descripcion, 'descripción', { max: 500 });
        const fecha = text(body.fecha, 'fecha');
        if (!/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/.test(fecha) || !Number.isFinite(Date.parse(fecha))) {
          throw new ApiError(400, 'La fecha debe tener el formato yyyy-MM-ddTHH:mm:ss.');
        }
        if (!Number.isSafeInteger(body.categoriaId) || !db.prepare('SELECT id FROM categories WHERE id = ? AND userId = ? AND activo = 1').get(body.categoriaId, userId)) {
          throw new ApiError(400, 'Selecciona una categoría válida.');
        }
        const { lastInsertRowid: id } = db.prepare('INSERT INTO transactions (cents, fecha, descripcion, categoriaId, userId) VALUES (?, ?, ?, ?, ?)')
          .run(cents, fecha, descripcion, body.categoriaId, userId);
        return send(response, 201, publicTransaction(id));
      }
      throw new ApiError(404, 'Ruta no encontrada.');
    } catch (error) {
      if (!(error instanceof ApiError)) console.error('Error interno de la API:', error.code || error.name);
      send(response, error.status || 500, { message: error.status ? error.message : 'No se pudo completar la operación.' });
    }
  });
  server.requestTimeout = 15000;
  server.on('close', () => db.close());
  return server;
}

if (require.main === module) {
  const host = process.env.HOST || '127.0.0.1';
  const port = Number(process.env.PORT || 8081);
  const server = createApiServer();
  server.on('error', (error) => {
    console.error(error.code === 'EADDRINUSE' ? `El puerto ${port} ya está en uso. Comprueba si el servidor ya está abierto.` : error.message);
    process.exitCode = 1;
  });
  server.listen(port, host, () => {
    console.log(`Servidor Finanzas UDB listo en http://${host}:${port}`);
    console.log('Ya puedes registrarte desde el emulador. Deja esta ventana abierta.');
    console.log('Los datos se guardan en backend/.data/finanzas.sqlite. Ctrl+C para detener.');
  });
  for (const signal of ['SIGINT', 'SIGTERM']) process.on(signal, () => server.close());
}

module.exports = { createApiServer };
