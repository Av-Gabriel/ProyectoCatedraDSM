package com.udb.appfinanzas.categorias.data

data class CategoryResponse(
    val id: Long,
    val nombre: String,
    val tipo: String,
    val icono: String?,
    val color: String?,
    val activo: Boolean,
    val userId: Long?
)