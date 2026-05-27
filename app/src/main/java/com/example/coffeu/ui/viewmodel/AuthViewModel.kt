package com.example.coffeu.ui.viewmodel

import android.content.SharedPreferences
import com.example.coffeu.BuildConfig
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeu.data.api.AuthService
import com.example.coffeu.data.model.AddProductRequest
import com.example.coffeu.data.model.AuthResponse
import com.example.coffeu.data.model.CartItem
import com.example.coffeu.data.model.FirebaseVerifyRequest
import com.example.coffeu.data.model.Kitchen
import com.example.coffeu.data.model.LoginRequest
import com.example.coffeu.data.model.RegisterRequest
import com.example.coffeu.data.model.RegistroRequest
import com.example.coffeu.data.model.VerifyCodeRequest
import com.example.coffeu.data.model.NotificationItem
import com.example.coffeu.data.model.UserUpdateRequest
import com.example.coffeu.data.model.UserUpdateResponse
import com.example.coffeu.data.model.UserDto
import com.example.coffeu.ui.auth.normalizarTelefonoParaBackend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val message: String? = null) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthRepository @Inject constructor(
    private val authService: AuthService
) {
    suspend fun registro(
        nombreUsuario: String,
        telefonoCelular: String,
        email: String,
        password: String,
        password2: String
    ): Result<Unit> {
        return runCatching {
            authService.registro(
                RegistroRequest(
                    nombreUsuario = nombreUsuario,
                    telefonoCelular = telefonoCelular,
                    email = email,
                    password = password,
                    password2 = password2
                )
            )
            Unit
        }
    }

    suspend fun firebaseVerify(idToken: String): Result<AuthResponse> {
        return runCatching {
            authService.firebaseVerify(FirebaseVerifyRequest(idToken = idToken))
        }
    }

    suspend fun login(identificador: String, password: String): Result<AuthResponse> {
        return runCatching {
            authService.login(LoginRequest(identificador, password))
        }
    }
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService,
    private val authRepository: AuthRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    var loginState by mutableStateOf<AuthResponse?>(null)
        private set
    var registerSuccess by mutableStateOf(false)
        private set
    var verifyCodeSuccess by mutableStateOf(false)
        private set
    var addProductSuccess by mutableStateOf(false)
        private set
    var updateProfileSuccess by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val _registroState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val registroState: StateFlow<AuthUiState> = _registroState.asStateFlow()

    private val _firebaseVerifyState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val firebaseVerifyState: StateFlow<AuthUiState> = _firebaseVerifyState.asStateFlow()

    // Para Kitchen la carga de los products
    var kitchenList by mutableStateOf<List<Kitchen>>(emptyList())
        private set
    var kitchenListError by mutableStateOf<String?>(null)

    // ✅ ESTADO para la lista de favoritos
    val favoriteKitchens = mutableStateListOf<Kitchen>()

    // ✅ ESTADO para el carrito de compras
    val cartItems = mutableStateListOf<CartItem>()

    // ✅ ESTADO para las notificaciones
    val notifications = mutableStateListOf(
        NotificationItem(
            id = 1,
            title = "Notificación",
            message = "esta es una notificacion de Ejemplo",
            time = "Ahora"
        ),
        NotificationItem(
            id = 2,
            title = "Promoción de Café",
            message = "¡Disfruta de un 2x1 en todos nuestros lattes hoy!",
            time = "Hace 1 hora"
        ),
        NotificationItem(
            id = 3,
            title = "Actualización de Pedido",
            message = "Tu pedido ha sido recibido y está en preparación.",
            time = "Hace 3 horas",
            isRead = true
        )
    )

    // ✅ Contador de notificaciones no leídas
    val unreadNotificationsCount by derivedStateOf {
        notifications.count { !it.isRead }
    }

    fun markNotificationAsRead(id: Int) {
        val index = notifications.indexOfFirst { it.id == id }
        if (index != -1 && !notifications[index].isRead) {
            notifications[index] = notifications[index].copy(isRead = true)
        }
    }

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

    fun attemptLogin(email: String, password: String) {
        updateErrorMessage(null)
        isLoading = true
        loginState = null

        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = authService.login(request)
                loginState = response
                sharedPreferences.edit()
                    .putString("auth_token", response.accessToken)
                    .putString("refresh_token", response.refreshToken)
                    .apply()
            } catch (e: HttpException) {
                errorMessage = "Credenciales inválidas. Verifica tu email y contraseña."
            } catch (e: IOException) {
                errorMessage = "Error de conexión: verifica que el backend esté activo y que API_BASE_URL sea correcta (${e.localizedMessage ?: "sin detalle"})."
            } catch (e: Exception) {
                errorMessage = "Ocurrió un error inesperado al iniciar sesión."
            } finally {
                isLoading = false
            }
        }
    }

    fun registrar(
        nombreUsuario: String,
        telefonoCelular: String,
        email: String,
        password: String,
        password2: String
    ) {
        val telefonoNormalizado = telefonoCelular.normalizarTelefonoParaBackend()
        val localPhoneRegex = Regex("^\\d{10}$")
        val e164MxRegex = Regex("^\\+52\\d{10}$")
        val strongPasswordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$")

        when {
            nombreUsuario.isBlank() || telefonoNormalizado.isBlank() || email.isBlank() || password.isBlank() || password2.isBlank() -> {
                _registroState.value = AuthUiState.Error("Completa todos los campos.")
                return
            }
            !localPhoneRegex.matches(telefonoNormalizado) && !e164MxRegex.matches(telefonoNormalizado) -> {
                _registroState.value = AuthUiState.Error("Ingresa un teléfono válido (10 dígitos o +52 seguido de 10 dígitos).")
                return
            }
            password != password2 -> {
                _registroState.value = AuthUiState.Error("Las contraseñas no coinciden.")
                return
            }
            !strongPasswordRegex.matches(password) -> {
                _registroState.value = AuthUiState.Error("La contraseña debe tener 8+ caracteres, mayúscula, minúscula, número y símbolo.")
                return
            }
        }

        viewModelScope.launch {
            _registroState.value = AuthUiState.Loading
            isLoading = true
            val result = authRepository.registro(
                nombreUsuario = nombreUsuario,
                telefonoCelular = telefonoNormalizado,
                email = email,
                password = password,
                password2 = password2
            )
            _registroState.value = result.fold(
                onSuccess = { AuthUiState.Success("Registro exitoso. Te enviamos un OTP.") },
                onFailure = {
                    AuthUiState.Error(it.localizedMessage ?: "No se pudo completar el registro.")
                }
            )
            isLoading = false
        }
    }

    fun verificarFirebase(idToken: String) {
        if (idToken.isBlank()) {
            val message = "No se pudo validar el token de Firebase."
            errorMessage = message
            _firebaseVerifyState.value = AuthUiState.Error(message)
            return
        }

        viewModelScope.launch {
            _firebaseVerifyState.value = AuthUiState.Loading
            isLoading = true
            errorMessage = null
            val result = authRepository.firebaseVerify(idToken)
            _firebaseVerifyState.value = result.fold(
                onSuccess = { auth ->
                    loginState = auth
                    sharedPreferences.edit()
                        .putString("auth_token", auth.accessToken)
                        .putString("refresh_token", auth.refreshToken)
                        .apply()
                    errorMessage = null
                    AuthUiState.Success("Cuenta verificada correctamente.")
                },
                onFailure = { throwable ->
                    val message = when (throwable) {
                        is HttpException -> when (throwable.code()) {
                            401 -> "El backend rechazó el ID token de Firebase (401). Verifica Firebase Admin en Django, el proyecto Firebase correcto y reinstala la app con el google-services.json actualizado."
                            403 -> "El backend no autorizó la verificación Firebase (403). Revisa permisos o configuración del endpoint."
                            404 -> "El endpoint /accounts/firebase-verify/ no fue encontrado en ${BuildConfig.API_BASE_URL}."
                            400 -> {
                                val backendMessage = parseBackendErrorBody(throwable)
                                "Solicitud inválida (400): $backendMessage"
                            }
                            else -> {
                                val backendMessage = parseBackendErrorBody(throwable)
                                "Error del backend (${throwable.code()}) al verificar Firebase: $backendMessage"
                            }
                        }
                        is IOException -> "No se pudo conectar con el backend en ${BuildConfig.API_BASE_URL}. Revisa la IP local, el puerto 3000 y que el teléfono esté en la misma red Wi‑Fi."
                        else -> throwable.localizedMessage ?: "No se pudo verificar la sesión en backend."
                    }
                    errorMessage = message
                    AuthUiState.Error(message)
                }
            )
            isLoading = false
        }
    }

    fun resetRegistroUiState() {
        _registroState.value = AuthUiState.Idle
    }

    fun resetFirebaseVerifyUiState() {
        _firebaseVerifyState.value = AuthUiState.Idle
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
                isLoading = false
                registerSuccess = true
            } catch (e: IOException) {
                isLoading = false
                registerSuccess = true
            } catch (e: Exception) {
                isLoading = false
                registerSuccess = true
            } finally {
                isLoading = false
                registerSuccess = true
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
                authService.verifyCode(request)
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

    // --- FUNCIÓN DE ACTUALIZAR PERFIL ---
    fun attemptUpdateProfile(
        nombre_usuario: String,
        email: String,
        telefono_celular: String,
        fecha_nacimiento: String
    ) {
        updateErrorMessage(null)
        isLoading = true
        updateProfileSuccess = false

        viewModelScope.launch {
            try {
                val request = UserUpdateRequest(nombre_usuario, email, telefono_celular, fecha_nacimiento)
                val response = authService.updateProfile(request)

                // Actualizamos el estado local del usuario con los nuevos datos
                if (response.user != null) {
                    val currentLoginState = loginState
                    if (currentLoginState != null) {
                        val updatedUser = UserDto(
                            id = response.user.id,
                            nombreUsuario = response.user.nombreUsuario,
                            email = response.user.email,
                            telefonoCelular = response.user.telefonoCelular
                        )
                        loginState = currentLoginState.copy(user = updatedUser)
                    }
                }

                updateProfileSuccess = true
            } catch (e: HttpException) {
                errorMessage = "Error al actualizar perfil: ${e.message()}"
            } catch (e: IOException) {
                errorMessage = "Error de conexión al actualizar perfil."
            } catch (e: Exception) {
                errorMessage = "Ocurrió un error inesperado: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // --- FUNCIÓN DE AÑADIR PRODUCTO ---
    fun attemptAddProduct(
        name: String, description: String, stock: String, imageUrl: String, price: String,
        rating: String, reviewCount: String, category: String, size: String, deliveryTime: String, distance: String, discount: String
    ) {
        updateErrorMessage(null)

        val stockInt = stock.toIntOrNull()
        val ratingDouble = rating.toDoubleOrNull()
        val reviewCountInt = reviewCount.toIntOrNull()

        if (name.isBlank() || description.isBlank() || stock.isBlank() || imageUrl.isBlank() || price.isBlank() || rating.isBlank() || reviewCount.isBlank() || category.isBlank() || size.isBlank() || deliveryTime.isBlank() || distance.isBlank() || discount.isBlank()) {
            updateErrorMessage("Todos los campos son obligatorios.")
            return
        }

        if (stockInt == null || ratingDouble == null || reviewCountInt == null) {
            updateErrorMessage("Stock, Rating y Review Count deben ser números válidos.")
            return
        }

        isLoading = true
        addProductSuccess = false

        viewModelScope.launch {
            try {
                val request = AddProductRequest(
                    name = name,
                    description = description,
                    stock = stockInt,
                    imageUrl = imageUrl,
                    price = price,
                    rating = ratingDouble,
                    reviewCount = reviewCountInt,
                    category = category,
                    size = size,
                    deliveryTime = deliveryTime,
                    distance = distance,
                    discount = discount
                )
                val newProduct = authService.addProduct(request)
                kitchenList = kitchenList + newProduct
                addProductSuccess = true
            } catch (e: HttpException) {
                updateErrorMessage("Error al añadir el producto: ${e.message()}")
            } catch (e: IOException) {
                updateErrorMessage("Error de conexión. No se pudo añadir el producto.")
            } catch (e: Exception) {
                updateErrorMessage("Ocurrió un error inesperado: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // ✅ FUNCIÓN para cargar la lista de cocinas
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

    fun resetVerifyCodeState() {
        verifyCodeSuccess = false
        updateErrorMessage(null)
    }

    fun resetAddProductState() {
        addProductSuccess = false
        updateErrorMessage(null)
    }

    fun resetUpdateProfileState() {
        updateProfileSuccess = false
        updateErrorMessage(null)
    }

    fun logout() {
        loginState = null
        sharedPreferences.edit()
            .remove("auth_token")
            .remove("refresh_token")
            .apply()
    }

    private fun parseBackendErrorBody(httpException: HttpException): String {
        val raw = httpException.response()?.errorBody()?.string()?.trim().orEmpty()
        if (raw.isBlank()) return "sin detalle"

        return runCatching {
            val json = JSONObject(raw)
            when {
                json.has("mensaje") -> json.getString("mensaje")
                json.has("detail") -> json.getString("detail")
                json.has("id_token") -> "id_token: ${json.get("id_token")}" 
                else -> raw
            }
        }.getOrElse { raw }
    }
}
