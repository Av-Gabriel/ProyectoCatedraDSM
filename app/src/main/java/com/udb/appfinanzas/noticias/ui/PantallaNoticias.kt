package com.udb.appfinanzas.noticias.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private data class Consejo(val categoria: String, val titulo: String, val resumen: String, val lectura: String, val icono: ImageVector, val fondo: Color)
private val consejos = listOf(
    Consejo("AHORRO", "Ahorra con metas pequeñas", "Define una cantidad semanal realista y sepárala antes de empezar a gastar. La constancia suele ser más útil que una meta difícil de sostener.", "3 min de lectura", Icons.Default.Savings, Color(0xFFE7F3ED)),
    Consejo("EDUCACIÓN FINANCIERA", "Organiza tus gastos del mes", "Anota tus gastos fijos y variables. Así podrás reconocer qué categorías consumen más de tu presupuesto y decidir dónde hacer ajustes.", "4 min de lectura", Icons.Default.MenuBook, Color(0xFFEAF0FA)),
    Consejo("INVERSIÓN", "Antes de invertir, crea un fondo", "Un fondo para imprevistos puede ayudarte a cubrir necesidades urgentes sin depender de créditos. Empieza con aportes que se ajusten a tus ingresos.", "5 min de lectura", Icons.Default.AccountBalance, Color(0xFFFFF1DF)),
    Consejo("HÁBITOS", "Compara antes de comprar", "Revisar precios y esperar antes de una compra no esencial puede ayudarte a distinguir una necesidad de un impulso.", "2 min de lectura", Icons.Default.Paid, Color(0xFFF4EAF4))
)

@Composable
fun Noticias() {
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Column(Modifier.padding(bottom = 2.dp)) {
                Text("Aprende a manejar mejor tu dinero", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF20232B))
                Text("Consejos prácticos para tus finanzas personales", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7F8C), modifier = Modifier.padding(top = 4.dp))
            }
        }
        items(consejos) { consejo -> ConsejoCard(consejo) }
    }
}

@Composable
private fun ConsejoCard(consejo: Consejo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Surface(modifier = Modifier.fillMaxWidth().height(94.dp), color = consejo.fondo) {
                Icon(consejo.icono, contentDescription = null, tint = Color(0xFF2E7D63), modifier = Modifier.padding(28.dp))
            }
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(consejo.categoria, style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D63), fontWeight = FontWeight.Bold)
                Text(consejo.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF20232B))
                Text(consejo.resumen, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF626875))
                Text(consejo.lectura, style = MaterialTheme.typography.labelMedium, color = Color(0xFF7A7F8C), modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoticiasPreview() { MaterialTheme { Column(Modifier.background(Color(0xFFF8F9FA)).padding(16.dp)) { Noticias() } } }
