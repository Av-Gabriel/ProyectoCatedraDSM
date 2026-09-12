package com.udb.appfinanzas.network

import com.udb.appfinanzas.auth.data.AuthResponse
import com.udb.appfinanzas.auth.data.LoginRequest
import com.udb.appfinanzas.auth.data.RegistroRequest
import com.udb.appfinanzas.auth.data.UserResponse
import com.udb.appfinanzas.categorias.data.CategoryResponse
import com.udb.appfinanzas.transacciones.TransactionRegistroDTO
import com.udb.appfinanzas.transacciones.TransactionResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/users")
    suspend fun registrar(@Body request: RegistroRequest): Response<UserResponse>

    @GET("api/users")
    suspend fun getUsers(): Response<List<UserResponse>>

    //obtener transacciones filtradas por ID
    @GET("api/transactions")
    suspend fun getTransactionsByUserId(
        @Query("userId") userId: Long):
            Response<List<TransactionResponseDTO>>

    @POST("api/transactions")
    suspend fun registrarTransaccion(
        @Body transaccion: TransactionRegistroDTO // convierte el objeto a JSON
    ): Response<TransactionResponseDTO> // devuelve la transaccion creada usualmente incluye el ID generado


    // dentro de la interfaz junto a las demas
    @GET("api/categories")
    suspend fun getCategorias(
        @Query("userId") userId: Long):
            Response<List<CategoryResponse>>
}