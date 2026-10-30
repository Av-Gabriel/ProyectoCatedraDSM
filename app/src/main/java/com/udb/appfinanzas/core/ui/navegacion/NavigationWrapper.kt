package com.udb.appfinanzas.core.ui.navegacion

import PantallaLogin
import ScaffoldApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavDestination.Companion.hasRoute

import com.udb.appfinanzas.auth.ui.login.LoginState
import com.udb.appfinanzas.auth.ui.login.LoginViewModel
import com.udb.appfinanzas.auth.ui.PantallaRegistro
import com.udb.appfinanzas.auth.ui.registro.RegistroState
import com.udb.appfinanzas.auth.ui.registro.RegistroViewModel
import com.udb.appfinanzas.dashboard.ui.PantallaDashboard
import com.udb.appfinanzas.historial.ui.Historial as HistorialScreen
import com.udb.appfinanzas.movimientos.ui.AgregarMovimiento
import com.udb.appfinanzas.movimientos.ui.AgregarMovimientoViewModel
import com.udb.appfinanzas.movimientos.ui.Movimientos
import com.udb.appfinanzas.noticias.ui.Noticias
import com.udb.appfinanzas.presupuesto.ui.Presupuesto
import com.udb.appfinanzas.transacciones.GuardarTransaccionState

// Asegúrate de importar tu ScaffoldApp correctamente aquí

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    var registroCompletado by rememberSaveable { mutableStateOf(false) }

    // observamos la ruta actual globalmente
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destinoActual = navBackStackEntry?.destination

    // oculto el top y bottom bar en login registro y agregar movimiento
    val mostrarBarras = destinoActual?.hasRoute(Login::class) == false &&
            destinoActual?.hasRoute(Registro::class) == false &&
            destinoActual?.hasRoute(AgregarMovimiento::class) ==false

    // titulo de topbar con la ruta que este seleccionada
    val tituloTopBar = when {
        destinoActual?.hasRoute(Dashboard::class) == true -> "Dashboard"
        destinoActual?.hasRoute(Movimientos::class) == true -> "Mis Movimientos"
        destinoActual?.hasRoute(Presupuesto::class) == true -> "Presupuesto"
        destinoActual?.hasRoute(Noticias::class) == true -> "Noticias"
        destinoActual?.hasRoute(Historial::class) == true -> "Historial"
        else -> "App Finanzas"
    }

    // scaffold
    ScaffoldApp(
        onAgregarClick = { navController.navigate(AgregarMovimiento) },
        navController = navController,
        title = tituloTopBar,
        mostrarTopBar = mostrarBarras,
        mostrarBottomBar = mostrarBarras,
        onLogoutClick = {
            // navegamos al login y destruimos el historial para que no puedan volver atras
            navController.navigate(Login) {
                popUpTo(0)
            }
        },
        onHistorialClick = { navController.navigate(Historial) }
    ) {
        // navhost dentro del scaffold
        NavHost(navController, startDestination = Login) {

            composable<Login> {
                val loginViewModel: LoginViewModel = hiltViewModel()
                val estado by loginViewModel.estado.collectAsState()

                LaunchedEffect(estado) {
                    if (estado is LoginState.Exitoso) {
                        navController.navigate(Dashboard) {
                            // Limpiamos la pantalla de login del stack
                            popUpTo(Login) { inclusive = true }
                        }
                    }
                }
                PantallaLogin(
                    estado = estado,
                    onLoginClick = { email, password -> loginViewModel.login(email, password) },
                    onRegistroClick = { registroCompletado = false; navController.navigate(Registro) },
                    mensajeInformativo = if (registroCompletado) "Cuenta creada. Inicia sesión con tu correo y contraseña." else null
                )
            }

            composable<Registro> {
                val registroViewModel: RegistroViewModel = hiltViewModel()
                val estado by registroViewModel.estado.collectAsState()

                LaunchedEffect(estado) {
                    if (estado is RegistroState.Exitoso) {
                        registroCompletado = true
                        navController.navigate(Login) {
                            popUpTo(Login) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
                PantallaRegistro(
                    onRegistroClick = { nombre, email, password, confirmPassword ->
                        registroViewModel.registrar(nombre, email, password, confirmPassword)
                    },
                    onLoginClick = { navController.popBackStack() },
                    estado = estado
                )
            }

            // pantallas
            composable<Movimientos> {
                Movimientos()
            }

            composable<AgregarMovimiento>{
                val agregarViewModel: AgregarMovimientoViewModel = hiltViewModel()
                val guardarState by agregarViewModel.guardarTransaccionState.collectAsState()

                LaunchedEffect(guardarState) {
                    if(guardarState is GuardarTransaccionState.Exitoso){
                        navController.popBackStack()
                    }
                }
                AgregarMovimiento(viewModel = agregarViewModel)
            }

            composable<Dashboard> {
                PantallaDashboard()
            }

            composable<Noticias> {
                Noticias()
            }

            composable<Historial> {
                HistorialScreen()
            }

            composable<Presupuesto> {
                Presupuesto()
            }
        }
    }
}
