package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class Kitchen(
    val id: Int = 0,
    val name: String? = "",
    val description: String? = "",
    val stock: Int = 0,
    val imageUrl: String? = null,
    val price: String? = "",
    val rating: Double? = 0.0,
    val reviewCount: Int? = 0,
    val category: String? = "",
    val size: String? = "",
    val deliveryTime: String? = "",
    val distance: String? = "",
    val discount: String? = ""
)
