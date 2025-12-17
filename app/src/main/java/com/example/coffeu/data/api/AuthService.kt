package com.example.coffeu.data.api

import com.example.coffeu.data.model.AddProductRequest
import com.example.coffeu.data.model.Kitchen
import com.example.coffeu.data.model.LoginRequest
import com.example.coffeu.data.model.LoginResponse
import com.example.coffeu.data.model.RegisterRequest
import com.example.coffeu.data.model.Product
import com.example.coffeu.data.model.VerifyCodeRequest
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    // Login: http://10.0.2.2:8000/accounts/login/
    @POST("accounts/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Registro: http://10.0.2.2:8000/accounts/registro/
    @POST("accounts/registro/")
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    // Activar cuenta: http://127.0.0.1:3000/accounts/activar/
    @POST("accounts/activar/")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): LoginResponse

    // Obtener la lista de productos (cocinas)
    @GET("products/")
    suspend fun getKitchens(): List<Kitchen>

    // Obtener detalles del producto por ID
    @GET("products/{productId}/")
    suspend fun getProductDetail(@Path("productId") productId: Int): Product

    // Añadir un nuevo producto
    @POST("products/")
    suspend fun addProduct(@Body request: AddProductRequest): Kitchen
}