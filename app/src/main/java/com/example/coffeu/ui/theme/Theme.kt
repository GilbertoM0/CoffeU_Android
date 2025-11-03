package com.example.coffeu.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = LabelPrimary,
    secondary = PrimaryLightActive,
    onSecondary = LabelPrimary,
    tertiary = SystemOrange,
    onTertiary = LabelPrimary,
    background = LabelPrimary,
    onBackground = LabelTertiary,
    surface = LabelSecondary,
    onSurface = LabelTertiary,
    error = SystemRed,
    onError = LabelTertiary
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryNormal,
    onPrimary = LabelTertiary,
    secondary = PrimaryLight,
    onSecondary = LabelPrimary,
    tertiary = SystemBlue,
    onTertiary = LabelTertiary,
    background = BackgroundQuaternary,
    onBackground = LabelPrimary,
    surface = BackgroundTertiary,
    onSurface = LabelPrimary,
    error = SystemRed,
    onError = LabelTertiary
)

@Composable
fun CoffeUTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}