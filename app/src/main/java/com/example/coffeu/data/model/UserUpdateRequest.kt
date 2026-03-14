package com.example.coffeu.data.model

data class UserUpdateRequest(
    val nombre_usuario: String,
    val email: String,
    val telefono_celular: String,
    val fecha_nacimiento: String // Formato YYYY-MM-DD
)
