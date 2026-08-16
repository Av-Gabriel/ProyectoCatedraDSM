package com.udb.appfinanzas.movimientos.ui

import ScaffoldApp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun Movimientos(){
ScaffoldApp(title = "Movimientos",
    //puse boton de "atras" a pantallas que no sean el dashboard
    navigationIcon = { Icon(Icons.Default.ArrowBackIosNew, contentDescription = "atras") })
{
    Text("Contenido de movimientos")
}
}


//Preview
@Preview(showBackground = true,
    showSystemUi = true)
@Composable
private fun MovimientosPreview(){
    MaterialTheme{
        Movimientos()
    }
}