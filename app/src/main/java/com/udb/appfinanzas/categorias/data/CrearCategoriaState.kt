package com.udb.appfinanzas.categorias.data

sealed class CrearCategoriaState {
    object Idle : CrearCategoriaState()
    object Cargando : CrearCategoriaState()
    data class Exitoso(val categoria: CategoryResponse) : CrearCategoriaState()
    data class Error(val mensaje: String) : CrearCategoriaState()
}
