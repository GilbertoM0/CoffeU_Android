// Archivo: AppNavigation.kt
package com.example.coffeu.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coffeu.ui.auth.HomeScreen
import com.example.coffeu.ui.auth.LoginScreen
import com.example.coffeu.ui.auth.RegisterScreen
import com.example.coffeu.ui.viewmodel.AuthViewModel // Importa el ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel // Importa el proveedor de ViewModel

// 1. Define las rutas de navegación de forma segura
object Screen {
    const val Login = "login_screen"
    const val Register = "register_screen"
    const val Home = "home_screen"
}

@Composable
fun AppNavigation(
    // Opcional: Puedes pasar el ViewModel aquí si lo creas en MainActivity
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login // Comienza siempre en la pantalla de Login
    ) {
        // --- LOGIN SCREEN ---
        composable(Screen.Login) {
            // Pasaremos el AuthViewModel a LoginScreen para manejar la lógica de inicio de sesión
            LoginScreen(
                // Al iniciar sesión exitosamente, navega al Home
                onLoginSuccess = {
                    navController.navigate(Screen.Home) {
                        // Limpia la pila para que el botón "Atrás" no regrese al Login
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                // Navega a la pantalla de Registro
                onNavigateToRegister = {
                    navController.navigate(Screen.Register)
                },
                // Pasa el ViewModel
                // viewModel = authViewModel
            )
        }

        // --- REGISTER SCREEN ---
        composable(Screen.Register) {
            RegisterScreen(
                // Al registrarse exitosamente, vuelve al Login
                onRegistrationSuccess = {
                    navController.navigate(Screen.Login) {
                        // Opcional: Limpiar el registro si vienes directamente del Home y te registras de nuevo
                        popUpTo(Screen.Register) { inclusive = true }
                    }
                },
                // Pasa el ViewModel
                // viewModel = authViewModel
            )
        }

        // --- HOME SCREEN ---
        composable(Screen.Home) {
            HomeScreen(
                // Puedes agregar una función para cerrar sesión aquí
                onLogout = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                }
            )
        }
    }
}