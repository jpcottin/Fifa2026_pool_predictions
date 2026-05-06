package com.example.fifa2026poolpredictions.theme

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

private val LightColorScheme = lightColorScheme(
    primary             = AppGreen,        // buttons, CTAs, active tabs, winner text
    onPrimary           = Color.White,
    primaryContainer    = AppGreenLight,   // score badge bg, countdown border
    onPrimaryContainer  = AppGreenDark,    // headings, selected labels, score text
    secondary           = GreenGrey40,
    onSecondary         = Color.White,
    secondaryContainer  = GreenGrey80,
    onSecondaryContainer = Color(0xFF002020),
    tertiary            = Gold40,
    onTertiary          = Color.White,
    tertiaryContainer   = Gold80,
    onTertiaryContainer = Color(0xFF241A00),
    background          = Color.White,
    onBackground        = Gray900,
    surface             = Color.White,
    onSurface           = Gray900,
    surfaceVariant      = AppGreenSurface, // faint green: selected cards, champion bg, step numbers
    onSurfaceVariant    = Gray600,
    outline             = Gray200,         // card borders, dividers
    outlineVariant      = Gray300,         // bracket connector lines
)

private val DarkColorScheme = darkColorScheme(
    primary             = Green80,
    onPrimary           = Color(0xFF003918),
    primaryContainer    = Green40,
    onPrimaryContainer  = Color(0xFFB7F5BC),
    secondary           = GreenGrey80,
    onSecondary         = Color(0xFF003735),
    secondaryContainer  = GreenGrey40,
    onSecondaryContainer = Color(0xFFCEE8E5),
    tertiary            = Gold80,
    onTertiary          = Color(0xFF3E2E00),
    tertiaryContainer   = Gold40,
    onTertiaryContainer = Color(0xFFFFDFA5),
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
