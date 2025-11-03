package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("imagen_url")
    val imagen_url: String,
    @SerializedName("precio")
    val precio: Double,

)