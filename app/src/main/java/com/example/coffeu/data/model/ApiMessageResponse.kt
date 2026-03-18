package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class ApiMessageResponse(
    @SerializedName("mensaje")
    val mensaje: String? = null,
    @SerializedName("detail")
    val detail: String? = null
)

