package com.udb.appfinanzas.transacciones

data class TransactionRegistroDTO(
    val monto: Double,
    val fecha: String,
    val descripcion: String,
    val categoriaId: Long
)