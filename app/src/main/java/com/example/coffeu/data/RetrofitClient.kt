package com.example.coffeu.data

import com.example.coffeu.data.api.AuthService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// ¡IMPORTANTE! 10.0.2.2 es para acceder a tu localhost desde el emulador.
const val BASE_URL = "http://10.0.2.2:3000/"

object RetrofitClient {
    // ... (El código de inicialización de Retrofit) ...
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }
}