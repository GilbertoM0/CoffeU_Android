package com.example.coffeu.data.api

import com.example.coffeu.data.model.AddProductRequest
import com.example.coffeu.data.model.ApiMessageResponse
import com.example.coffeu.data.model.ChangePasswordRequest
import com.example.coffeu.data.model.ForgotPasswordRequest
import com.example.coffeu.data.model.Kitchen
import com.example.coffeu.data.model.LoginRequest
import com.example.coffeu.data.model.LoginResponse
import com.example.coffeu.data.model.Product
import com.example.coffeu.data.model.RegisterRequest
import com.example.coffeu.data.model.ResetPasswordRequest
import com.example.coffeu.data.model.VerifyCodeRequest
import com.example.coffeu.data.model.UserUpdateRequest
import com.example.coffeu.data.model.UserUpdateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthService {

    @POST("accounts/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("accounts/registro/")
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    @POST("accounts/activar/")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): LoginResponse

    // Flujo de recuperación de contraseña (sin sesión)
    @POST("accounts/forgot/")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ApiMessageResponse

    @POST("accounts/reset/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ApiMessageResponse

    // Cambio de contraseña interno (si el backend lo mantiene)
    @POST("accounts/reset/")
    suspend fun changePassword(@Body request: ChangePasswordRequest): LoginResponse

    @PUT("accounts/update-profile/")
    suspend fun updateProfile(@Body request: UserUpdateRequest): UserUpdateResponse

    @GET("products/")
    suspend fun getKitchens(): List<Kitchen>

    @GET("products/{productId}/")
    suspend fun getProductDetail(@Path("productId") productId: Int): Product

    @POST("products/")
    suspend fun addProduct(@Body request: AddProductRequest): Kitchen
}
