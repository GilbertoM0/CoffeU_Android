package com.example.coffeu.data.api

import com.example.coffeu.data.model.AddProductRequest
import com.example.coffeu.data.model.ApiMessageResponse
import com.example.coffeu.data.model.AuthResponse
import com.example.coffeu.data.model.ChangePasswordRequest
import com.example.coffeu.data.model.FirebaseVerifyRequest
import com.example.coffeu.data.model.ForgotPasswordRequest
import com.example.coffeu.data.model.Kitchen
import com.example.coffeu.data.model.LoginRequest
import com.example.coffeu.data.model.LoginResponse
import com.example.coffeu.data.model.Product
import com.example.coffeu.data.model.RegisterRequest
import com.example.coffeu.data.model.RegistroRequest
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

    @POST(ApiEndpoints.LOGIN)
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST(ApiEndpoints.REGISTRO)
    suspend fun registro(@Body request: RegistroRequest): ApiMessageResponse

    @POST(ApiEndpoints.FIREBASE_VERIFY)
    suspend fun firebaseVerify(@Body request: FirebaseVerifyRequest): AuthResponse

    // Endpoint legacy que se sigue usando en otras pantallas existentes
    @POST(ApiEndpoints.REGISTRO)
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    @POST(ApiEndpoints.ACTIVAR)
    suspend fun verifyCode(@Body request: VerifyCodeRequest): LoginResponse

    // Flujo de recuperación de contraseña (sin sesión)
    @POST(ApiEndpoints.FORGOT)
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ApiMessageResponse

    @POST(ApiEndpoints.RESET)
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ApiMessageResponse

    // Cambio de contraseña interno (si el backend lo mantiene)
    @Suppress("unused")
    @POST(ApiEndpoints.RESET)
    suspend fun changePassword(@Body request: ChangePasswordRequest): LoginResponse

    @PUT(ApiEndpoints.UPDATE_PROFILE)
    suspend fun updateProfile(@Body request: UserUpdateRequest): UserUpdateResponse

    @GET("products/")
    suspend fun getKitchens(): List<Kitchen>

    @Suppress("unused")
    @GET("products/{productId}/")
    suspend fun getProductDetail(@Path("productId") productId: Int): Product

    @POST("products/")
    suspend fun addProduct(@Body request: AddProductRequest): Kitchen
}
