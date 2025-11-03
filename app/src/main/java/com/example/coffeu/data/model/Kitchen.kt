package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class Kitchen(
    val id: Int,
    val name: String,
    val description: String,
    val stock: Int,
    val imageUrl: String? = null, // Usamos String? porque el campo de Django es nullable

    val price: String,
    val rating: Double,
    val reviewCount: Int,
    val deliveryTime: String,
    val distance: String,
    val discount: String
    // Nota: Si alguno de estos campos puede ser NULL desde Django,
    // añade '?' al tipo (ej: val distance: String?)
)