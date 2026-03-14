package com.example.coffeu.data.model

data class UserUpdateResponse(
    val status: String,
    val message: String,
    val user: UserResponse? = null
)
