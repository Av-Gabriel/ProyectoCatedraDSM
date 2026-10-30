package com.udb.appfinanzas.historial.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.udb.appfinanzas.core.ui.ActualizarAlReanudar
import com.udb.appfinanzas.movimientos.ui.MovimientosViewModel
import com.udb.appfinanzas.transacciones.TransaccionesState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import java.text.NumberFormat
import java.util.Locale

private data class RegistroHistorial(val titulo: String, val categoria: String, val fecha: String, val monto: String, val ingreso: Boolean, val icono: ImageVector)
private val registrosDemo = listOf(
    RegistroHistorial("Pago quincena", "Salario", "12 ago 2026", "+$300.00", true, Icons.Default.ArrowDownward),
    RegistroHistorial("Almuerzo", "Alimentación", "12 ago 2026", "−$8.50", false, Icons.Default.ArrowUpward),
    RegistroHistorial("Pasaje de bus", "Transporte", "11 ago 2026", "−$1.00", false, Icons.Default.ArrowUpward),
    RegistroHistorial("Compra semanal", "Alimentación", "10 ago 2026", "−$32.75", false, Icons.Default.ArrowUpward)
)

@Composable
fun Historial(viewModel: MovimientosViewModel = hiltViewModel()) {
    val estado by viewModel.estado.collectAsState()
    ActualizarAlReanudar { viewModel.cargarTodo() }
    when (val actual = estado) {
        is TransaccionesState.Exitoso -> ContenidoHistorial(actual.transacciones.map { registro ->
            val ingreso = registro.tipo == "INGRESO"
            RegistroHistorial(
                titulo = registro.descripcion,
                categoria = viewModel.obtenerCategoria(registro.categoriaId)?.nombre ?: "Sin categoría",
                fecha = registro.fecha.substringBefore("T"),
                monto = (if (ingreso) "+" else "−") + NumberFormat.getCurrencyInstance(Locale.US).format(registro.monto),
                ingreso = ingreso,
                icono = if (ingreso) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward
            )
        })
        is TransaccionesState.Error -> Column {
            Text(actual.mensaje, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { viewModel.cargarTodo() }) { Text("Reintentar") }
        }
        else -> CircularProgressIndicator()
    }
}

@Composable
private fun ContenidoHistorial(registros: List<RegistroHistorial>) {
    var busqueda by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Todos") }
    val filtrados = registros.filter { registro ->
        val coincideTexto = registro.titulo.contains(busqueda, true) || registro.categoria.contains(busqueda, true)
        val coincideTipo = when (filtro) {
            "Ingresos" -> registro.ingreso
            "Gastos" -> !registro.ingreso
            else -> true
        }
        coincideTexto && coincideTipo
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Consulta tus movimientos guardados", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF7A7F8C))
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Buscar por descripción o categoría") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todos", "Ingresos", "Gastos").forEach { opcion ->
                FilterChip(selected = filtro == opcion, onClick = { filtro = opcion }, label = { Text(opcion) })
            }
        }
        Text("Todos tus movimientos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF20232B), modifier = Modifier.padding(top = 4.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtrados) { registro -> RegistroCard(registro) }
            if (filtrados.isEmpty()) item {
                Text("No hay movimientos que coincidan con la búsqueda.", color = Color(0xFF7A7F8C), modifier = Modifier.padding(vertical = 20.dp))
            }
        }
    }
}

@Composable
private fun RegistroCard(registro: RegistroHistorial) {
    val acento = if (registro.ingreso) Color(0xFF2E7D63) else Color(0xFFD05A5A)
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(registro.icono, contentDescription = null, tint = acento)
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(registro.titulo, fontWeight = FontWeight.SemiBold, color = Color(0xFF20232B))
                Text("${registro.categoria} · ${registro.fecha}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7F8C))
            }
            Text(registro.monto, fontWeight = FontWeight.Bold, color = acento)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistorialPreview() { MaterialTheme { ContenidoHistorial(registrosDemo) } }
