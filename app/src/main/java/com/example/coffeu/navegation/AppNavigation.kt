package com.example.coffeu.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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
import com.example.coffeu.ui.products.AllProductsScreen
import com.example.coffeu.ui.products.FavProductsScreen
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
    const val AllProducts = "all_products_screen"
    const val FavoriteProducts = "favorite_products_screen"
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)
    }

    val isLoggedIn = remember { sharedPreferences.getBoolean("is_logged_in", false) }
    val username = remember { sharedPreferences.getString("username", "") ?: "" }

    val startDestination = if (isLoggedIn) {
        Screen.Home.replace("{username}", username)
    } else {
        Screen.Splash
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
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
                    val loggedInUsername = loginResponse?.user?.nombreUsuario ?: "Invitado"
                    navController.navigate(Screen.Home.replace("{username}", loggedInUsername)) {
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
            val currentUsername = backStackEntry.arguments?.getString("username") ?: "Error"
            HomeScreen(
                username = currentUsername,
                authViewModel = authViewModel,
                onLogout = {
                    authViewModel.logout()
                    with(sharedPreferences.edit()) {
                        clear()
                        apply()
                    }
                    navController.navigate(Screen.Login) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onSearchClicked = {
                    navController.navigate(Screen.AllProducts)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile)
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.FavoriteProducts)
                },
                onNavigateToProductDetail = { kitchenId ->
                    navController.navigate("product_detail_screen/$kitchenId")
                },
                onNavigateToAllProducts = {
                    navController.navigate(Screen.AllProducts)
                }
            )
        }

        // --- ALL PRODUCTS SCREEN ---
        composable(Screen.AllProducts) {
            AllProductsScreen(
                authViewModel = authViewModel,
                onBackClicked = { navController.popBackStack() },
                onProductClicked = { kitchenId ->
                    navController.navigate("product_detail_screen/$kitchenId")
                }
            )
        }

        // --- FAVORITE PRODUCTS SCREEN ---
        composable(Screen.FavoriteProducts) {
            FavProductsScreen(
                authViewModel = authViewModel,
                onBackClicked = { navController.popBackStack() },
                onProductClicked = { kitchenId ->
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
                ProductDetailScreen(
                    kitchen = kitchen,
                    authViewModel = authViewModel,
                    onBackClicked = { navController.popBackStack() } // ✅ Conectado
                )
            } else {
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
                    dateOfBirth = "",
                    onBackClicked = { navController.popBackStack() }
                )
            } else {
                navController.navigate(Screen.Login) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }

        // --- NOTIFICATIONS SCREEN ---
        composable(Screen.Notifications) {
            NotificationsScreen(onBackClicked = { navController.popBackStack() })
        }

        // --- CHANGE PASSWORD SCREEN ---
        composable(Screen.ChangePassword) {
            ChangePasswordScreen(
                onBackClicked = { navController.popBackStack() },
                onForgotPasswordClicked = { navController.navigate(Screen.SendCode) }
            )
        }

        // --- SEND CODE SCREEN ---
        composable(Screen.SendCode) {
            SendCodeScreen(
                onBackClicked = { navController.popBackStack() },
                onContinueClicked = { navController.navigate(Screen.VerifyCode) }
            )
        }

        // --- VERIFY CODE SCREEN ---
        composable(Screen.VerifyCode) {
            VerifyCodeScreen(
                onBackClicked = { navController.popBackStack() },
                onContinueClicked = { navController.navigate(Screen.NewPassword) }
            )
        }

        // --- NEW PASSWORD SCREEN ---
        composable(Screen.NewPassword) {
            NewPasswordScreen(
                onBackClicked = { navController.popBackStack() },
                onCreatePasswordClicked = { /* TODO */ }
            )
        }
    }
}
