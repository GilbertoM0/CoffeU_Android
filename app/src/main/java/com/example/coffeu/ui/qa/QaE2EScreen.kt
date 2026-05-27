package com.example.coffeu.ui.qa

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coffeu.ui.auth.normalizarTelefonoParaBackend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QaE2EScreen(
    onBackClicked: () -> Unit = {},
    viewModel: QaE2EViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }

    val statusText = viewModel.statusText
    val isLoading = viewModel.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "QA – Firebase OTP E2E",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ── PASO 1: Datos de registro ──────────────────────────────────
            SectionTitle("Paso 1 · Registrar usuario en backend")

            QaTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre de usuario")
            QaTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = "Teléfono (ej: +523531265044 o 3531265044)",
                keyboardType = KeyboardType.Phone
            )
            QaTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                keyboardType = KeyboardType.Email
            )
            QaTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true
            )
            QaTextField(
                value = password2,
                onValueChange = { password2 = it },
                label = "Confirmar password",
                isPassword = true
            )

            Button(
                onClick = {
                    viewModel.registrar(
                        nombre = nombre.trim(),
                        telefono = telefono.trim().normalizarTelefonoParaBackend(),
                        email = email.trim(),
                        password = password,
                        password2 = password2
                    )
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("1 · REGISTRAR en /accounts/registro/", fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(thickness = 2.dp)

            // ── PASO 2: Enviar OTP Firebase ────────────────────────────────
            SectionTitle("Paso 2 · Enviar OTP por Firebase")

            Button(
                onClick = {
                    if (activity != null) {
                        viewModel.enviarOtpFirebase(
                            activity = activity,
                            telefono = telefono.trim().normalizarTelefonoParaBackend()
                        )
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("2 · ENVIAR OTP Firebase", fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(thickness = 2.dp)

            // ── PASO 3: OTP manual + idToken al backend ────────────────────
            SectionTitle("Paso 3 · Verificar OTP → idToken → Backend")

            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) otpCode = it },
                label = { Text("Código OTP recibido por SMS (6 dígitos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                supportingText = { Text("${otpCode.length}/6 dígitos") }
            )

            Button(
                onClick = { viewModel.verificarOtpYEnviarIdToken(otpCode) },
                enabled = !isLoading && otpCode.length == 6,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text("3 · VERIFICAR OTP → idToken → /accounts/firebase-verify/",
                    fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(thickness = 2.dp)

            // ── ESTADO ACTUAL ──────────────────────────────────────────────
            SectionTitle("Estado actual")

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 120.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp
            ) {
                Text(
                    text = statusText,
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun QaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )
}
