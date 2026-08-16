import android.R
import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldApp(title: String,
                navigationIcon: @Composable () -> Unit = {},
                onAgregarMovClick:() -> Unit = {},
                onConfigClick:() -> Unit = {},
                onAtrasClick: () -> Unit ={},
                onLogoutClick: () -> Unit = {},
                onPerfilClick: () -> Unit = {},
                content: @Composable () -> Unit) {
    var menuExpandido by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(title)
                },
                //CODIGO PARA OTRAS PANTALLAS DONDE SE NECESITE BACK QUE NO SEA EN EL DASHBOARD
                /*navigationIcon = {
                    IconButton(onClick = onAtrasClick) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "perfil")
                    }
                },*/
                navigationIcon = navigationIcon,
                actions = {
                    //boton perfil al lado de los 3 puntos
                    IconButton(onClick = onPerfilClick) {Icon(Icons.Default.Person, contentDescription = "perfil") }
                    //box para menu 3 puntos
                    Box{
                        //boton 3 puntos
                        IconButton(onClick = {menuExpandido = true}) {
                            Icon(Icons.Default.MoreVert, contentDescription = "menu 2")
                        }
                        //menu desplegable
                        DropdownMenu(expanded = menuExpandido, onDismissRequest = {menuExpandido = false})
                        {
                            //opcion perfil
                            DropdownMenuItem(text = {Text("Salir")}, onClick = {
                                menuExpandido = false
                                onConfigClick()
                            }, leadingIcon = {
                                Icon(Icons.Default.Logout, contentDescription = null)
                            }
                            )
                            //boton configuracion
                            DropdownMenuItem(text = {Text("Configuracion")}, onClick = {
                                menuExpandido = false
                                onConfigClick()
                            }, leadingIcon = {Icon(Icons.Default.Settings, contentDescription = null)})
                            //boton historial
                            DropdownMenuItem(text = {Text("Historial")}, onClick = {
                                menuExpandido = false
                                onConfigClick()
                            }, leadingIcon = {Icon(Icons.Default.History, contentDescription = null)})
                        }
                    }
                }

            )
        },
        bottomBar = {
                NavigationBar {
                    //boton home
                    NavigationBarItem(
                        selected = true,
                        onClick = {/* navegar a Dashboard */},
                        icon = {Icon(Icons.Default.Home, contentDescription = "Inicio")},
                        label = {Text("Inicio")}
                    )
                    //boton movimientos
                    NavigationBarItem(
                        selected = false,
                        onClick = {/* navegar a Historial */},
                        icon = {Icon(Icons.Filled.Analytics, contentDescription = "movimientos")},
                        label = {Text("Movimientos")}
                    )
                    //boton presupuestos
                    NavigationBarItem(
                        selected = false,
                        onClick = {/* navegar a Presupuesto */},
                        icon = {Icon(Icons.Filled.AttachMoney, contentDescription = "presupuestos")},
                        label = {Text("Presupuestos", style = MaterialTheme.typography.labelSmall)}
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {/* navegar a Presupuesto */},
                        icon = {Icon(Icons.Filled.Newspaper, contentDescription = "noticias")},
                        label = {Text("Noticias")}
                    )
                }

        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            content()
        }

    }
}

// -------------------------------------------------------------
// FUNCIÓN DE PREVIEW PARA PANTALLAS COMPLETAS
// -------------------------------------------------------------

@Preview(
    showBackground = true,
    showSystemUi = true // Muestra la barra de estado superior y la de navegación inferior del teléfono
)
@Composable
private fun DashboardPreview() {
    // Envolvemos en MaterialTheme para que cargue la paleta de colores de la TopAppBar
    MaterialTheme {
        ScaffoldApp(title = "Pruebas"){
            Text("Prueba")
        }
    }
}