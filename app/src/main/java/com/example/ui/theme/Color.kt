package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

// Light/Dark mode reactive state
var isLightModeTheme by mutableStateOf(false)

// Shubham Graphics - High-Fidelity Custom "CMYK" Palette
val CyanPrimary: Color
    get() = if (isLightModeTheme) Color(0xFF00ACC1) else Color(0xFF00E5FF)       // Primary: Sky Teal (Light Mode) vs Cyber Neon Cyan (Dark Mode)

val MagentaSecondary: Color
    get() = if (isLightModeTheme) Color(0xFFD81B60) else Color(0xFFFF2A85)  // Secondary: Ruby Cranberry (Light Mode) vs Hot Neon Magenta (Dark Mode)

val YellowTertiary: Color
    get() = if (isLightModeTheme) Color(0xFFF57C00) else Color(0xFFFFD400)    // Tertiary: Deep Gold Amber (Light Mode) vs Light Canary Yellow (Dark Mode)

val SlateBlack: Color
    get() = if (isLightModeTheme) Color(0xFFF1F5F9) else Color(0xFF0C0E14)        // Midnight Deep background: Crisp slate-white vs Pure OLED midnight black

val DeepSlateSurface: Color
    get() = if (isLightModeTheme) Color(0xFFE2E8F0) else Color(0xFF141822)  // Soft card backing surface

val SoftCardBg: Color
    get() = if (isLightModeTheme) Color(0xFFFFFFFF) else Color(0xFF1C2230)        // High-contrast clean white surface vs Rich cozy navy card

val TitleWhite: Color
    get() = if (isLightModeTheme) Color(0xFF0F172A) else Color(0xFFF2F5FA)        // Dark Charcoal Slate vs Crisp Titanium White

val BodyGray: Color
    get() = if (isLightModeTheme) Color(0xFF475569) else Color(0xFFA5B4FC)          // Soft slate-gray vs Beautiful light indigo-slate

// Cashflow semantic indicators
val ProfitGreen: Color
    get() = if (isLightModeTheme) Color(0xFF16A34A) else Color(0xFF00E676)

val LossRed: Color
    get() = if (isLightModeTheme) Color(0xFFDC2626) else Color(0xFFF50057)

// Soft dynamic gradients
val CyanGradientStart: Color
    get() = if (isLightModeTheme) Color(0xFFE0F7FA) else Color(0xFF001220)
val CyanGradientEnd: Color
    get() = if (isLightModeTheme) Color(0xFFB2EBF2) else Color(0xFF004455)

