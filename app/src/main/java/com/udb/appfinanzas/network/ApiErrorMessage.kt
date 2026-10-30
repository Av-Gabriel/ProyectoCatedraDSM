package com.udb.appfinanzas.network

import org.json.JSONObject
import retrofit2.Response

fun Response<*>.mensajeDeError(predeterminado: String): String = try {
    val mensaje = JSONObject(errorBody()?.string().orEmpty()).optString("message")
    mensaje.takeIf { it.isNotBlank() && it.length <= 250 } ?: predeterminado
} catch (_: Exception) {
    predeterminado
}
