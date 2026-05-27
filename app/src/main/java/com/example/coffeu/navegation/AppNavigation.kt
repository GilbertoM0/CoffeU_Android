package com.example.coffeu.navigation

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coffeu.ui.auth.HomeScreen
import com.example.coffeu.ui.auth.LoginScreen
import com.example.coffeu.ui.auth.RegisterScreen
import com.example.coffeu.ui.password.NewPasswordScreen
import com.example.coffeu.ui.password.SendCodeScreen
import com.example.coffeu.ui.password.VerifyCodeScreen
import com.example.coffeu.ui.preview.PreviewScreen
import com.example.coffeu.ui.preview.SplashScreen
import com.example.coffeu.ui.products.AddProductScreen
import com.example.coffeu.ui.products.AllProductsScreen
import com.example.coffeu.ui.products.FavProductsScreen
import com.example.coffeu.ui.products.ProductDetailScreen
import com.example.coffeu.ui.profilensetting.ChangePasswordScreen
import com.example.coffeu.ui.profilensetting.EditProfileScreen
import com.example.coffeu.ui.notifications.NotificationsScreen
import com.example.coffeu.ui.profilensetting.ProfileScreen
import com.example.coffeu.ui.viewmodel.AuthViewModel
import com.example.coffeu.ui.myorder.MyOrderScreen
import com.example.coffeu.ui.qa.QaE2EScreen
import kotlinx.coroutines.delay

object Screen {
    const val ARG_USERNAME = "username"
    const val ARG_KITCHEN_ID = "kitchenId"
    const val ARG_PHONE_NUMBER = "phoneNumber"

    const val SPLASH = "splash_screen"
    const val PREVIEW = "preview_screen"
    const val LOGIN = "login_screen"
    const val REGISTER = "register_screen"
    const val HOME = "home_screen/{$ARG_USERNAME}"
    const val PROFILE = "profile_screen"
    const val EDIT_PROFILE = "edit_profile_screen"
    const val NOTIFICATIONS = "notifications_screen"
    const val CHANGE_PASSWORD = "change_password_screen"
    const val SEND_CODE = "send_code_screen"
    const val VERIFY_CODE = "verify_code_screen/{$ARG_PHONE_NUMBER}"
    const val NEW_PASSWORD = "new_password_screen"
    const val PRODUCT_DETAIL = "product_detail_screen/{$ARG_KITCHEN_ID}"
    const val ALL_PRODUCTS = "all_products_screen"
    const val FAVORITE_PRODUCTS = "favorite_products_screen"
    const val MY_ORDER = "my_order_screen"
    const val ADD_PRODUCT = "add_product_screen"
    const val QA_E2E = "qa_e2e_screen"

