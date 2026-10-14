package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val FigmaLightColorScheme = lightColorScheme(
    primary = FigmaTealPrimary,
    onPrimary = Color.White,
    primaryContainer = FigmaTealChip,
    onPrimaryContainer = FigmaTealDark,
    secondary = FigmaAccentGreen,
    onSecondary = Color.White,
    secondaryContainer = FigmaTealLight,
    onSecondaryContainer = FigmaTextPrimary,
    tertiary = FigmaAccentBlue,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDDE9F0),
    background = FigmaBackground,
    onBackground = FigmaTextPrimary,
    surface = FigmaSurface,
    onSurface = FigmaTextPrimary,
    surfaceVariant = FigmaTealChip,
    onSurfaceVariant = FigmaTextSecondary,
    outline = FigmaBorder,
    outlineVariant = Color(0xFFD7E5E3)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = FigmaLightColorScheme,
        typography = Typography,
        content = content
    )
}
