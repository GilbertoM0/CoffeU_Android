package com.example.coffeu.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegistrationSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel() // Obtener el ViewModel
) {
    // Estados locales para los campos de texto
    var email by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }
    var telefonoCelular by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var password2Visible by remember { mutableStateOf(false) }

    // --- ESTADOS DEL VIEWMODEL (OBSERVABLES) ---
    val isLoading = authViewModel.isLoading
    val errorMessage = authViewModel.errorMessage
    val registerSuccess = authViewModel.registerSuccess

    // 1. Efecto: Reaccionar al registro exitoso (navegación)
    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            authViewModel.resetRegisterState() // Limpiar el estado
            onRegistrationSuccess() // Navegar a Login (callback de AppNavigation)
        }
    }

    // 2. Efecto: Limpiar el error cuando los campos cambian (Opcional, pero útil para UX)
    LaunchedEffect(email, password, password2) {
        authViewModel.updateErrorMessage(null) // Usamos la función corregida
    }

    // Usaremos un Box para centrar el contenido y establecer el fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                // --- Encabezado ---
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Crea tu cuenta",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Únete a CoffeU y disfruta de nuestros beneficios",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // --- Campos de Texto (MANTENIDOS) ---
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = nombreUsuario,
                    onValueChange = { nombreUsuario = it },
                    label = { Text("Nombre de Usuario") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nombre de Usuario") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = telefonoCelular,
                    onValueChange = { telefonoCelular = it },
                    label = { Text("Teléfono Celular") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Teléfono Celular") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                            Icon(imageVector = image, contentDescription = "Toggle password visibility")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password2,
                    onValueChange = { password2 = it },
                    label = { Text("Confirmar Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirmar Contraseña") },
                    visualTransformation = if (password2Visible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (password2Visible) Icons.Filled.Lock else Icons.Filled.Lock
                        IconButton(onClick = { password2Visible = !password2Visible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password2 visibility")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Términos y Condiciones
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var checked by remember { mutableStateOf(false) }
                    Checkbox(checked = checked, onCheckedChange = { checked = it })
                    Spacer(Modifier.width(8.dp))
                    val annotatedString = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))) {
                            append("Al registrarte, aceptas nuestros ")
                        }
                        pushStringAnnotation(tag = "URL", annotation = "https://your.terms.url")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                            append("Términos de Uso")
                        }
                        pop()
                    }

                    ClickableText(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodySmall,
                        onClick = {
                            // TODO: Lógica para abrir los términos de uso
                        }
                    )
                }


                Spacer(modifier = Modifier.height(32.dp))

                // --- Botón de Registrarse CONECTADO A LA LÓGICA ---
                Button(
                    onClick = {
                        // Llama a la función del ViewModel con los datos
                        authViewModel.attemptRegister(
                            email = email,
                            nombre_usuario = nombreUsuario,
                            telefono_celular = telefonoCelular,
                            password = password,
                            password2 = password2
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    enabled = !isLoading, // Deshabilita el botón mientras carga
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "REGISTRARSE",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // --- Mostrar Error ---
                if (errorMessage != null && !isLoading) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                // Enlace a Iniciar Sesión si ya tienen cuenta
                Spacer(modifier = Modifier.height(32.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "¿Ya tienes una cuenta?",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                            text = "Iniciar sesión",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    CoffeUTheme {
        RegisterScreen(onRegistrationSuccess = {})
    }
}