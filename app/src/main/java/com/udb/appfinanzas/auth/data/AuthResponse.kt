package com.udb.appfinanzas.auth.data

data class AuthResponse(
    val token: String,
    val userId: Long
)