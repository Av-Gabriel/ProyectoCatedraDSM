package com.udb.appfinanzas.auth.ui.login

sealed class LoginState {
    object Idle : LoginState()
    object Cargando : LoginState()
    object Exitoso : LoginState()
    data class Error(val mensaje: String) : LoginState()
}