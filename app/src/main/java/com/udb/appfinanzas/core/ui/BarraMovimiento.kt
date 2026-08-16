package com.udb.appfinanzas.core.ui

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import android.R.attr.shape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp



@Composable

fun BarraMovimiento(nombreCategoria: String,
                    montoGastado: Double,
                    montoTotal: Double,
                    modifier: Modifier = Modifier
){
    //variable temporal porcentaje en lo que usamos una bd
    val porcentaje = if (montoTotal > 0)(montoGastado / montoTotal *100).toInt()
    else 0
    //variable temporal de saldo restante en lo que usamos una bd
    val saldoRestante = (montoTotal - montoGastado)

    Card(modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors()) {
        Column(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(nombreCategoria)
            Box(contentAlignment = Alignment.Center)
            {CircularProgressIndicator(progress = { if (montoTotal > 0)(montoGastado / montoTotal).toFloat() else 0f},
                modifier = Modifier.size(64.dp))
            Text("${porcentaje}%", style = MaterialTheme.typography.labelSmall)}
            Text("$${saldoRestante}", style = MaterialTheme.typography.labelLarge)


        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BarraMovimientoPreview() {
    Box(modifier = Modifier.width(160.dp)) {
        BarraMovimiento(
            nombreCategoria = "Ocio",
            montoGastado = 100.0,
            montoTotal = 133.0
        )
    }
}