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

private val DarkColorScheme = darkColorScheme(
    primary = SuccessGreen,
    onPrimary = NavyDark,
    primaryContainer = NavySecondary,
    onPrimaryContainer = Color.White,
    secondary = NavySecondary,
    onSecondary = Color.White,
    tertiary = AccentGold,
    background = NavyDark,
    onBackground = Color.White,
    surface = NavyPrimary,
    onSurface = Color.White,
    surfaceVariant = NavySurface,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = NavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = NavySecondary,
    onPrimaryContainer = Color.White,
    secondary = SuccessGreenDark,
    onSecondary = Color.White,
    tertiary = AccentGold,
    background = LightBackground,
    onBackground = SlateTextDark,
    surface = LightSurface,
    onSurface = SlateTextDark,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = SlateTextMuted,
    outline = SlateBorder
)

@Composable
fun PayAlertTheme(
    darkTheme: Boolean = false, // Default to clean dark blue & white theme
    dynamicColor: Boolean = false, // Consistent brand colors preferred for PAY ALERT
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward-compatible alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    PayAlertTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
