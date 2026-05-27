package com.example.coffeu.domain.model

import com.google.gson.annotations.SerializedName

data class FirebaseVerifyRequest(
    @SerializedName("id_token")
    val idToken: String
)
