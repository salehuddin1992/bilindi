package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Facebook Light Color Scheme - Bright, clean Facebook aesthetic (#F0F2F5 background, #FFFFFF surface, #1877F2 primary)
private val FacebookLightColorScheme = lightColorScheme(
    primary = BilindiBlue,
    onPrimary = Color.White,
    primaryContainer = BilindiBlueLight,
    onPrimaryContainer = BilindiBlueDark,
    background = BilindiBg,
    surface = BilindiCard,
    onBackground = BilindiTextPrimary,
    onSurface = BilindiTextPrimary,
    surfaceVariant = BilindiSurfaceVariant,
    onSurfaceVariant = BilindiTextSecondary,
    outline = BorderLight
)

// Facebook Dark Color Scheme - Eye-friendly dark aesthetic (#18191A background, #242526 surface)
private val FacebookDarkColorScheme = darkColorScheme(
    primary = BilindiBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF263951),
    onPrimaryContainer = Color(0xFFD0E1FD),
    background = FacebookDarkBg,
    surface = FacebookDarkCard,
    onBackground = FacebookDarkTextPrimary,
    onSurface = FacebookDarkTextPrimary,
    surfaceVariant = FacebookDarkSurfaceVariant,
    onSurfaceVariant = FacebookDarkTextSecondary,
    outline = FacebookDarkBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FacebookDarkColorScheme else FacebookLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

