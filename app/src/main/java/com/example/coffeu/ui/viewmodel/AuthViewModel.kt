package com.example.coffeu.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.coffeu.data.api.AuthService
import com.example.coffeu.data.model.Kitchen
import com.example.coffeu.data.model.LoginRequest
import com.example.coffeu.data.model.LoginResponse
import com.example.coffeu.data.model.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    var loginState by mutableStateOf<LoginResponse?>(null)
        private set
    var registerSuccess by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    var kitchenList by mutableStateOf<List<Kitchen>>(emptyList())
        private set
    var kitchenListError by mutableStateOf<String?>(null)

    fun updateErrorMessage(message: String?) {
        errorMessage = message
    }

    fun attemptLogin(email: String, password: String) {
        updateErrorMessage(null)
        isLoading = true
        loginState = null

        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = authService.login(request)
                loginState = response
                sharedPreferences.edit().putString("auth_token", response.token).apply()
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

    fun attemptRegister(
        email: String,
        nombre_usuario: String,
        telefono_celular: String,
        password: String,
        password2: String
    ) {
        updateErrorMessage(null)
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
                authService.register(request)
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

    fun loadKitchens() {
        if (kitchenList.isNotEmpty()) return

        kitchenListError = null
        isLoading = true

        viewModelScope.launch {
            try {
                val list = authService.getKitchens()
                kitchenList = list
            } catch (e: Exception) {
                kitchenListError = "No se pudo cargar la lista de cocinas: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetRegisterState() {
        registerSuccess = false
        updateErrorMessage(null)
    }

    fun logout() {
        loginState = null
        sharedPreferences.edit().remove("auth_token").apply()
    }
}
