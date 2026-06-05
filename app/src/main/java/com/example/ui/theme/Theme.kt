package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val VoidDarkColorScheme = darkColorScheme(
    primary = VoidPurple,
    secondary = VoidTeal,
    tertiary = VoidMagenta,
    background = VoidBlack,
    surface = VoidDark,
    onBackground = VoidTextPrimary,
    onSurface = VoidTextPrimary,
    primaryContainer = VoidSurface,
    onPrimaryContainer = VoidTextPrimary,
    secondaryContainer = VoidSurfaceLight,
    onSecondaryContainer = VoidTextPrimary,
    error = VoidMagenta
)

// We fall back to the custom scheme so V.O.I.D. aesthetic is guaranteed
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme by defaultfor V.O.I.D.
    dynamicColor: Boolean = false, // Disable dynamic color to maintain the custom neon-purple theme
    content: @Composable () -> Unit,
) {
    val colorScheme = VoidDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
