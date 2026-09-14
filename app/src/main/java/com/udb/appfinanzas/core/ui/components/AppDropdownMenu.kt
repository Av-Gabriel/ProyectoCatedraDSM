@file:OptIn(ExperimentalMaterial3Api::class)

package com.udb.appfinanzas.core.ui.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier


@Composable
fun AppDropdownMenu(
    selectedValue: String,
    onOpcionSeleccionada: (String) -> Unit,
    opciones: List<String>,
    modifier: Modifier = Modifier,
){
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {expanded = it},
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor())

        ExposedDropdownMenu(expanded = expanded,
            onDismissRequest = {expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(text = {Text(opcion)},
                    onClick = {onOpcionSeleccionada(opcion)
                    expanded = false
                    }
                )
            }
        }
    }
}