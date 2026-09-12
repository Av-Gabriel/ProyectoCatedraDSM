package com.udb.appfinanzas.historial.ui
import ScaffoldApp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Historial(){


        Text("contenido de historial")
    }



//Preview
@Preview(showBackground = true,
    showSystemUi = true)
@Composable
private fun HistorialPreview(){
    MaterialTheme() {
        Historial()
    }
}