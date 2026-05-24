package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = IkeaYellow,
    secondary = IkeaBlue,
    tertiary = IkeaCobalt,
    background = DarkBg,
    surface = DarkSurface,
    onPrimary = IkeaBlue,
    onSecondary = LightSurface,
    onBackground = DarkTextMain,
    onSurface = DarkTextMain
)

private val LightColorScheme = lightColorScheme(
    primary = IkeaBlue,
    secondary = IkeaCobalt,
    tertiary = IkeaYellow,
    background = LightBg,
    surface = LightSurface,
    onPrimary = LightSurface,
    onSecondary = LightSurface,
    onTertiary = IkeaBlue,
    onBackground = LightTextMain,
    onSurface = LightTextMain
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
