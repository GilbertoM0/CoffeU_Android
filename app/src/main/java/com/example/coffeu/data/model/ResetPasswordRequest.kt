package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("telefono_celular")
    val telefonoCelular: String? = null,
    @SerializedName("otp")
    val otp: String,
    @SerializedName("new_password")
    val newPassword: String,
    @SerializedName("new_password2")
    val newPassword2: String
)
