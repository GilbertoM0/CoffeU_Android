package com.example.coffeu.ui.auth

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeu.R
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.theme.PrimaryNormal
import com.example.coffeu.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (token: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val isLoading = authViewModel.isLoading
    val errorMessage = authViewModel.errorMessage
    val loginState = authViewModel.loginState

    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)
    }

    LaunchedEffect(loginState) {
        if (loginState != null) {
            with(sharedPreferences.edit()) {
                putBoolean("is_logged_in", true)
                putString("username", loginState.user.nombreUsuario)
                apply()
            }
            onLoginSuccess(loginState.token)
        }
    }

    LoginContent(
        isLoading = isLoading,
        errorMessage = errorMessage,
        onLoginClick = { id, pw -> authViewModel.attemptLogin(id, pw) },
        onUpdateError = { authViewModel.updateErrorMessage(it) },
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
fun LoginContent(
    isLoading: Boolean,
    errorMessage: String?,
    onLoginClick: (String, String) -> Unit,
    onUpdateError: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var identificador by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. IMAGEN DE FONDO
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. GRADIENTE PARA MEJOR CONTRASTE (Sustituye a la capa negra plana)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        // 3. CONTENIDO PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido de vuelta",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Inicia sesión para continuar con CasaGamu",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campos de texto estilo Glassmorphism
            OutlinedTextField(
                value = identificador,
                onValueChange = { identificador = it },
                label = { Text("Teléfono Celular", color = Color.White.copy(alpha = 0.7f)) },
                leadingIcon = { Icon(Icons.Default.Call, contentDescription = null, tint = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.15f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedLabelColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = Color.White.copy(alpha = 0.7f)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.15f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedLabelColor = Color.White
                )
            )

            TextButton(
                onClick = { /* Acción */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("¿Olvidaste tu contraseña?", color = Color.White.copy(alpha = 0.8f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (identificador.isNotBlank() && password.isNotBlank()) {
                        onLoginClick(identificador, password)
                    } else {
                        onUpdateError("Por favor, introduce tu telefono y contraseña.")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryNormal,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("INICIAR SESIÓN", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            if (errorMessage != null && !isLoading) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF5252),
                    modifier = Modifier.padding(top = 16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes una cuenta?", color = Color.White.copy(alpha = 0.7f))
                TextButton(onClick = onNavigateToRegister) {
                    Text("Regístrate", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(name = "Modo Claro", showBackground = true, showSystemUi = true)
@Preview(name = "Modo Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    CoffeUTheme {
        LoginContent(
            isLoading = false,
            errorMessage = "Por favor, introduce tu telefono y contraseña.",
            onLoginClick = { _, _ -> },
            onUpdateError = {},
            onNavigateToRegister = {}
        )
    }
}
