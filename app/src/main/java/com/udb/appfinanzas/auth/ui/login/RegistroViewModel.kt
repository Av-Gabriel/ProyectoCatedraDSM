package com.udb.appfinanzas.auth.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udb.appfinanzas.auth.data.RegistroRequest
import com.udb.appfinanzas.network.ApiService
import com.udb.appfinanzas.network.mensajeDeError
import kotlinx.coroutines.CancellationException
import java.io.IOException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _estado = MutableStateFlow<RegistroState>(RegistroState.Idle)
    val estado: StateFlow<RegistroState> = _estado

    fun registrar(nombre: String, email: String, password: String, confirmPassword: String) {
        if (_estado.value is RegistroState.Cargando) return
        viewModelScope.launch {
            val nombreLimpio = nombre.trim()
            val correoLimpio = email.trim()
            if (nombreLimpio.isBlank() || correoLimpio.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                _estado.value = RegistroState.Error("Completa todos los campos para continuar")
                return@launch
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
                _estado.value = RegistroState.Error("Ingresa un correo electrónico válido")
                return@launch
            }

            if (password != confirmPassword) {
                _estado.value = RegistroState.Error("Las contraseñas no coinciden")
                return@launch
            }

            if (password.length !in 8..128) {
                _estado.value = RegistroState.Error("La contraseña debe tener entre 8 y 128 caracteres")
                return@launch
            }

            _estado.value = RegistroState.Cargando

            try {
                val request = RegistroRequest(
                    nombre = nombreLimpio,
                    apellido = "", // El formulario actual solicita el nombre completo en un solo campo
                    nombreUsuario = correoLimpio.substringBefore("@"), // temporal, derivado del email
                    correo = correoLimpio,
                    contrasena = password,
                    fechaNacimiento = null
                )

                val response = apiService.registrar(request)

                if (response.isSuccessful) {
                    _estado.value = RegistroState.Exitoso
                } else {
                    val mensaje = when (response.code()) {
                        409 -> "Ya existe una cuenta con ese correo. Prueba iniciar sesión."
                        400 -> "El servidor rechazó los datos. Revisa la información e intenta de nuevo."
                        else -> "No se pudo crear la cuenta (HTTP ${response.code()}). Intenta de nuevo."
                    }
                    _estado.value = RegistroState.Error(response.mensajeDeError(mensaje))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: IOException) {
                _estado.value = RegistroState.Error(
                    "No se pudo conectar. Abre iniciar-servidor.cmd en tu PC y vuelve a intentar."
                )
            } catch (_: Exception) {
                _estado.value = RegistroState.Error("No se pudo procesar la respuesta del servidor")
            }
        }
    }
}
