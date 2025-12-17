package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class VerifyCodeRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("otp")
    val otp: String
)
