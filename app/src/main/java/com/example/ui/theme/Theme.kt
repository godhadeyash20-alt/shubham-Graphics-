package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CustomDarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = Color(0xFF070B13),
    secondary = MagentaSecondary,
    onSecondary = Color.White,
    tertiary = YellowTertiary,
    onTertiary = Color(0xFF070B13),
    background = SlateBlack,
    onBackground = TitleWhite,
    surface = DeepSlateSurface,
    onSurface = TitleWhite,
    surfaceVariant = SoftCardBg,
    onSurfaceVariant = BodyGray,
    error = LossRed,
    onError = Color.White,
    primaryContainer = Color(0xFF002F3A),
    onPrimaryContainer = CyanPrimary,
    secondaryContainer = Color(0xFF420922),
    onSecondaryContainer = MagentaSecondary
)

private val CustomLightColorScheme = lightColorScheme(
    primary = CyanPrimary,
    onPrimary = Color.White,
    secondary = MagentaSecondary,
    onSecondary = Color.White,
    tertiary = YellowTertiary,
    onTertiary = Color.White,
    background = SlateBlack,
    onBackground = TitleWhite,
    surface = DeepSlateSurface,
    onSurface = TitleWhite,
    surfaceVariant = SoftCardBg,
    onSurfaceVariant = BodyGray,
    error = LossRed,
    onError = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = CyanPrimary,
    secondaryContainer = Color(0xFFFCE4EC),
    onSecondaryContainer = MagentaSecondary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Forcing our custom premium Shubham Graphics "CMYK" color theme
    // to preserve design constraints and avoid generic auto-color overrides.
    val scheme = if (isLightModeTheme) CustomLightColorScheme else CustomDarkColorScheme
    
    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        content = content
    )
}

