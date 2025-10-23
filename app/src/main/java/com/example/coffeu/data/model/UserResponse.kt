package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nombre_usuario") // ✅ Usamos el nombre que Django envía
    val nombreUsuario: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("telefono_celular")
    val telefonoCelular: String
)