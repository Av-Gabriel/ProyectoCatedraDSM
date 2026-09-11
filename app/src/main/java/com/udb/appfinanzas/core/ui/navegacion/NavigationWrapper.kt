package com.udb.appfinanzas.core.ui.navegacion

import PantallaLogin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.udb.appfinanzas.auth.ui.login.LoginState
import com.udb.appfinanzas.auth.ui.login.LoginViewModel
import com.udb.appfinanzas.auth.ui.PantallaRegistro
import com.udb.appfinanzas.auth.ui.registro.RegistroState
import com.udb.appfinanzas.auth.ui.registro.RegistroViewModel
import com.udb.appfinanzas.dashboard.ui.PantallaDashboard

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Login) {

        composable<Login> {
            val loginViewModel: LoginViewModel = hiltViewModel()
            val estado by loginViewModel.estado.collectAsState()

            LaunchedEffect(estado) {
                if (estado is LoginState.Exitoso) {
                    navController.navigate(Dashboard)
                }
            }
            PantallaLogin(
                estado = estado,
                onLoginClick = { email, password -> loginViewModel.login(email, password) },
                onRegistroClick = { navController.navigate(Registro) }
            )
        }

        composable<Registro> {
            val registroViewModel: RegistroViewModel = hiltViewModel()
            val estado by registroViewModel.estado.collectAsState()

            LaunchedEffect(estado) {
                if (estado is RegistroState.Exitoso) {
                    navController.navigate(Login)
                }
            }
            PantallaRegistro(
                onRegistroClick = { nombre, email, password, confirmPassword ->
                    registroViewModel.registrar(nombre, email, password, confirmPassword)
                },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable<Dashboard> {
            PantallaDashboard()
        }
    }
}