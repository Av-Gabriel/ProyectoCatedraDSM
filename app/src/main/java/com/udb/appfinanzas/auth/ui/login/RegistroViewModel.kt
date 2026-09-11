package com.udb.appfinanzas.auth.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udb.appfinanzas.auth.data.RegistroRequest
import com.udb.appfinanzas.network.ApiService
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
        viewModelScope.launch {
            if (password != confirmPassword) {
                _estado.value = RegistroState.Error("Las contraseñas no coinciden")
                return@launch
            }

            _estado.value = RegistroState.Cargando

            try {
                val request = RegistroRequest(
                    nombre = nombre,
                    apellido = "Apellido",              // temporal, falta el campo en PantallaRegistro
                    nombreUsuario = email.substringBefore("@"), // temporal, derivado del email
                    correo = email,
                    contrasena = password,
                    fechaNacimiento = null
                )

                val response = apiService.registrar(request)

                if (response.isSuccessful) {
                    _estado.value = RegistroState.Exitoso
                } else {
                    _estado.value = RegistroState.Error("Error ${response.code()}: revisa los datos")
                }
            } catch (e: Exception) {
                _estado.value = RegistroState.Error("Error de conexion: ${e.message}")
            }
        }
    }
}