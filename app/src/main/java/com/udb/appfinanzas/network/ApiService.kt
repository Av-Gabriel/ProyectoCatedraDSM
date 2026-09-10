package com.udb.appfinanzas.network

import com.udb.appfinanzas.auth.data.AuthResponse
import com.udb.appfinanzas.auth.data.LoginRequest
import com.udb.appfinanzas.auth.data.RegistroRequest
import com.udb.appfinanzas.auth.data.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/users")
    suspend fun registrar(@Body request: RegistroRequest): Response<UserResponse>
}