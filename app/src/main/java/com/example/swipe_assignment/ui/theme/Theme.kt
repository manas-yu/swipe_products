package com.example.swipe_assignment.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ===================== Brand Palette (Teal / Indigo / Charcoal) =====================
private val Teal = Color(0xFF00C2A8)            // Primary accent
private val TealContainer = Color(0xFFBDF4EA)

private val Indigo = Color(0xFF3B5BDB)          // Secondary accent
private val IndigoContainer = Color(0xFFDDE4FF)

private val Purple = Color(0xFFB267F6)          // Tertiary accent
private val PurpleContainer = Color(0xFFEEDBFF)

private val Charcoal900 = Color(0xFF0D0F12)     // Dark background
private val Charcoal800 = Color(0xFF14171C)     // Dark surface
private val Charcoal700 = Color(0xFF1C2128)     // Surface variant
private val LineDark = Color(0xFF2A313A)        // Outlines on dark
private val OnDarkHigh = Color(0xFFE8EAED)      // Primary text on dark
private val OnDarkMed = Color(0xFFB7BEC8)       // Secondary text on dark

private val Snow = Color(0xFFF8FAFC)            // Light background
private val Paper = Color(0xFFFFFFFF)           // Light surface
private val Mist = Color(0xFFF1F4F8)            // Surface variant
private val LineLight = Color(0xFFE2E8F0)       // Outlines on light
private val OnLightHigh = Color(0xFF0E141B)     // Primary text on light
private val OnLightMed = Color(0xFF495667)      // Secondary text on light

// ===================== Dark Scheme =====================
private val DarkColorScheme = darkColorScheme(
    primary = Teal,
    onPrimary = Color.Black,                   // teal is bright → dark text
    primaryContainer = Charcoal700,
    onPrimaryContainer = OnDarkHigh,

    secondary = Indigo,
    onSecondary = Color.White,
    secondaryContainer = Charcoal700,
    onSecondaryContainer = OnDarkHigh,

    tertiary = Purple,
    onTertiary = Color.Black,
    tertiaryContainer = Charcoal700,
    onTertiaryContainer = OnDarkHigh,

    background = Charcoal900,
    onBackground = OnDarkHigh,
    surface = Charcoal800,
    onSurface = OnDarkHigh,
    surfaceVariant = Charcoal700,
    onSurfaceVariant = OnDarkMed,

    outline = LineDark,
    outlineVariant = LineDark,

    inverseSurface = Snow,
    inverseOnSurface = OnLightHigh,
    scrim = Color(0xFF000000)
)

// ===================== Light Scheme =====================
private val LightColorScheme = lightColorScheme(
    primary = Teal,
    onPrimary = Color.Black,
    primaryContainer = TealContainer,
    onPrimaryContainer = OnLightHigh,

    secondary = Indigo,
    onSecondary = Color.White,
    secondaryContainer = IndigoContainer,
    onSecondaryContainer = OnLightHigh,

    tertiary = Purple,
    onTertiary = Color.Black,
    tertiaryContainer = PurpleContainer,
    onTertiaryContainer = OnLightHigh,

    background = Snow,
    onBackground = OnLightHigh,
    surface = Paper,
    onSurface = OnLightHigh,
    surfaceVariant = Mist,
    onSurfaceVariant = OnLightMed,

    outline = LineLight,
    outlineVariant = LineLight,

    inverseSurface = Charcoal900,
    inverseOnSurface = OnDarkHigh,
    scrim = Color(0xFF000000)
)

@Composable
fun Swipe_assignmentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // keep brand colors stable for the assignment
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Match system bars to surfaces and set icon contrast correctly
            val surfaceArgb = colorScheme.surface.toArgb()
            window.statusBarColor = surfaceArgb
            window.navigationBarColor = surfaceArgb
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
