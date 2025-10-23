package com.example.coffeu.data.api

import com.example.coffeu.data.model.LoginRequest
import com.example.coffeu.data.model.LoginResponse
import com.example.coffeu.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST



interface AuthService {

    // Login: http://10.0.2.2:8000/accounts/login/
    @POST("login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Registro: http://10.0.2.2:8000/accounts/registro/
    @POST("registro/") // Corregido: usa "registro/"
    suspend fun register(@Body request: RegisterRequest): LoginResponse
}