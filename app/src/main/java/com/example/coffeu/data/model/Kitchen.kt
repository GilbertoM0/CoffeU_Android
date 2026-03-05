package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class Kitchen(
    val id: Int,
    val name: String,
    val description: String,
    val stock: Int,
    val imageUrl: String? = null,
    val price: String,
    val rating: Double,
    val reviewCount: Int, // Nueva propiedad
    val category: String, // Nueva propiedad
    val size: String,     // Nueva propiedad
    val deliveryTime: String,
    val distance: String,
    val discount: String
)
