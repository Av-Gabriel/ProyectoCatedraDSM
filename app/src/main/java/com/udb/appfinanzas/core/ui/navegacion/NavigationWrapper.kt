package com.udb.appfinanzas.core.ui.navegacion

import PantallaLogin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.udb.appfinanzas.auth.ui.login.LoginState
import com.udb.appfinanzas.auth.ui.login.LoginViewModel

import com.udb.appfinanzas.dashboard.ui.PantallaDashboard

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Login){
        composable<Login>{
            val loginViewModel: LoginViewModel = viewModel()
            val estado by loginViewModel.estado.collectAsState()

            LaunchedEffect(estado) {
                if (estado is LoginState.Exitoso){
                    navController.navigate(Dashboard)
                }
            }
            PantallaLogin(estado = estado, onLoginClick = {email, password -> loginViewModel.login(email, password)})

        }
        composable<Dashboard>{
            PantallaDashboard()
        }
    }
}