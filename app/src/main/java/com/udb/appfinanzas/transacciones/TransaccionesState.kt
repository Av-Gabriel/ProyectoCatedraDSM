package com.udb.appfinanzas.transacciones

sealed class TransaccionesState {
    object Idle : TransaccionesState()
    object Cargando : TransaccionesState()
    data class Exitoso(val transacciones: List<TransactionResponseDTO>) : TransaccionesState()
    data class Error(val mensaje: String) : TransaccionesState()
}