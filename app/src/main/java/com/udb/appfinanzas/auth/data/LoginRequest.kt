package com.udb.appfinanzas.auth.data

data class LoginRequest(
    val correo: String,
    val contrasena: String
)