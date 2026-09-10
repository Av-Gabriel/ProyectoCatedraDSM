import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// -------------------------------------------------------------
// SCAFFOLD PRINCIPAL
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldApp(
    title: String,
    navigationIcon: @Composable () -> Unit = {},
    onAgregarMovClick: () -> Unit = {},
    onConfigClick: () -> Unit = {},
    onAtrasClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    tabSeleccionadoInicial: Int = 0,
    content: @Composable () -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }
    var tabSeleccionado by remember { mutableIntStateOf(tabSeleccionadoInicial) }

    Scaffold(
        containerColor = AppTheme.colors.background,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.topBar,
                    titleContentColor = AppTheme.colors.onTopBar,
                ),
                title = { Text(title) },
                navigationIcon = navigationIcon,
                actions = {
                    IconButton(onClick = onPerfilClick) {
                        Icon(Icons.Default.Person, contentDescription = "perfil", tint = AppTheme.colors.onSurfaceMuted)
                    }
                    Box {
                        IconButton(onClick = { menuExpandido = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "menu", tint = AppTheme.colors.onSurfaceMuted)
                        }
                        DropdownMenu(expanded = menuExpandido, onDismissRequest = { menuExpandido = false }) {
                            DropdownMenuItem(
                                text = { Text("Salir") },
                                onClick = { menuExpandido = false; onLogoutClick() },
                                leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Configuracion") },
                                onClick = { menuExpandido = false; onConfigClick() },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Historial") },
                                onClick = { menuExpandido = false; onConfigClick() },
                                leadingIcon = { Icon(Icons.Default.History, contentDescription = null) }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                seleccionado = tabSeleccionado,
                onTabClick = { tabSeleccionado = it },
                onAgregarClick = onAgregarMovClick
            )
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
// BARRA INFERIOR TRADICIONAL — pegada al borde, 5 items con el
// mismo peso (4 navegación + agregar), sin agrupamientos raros.
// -------------------------------------------------------------

private val iconosNav = listOf(
    Icons.Rounded.Home,
    Icons.Outlined.Analytics,
    Icons.Outlined.AttachMoney,
    Icons.Outlined.Newspaper
)

@Composable
private fun BottomNavBar(
    seleccionado: Int,
    onTabClick: (Int) -> Unit,
    onAgregarClick: () -> Unit
) {
    Surface(color = AppTheme.colors.bottomBar) {
        Column {
            Divider(color = AppTheme.colors.divider, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Primeros 2 iconos
                iconosNav.take(2).forEachIndexed { index, icono ->
                    NavIconoItem(
                        icono = icono,
                        activo = seleccionado == index,
                        onClick = { onTabClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // boton central de agregar, mismo peso que el resto,
                // asi la fila queda pareja y no agrupada a un lado.
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        onClick = onAgregarClick,
                        shape = CircleShape,
                        color = AppTheme.colors.iconActivo,
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "agregar movimiento",
                                tint = AppTheme.colors.onAcentoAgregar
                            )
                        }
                    }
                }

                // Últimos 2 iconos
                iconosNav.takeLast(2).forEachIndexed { i, icono ->
                    val index = i + 2
                    NavIconoItem(
                        icono = icono,
                        activo = seleccionado == index,
                        onClick = { onTabClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavIconoItem(
    icono: ImageVector,
    activo: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorIcono by animateColorAsState(
        targetValue = if (activo) AppTheme.colors.iconActivo else AppTheme.colors.iconInactivo,
        label = "colorIcono"
    )
    val colorPunto by animateColorAsState(
        targetValue = if (activo) AppTheme.colors.indicadorActivo else Color.Transparent,
        label = "colorPunto"
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = onClick) {
                Icon(imageVector = icono, contentDescription = null, tint = colorIcono)
            }
            // puntito indicador debajo del icono activo, sin texto
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(colorPunto)
            )
        }
    }
}

// -------------------------------------------------------------
// PREVIEW
// -------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun DashboardPreview() {
    AppTheme {
        ScaffoldApp(title = "Pruebas") {
            Text("Prueba")
        }
    }
}