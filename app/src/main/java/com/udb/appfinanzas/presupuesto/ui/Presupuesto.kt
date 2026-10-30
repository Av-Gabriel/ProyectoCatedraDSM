package com.udb.appfinanzas.presupuesto.ui

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
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private data class PresupuestoItem(val nombre: String, val gastado: Double, val limite: Double, val icono: ImageVector)
private val presupuestosDemo = listOf(
    PresupuestoItem("Alimentación", 180.0, 200.0, Icons.Default.Restaurant),
    PresupuestoItem("Transporte", 40.0, 100.0, Icons.Default.DirectionsBus),
    PresupuestoItem("Ocio", 95.0, 100.0, Icons.Default.FavoriteBorder)
)

@Composable
fun Presupuesto() {
    val totalGastado = presupuestosDemo.sumOf { it.gastado }
    val totalLimite = presupuestosDemo.sumOf { it.limite }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D63))
            ) {
                Row(Modifier.fillMaxWidth().padding(20.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("Ejemplo de límite mensual", color = Color.White.copy(alpha = .82f), style = MaterialTheme.typography.labelLarge)
                        Text("\$${"%.2f".format(totalLimite)}", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("\$${"%.2f".format(totalGastado)} utilizados", color = Color.White.copy(alpha = .9f), style = MaterialTheme.typography.bodyMedium)
                    }
                    androidx.compose.material3.Icon(Icons.Default.Savings, contentDescription = null, tint = Color.White)
                }
            }
        }
        item {
            Text("Presupuestos por categoría", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
        }
        items(presupuestosDemo) { item -> PresupuestoCard(item) }
        item {
            Text("Diseño de ejemplo. Estos límites no corresponden a los movimientos de tu cuenta.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7F8C), modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
private fun PresupuestoCard(item: PresupuestoItem) {
    val progreso = (item.gastado / item.limite).toFloat().coerceIn(0f, 1f)
    val excedido = item.gastado >= item.limite
    val acento = if (excedido) Color(0xFFC45151) else Color(0xFF2E7D63)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth()) {
                androidx.compose.material3.Icon(item.icono, contentDescription = null, tint = acento)
                Text(item.nombre, modifier = Modifier.weight(1f).padding(start = 10.dp), fontWeight = FontWeight.SemiBold)
                Text("${(progreso * 100).toInt()}%", color = acento, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(progress = { progreso }, modifier = Modifier.fillMaxWidth(), color = acento, trackColor = Color(0xFFE9ECEB))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Gastado \$${"%.2f".format(item.gastado)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7F8C))
                Text("Límite \$${"%.2f".format(item.limite)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7F8C))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PresupuestosPreview() { MaterialTheme { Presupuesto() } }
