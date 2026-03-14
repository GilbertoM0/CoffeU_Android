package com.example.coffeu.data

import com.example.coffeu.data.api.AuthService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// ¡IMPORTANTE! 10.0.2.2 es para acceder a tu localhost desde el emulador.
const val BASE_URL = "http://10.0.2.2:3000/"

object RetrofitClient {
    private var authToken: String? = null

    // Función para guardar el token cuando el usuario hace login
    fun setToken(token: String?) {
        authToken = token
    }

    private val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        
        // Si tenemos un token, lo añadimos a la cabecera "Authorization"
        authToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        val request = requestBuilder.build()
        chain.proceed(request)
    }.build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // <-- Usamos el cliente con el interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }
}
