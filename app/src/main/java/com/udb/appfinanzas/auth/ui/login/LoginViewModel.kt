package com.udb.appfinanzas.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _estado = MutableStateFlow<LoginState>(LoginState.Idle)
    val estado: StateFlow<LoginState> = _estado

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _estado.value = LoginState.Cargando //avisa que empezo
            delay(1000)// simulacion de espera esto creo que lo quitamos despues

            //regla temporal de validacion en lo que implementamos backend
            if (email.isNotBlank() && password.length >= 4) {
            _estado.value = LoginState.Exitoso
            } else {
                _estado.value = LoginState.Error("Credenciales Invalidas")
            }
        }
    }
}