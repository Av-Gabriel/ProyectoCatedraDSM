package com.udb.appfinanzas.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udb.appfinanzas.auth.data.LoginRequest
import com.udb.appfinanzas.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _estado = MutableStateFlow<LoginState>(LoginState.Idle)
    val estado: StateFlow<LoginState> = _estado

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _estado.value = LoginState.Cargando

            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))

                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        // TODO: guardar el token con DataStore (siguiente paso)
                        _estado.value = LoginState.Exitoso
                    } else {
                        _estado.value = LoginState.Error("Respuesta invalida del servidor")
                    }
                } else {
                    _estado.value = LoginState.Error("Credenciales invalidas")
                }
            } catch (e: Exception) {
                _estado.value = LoginState.Error("Error de conexion: ${e.message}")
            }
        }
    }
}