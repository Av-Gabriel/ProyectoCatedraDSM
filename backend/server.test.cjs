'use strict';

const { test } = require('node:test');
const assert = require('node:assert/strict');
const { mkdtempSync, rmSync } = require('node:fs');
const { tmpdir } = require('node:os');
const { join } = require('node:path');
const { createApiServer } = require('./server.cjs');

async function start(databasePath) {
  const server = createApiServer({ databasePath });
  await new Promise((resolve) => server.listen(0, '127.0.0.1', resolve));
  const baseUrl = `http://127.0.0.1:${server.address().port}`;
  return {
    stop: () => new Promise((resolve) => { server.close(resolve); server.closeIdleConnections(); }),
    request: async (path, { method = 'GET', body, token } = {}) => {
      const response = await fetch(baseUrl + path, {
        method,
        headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
        body: body === undefined ? undefined : JSON.stringify(body)
      });
      return { status: response.status, body: await response.json() };
    }
  };
}

test('registro, autenticación, categorías, movimientos y persistencia', async () => {
  const directory = mkdtempSync(join(tmpdir(), 'finanzas-api-'));
  const databasePath = join(directory, 'test.sqlite');
  let api = await start(databasePath);
  const account = { nombre: 'Usuario de prueba', apellido: 'Local', correo: 'prueba@example.com', contrasena: 'SoloParaPrueba-123' };
  try {
    assert.equal((await api.request('/api/health')).status, 200);
    const registered = await api.request('/api/users', { method: 'POST', body: account });
    assert.equal(registered.status, 201);
    assert.equal(registered.body.correo, account.correo);
    assert.equal(registered.body.passwordHash, undefined);
    assert.equal(registered.body.contrasena, undefined);
    assert.equal((await api.request('/api/users', { method: 'POST', body: { ...account, correo: 'PRUEBA@example.com' } })).status, 409);
    assert.equal((await api.request('/api/auth/login', { method: 'POST', body: { correo: account.correo, contrasena: 'incorrecta' } })).status, 401);
    assert.equal((await api.request('/api/categories?userId=1')).status, 401);
    const login = await api.request('/api/auth/login', { method: 'POST', body: account });
    assert.equal(login.status, 200);
    const { token, userId } = login.body;
    const categories = await api.request(`/api/categories?userId=${userId}`, { token });
    assert.equal(categories.status, 200);
    assert.ok(categories.body.some((category) => category.tipo === 'INGRESO'));
    assert.ok(categories.body.every((category) => category.activo === true && category.userId === userId));
    const categoriaId = categories.body.find((category) => category.tipo === 'GASTO').id;
    const movimiento = { monto: 12.35, fecha: '2026-10-30T10:15:00', descripcion: 'Almuerzo de prueba', categoriaId };
    const saved = await api.request(`/api/transactions?userId=${userId}`, { method: 'POST', token, body: movimiento });
    assert.equal(saved.status, 201);
    assert.equal(saved.body.monto, 12.35);
    assert.equal(saved.body.tipo, 'GASTO');
    assert.equal((await api.request(`/api/transactions?userId=${userId}`, { method: 'POST', token, body: { ...movimiento, monto: -1 } })).status, 400);
    assert.equal((await api.request(`/api/transactions?userId=${userId}`, { method: 'POST', token, body: { ...movimiento, fecha: 'inválida' } })).status, 400);

    const second = await api.request('/api/users', { method: 'POST', body: { ...account, correo: 'otra@example.com' } });
    const secondLogin = await api.request('/api/auth/login', { method: 'POST', body: { ...account, correo: 'otra@example.com' } });
    assert.equal((await api.request(`/api/transactions?userId=${second.body.id}`, { token })).status, 403);
    assert.equal((await api.request(`/api/transactions?userId=${second.body.id}`, { method: 'POST', token: secondLogin.body.token, body: movimiento })).status, 400);
    assert.deepEqual((await api.request(`/api/transactions?userId=${second.body.id}`, { token: secondLogin.body.token })).body, []);

    await api.stop();
    api = await start(databasePath);
    const persisted = await api.request(`/api/transactions?userId=${userId}`, { token });
    assert.equal(persisted.status, 200);
    assert.equal(persisted.body.length, 1);
    assert.equal(persisted.body[0].descripcion, movimiento.descripcion);
    assert.equal((await api.request('/api/auth/login', { method: 'POST', body: account })).status, 200);
  } finally {
    await api.stop();
    rmSync(directory, { recursive: true, force: true });
  }
});

