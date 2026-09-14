package com.udb.appfinanzas.movimientos.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udb.appfinanzas.categorias.data.CategoriasState
import com.udb.appfinanzas.categorias.data.CategoryResponse
import com.udb.appfinanzas.core.ui.components.AppDropdownMenu
import com.udb.appfinanzas.core.ui.components.AppFilterChips
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Composable
fun AgregarMovimiento(viewModel: AgregarMovimientoViewModel = hiltViewModel())
{
    val categoriasState by viewModel.categoriasState.collectAsState()
    ContenidoAgregarMovimiento(categoriasState = categoriasState,
    onGuardarClick = {monto, fecha, descripcion, categoriaId ->
        viewModel.guardarTransaccion(monto, fecha, descripcion, categoriaId)
    })
}

// contenido puro sin viewmodel para poder usarlo igual en la pantalla real y en el preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContenidoAgregarMovimiento(categoriasState: CategoriasState,
                                       onGuardarClick:(String, String, String, Long?) -> Unit = { _, _, _, _ -> }) {
    var tipoSeleccionado by remember { mutableStateOf("GASTO") }
    var monto by remember { mutableStateOf("") }
    var fecha by remember {
        mutableStateOf(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
        )
    }
    var descripcion by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<CategoryResponse?>(null) }
    //estado para controlar la visibilidad del datepicker
    var mostrarDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)){
    Column(modifier = Modifier
        .align(Alignment.Center)
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {


        AppFilterChips(listOf("GASTO", "INGRESO"),
            seleccionado = tipoSeleccionado,
            onSeleccionarFiltro = { nuevoTipo -> tipoSeleccionado = nuevoTipo
                                  categoriaSeleccionada = null},
            )


        OutlinedTextField(
            value = monto,
            onValueChange = { monto = it },
            label = { Text("Ingresa el monto") },
            placeholder = {Text("ej: 45.00", fontStyle = FontStyle.Italic)},
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fecha.take(10),
            onValueChange = { },
            readOnly = true,
            label = { Text("Ingresa la Fecha") },
            leadingIcon = {
                IconButton(onClick = { mostrarDatePicker = true }) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Seleccionar Fecha")
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
                .clickable { mostrarDatePicker = true }
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Describe la Transaccion", fontStyle = FontStyle.Italic) },
            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
            singleLine = false,
            modifier = Modifier.fillMaxWidth()
        )
        when (val estado = categoriasState) {
            is CategoriasState.Idle, is CategoriasState.Cargando -> {
                Box(modifier = Modifier.padding(24.dp),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is CategoriasState.Exitoso -> {
                val categorias = estado.categorias
                AppDropdownMenu(
                    selectedValue = categoriaSeleccionada?.nombre ?: "Selecciona una Categoria",
                    opciones = categorias.filter { it.tipo == tipoSeleccionado }.map { it.nombre },
                    onOpcionSeleccionada = { nombreElegido ->
                        categoriaSeleccionada = categorias.find { it.nombre == nombreElegido  && it.tipo == tipoSeleccionado}
                    })

            }

            is CategoriasState.Error -> {
                Text(
                    text = estado.mensaje,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        Button(
            onClick = { onGuardarClick(monto, fecha, descripcion, categoriaSeleccionada?.id) },
            modifier = Modifier.fillMaxWidth()
                .height(48.dp)

        ) {
            Text("Guardar")
        }
    }
}
    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            val fechaSeleccionadaStr = formatter.format(Date(millis))
                            val horaActualStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                            fecha = "${fechaSeleccionadaStr}T${horaActualStr}"
                        }
                        mostrarDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// preview simple, con categorias falsas
// ya no hace falta duplicar los outlinedtextfield aqui, el preview usa el mismo
// ContenidoAgregarMovimiento que usa la pantalla real
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AgregarMovimientosPreview() {
    val categoriasFalsas = CategoriasState.Exitoso(
        listOf(
            CategoryResponse(1, "Comida", "GASTO", null, null, true, 4),
            CategoryResponse(2, "Salario", "INGRESO", null, null, true, 4)
        )
    )

    MaterialTheme {
        ContenidoAgregarMovimiento(categoriasState = categoriasFalsas)
    }
}