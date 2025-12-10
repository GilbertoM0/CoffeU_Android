package com.example.coffeu.data.api

import com.example.coffeu.data.model.ChangePasswordRequest
import com.example.coffeu.data.model.Kitchen
import com.example.coffeu.data.model.LoginRequest
import com.example.coffeu.data.model.LoginResponse
import com.example.coffeu.data.model.Product
import com.example.coffeu.data.model.RegisterRequest
import com.example.coffeu.data.model.ResetPasswordRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {

    @POST("accounts/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("accounts/registro/")
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    // Corregido: apunta a "reset/" en lugar de "change-password/"
    @POST("accounts/reset/")
    suspend fun changePassword(@Body request: ChangePasswordRequest): LoginResponse

    @POST("accounts/reset/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): LoginResponse

    @GET("products/")
    suspend fun getKitchens(): List<Kitchen>

    @GET("products/{productId}/")
    suspend fun getProductDetail(@Path("productId") productId: Int): Product
}
