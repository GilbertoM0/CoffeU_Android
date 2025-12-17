package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class AddProductRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("reviewCount")
    val reviewCount: Int,
    @SerializedName("deliveryTime")
    val deliveryTime: String,
    @SerializedName("distance")
    val distance: String,
    @SerializedName("discount")
    val discount: String
)
