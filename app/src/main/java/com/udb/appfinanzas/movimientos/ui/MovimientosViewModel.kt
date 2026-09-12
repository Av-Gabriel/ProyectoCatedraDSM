package com.udb.appfinanzas.movimientos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udb.appfinanzas.categorias.data.CategoryResponse
import com.udb.appfinanzas.core.data.TokenManager
import com.udb.appfinanzas.network.ApiService
import com.udb.appfinanzas.transacciones.TransaccionesState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MovimientosViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _estado = MutableStateFlow<TransaccionesState>(TransaccionesState.Idle)
    val estado: StateFlow<TransaccionesState> = _estado

    private var categoriasPorId: Map<Long, CategoryResponse> = emptyMap()

    init {
        cargarTodo()
    }

    fun cargarTodo() {
        viewModelScope.launch {
            _estado.value = TransaccionesState.Cargando
            val userId = tokenManager.getUserIdSync()

            if (userId == null) {
                _estado.value = TransaccionesState.Error("No se encontro sesion activa")
                return@launch
            }

            try {
                //cargar categorías primero
                val catResponse = apiService.getCategorias(userId)
                if (catResponse.isSuccessful) {
                    categoriasPorId = (catResponse.body() ?: emptyList()).associateBy { it.id }
                }

                //cargar transacciones
                val txResponse = apiService.getTransactionsByUserId(userId)
                if (txResponse.isSuccessful) {
                    val lista = txResponse.body() ?: emptyList()
                    _estado.value = TransaccionesState.Exitoso(lista)
                } else {
                    _estado.value = TransaccionesState.Error("Error ${txResponse.code()}")
                }
            } catch (e: Exception) {
                _estado.value = TransaccionesState.Error("Error de conexion: ${e.message}")
            }
        }
    }

    fun obtenerCategoria(categoriaId: Long): CategoryResponse? {
        return categoriasPorId[categoriaId]
    }
}