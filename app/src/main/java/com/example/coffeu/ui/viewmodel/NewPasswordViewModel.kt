package com.example.coffeu.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeu.data.api.AuthService
import com.example.coffeu.data.model.ChangePasswordRequest
import com.example.coffeu.data.model.ResetPasswordRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPasswordViewModel @Inject constructor(
    private val authService: AuthService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val currentPassword: String? = savedStateHandle.get<String>("currentPassword")
    private val token: String? = savedStateHandle.get<String>("token")

    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    private val _uiState = MutableStateFlow<NewPasswordUiState>(NewPasswordUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun createNewPassword() {
        viewModelScope.launch {
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                _uiState.value = NewPasswordUiState.Error("Password fields cannot be empty.")
                return@launch
            }

            if (newPassword != confirmPassword) {
                _uiState.value = NewPasswordUiState.Error("Passwords do not match.")
                return@launch
            }

            if (newPassword.length < 8) {
                _uiState.value = NewPasswordUiState.Error("Password must be at least 8 characters long.")
                return@launch
            }

            _uiState.value = NewPasswordUiState.Loading
            try {
                if (!currentPassword.isNullOrBlank()) {
                    val request = ChangePasswordRequest(currentPassword = currentPassword, newPassword = newPassword)
                    authService.changePassword(request)
                } else if (!token.isNullOrBlank()) {
                    val request = ResetPasswordRequest(token = token, newPassword = newPassword)
                    authService.resetPassword(request)
                } else {
                    _uiState.value = NewPasswordUiState.Error("Invalid operation.")
                    return@launch
                }
                _uiState.value = NewPasswordUiState.Success
            } catch (e: Exception) {
                _uiState.value = NewPasswordUiState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }

    fun resetState() {
        _uiState.value = NewPasswordUiState.Idle
    }
}

sealed class NewPasswordUiState {
    object Idle : NewPasswordUiState()
    object Loading : NewPasswordUiState()
    object Success : NewPasswordUiState()
    data class Error(val message: String) : NewPasswordUiState()
}
