package com.udb.appfinanzas.movimientos.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udb.appfinanzas.categorias.data.CategoriasState
import com.udb.appfinanzas.categorias.data.CategoryResponse
import com.udb.appfinanzas.categorias.data.CategoryRegistroRequest
import com.udb.appfinanzas.categorias.data.CrearCategoriaState
import com.udb.appfinanzas.core.data.TokenManager
import com.udb.appfinanzas.network.ApiService
import com.udb.appfinanzas.transacciones.GuardarTransaccionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.udb.appfinanzas.transacciones.TransactionRegistroDTO
import com.udb.appfinanzas.network.mensajeDeError
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import java.io.IOException

@HiltViewModel
class AgregarMovimientoViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel(){
    private val _categoriasState = MutableStateFlow<CategoriasState>(CategoriasState.Idle)
    val categoriasState: StateFlow<CategoriasState> = _categoriasState
    private var categoriasPorId: Map<Long, CategoryResponse> = emptyMap()
    private val _guardarTransaccionState= MutableStateFlow<GuardarTransaccionState>(GuardarTransaccionState.Idle)
    val guardarTransaccionState: StateFlow<GuardarTransaccionState> = _guardarTransaccionState
    private val _crearCategoriaState = MutableStateFlow<CrearCategoriaState>(CrearCategoriaState.Idle)
    val crearCategoriaState: StateFlow<CrearCategoriaState> = _crearCategoriaState

    init {
        cargarCategorias()
    }

    fun cargarCategorias(){
        viewModelScope.launch {
            _categoriasState.value = CategoriasState.Cargando
            val userId = tokenManager.getUserIdSync()

            if(userId == null){
                _categoriasState.value = CategoriasState.Error("No se encontro Sesion Activa")
                return@launch
            }
            try{
                //cargar categorias
                val catResponse = apiService.getCategorias(userId) //esto guarda la respuesta del servidor http

                if(catResponse.isSuccessful){
                    val lista = catResponse.body() ?: emptyList() //almacena las categorias en una lista y si viene vacia hace una empty list con ?: emptylist
                    _categoriasState.value = CategoriasState.Exitoso(lista)// si es existoso lo almacena en lista

                }else{
                    _categoriasState.value = CategoriasState.Error("Error: ${catResponse.code()}")
                }

            }catch (e: Exception){_categoriasState.value = CategoriasState.Error("Error de conexion: ${e.message}")
            }
        }
    }

    fun restablecerCrearCategoria() {
        if (_crearCategoriaState.value !is CrearCategoriaState.Cargando) {
            _crearCategoriaState.value = CrearCategoriaState.Idle
        }
    }

    fun crearCategoria(nombre: String, tipo: String) {
        if (_crearCategoriaState.value is CrearCategoriaState.Cargando) return
        val nombreLimpio = nombre.trim().replace(Regex("\\s+"), " ")
        if (nombreLimpio.isBlank() || nombreLimpio.length > 60) {
            _crearCategoriaState.value = CrearCategoriaState.Error("Escribe un nombre de entre 1 y 60 caracteres")
            return
        }
        if (tipo !in listOf("GASTO", "INGRESO")) {
            _crearCategoriaState.value = CrearCategoriaState.Error("Selecciona si es una categoría de gasto o ingreso")
            return
        }
        _crearCategoriaState.value = CrearCategoriaState.Cargando
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserIdSync()
                if (userId == null) {
                    _crearCategoriaState.value = CrearCategoriaState.Error("Inicia sesión para crear categorías")
                    return@launch
                }
                val respuesta = apiService.crearCategoria(userId, CategoryRegistroRequest(nombreLimpio, tipo))
                val categoria = respuesta.body()
                if (respuesta.isSuccessful && categoria != null) {
                    val actuales = (_categoriasState.value as? CategoriasState.Exitoso)?.categorias.orEmpty()
                    _categoriasState.value = CategoriasState.Exitoso(actuales.filterNot { it.id == categoria.id } + categoria)
                    _crearCategoriaState.value = CrearCategoriaState.Exitoso(categoria)
                } else {
                    _crearCategoriaState.value = CrearCategoriaState.Error(
                        respuesta.mensajeDeError("No se pudo crear la categoría (HTTP ${respuesta.code()})")
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: IOException) {
                _crearCategoriaState.value = CrearCategoriaState.Error("No se pudo conectar. Comprueba que el servidor esté activo e intenta de nuevo.")
            } catch (_: Exception) {
                _crearCategoriaState.value = CrearCategoriaState.Error("No se pudo procesar la respuesta del servidor")
            }
        }
    }

    fun guardarTransaccion(monto: String, fecha: String, descripcion: String, categoriaId: Long?) {
        if (_guardarTransaccionState.value is GuardarTransaccionState.Cargando) return
        viewModelScope.launch {
            val userId = tokenManager.getUserIdSync()
            if(userId == null){
                _guardarTransaccionState.value = GuardarTransaccionState.Error("No se encontro Sesion Activa")
                return@launch
            }

            if (categoriaId == null){
                _guardarTransaccionState.value = GuardarTransaccionState.Error("Obligatorio seleccionar Categoria")
                return@launch
            }
            val montoDecimal = monto.trim().replace(',', '.').toDoubleOrNull()
            if (montoDecimal == null || !montoDecimal.isFinite() || montoDecimal <= 0){
                _guardarTransaccionState.value = GuardarTransaccionState.Error("El monto debe ser un número mayor que cero")
                return@launch
            }
            if (descripcion.isBlank()) {
                _guardarTransaccionState.value = GuardarTransaccionState.Error("Agrega una descripción al movimiento")
                return@launch
            }
            _guardarTransaccionState.value = GuardarTransaccionState.Cargando

            try {
                val request = TransactionRegistroDTO(
                    monto = montoDecimal,
                    fecha = fecha,
                    descripcion = descripcion.trim(),
                    categoriaId = categoriaId
                )
                val response = apiService.registrarTransaccion(userId, request)
                if(response.isSuccessful){
                    _guardarTransaccionState.value = GuardarTransaccionState.Exitoso
                }else{
                    _guardarTransaccionState.value = GuardarTransaccionState.Error(response.mensajeDeError("No se pudo guardar (HTTP ${response.code()})"))
                }

            }catch (e: Exception){
                _guardarTransaccionState.value = GuardarTransaccionState.Error("Error de conexion: ${e.message}")
            }
        }
    }
}
