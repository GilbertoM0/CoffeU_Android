package com.example.coffeu.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeu.data.api.AuthService
import com.example.coffeu.data.model.ResetPasswordRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NewPasswordViewModel @Inject constructor(
    private val authService: AuthService,
    @Suppress("UNUSED_PARAMETER") savedStateHandle: SavedStateHandle
) : ViewModel() {

    var identifier by mutableStateOf("")
    var otp by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    private val _uiState = MutableStateFlow<NewPasswordUiState>(NewPasswordUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun createNewPassword() {
        viewModelScope.launch {
            if (identifier.isBlank() || otp.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                _uiState.value = NewPasswordUiState.Error("Completa correo/teléfono, OTP y ambas contraseñas.")
                return@launch
            }

            if (otp.length != 6) {
                _uiState.value = NewPasswordUiState.Error("El OTP debe tener 6 dígitos.")
                return@launch
            }

            if (newPassword != confirmPassword) {
                _uiState.value = NewPasswordUiState.Error("Las contraseñas no coinciden.")
                return@launch
            }

            if (!isStrongPassword(newPassword)) {
                _uiState.value = NewPasswordUiState.Error("La contraseña debe tener al menos 8 caracteres, mayúscula, minúscula, número y símbolo.")
                return@launch
            }

            _uiState.value = NewPasswordUiState.Loading
            try {
                val request = if (identifier.contains("@")) {
                    ResetPasswordRequest(
                        email = identifier.trim(),
                        otp = otp.trim(),
                        newPassword = newPassword,
                        newPassword2 = confirmPassword
                    )
                } else {
                    ResetPasswordRequest(
                        telefonoCelular = identifier.trim(),
                        otp = otp.trim(),
                        newPassword = newPassword,
                        newPassword2 = confirmPassword
                    )
                }
                authService.resetPassword(request)
                _uiState.value = NewPasswordUiState.Success
            } catch (e: HttpException) {
                _uiState.value = NewPasswordUiState.Error("No se pudo restablecer la contraseña. Verifica OTP, expiración o datos del usuario.")
            } catch (e: IOException) {
                _uiState.value = NewPasswordUiState.Error("Error de red o timeout. Revisa conexión y servidor.")
            } catch (e: Exception) {
                _uiState.value = NewPasswordUiState.Error("Ocurrió un error inesperado al restablecer la contraseña.")
            }
        }
    }

    fun requestOtp() {
        viewModelScope.launch {
            if (identifier.isBlank()) {
                _uiState.value = NewPasswordUiState.Error("Ingresa correo o teléfono para enviar OTP.")
                return@launch
            }

            _uiState.value = NewPasswordUiState.Loading
            try {
                val request = if (identifier.contains("@")) {
                    com.example.coffeu.data.model.ForgotPasswordRequest(email = identifier.trim())
                } else {
                    com.example.coffeu.data.model.ForgotPasswordRequest(telefonoCelular = identifier.trim())
                }
                authService.forgotPassword(request)
                _uiState.value = NewPasswordUiState.OtpSent
            } catch (e: HttpException) {
                _uiState.value = NewPasswordUiState.Error("No se pudo enviar OTP. Verifica si el usuario existe.")
            } catch (e: IOException) {
                _uiState.value = NewPasswordUiState.Error("Error de red o timeout al solicitar OTP.")
            } catch (e: Exception) {
                _uiState.value = NewPasswordUiState.Error("Ocurrió un error inesperado al solicitar OTP.")
            }
        }
    }

    fun resetState() {
        _uiState.value = NewPasswordUiState.Idle
    }

    private fun isStrongPassword(password: String): Boolean {
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }
        return password.length >= 8 && hasUpper && hasLower && hasDigit && hasSymbol
    }
}

sealed class NewPasswordUiState {
    object Idle : NewPasswordUiState()
    object Loading : NewPasswordUiState()
    object OtpSent : NewPasswordUiState()
    object Success : NewPasswordUiState()
    data class Error(val message: String) : NewPasswordUiState()
}
