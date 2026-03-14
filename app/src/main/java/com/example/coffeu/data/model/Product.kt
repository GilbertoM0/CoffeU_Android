package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val stock: Int,
    val imageUrl: String,
    val price: Double,
    val rating: Double,
    val category: String,
    val size: String,
    val deliveryTime: String,
    val distance: String,
    val discount: String
)
