package com.example.coffeu.data.api

import com.example.coffeu.data.model.Kitchen
import com.example.coffeu.data.model.LoginRequest
import com.example.coffeu.data.model.LoginResponse
import com.example.coffeu.data.model.RegisterRequest
import com.example.coffeu.data.model.Product // Importa el nuevo modelo
import retrofit2.http.GET
import retrofit2.http.Path // Necesario para argumentos de ruta
import retrofit2.http.Body
import retrofit2.http.POST



interface AuthService {

    // Login: http://10.0.2.2:8000/accounts/login/
    @POST("accounts/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Registro: http://10.0.2.2:8000/accounts/registro/
    @POST("accounts/registro/") // Corregido: usa "registro/"
    suspend fun register(@Body request: RegisterRequest): LoginResponse


    // ✅ NUEVA FUNCIÓN: Obtener la lista de productos (cocinas)
    @GET("products/") // Cambia "productos/" por la URL de tu API de listado
    suspend fun getKitchens(): List<Kitchen>




    // ✅ NUEVA FUNCIÓN: Obtener detalles del producto por ID
    // Usa un placeholder en la URL de Django para inyectar el {productId}
    @GET("products/{productId}/")
    suspend fun getProductDetail(@Path("productId") productId: Int): Product
}