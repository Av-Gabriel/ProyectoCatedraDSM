package com.udb.appfinanzas.presupuesto.ui

import ScaffoldApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.udb.appfinanzas.core.ui.BarraMovimiento

@Composable
fun Presupuesto(){
ScaffoldApp(title = "Presupuestos",
    //puse boton de "atras" a pantallas que no sean el dashboard
    navigationIcon = { Icon(Icons.Default.ArrowBackIosNew, contentDescription = "atras") })
{
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BarraMovimiento(
            nombreCategoria = "Comida",
            montoGastado = 100.00,
            montoTotal = 300.00,
            modifier = Modifier.weight(1f)
        )
        BarraMovimiento(
            nombreCategoria = "Ocio",
            montoGastado = 94.00,
            montoTotal = 150.00,
            modifier = Modifier.weight(1f)
        )
    }
}

}

//Preview
@Preview(showBackground = true,
    showSystemUi = true)
@Composable
private fun PresupuestosPreview(){
    MaterialTheme{
        Presupuesto()
    }
}