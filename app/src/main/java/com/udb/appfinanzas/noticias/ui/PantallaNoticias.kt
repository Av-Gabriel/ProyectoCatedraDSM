package com.udb.appfinanzas.noticias.ui

import ScaffoldApp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Noticias(){

        Text("Contenido de noticias")
}

// preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NoticiasPreview() {
    MaterialTheme {
        Noticias()
    }
}