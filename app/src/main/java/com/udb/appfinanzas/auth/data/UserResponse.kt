package com.udb.appfinanzas.auth.data
//que matchee lo de userResponseDTO en mi backend
data class UserResponse(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val correo: String
)