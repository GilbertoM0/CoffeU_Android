package com.example.coffeu.data.remote

import com.example.coffeu.data.api.ApiEndpoints
import com.example.coffeu.domain.model.AuthResponse
import com.example.coffeu.domain.model.FirebaseVerifyRequest
import com.example.coffeu.domain.model.ForgotPasswordRequest
import com.example.coffeu.domain.model.LoginRequest
import com.example.coffeu.domain.model.MensajeResponse
import com.example.coffeu.domain.model.RegistroRequest
import com.example.coffeu.domain.model.ResetPasswordRequest
import com.example.coffeu.domain.model.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {

    @POST(ApiEndpoints.REGISTRO)
    suspend fun registro(
        @Body request: RegistroRequest
    ): MensajeResponse

    @POST(ApiEndpoints.LOGIN)
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    @POST(ApiEndpoints.FIREBASE_VERIFY)
    suspend fun firebaseVerify(
        @Body request: FirebaseVerifyRequest
    ): AuthResponse

    @POST(ApiEndpoints.FORGOT)
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): MensajeResponse

    @POST(ApiEndpoints.RESET)
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): MensajeResponse

    @POST(ApiEndpoints.ACTIVAR)
    suspend fun activar(
        @Body request: Map<String, String>
    ): MensajeResponse

    @POST(ApiEndpoints.REFRESH)
    suspend fun refresh(
        @Body request: Map<String, String>
    ): Map<String, String>

    @GET(ApiEndpoints.UPDATE_PROFILE)
    suspend fun getUpdateProfile(): UserDto

    @PUT(ApiEndpoints.UPDATE_PROFILE)
    suspend fun updateProfile(
        @Body request: Map<String, @JvmSuppressWildcards Any?>
    ): UserDto
}
