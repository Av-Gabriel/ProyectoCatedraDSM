package com.udb.appfinanzas.transacciones

sealed class GuardarTransaccionState {
    object Idle : GuardarTransaccionState()
    object Cargando : GuardarTransaccionState()
    object Exitoso : GuardarTransaccionState()
    data class Error(val mensaje: String) : GuardarTransaccionState()
}