package com.example.coffeu.data.model

import com.google.gson.annotations.SerializedName

data class RegistroRequest(
	@SerializedName("nombre_usuario")
	val nombreUsuario: String,
	@SerializedName("telefono_celular")
	val telefonoCelular: String,
	@SerializedName("email")
	val email: String,
	@SerializedName("password")
	val password: String,
	@SerializedName("password2")
	val password2: String
)

