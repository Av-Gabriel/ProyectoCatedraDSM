package com.udb.appfinanzas.transacciones

data class TransactionResponseDTO(
    val id: Long,
    val monto: Double,
    val fecha: String, // formato ej. "2026-09-10T18:30:00"
    val tipo: String, //"GASTO" O "INGRESO" yo decido que hacer con uno de esos 2 datos en la UI
    val descripcion: String,
    val categoriaId: Long,
    val userId: Long
)