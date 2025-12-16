package com.example.coffeu.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeu.data.RetrofitClient
import com.example.coffeu.data.model.CartItem
import com.example.coffeu.data.model.Kitchen
import com.example.coffeu.data.model.LoginRequest
import com.example.coffeu.data.model.LoginResponse
import com.example.coffeu.data.model.RegisterRequest
import com.example.coffeu.data.model.VerifyCodeRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel : ViewModel() {
    // --- ESTADOS DE LA UI OBSERVABLES ---
    var loginState by mutableStateOf<LoginResponse?>(null)
        private set
    var registerSuccess by mutableStateOf(false)
        private set
    var verifyCodeSuccess by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Para Kitchen la carga de los products
    var kitchenList by mutableStateOf<List<Kitchen>>(emptyList())
        private set
    var kitchenListError by mutableStateOf<String?>(null)

    // ✅ ESTADO para la lista de favoritos
    val favoriteKitchens = mutableStateListOf<Kitchen>()

    // ✅ ESTADO para el carrito de compras
    val cartItems = mutableStateListOf<CartItem>()

    fun isFavorite(kitchen: Kitchen): Boolean {
        return favoriteKitchens.any { it.id == kitchen.id }
    }

    fun toggleFavorite(kitchen: Kitchen) {
        if (isFavorite(kitchen)) {
            favoriteKitchens.removeIf { it.id == kitchen.id }
        } else {
            favoriteKitchens.add(kitchen)
        }
    }

    // ✅ FUNCIÓN para agregar al carrito (manejando cantidades)
    fun addToCart(kitchen: Kitchen) {
        val existingItem = cartItems.find { it.kitchen.id == kitchen.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(CartItem(kitchen = kitchen))
        }
    }

    // ✅ FUNCIÓN para incrementar la cantidad de un item del carrito
    fun increaseCartItemQuantity(item: CartItem) {
        item.quantity++
    }

    // ✅ FUNCIÓN para decrementar la cantidad de un item del carrito
    fun decreaseCartItemQuantity(item: CartItem) {
        if (item.quantity > 1) {
            item.quantity--
        } else {
            cartItems.remove(item)
        }
    }


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

    // --- FUNCIÓN DE VERIFICACIÓN DE CÓDIGO ---
    fun attemptVerifyCode(email: String, otp: String) {
        updateErrorMessage(null)
        isLoading = true
        verifyCodeSuccess = false

        if (otp.length != 6) {
            updateErrorMessage("El código debe tener 6 dígitos.")
            isLoading = false
            return
        }

        viewModelScope.launch {
            try {
                val request = VerifyCodeRequest(email, otp)
                RetrofitClient.authService.verifyCode(request)
                verifyCodeSuccess = true
            } catch (e: HttpException) {
                updateErrorMessage("Código de verificación incorrecto o expirado.")
            } catch (e: IOException) {
                updateErrorMessage("Error de conexión al verificar el código.")
            } catch (e: Exception) {
                updateErrorMessage("Ocurrió un error inesperado al verificar.")
            } finally {
                isLoading = false
            }
        }
    }


    // ✅ FUNCIÓN para cargar la lista de cocinas
    fun loadKitchens() {
        if (kitchenList.isNotEmpty()) return // No recargar si ya hay datos

        kitchenListError = null
        isLoading = true // Usamos el indicador global

        viewModelScope.launch {
            try {
                val list = RetrofitClient.authService.getKitchens()
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

    fun resetVerifyCodeState() {
        verifyCodeSuccess = false
        updateErrorMessage(null)
    }

    fun logout() {
        loginState = null
    }
}