package com.udb.appfinanzas.movimientos.ui

import ScaffoldApp
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Importación de componentes reutilizables desde Core
import com.udb.appfinanzas.core.ui.components.AppFilterChips
import com.udb.appfinanzas.core.ui.components.AppListItem
import com.udb.appfinanzas.core.ui.components.AppSearchBar
import com.udb.appfinanzas.core.ui.components.AppSummaryCard

// -------------------------------------------------------------
// MODELO DE DATOS Y DATOS DE PRUEBA
// -------------------------------------------------------------
enum class TipoMovimiento { INGRESO, GASTO }

data class Movimiento(
    val id: String,
    val titulo: String,
    val categoria: String,
    val monto: Double,
    val tipo: TipoMovimiento,
    val fecha: String,
    val hora: String,
    val icono: ImageVector
)

private val movimientosEjemplo = listOf(
    Movimiento("1", "Supermercado Walmart", "Comida", 125.50, TipoMovimiento.GASTO, "Hoy", "18:20", Icons.Default.ShoppingBag),
    Movimiento("2", "Pago de Salario", "Trabajo", 2500.00, TipoMovimiento.INGRESO, "Hoy", "09:00", Icons.Default.Work),
    Movimiento("3", "Suscripción Netflix", "Entretenimiento", 15.99, TipoMovimiento.GASTO, "Ayer", "20:15", Icons.Default.Tv),
    Movimiento("4", "Transferencia Recibida", "Personal", 50.00, TipoMovimiento.INGRESO, "Ayer", "11:30", Icons.Default.SwapHoriz),
    Movimiento("5", "Restaurante Italia", "Restaurante", 68.00, TipoMovimiento.GASTO, "10 Oct 2024", "21:00", Icons.Default.Restaurant)
)

// -------------------------------------------------------------
// PANTALLA PRINCIPAL
// -------------------------------------------------------------
@Composable
fun Movimientos(
    onAtrasClick: () -> Unit = {},
    onAgregarMovClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onConfigClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onMovimientoClick: (Movimiento) -> Unit = {}
) {
    ScaffoldApp(
        title = "Movimientos",
        navigationIcon = {
            IconButton(onClick = onAtrasClick) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "atras")
            }
        },
        onAgregarMovClick = onAgregarMovClick,
        onPerfilClick = onPerfilClick,
        onConfigClick = onConfigClick,
        onLogoutClick = onLogoutClick
    ) {
        ContenidoMovimientos(
            movimientos = movimientosEjemplo,
            onMovimientoClick = onMovimientoClick
        )
    }
}

// -------------------------------------------------------------
// CONTENIDO CON REUTILIZABLES
// -------------------------------------------------------------
@Composable
private fun ContenidoMovimientos(
    movimientos: List<Movimiento>,
    onMovimientoClick: (Movimiento) -> Unit
) {
    var filtroSeleccionado by remember { mutableStateOf("Todos") }
    var textoBusqueda by remember { mutableStateOf("") }

    val movimientosFiltrados = movimientos.filter { mov ->
        val cumpleFiltro = when (filtroSeleccionado) {
            "Ingresos" -> mov.tipo == TipoMovimiento.INGRESO
            "Gastos" -> mov.tipo == TipoMovimiento.GASTO
            else -> true
        }
        val cumpleBusqueda = mov.titulo.contains(textoBusqueda, ignoreCase = true) ||
                mov.categoria.contains(textoBusqueda, ignoreCase = true)
        cumpleFiltro && cumpleBusqueda
    }

    val agrupadosPorFecha = movimientosFiltrados.groupBy { it.fecha }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // carta de balances
        AppSummaryCard(
            titulo = "Balance total",
            montoPrincipal = 2358.51,
            montoIngresos = 2550.00,
            montoGastos = 209.49
        )

        Spacer(modifier = Modifier.height(16.dp))

        // barra de busqueda
        AppSearchBar(
            query = textoBusqueda,
            onQueryChange = { textoBusqueda = it },
            placeholderText = "Buscar por nombre o categoría..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // filtros (todos, ingresos gastos)
        AppFilterChips(
            opciones = listOf("Todos", "Ingresos", "Gastos"),
            seleccionado = filtroSeleccionado,
            onSeleccionarFiltro = { filtroSeleccionado = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // lista agrupada
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            agrupadosPorFecha.forEach { (fecha, listaMovs) ->
                item {
                    Text(
                        text = fecha,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                    )
                }
                items(listaMovs, key = { it.id }) { mov ->
                    val esIngreso = mov.tipo == TipoMovimiento.INGRESO
                    val signo = if (esIngreso) "+" else "-"

                    // fila de elemento
                    AppListItem(
                        titulo = mov.titulo,
                        subtitulo = "${mov.categoria} • ${mov.hora}",
                        valorTextoDerecha = "$signo$${String.format("%.2f", mov.monto)}",
                        icono = mov.icono,
                        colorIcono = if (esIngreso) Color(0xFF059669) else Color(0xFFDC2626),
                        colorFondoIcono = if (esIngreso) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                        colorValorDerecha = if (esIngreso) Color(0xFF059669) else Color(0xFFDC2626),
                        onClick = { onMovimientoClick(mov) }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PREVIEW
// -------------------------------------------------------------
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MovimientosPreview() {
    MaterialTheme {
        Movimientos()
    }
}