    fun homeRoute(username: String): String = "home_screen/${Uri.encode(username)}"
    fun productDetailRoute(kitchenId: Int): String = "product_detail_screen/$kitchenId"
    fun verifyCodeRoute(phoneNumber: String): String = "verify_code_screen/${Uri.encode(phoneNumber)}"
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)
    }

    val startDestination = Screen.SPLASH

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // --- SPLASH SCREEN ---
        composable(Screen.SPLASH) {
            SplashScreen()
            LaunchedEffect(Unit) {
                delay(3000)
                navController.navigate(Screen.PREVIEW) {
                    popUpTo(Screen.SPLASH) { inclusive = true }
                }
            }
        }

        // --- PREVIEW SCREEN ---
        composable(Screen.PREVIEW) {
            PreviewScreen(onNavigateToLogin = {
                navController.navigate(Screen.LOGIN) {
                    popUpTo(Screen.PREVIEW) { inclusive = true }
                }
            })
        }

        // --- LOGIN SCREEN ---
        composable(Screen.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { _ ->
                    val loginResponse = authViewModel.loginState
                    val loggedInUsername = loginResponse?.user?.nombreUsuario ?: "Invitado"
                    navController.navigate(Screen.homeRoute(loggedInUsername)) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.REGISTER) }
            )
        }

        // --- REGISTER SCREEN ---
        composable(Screen.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegistrationSuccess = { phoneNumber ->
                    navController.navigate(Screen.verifyCodeRoute(phoneNumber))
                },
                onNavigateToLogin = { navController.navigate(Screen.LOGIN) }
            )
        }

        // --- HOME SCREEN ---
        composable(
            route = Screen.HOME,
            arguments = listOf(navArgument(Screen.ARG_USERNAME) { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUsername = backStackEntry.arguments?.getString(Screen.ARG_USERNAME) ?: "Error"
            HomeScreen(
                username = currentUsername,
                authViewModel = authViewModel,
                onLogout = {
                    authViewModel.logout()
                    sharedPreferences.edit { clear() }
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onSearchClicked = {
                    navController.navigate(Screen.ALL_PRODUCTS)
                },
                onNotificationClicked = {
                    navController.navigate(Screen.NOTIFICATIONS)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.PROFILE)
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.FAVORITE_PRODUCTS)
                },
                onNavigateToMyOrder = {
                    navController.navigate(Screen.MY_ORDER)
                },
                onNavigateToProductDetail = { kitchenId ->
                    navController.navigate(Screen.productDetailRoute(kitchenId))
                },
                onNavigateToAllProducts = {
                    navController.navigate(Screen.ALL_PRODUCTS)
                },
                onNavigateToAddProduct = {
                    navController.navigate(Screen.ADD_PRODUCT)
                }
            )
        }

        // --- ALL PRODUCTS SCREEN ---
        composable(Screen.ALL_PRODUCTS) {
            AllProductsScreen(
                authViewModel = authViewModel,
                onBackClicked = { navController.popBackStack() },
                onProductClicked = { kitchenId ->
                    navController.navigate(Screen.productDetailRoute(kitchenId))
                }
            )
        }

        // --- FAVORITE PRODUCTS SCREEN ---
        composable(Screen.FAVORITE_PRODUCTS) {
            FavProductsScreen(
                authViewModel = authViewModel,
                onBackClicked = { navController.popBackStack() },
                onProductClicked = { kitchenId ->
                    navController.navigate(Screen.productDetailRoute(kitchenId))
                }
            )
        }

        // --- MY ORDER SCREEN ---
        composable(Screen.MY_ORDER) {
            MyOrderScreen(authViewModel = authViewModel)
        }

        // --- PRODUCT DETAIL SCREEN ---
        composable(
            route = Screen.PRODUCT_DETAIL,
            arguments = listOf(navArgument(Screen.ARG_KITCHEN_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val kitchenId = backStackEntry.arguments?.getInt(Screen.ARG_KITCHEN_ID)
            val kitchen = authViewModel.kitchenList.find { it.id == kitchenId }
            if (kitchen != null) {
                ProductDetailScreen(
                    kitchen = kitchen,
                    authViewModel = authViewModel,
                    onBackClicked = { navController.popBackStack() }
                )
            } else {
                navController.popBackStack()
            }
        }

        // --- PROFILE SCREEN ---
        composable(Screen.PROFILE) {
            val user = authViewModel.loginState?.user
            if (user != null) {
                ProfileScreen(
                    userName = user.nombreUsuario,
                    userEmail = user.email,
                    onNavigateToEditProfile = { navController.navigate(Screen.EDIT_PROFILE) },
                    onNavigateToNotifications = { navController.navigate(Screen.NOTIFICATIONS) },
                    onNavigateToChangePassword = { navController.navigate(Screen.CHANGE_PASSWORD) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.LOGIN) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }

        // --- EDIT PROFILE SCREEN ---
        composable(Screen.EDIT_PROFILE) {
            val user = authViewModel.loginState?.user
            if (user != null) {
                EditProfileScreen(
                    fullName = user.nombreUsuario,
                    email = user.email,
                    phoneNumber = user.telefonoCelular,
                    dateOfBirth = "",
                    onBackClicked = { navController.popBackStack() },
                    authViewModel = authViewModel
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }

        // --- NOTIFICATIONS SCREEN ---
        composable(Screen.NOTIFICATIONS) {
            NotificationsScreen(
                authViewModel = authViewModel,
                onBackClicked = { navController.popBackStack() }
            )
        }

        // --- CHANGE PASSWORD SCREEN ---
        composable(Screen.CHANGE_PASSWORD) {
            ChangePasswordScreen(
                onBackClicked = { navController.popBackStack() },
                onForgotPasswordClicked = { navController.navigate(Screen.SEND_CODE) },
                onCreateNewPasswordClicked = { _ ->
                    navController.navigate(Screen.NEW_PASSWORD)
                }
            )
        }

        // --- SEND CODE SCREEN ---
        composable(Screen.SEND_CODE) {
            SendCodeScreen(
                onBackClicked = { navController.popBackStack() },
                onContinueClicked = { navController.navigate(Screen.NEW_PASSWORD) }
            )
        }

        // --- VERIFY CODE SCREEN ---
        composable(
            route = Screen.VERIFY_CODE,
            arguments = listOf(navArgument(Screen.ARG_PHONE_NUMBER) { type = NavType.StringType })
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString(Screen.ARG_PHONE_NUMBER).orEmpty()
            VerifyCodeScreen(
                phoneNumber = phoneNumber,
                authViewModel = authViewModel,
                onBackClicked = { navController.popBackStack() },
                onVerificationSuccess = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(Screen.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // --- NEW PASSWORD SCREEN ---
        composable(Screen.NEW_PASSWORD) {
            NewPasswordScreen(
                onBackClicked = { navController.popBackStack() },
                onCreatePasswordClicked = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // --- ADD PRODUCT SCREEN ---
        composable(Screen.ADD_PRODUCT) {
            AddProductScreen(
                authViewModel = authViewModel,
                onProductAdded = { navController.popBackStack() },
                onBackClicked = { navController.popBackStack() }
            )
        }

        // --- QA E2E SCREEN (solo para pruebas) ---
        composable(Screen.QA_E2E) {
            QaE2EScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }
    }
}