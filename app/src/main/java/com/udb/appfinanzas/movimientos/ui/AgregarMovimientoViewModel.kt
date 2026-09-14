package com.udb.appfinanzas.movimientos.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udb.appfinanzas.categorias.data.CategoriasState
import com.udb.appfinanzas.categorias.data.CategoryResponse
import com.udb.appfinanzas.core.data.TokenManager
import com.udb.appfinanzas.network.ApiService
import com.udb.appfinanzas.transacciones.GuardarTransaccionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.udb.appfinanzas.transacciones.TransactionRegistroDTO
import javax.inject.Inject

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

    fun guardarTransaccion(monto: String, fecha: String, descripcion: String, categoriaId: Long?) {
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
            val montoDecimal = monto.toDoubleOrNull()
            if (montoDecimal == null){
                _guardarTransaccionState.value = GuardarTransaccionState.Error("El monto debe ser un numero valido")
                return@launch
            }
            _guardarTransaccionState.value = GuardarTransaccionState.Cargando

            try {
                val request = TransactionRegistroDTO(
                    monto = montoDecimal,
                    fecha = fecha,
                    descripcion = descripcion,
                    categoriaId = categoriaId
                )
                val response = apiService.registrarTransaccion(userId, request)
                if(response.isSuccessful){
                    _guardarTransaccionState.value = GuardarTransaccionState.Exitoso
                }else{
                    _guardarTransaccionState.value = GuardarTransaccionState.Error("Error ${response.code()}: revisa los datos")
                }

            }catch (e: Exception){
                _guardarTransaccionState.value = GuardarTransaccionState.Error("Error de conexion: ${e.message}")
            }
        }
    }
}