package com.example.coffeu.domain.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("identificador")
    val identificador: String,
    @SerializedName("password")
    val password: String
)
