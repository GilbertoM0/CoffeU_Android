package com.example.coffeu.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.coffeu.ui.profilensetting.ChangePasswordScreen
import com.example.coffeu.ui.profilensetting.EditProfileScreen
import com.example.coffeu.ui.profilensetting.NotificationsScreen
import com.example.coffeu.ui.profilensetting.ProfileScreen
import com.example.coffeu.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

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
    const val NewPassword = "new_password_screen?currentPassword={currentPassword}&token={token}"
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

        composable(Screen.Splash) {
            SplashScreen()
            LaunchedEffect(Unit) {
                delay(3000)
                navController.navigate(Screen.Preview) {
                    popUpTo(Screen.Splash) { inclusive = true }
                }
            }
        }

        composable(Screen.Preview) {
            PreviewScreen(onNavigateToLogin = {
                navController.navigate(Screen.Login) {
                    popUpTo(Screen.Preview) { inclusive = true }
                }
            })
        }

        composable(Screen.Login) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { token ->
                    val username = authViewModel.loginState?.user?.nombreUsuario ?: "Invitado"
                    navController.navigate(Screen.Home.replace("{username}", username)) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register) }
            )
        }

        composable(Screen.Register) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegistrationSuccess = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Register) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate(Screen.Login) }
            )
        }

        composable(
            route = Screen.Home,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: "Error"
            HomeScreen(
                username = username,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onNavigateToProfile = { navController.navigate(Screen.Profile) }
            )
        }

        composable(Screen.Profile) {
            val user = authViewModel.loginState?.user
            if (user != null) {
                ProfileScreen(
                    userName = user.nombreUsuario,
                    userEmail = user.email,
                    onNavigateToEditProfile = { navController.navigate(Screen.EditProfile) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications) },
                    onNavigateToChangePassword = { navController.navigate(Screen.ChangePassword) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                )
            } else {
                navController.navigate(Screen.Login) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }

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

        composable(Screen.Notifications) {
            NotificationsScreen(onBackClicked = { navController.popBackStack() })
        }

        composable(Screen.ChangePassword) {
            ChangePasswordScreen(
                onBackClicked = { navController.popBackStack() },
                onForgotPasswordClicked = { navController.navigate(Screen.SendCode) },
                onCreateNewPasswordClicked = { currentPassword ->
                    navController.navigate("new_password_screen?currentPassword=$currentPassword&token=")
                }
            )
        }

        composable(Screen.SendCode) {
            SendCodeScreen(
                onBackClicked = { navController.popBackStack() },
                onContinueClicked = { 
                    navController.navigate(Screen.VerifyCode)
                }
            )
        }

        composable(Screen.VerifyCode) {
            VerifyCodeScreen(
                onBackClicked = { navController.popBackStack() },
                onContinueClicked = { token ->
                    navController.navigate("new_password_screen?currentPassword=&token=$token")
                }
            )
        }

        composable(
            route = Screen.NewPassword,
            arguments = listOf(
                navArgument("currentPassword") { 
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("token") { 
                    type = NavType.StringType
                    nullable = true 
                }
            )
        ) {
            NewPasswordScreen(
                onBackClicked = { navController.popBackStack() },
                onCreatePasswordClicked = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }
    }
}