package com.udb.appfinanzas.categorias.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udb.appfinanzas.categorias.data.CrearCategoriaState
import com.udb.appfinanzas.core.ui.components.AppFilterChips

@Composable
fun CrearCategoriaDialog(
    tipoInicial: String,
    estado: CrearCategoriaState,
    onCrear: (String, String) -> Unit,
    onCerrar: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var tipo by rememberSaveable { mutableStateOf(tipoInicial) }
    val guardando = estado is CrearCategoriaState.Cargando

    AlertDialog(
        onDismissRequest = { if (!guardando) onCerrar() },
        title = { Text("Nueva categoría") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Organiza tus movimientos con tus propias categorías.")
                AppFilterChips(
                    opciones = listOf("GASTO", "INGRESO"),
                    seleccionado = tipo,
                    onSeleccionarFiltro = { if (!guardando) tipo = it }
                )
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { if (it.length <= 60) nombre = it },
                    label = { Text("Nombre de la categoría") },
                    placeholder = { Text("Ej.: Salud, Educación o Ventas") },
                    supportingText = { Text("${nombre.length}/60 caracteres") },
                    singleLine = true,
                    enabled = !guardando,
                    modifier = Modifier.fillMaxWidth()
                )
                if (estado is CrearCategoriaState.Error) {
                    Text(estado.mensaje, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onCrear(nombre, tipo) }, enabled = nombre.isNotBlank() && !guardando) {
                Text(if (guardando) "Guardando..." else "Crear categoría")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar, enabled = !guardando) { Text("Cancelar") }
        }
    )
}
