package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("identificador")
    val email: String,
    @SerializedName("password")
    val password: String
)