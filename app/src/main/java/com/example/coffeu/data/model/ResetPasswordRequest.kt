package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("token")
    val token: String,
    @SerializedName("new_password")
    val newPassword: String
)
