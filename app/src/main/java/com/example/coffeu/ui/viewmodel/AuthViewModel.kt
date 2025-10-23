package com.example.coffeu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.coffeu.data.RetrofitClient
import com.example.coffeu.data.model.LoginRequest
import com.example.coffeu.data.model.LoginResponse
import com.example.coffeu.data.model.RegisterRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel : ViewModel() {
    // --- ESTADOS DE LA UI OBSERVABLES ---
    var loginState by mutableStateOf<LoginResponse?>(null)
        private set
    var registerSuccess by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // --- CORRECCIÓN: Usamos 'updateErrorMessage' para evitar el choque de firmas (Clash) ---
    fun updateErrorMessage(message: String?) {
        errorMessage = message
    }

    // --- FUNCIÓN DE LOGIN CON RETROFIT ---
    fun attemptLogin(email: String, password: String) {
        updateErrorMessage(null) // Limpia errores previos de la UI
        isLoading = true
        loginState = null

        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = RetrofitClient.authService.login(request)
                loginState = response
            } catch (e: HttpException) {
                errorMessage = "Credenciales inválidas. Verifica tu email y contraseña."
            } catch (e: IOException) {
                errorMessage = "Error de conexión: No se pudo conectar al servidor."
            } catch (e: Exception) {
                errorMessage = "Ocurrió un error inesperado al iniciar sesión."
            } finally {
                isLoading = false
            }
        }
    }

    // --- FUNCIÓN DE REGISTRO CON RETROFIT ---
    fun attemptRegister(
        email: String,
        nombre_usuario: String,
        telefono_celular: String,
        password: String,
        password2: String
    ) {
        updateErrorMessage(null) // Limpia errores previos de la UI
        isLoading = true
        registerSuccess = false

        if (password != password2) {
            updateErrorMessage("Las contraseñas no coinciden.")
            isLoading = false
            return
        }

        viewModelScope.launch {
            try {
                val request = RegisterRequest(email, nombre_usuario, telefono_celular, password, password2)
                RetrofitClient.authService.register(request)
                registerSuccess = true

            } catch (e: HttpException) {
                updateErrorMessage("Error de Registro: El usuario o email ya existe.")
            } catch (e: IOException) {
                updateErrorMessage("Error de conexión al intentar registrarse.")
            } catch (e: Exception) {
                updateErrorMessage("Ocurrió un error inesperado al registrarse.")
            } finally {
                isLoading = false
            }
        }
    }

    // Función para limpiar el estado de éxito después de navegar o un error
    fun resetRegisterState() {
        registerSuccess = false
        updateErrorMessage(null)
    }

    fun logout() {
        loginState = null
    }
}