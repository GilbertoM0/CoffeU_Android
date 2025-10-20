// Archivo: AuthViewModel.kt
package com.example.coffeu.ui.viewmodel

// **IMPORTANTE:** Debes importar y extender de ViewModel
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf // Ejemplo de uso
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// El ViewModel ahora tiene un constructor por defecto (sin argumentos)
class AuthViewModel : ViewModel() {
    // Ejemplo: Un estado simple para verificar si el usuario está logueado
    var isAuthenticated by mutableStateOf(false)
        private set

    fun attemptLogin(email: String, password: String) {
        // Lógica de autenticación simple.
        // ¡Aquí es donde integrarías tu RetrofitClient!
        if (email.isNotEmpty() && password.isNotEmpty()) {
            isAuthenticated = true
        }
    }

    fun logout() {
        isAuthenticated = false
    }
}