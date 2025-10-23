package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    // 1. Usamos 'access_token' para el token principal
    @SerializedName("access_token")
    val token: String,

    // 2. Usamos el modelo anidado para el objeto 'user'
    @SerializedName("user")
    val user: UserResponse,

    // 3. (Opcional) Si necesitas el refresh token más adelante
    @SerializedName("refresh_token")
    val refreshToken: String? = null
)