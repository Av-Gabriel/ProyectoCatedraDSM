package com.udb.appfinanzas.categorias.data

sealed class CategoriasState {
    object Idle : CategoriasState()
    object Cargando : CategoriasState()
    data class Exitoso(val categorias: List<CategoryResponse>) : CategoriasState()
    data class Error(val mensaje: String) : CategoriasState()
}