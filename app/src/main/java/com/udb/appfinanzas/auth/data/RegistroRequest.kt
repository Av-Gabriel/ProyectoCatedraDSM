package com.udb.appfinanzas.auth.data

data class RegistroRequest(
    val nombre: String,
    val apellido: String,
    val nombreUsuario: String,
    val correo: String,
    val contrasena: String,
    val fechaNacimiento: String? = null // formato "yyyy-MM-dd", ej. "2000-05-15"
)