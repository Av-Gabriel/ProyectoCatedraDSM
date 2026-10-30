package com.udb.appfinanzas.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udb.appfinanzas.auth.data.LoginRequest
import com.udb.appfinanzas.core.data.TokenManager
import com.udb.appfinanzas.network.ApiService
import com.udb.appfinanzas.network.mensajeDeError
import kotlinx.coroutines.CancellationException
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _estado = MutableStateFlow<LoginState>(LoginState.Idle)
    val estado: StateFlow<LoginState> = _estado

    fun login(email: String, password: String) {
        if (_estado.value is LoginState.Cargando) return
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _estado.value = LoginState.Error("Completa el correo y la contraseña")
                return@launch
            }
            _estado.value = LoginState.Cargando
            try {
                val response = apiService.login(LoginRequest(email.trim(), password))
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    val userId = response.body()?.userId
                    if (token != null && userId != null) {
                        tokenManager.saveToken(token)
                        tokenManager.saveUserId(userId)
                        _estado.value = LoginState.Exitoso
                    } else {
                        _estado.value = LoginState.Error("Respuesta invalida del servidor")
                    }
                } else {
                    _estado.value = LoginState.Error(response.mensajeDeError("No se pudo iniciar sesión (HTTP ${response.code()})"))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: IOException) {
                _estado.value = LoginState.Error("No se pudo conectar. Abre iniciar-servidor.cmd en tu PC y vuelve a intentar.")
            } catch (_: Exception) {
                _estado.value = LoginState.Error("No se pudo procesar la respuesta del servidor")
            }
        }
    }
}
