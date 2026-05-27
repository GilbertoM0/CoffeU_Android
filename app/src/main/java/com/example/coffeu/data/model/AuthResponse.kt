package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
	@SerializedName("mensaje")
	val mensaje: String? = null,
	@SerializedName("access_token")
	val accessToken: String,
	@SerializedName("refresh_token")
	val refreshToken: String,
	@SerializedName("user")
	val user: UserDto
)