test('categorías propias: validación, tipos, aislamiento y persistencia', async () => {
  const directory = mkdtempSync(join(tmpdir(), 'finanzas-categorias-'));
  const databasePath = join(directory, 'test.sqlite');
  let api = await start(databasePath);
  try {
    const account = { nombre: 'Prueba categorías', correo: 'categorias@example.com', contrasena: 'SoloParaPrueba-123' };
    await api.request('/api/users', { method: 'POST', body: account });
    const { body: { token, userId } } = await api.request('/api/auth/login', { method: 'POST', body: account });
    const path = `/api/categories?userId=${userId}`;
    const body = { nombre: '  Educación  superior  ', tipo: 'GASTO' };
    assert.equal((await api.request(path, { method: 'POST', body })).status, 401);
    const created = await api.request(path, { method: 'POST', token, body });
    assert.equal(created.status, 201);
    assert.equal(created.body.nombre, 'Educación superior');
    assert.equal(created.body.tipo, 'GASTO');
    assert.equal(created.body.activo, true);
    assert.equal(created.body.userId, userId);
    assert.ok(Number.isSafeInteger(created.body.id));
    assert.equal((await api.request(path, { method: 'POST', token, body: { nombre: 'EDUCACIÓN SUPERIOR', tipo: 'GASTO' } })).status, 409);
    assert.equal((await api.request(path, { method: 'POST', token, body: { nombre: 'alimentación', tipo: 'GASTO' } })).status, 409);
    for (const invalid of [
      { nombre: '   ', tipo: 'GASTO' },
      { nombre: 'a'.repeat(61), tipo: 'GASTO' },
      { nombre: 'Salud', tipo: 'OTRO' }
    ]) {
      assert.equal((await api.request(path, { method: 'POST', token, body: invalid })).status, 400);
    }
    const income = await api.request(path, { method: 'POST', token, body: { nombre: 'Educación superior', tipo: 'INGRESO' } });
    assert.equal(income.status, 201);
    assert.equal(income.body.tipo, 'INGRESO');
    const saved = await api.request(`/api/transactions?userId=${userId}`, {
      method: 'POST', token,
      body: { monto: 25.5, fecha: '2026-10-30T11:20:00', descripcion: 'Clases particulares', categoriaId: income.body.id }
    });
    assert.equal(saved.status, 201);
    assert.equal(saved.body.tipo, 'INGRESO');
    assert.equal(saved.body.categoriaId, income.body.id);
    const secondAccount = { ...account, correo: 'otras-categorias@example.com' };
    await api.request('/api/users', { method: 'POST', body: secondAccount });
    const { body: second } = await api.request('/api/auth/login', { method: 'POST', body: secondAccount });
    assert.equal((await api.request(`/api/categories?userId=${second.userId}`, { method: 'POST', token, body })).status, 403);
    assert.equal((await api.request(path, { token: second.token })).status, 403);
    assert.equal((await api.request(`/api/categories?userId=${second.userId}`, { method: 'POST', token: second.token, body })).status, 201);
    await api.stop();
    api = await start(databasePath);
    const persisted = await api.request(path, { token });
    assert.ok(persisted.body.some((category) => category.id === created.body.id && category.nombre === 'Educación superior'));
    assert.ok(persisted.body.some((category) => category.id === income.body.id && category.tipo === 'INGRESO'));
  } finally {
    await api.stop();
    rmSync(directory, { recursive: true, force: true });
  }
});
