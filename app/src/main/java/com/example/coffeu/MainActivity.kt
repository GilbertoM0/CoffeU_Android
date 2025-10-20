// Archivo: MainActivity.kt
package com.example.coffeu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
// *************** Descomentar ******************
import com.example.coffeu.navigation.AppNavigation
//import androidx.lifecycle.viewmodel.compose.viewModel
// **********************************************
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoffeUTheme {
                // 1. Llama a AppNavigation para iniciar el sistema
                AppNavigation()

                // 2. Opcional: Si quieres pasar el ViewModel desde aquí (depende de tu arquitectura)
                // val authViewModel: AuthViewModel = viewModel()
                // AppNavigation(authViewModel = authViewModel)
            }
        }
    }
}
// ¡Tu MainActivity está listo!