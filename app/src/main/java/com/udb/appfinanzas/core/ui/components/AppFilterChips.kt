
package com.udb.appfinanzas.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppFilterChips(
    opciones: List<String>,
    seleccionado: String,
    onSeleccionarFiltro: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.forEach { opcion ->
            val esSeleccionado = seleccionado == opcion
            val bgColor by animateColorAsState(
                targetValue = if (esSeleccionado) Color(0xFF1E293B) else Color.White,
                label = "bgColor"
            )
            val textColor by animateColorAsState(
                targetValue = if (esSeleccionado) Color.White else Color(0xFF64748B),
                label = "textColor"
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = bgColor,
                shadowElevation = if (esSeleccionado) 1.dp else 0.dp,
                modifier = Modifier.clickable { onSeleccionarFiltro(opcion) }
            ) {
                Text(
                    text = opcion,
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}