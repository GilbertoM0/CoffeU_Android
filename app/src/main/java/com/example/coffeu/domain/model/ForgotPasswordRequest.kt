package com.example.coffeu.domain.model

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("telefono_celular")
    val telefonoCelular: String? = null
) {
    init {
        require(!email.isNullOrBlank() || !telefonoCelular.isNullOrBlank()) {
            "Debes enviar email o telefono_celular."
        }
    }
}
