package com.udb.appfinanzas.auth.ui.registro

sealed class RegistroState {
    object Idle : RegistroState()
    object Cargando : RegistroState()
    object Exitoso : RegistroState()
    data class Error(val mensaje: String) : RegistroState()
}