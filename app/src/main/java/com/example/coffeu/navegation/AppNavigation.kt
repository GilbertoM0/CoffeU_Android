package com.example.coffeu.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coffeu.ui.auth.HomeScreen
import com.example.coffeu.ui.auth.LoginScreen
import com.example.coffeu.ui.auth.RegisterScreen
import com.example.coffeu.ui.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

// 1. Define las rutas de navegación de forma segura
object Screen {
    const val Login = "login_screen"
    const val Register = "register_screen"
    const val Home = "home_screen/{username}"
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login
    ) {
        // --- LOGIN SCREEN ---
        composable(Screen.Login) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { token ->
                    val loginResponse = authViewModel.loginState

                    // ✅ CORRECCIÓN CLAVE: Accedemos al objeto anidado 'user' y a la propiedad 'nombreUsuario'.
                    // El encadenamiento ?. y el operador Elvis (?:) garantizan que NO haya crasheos.
                    val username = loginResponse?.user?.nombreUsuario ?: "Invitado"

                    // 3. Navegamos, reemplazando el placeholder {username}
                    navController.navigate(Screen.Home.replace("{username}", username)) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register)
                }
            )
        }

        // --- REGISTER SCREEN ---
        composable(Screen.Register) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegistrationSuccess = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Register) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login)
                }
            )
        }

        // --- HOME SCREEN ---
        composable(
            route = Screen.Home,
            arguments = listOf(navArgument("username") {
                type = NavType.StringType
                defaultValue = "Error"
            })
        ) { backStackEntry ->
            // Se lee el argumento que viene de la navegación
            val username = backStackEntry.arguments?.getString("username") ?: "Error"

            HomeScreen(
                username = username,
                onLogout = {
                    authViewModel.logout() // Limpia el estado del ViewModel
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                }
            )
        }
    }
}