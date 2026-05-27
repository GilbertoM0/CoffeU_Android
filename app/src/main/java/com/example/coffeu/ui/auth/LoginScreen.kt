package com.example.coffeu.ui.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.core.content.edit
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.viewmodel.AuthUiState
import com.example.coffeu.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

private const val GOOGLE_LOGIN_TAG = "GoogleLogin"

@Composable
fun LoginScreen(
    onLoginSuccess: (accessToken: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var identificador by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // --- ESTADOS DEL VIEWMODEL (OBSERVABLES) ---
    val isLoading = authViewModel.isLoading
    val errorMessage = authViewModel.errorMessage
    val loginState = authViewModel.loginState
    val firebaseVerifyState by authViewModel.firebaseVerifyState.collectAsState()

    val context = LocalContext.current
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val sharedPreferences = remember {
        context.getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)
    }

    // 1. EFECTO: Reaccionar al Login exitoso
    LaunchedEffect(loginState) {
        if (loginState != null) {
            Log.d(GOOGLE_LOGIN_TAG, "LOGIN_OK usuario=${loginState.user.email}")
            sharedPreferences.edit {
                putBoolean("is_logged_in", true)
                putString("username", loginState.user.nombreUsuario)
            }
            onLoginSuccess(loginState.accessToken)
        }
    }

    LaunchedEffect(firebaseVerifyState) {
        when (val state = firebaseVerifyState) {
            is AuthUiState.Loading -> Log.d(GOOGLE_LOGIN_TAG, "BACKEND_VERIFY_LOADING")
            is AuthUiState.Success -> Log.d(GOOGLE_LOGIN_TAG, "BACKEND_VERIFY_OK mensaje=${state.message}")
            is AuthUiState.Error -> Log.e(GOOGLE_LOGIN_TAG, "BACKEND_VERIFY_FAIL mensaje=${state.message}")
            AuthUiState.Idle -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Encabezado ---
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Bienvenido de vuelta",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Inicia sesión para continuar con CoffeU",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // --- Campos de Texto ---
            OutlinedTextField(
                value = identificador,
                onValueChange = { identificador = it },
                label = { Text("Telefono Celular") },
                leadingIcon = { Icon(Icons.Default.Call, contentDescription = "Identificador") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Lock else Icons.Filled.Lock
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector  = image, contentDescription = "Toggle password visibility")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Olvidé Contraseña
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { /* Acción para Olvidé Contraseña */ }) {
                    Text("¿Olvidaste tu contraseña?", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Botón de Iniciar Sesión CONECTADO A LA LÓGICA ---
            Button(
                onClick = {
                    // 2. ACCIÓN: valida campos y llama al ViewModel para iniciar la red
                    if (identificador.isNotBlank() && password.isNotBlank()) {
                        authViewModel.attemptLogin(identificador, password)
                    } else {
                        // CORRECCIÓN: Usamos la función pública updateErrorMessage()
                        authViewModel.updateErrorMessage("Por favor, introduce tu telefono y contraseña.")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                enabled = !isLoading, // Deshabilita mientras carga
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "INICIAR SESIÓN",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // --- Mostrar Error ---
            if (errorMessage != null && !isLoading) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    Log.d(GOOGLE_LOGIN_TAG, "GOOGLE_BUTTON_CLICK")
                    val webClientId = resolveWebClientId(context)
                    if (webClientId.isNullOrBlank()) {
                        Log.e(GOOGLE_LOGIN_TAG, "CONFIG_FAIL default_web_client_id no encontrado")
                        authViewModel.updateErrorMessage(
                            "No se encontró default_web_client_id. Verifica tu google-services.json."
                        )
                        return@OutlinedButton
                    }
                    val activity = context.findActivity()
                    if (activity == null) {
                        Log.e(GOOGLE_LOGIN_TAG, "CONTEXT_FAIL activity nula")
                        authViewModel.updateErrorMessage(
                            "No se encontró Activity para iniciar Google Sign-In."
                        )
                        return@OutlinedButton
                    }

                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setServerClientId(webClientId)
                        .setFilterByAuthorizedAccounts(false)
                        .setAutoSelectEnabled(false)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val authorizedGoogleIdOption = GetGoogleIdOption.Builder()
                        .setServerClientId(webClientId)
                        .setFilterByAuthorizedAccounts(true)
                        .setAutoSelectEnabled(false)
                        .build()

                    val authorizedRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(authorizedGoogleIdOption)
                        .build()

                    coroutineScope.launch {
                        authViewModel.updateErrorMessage(null)
                        try {
                            Log.d(GOOGLE_LOGIN_TAG, "CREDENTIAL_REQUEST_START")
                            val result = try {
                                credentialManager.getCredential(
                                    context = activity,
                                    request = authorizedRequest
                                )
                            } catch (firstTry: NoCredentialException) {
                                Log.w(
                                    GOOGLE_LOGIN_TAG,
                                    "NO_AUTHORIZED_CREDENTIALS -> retry con cualquier cuenta Google"
                                )
                                credentialManager.getCredential(
                                    context = activity,
                                    request = request
                                )
                            }
                            Log.d(GOOGLE_LOGIN_TAG, "CREDENTIAL_REQUEST_OK")
                            val credential = result.credential
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            val googleIdToken = googleIdTokenCredential.idToken

                            if (googleIdToken.isBlank()) {
                                authViewModel.updateErrorMessage("No se pudo obtener el token de Google.")
                                return@launch
                            }

                            Log.d(GOOGLE_LOGIN_TAG, "FIREBASE_SIGNIN_START")
                            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                            firebaseAuth.signInWithCredential(firebaseCredential)
                                .addOnSuccessListener { authResult ->
                                    Log.d(GOOGLE_LOGIN_TAG, "FIREBASE_SIGNIN_OK")
                                    val firebaseUser = authResult.user
                                    if (firebaseUser == null) {
                                        Log.e(GOOGLE_LOGIN_TAG, "FIREBASE_USER_NULL")
                                        authViewModel.updateErrorMessage("No se pudo autenticar con Firebase.")
                                        return@addOnSuccessListener
                                    }

                                    Log.d(GOOGLE_LOGIN_TAG, "FIREBASE_IDTOKEN_START")
                                    firebaseUser.getIdToken(true)
                                        .addOnSuccessListener { tokenResult ->
                                            val firebaseIdToken = tokenResult.token
                                            if (firebaseIdToken.isNullOrBlank()) {
                                                Log.e(GOOGLE_LOGIN_TAG, "FIREBASE_IDTOKEN_EMPTY")
                                                authViewModel.updateErrorMessage("Firebase no devolvió un id_token válido.")
                                                return@addOnSuccessListener
                                            }
                                            Log.d(GOOGLE_LOGIN_TAG, "FIREBASE_IDTOKEN_OK -> BACKEND_VERIFY_START")
                                            authViewModel.verificarFirebase(firebaseIdToken)
                                        }
                                        .addOnFailureListener { error ->
                                            Log.e(GOOGLE_LOGIN_TAG, "FIREBASE_IDTOKEN_FAIL", error)
                                            authViewModel.updateErrorMessage(
                                                "Error obteniendo token de Firebase: ${error.localizedMessage ?: "sin detalle"}"
                                            )
                                        }
                                }
                                .addOnFailureListener { error ->
                                    Log.e(GOOGLE_LOGIN_TAG, "FIREBASE_SIGNIN_FAIL", error)
                                    authViewModel.updateErrorMessage(
                                        "Falló la autenticación con Google/Firebase: ${error.localizedMessage ?: "sin detalle"}"
                                    )
                                }
                        } catch (e: GoogleIdTokenParsingException) {
                            Log.e(GOOGLE_LOGIN_TAG, "CREDENTIAL_TOKEN_PARSE_FAIL", e)
                            authViewModel.updateErrorMessage(
                                "No se pudo leer el token de Google: ${e.localizedMessage ?: "sin detalle"}"
                            )
                        } catch (e: NoCredentialException) {
                            Log.e(GOOGLE_LOGIN_TAG, "NO_CREDENTIALS_AVAILABLE", e)
                            authViewModel.updateErrorMessage(
                                "No hay credenciales de Google disponibles. Agrega una cuenta Google en el dispositivo o prueba en un teléfono real."
                            )
                        } catch (e: GetCredentialException) {
                            Log.e(GOOGLE_LOGIN_TAG, "CREDENTIAL_REQUEST_FAIL", e)
                            authViewModel.updateErrorMessage(
                                "Google Sign-In cancelado o fallido: ${e.localizedMessage ?: "sin detalle"}"
                            )
                        } catch (e: Exception) {
                            Log.e(GOOGLE_LOGIN_TAG, "GOOGLE_LOGIN_UNEXPECTED", e)
                            authViewModel.updateErrorMessage(
                                "Error inesperado al iniciar con Google: ${e.localizedMessage ?: "sin detalle"}"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "G",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continuar con Google",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }


            // Enlace a Registrarse
            Spacer(modifier = Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¿No tienes una cuenta?",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Regístrate",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun resolveWebClientId(context: Context): String? {
    val id = context.resources.getIdentifier(
        "default_web_client_id",
        "string",
        context.packageName
    )
    if (id == 0) return null
    return context.getString(id)
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    CoffeUTheme {
        Surface {
            Text("Preview de Login: disponible en ejecución con Hilt")
        }
    }
}
