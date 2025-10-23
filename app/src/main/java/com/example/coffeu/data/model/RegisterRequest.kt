package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("nombre_usuario")
    val username: String,
    @SerializedName("telefono_celular")
    val phone: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("password2")
    val password2: String // Confirmación
)