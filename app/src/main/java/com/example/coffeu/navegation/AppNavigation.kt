package com.example.coffeu.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coffeu.ui.preview.SplashScreen
import com.example.coffeu.ui.auth.HomeScreen
import com.example.coffeu.ui.auth.LoginScreen
import com.example.coffeu.ui.auth.RegisterScreen
import com.example.coffeu.ui.password.NewPasswordScreen
import com.example.coffeu.ui.password.SendCodeScreen
import com.example.coffeu.ui.password.VerifyCodeScreen
import com.example.coffeu.ui.products.ProductDetailScreen
import com.example.coffeu.ui.profilensetting.ChangePasswordScreen
import com.example.coffeu.ui.profilensetting.EditProfileScreen
import com.example.coffeu.ui.profilensetting.NotificationsScreen
import com.example.coffeu.ui.profilensetting.ProfileScreen
import com.example.coffeu.ui.preview.PreviewScreen
import com.example.coffeu.ui.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.delay

// 1. Define las rutas de navegación de forma segura
object Screen {
    const val Splash = "splash_screen"
    const val Preview = "preview_screen"
    const val Login = "login_screen"
    const val Register = "register_screen"
    const val Home = "home_screen/{username}"
    const val Profile = "profile_screen"
    const val EditProfile = "edit_profile_screen"
    const val Notifications = "notifications_screen"
    const val ChangePassword = "change_password_screen"
    const val SendCode = "send_code_screen"
    const val VerifyCode = "verify_code_screen"
    const val NewPassword = "new_password_screen"
    const val ProductDetail = "product_detail_screen/{kitchenId}"
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash
    ) {

        // --- SPLASH SCREEN ---
        composable(Screen.Splash) {
            SplashScreen()
            LaunchedEffect(Unit) {
                delay(3000) // 3-second delay
                navController.navigate(Screen.Preview) {
                    popUpTo(Screen.Splash) { inclusive = true }
                }
            }
        }

        // --- PREVIEW SCREEN ---
        composable(Screen.Preview) {
            PreviewScreen(onNavigateToLogin = {
                navController.navigate(Screen.Login) {
                    popUpTo(Screen.Preview) { inclusive = true }
                }
            })
        }

        // --- LOGIN SCREEN ---
        composable(Screen.Login) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { token ->
                    val loginResponse = authViewModel.loginState
                    val username = loginResponse?.user?.nombreUsuario ?: "Invitado"
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
            val username = backStackEntry.arguments?.getString("username") ?: "Error"
            HomeScreen(
                username = username,
                authViewModel = authViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile)
                },
                onNavigateToProductDetail = { kitchenId ->
                    navController.navigate("product_detail_screen/$kitchenId")
                }
            )
        }

        // --- PRODUCT DETAIL SCREEN ---
        composable(
            route = Screen.ProductDetail,
            arguments = listOf(navArgument("kitchenId") { type = NavType.IntType })
        ) { backStackEntry ->
            val kitchenId = backStackEntry.arguments?.getInt("kitchenId")
            val kitchen = authViewModel.kitchenList.find { it.id == kitchenId }
            if (kitchen != null) {
                ProductDetailScreen(kitchen = kitchen)
            } else {
                // Si no se encuentra el producto, vuelve a la pantalla anterior.
                navController.popBackStack()
            }
        }

        // --- PROFILE SCREEN ---
        composable(Screen.Profile) {
            val user = authViewModel.loginState?.user
            if (user != null) {
                ProfileScreen(
                    userName = user.nombreUsuario,
                    userEmail = user.email,
                    onNavigateToEditProfile = {
                        navController.navigate(Screen.EditProfile)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Screen.Notifications)
                    },
                    onNavigateToChangePassword = {
                        navController.navigate(Screen.ChangePassword)
                    }
                )
            } else {
                // If user data is not available, navigate back to the login screen.
                navController.navigate(Screen.Login) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }

        // --- EDIT PROFILE SCREEN ---
        composable(Screen.EditProfile) {
            val user = authViewModel.loginState?.user
            if (user != null) {
                EditProfileScreen(
                    fullName = user.nombreUsuario,
                    email = user.email,
                    phoneNumber = user.telefonoCelular,
                    dateOfBirth = "", // Pass an empty string for date of birth
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            } else {
                // If user data is not available, navigate back to the login screen.
                navController.navigate(Screen.Login) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }

        // --- NOTIFICATIONS SCREEN ---
        composable(Screen.Notifications) {
            NotificationsScreen(onBackClicked = {
                navController.popBackStack()
            })
        }

        // --- CHANGE PASSWORD SCREEN ---
        composable(Screen.ChangePassword) {
            ChangePasswordScreen(
                onBackClicked = {
                    navController.popBackStack()
                },
                onForgotPasswordClicked = {
                    navController.navigate(Screen.SendCode)
                }
            )
        }

        // --- SEND CODE SCREEN ---
        composable(Screen.SendCode) {
            SendCodeScreen(onBackClicked = {
                navController.popBackStack()
            }, onContinueClicked = {
                navController.navigate(Screen.VerifyCode)
            })
        }

        // --- VERIFY CODE SCREEN ---
        composable(Screen.VerifyCode) {
            VerifyCodeScreen(
                onBackClicked = {
                    navController.popBackStack()
                },
                onContinueClicked = {
                    navController.navigate(Screen.NewPassword)
                }
            )
        }

        // --- NEW PASSWORD SCREEN ---
        composable(Screen.NewPassword) {
            NewPasswordScreen(onBackClicked = {
                navController.popBackStack()
            }, onCreatePasswordClicked = {
                // TODO: Handle create password and navigate
            })
        }
    }
}
