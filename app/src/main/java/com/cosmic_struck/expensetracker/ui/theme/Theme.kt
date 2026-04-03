package com.cosmic_struck.expensetracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MintPrimary,
    secondary = MintSecondary,
    tertiary = MintTertiary,
    background = TextPrimary,
    surface = Color(0xFF202A31),
    surfaceVariant = Color(0xFF32414A),
    onPrimary = CardSurface,
    onSecondary = CardSurface,
    onTertiary = TextPrimary,
    onBackground = CardSurface,
    onSurface = CardSurface,
    onSurfaceVariant = Color(0xFFB8C2C9),
    error = ExpenseRed,
    onError = CardSurface
)

private val LightColorScheme = lightColorScheme(
    primary = MintPrimary,
    primaryContainer = MintPrimaryContainer,
    secondary = MintSecondary,
    tertiary = MintTertiary,
    background = AppBackground,
    surface = CardSurface,
    surfaceVariant = SurfaceVariant,
    onPrimary = CardSurface,
    onPrimaryContainer = TextPrimary,
    onSecondary = CardSurface,
    onTertiary = CardSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outlineVariant = SurfaceVariant,
    error = ExpenseRed,
    onError = CardSurface
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
