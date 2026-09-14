package com.udb.appfinanzas.movimientos.ui


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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Box

import com.udb.appfinanzas.core.ui.components.AppFilterChips
import com.udb.appfinanzas.core.ui.components.AppListItem
import com.udb.appfinanzas.core.ui.components.AppSearchBar
import com.udb.appfinanzas.core.ui.components.AppSummaryCard
import com.udb.appfinanzas.transacciones.TransaccionesState
import com.udb.appfinanzas.transacciones.TransactionResponseDTO

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


// mapeo de TransactionResponseDTO -> Movimiento

private fun mapearMovimiento(
    dto: TransactionResponseDTO,
    viewModel: MovimientosViewModel
): Movimiento {
    val categoria = viewModel.obtenerCategoria(dto.categoriaId)
    val tipoMov = if (dto.tipo == "INGRESO") TipoMovimiento.INGRESO else TipoMovimiento.GASTO

    return Movimiento(
        id = dto.id.toString(),
        titulo = dto.descripcion,
        categoria = categoria?.nombre ?: "Sin categoria",
        monto = dto.monto,
        tipo = tipoMov,
        fecha = dto.fecha.substringBefore("T"),
        hora = dto.fecha.substringAfter("T").take(5),
        icono = mapearIcono(categoria?.icono)
    )
}

// PANTALLA PRINCIPAL

@Composable
fun Movimientos(
    viewModel: MovimientosViewModel = hiltViewModel(),
    onMovimientoClick: (Movimiento) -> Unit = {},

) {
    val estado by viewModel.estado.collectAsState()


    when (val estadoActual = estado) {
        is TransaccionesState.Idle, is TransaccionesState.Cargando -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is TransaccionesState.Exitoso -> {
            val movimientos = estadoActual.transacciones.map { mapearMovimiento(it, viewModel) }
            ContenidoMovimientos(movimientos = movimientos, onMovimientoClick = onMovimientoClick)
        }

        is TransaccionesState.Error -> {
            Text(
                text = estadoActual.mensaje,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}



// CONTENIDO CON REUTILIZABLES

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

    val totalIngresos = movimientos.filter { it.tipo == TipoMovimiento.INGRESO }.sumOf { it.monto }
    val totalGastos = movimientos.filter { it.tipo == TipoMovimiento.GASTO }.sumOf { it.monto }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        AppSummaryCard(
            titulo = "Balance total",
            montoPrincipal = totalIngresos - totalGastos,
            montoIngresos = totalIngresos,
            montoGastos = totalGastos
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppSearchBar(
            query = textoBusqueda,
            onQueryChange = { textoBusqueda = it },
            placeholderText = "Buscar por nombre o categoría..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        AppFilterChips(
            opciones = listOf("Todos", "Ingresos", "Gastos"),
            seleccionado = filtroSeleccionado,
            onSeleccionarFiltro = { filtroSeleccionado = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

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
// PREVIEW (sin ViewModel, con datos falsos, para que siga funcionando en el editor)
// -------------------------------------------------------------
private val movimientosPreview = listOf(
    Movimiento("1", "Supermercado Walmart", "Comida", 125.50, TipoMovimiento.GASTO, "Hoy", "18:20", Icons.Default.ArrowBackIosNew)
)

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MovimientosPreview() {
    MaterialTheme {
        ContenidoMovimientos(movimientos = movimientosPreview, onMovimientoClick = {})
    }
}