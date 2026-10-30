package com.udb.appfinanzas.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udb.appfinanzas.core.ui.ActualizarAlReanudar
import com.udb.appfinanzas.movimientos.ui.MovimientosViewModel
import com.udb.appfinanzas.transacciones.TransaccionesState
import java.text.NumberFormat
import java.util.Locale

private val Verde = Color(0xFF2E7D63)
private val Tinta = Color(0xFF20232B)
private val Gris = Color(0xFF7A7F8C)

@Composable
fun PantallaDashboard(viewModel: MovimientosViewModel = hiltViewModel()) {
    val estado by viewModel.estado.collectAsState()
    ActualizarAlReanudar { viewModel.cargarTodo() }
    when (val actual = estado) {
        is TransaccionesState.Exitoso -> ContenidoDashboard(
            ingresos = actual.transacciones.filter { it.tipo == "INGRESO" }.sumOf { it.monto },
            gastos = actual.transacciones.filter { it.tipo == "GASTO" }.sumOf { it.monto }
        )
        is TransaccionesState.Error -> Column {
            Text(actual.mensaje, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { viewModel.cargarTodo() }) { Text("Reintentar") }
        }
        else -> CircularProgressIndicator()
    }
}

@Composable
private fun ContenidoDashboard(ingresos: Double, gastos: Double) {
    val formato = NumberFormat.getCurrencyInstance(Locale.US)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("Resumen de tus finanzas", style = MaterialTheme.typography.bodyMedium, color = Gris)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Verde)
        ) {
            Column(Modifier.padding(22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White)
                    Text("  Saldo disponible", color = Color.White.copy(alpha = .86f), style = MaterialTheme.typography.titleSmall)
                }
                Spacer(Modifier.height(12.dp))
                Text(formato.format(ingresos - gastos), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ResumenImporte("Ingresos", formato.format(ingresos), Icons.Default.ArrowDownward, Modifier.weight(1f))
                    ResumenImporte("Gastos", formato.format(gastos), Icons.Default.ArrowUpward, Modifier.weight(1f))
                }
            }
        }

        Text("Planifica tu mes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Tinta)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFE8F3EE)) {
                    Icon(Icons.Default.Savings, contentDescription = null, tint = Verde, modifier = Modifier.padding(12.dp))
                }
                Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                    Text("Organiza tu presupuesto", fontWeight = FontWeight.SemiBold, color = Tinta)
                    Text("Define límites por categoría antes de gastar", color = Gris, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5E8))
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Un hábito para empezar", color = Color(0xFF8A5A16), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("Registra tus ingresos y gastos con el botón +. Tu saldo se actualizará con los movimientos guardados.", color = Tinta, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ResumenImporte(titulo: String, monto: String, icono: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.padding(end = 6.dp))
        Column {
            Text(titulo, color = Color.White.copy(alpha = .78f), style = MaterialTheme.typography.labelSmall)
            Text(monto, color = Color.White, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() { MaterialTheme { ContenidoDashboard(1000.0, 500.0) } }
