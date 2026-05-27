package com.example.coffeu.domain.model

import com.google.gson.annotations.SerializedName

data class MensajeResponse(
    @SerializedName("mensaje")
    val mensaje: String
)
