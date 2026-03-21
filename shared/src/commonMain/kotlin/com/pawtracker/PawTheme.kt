package com.pawtracker

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PeachCream = Color(0xFFFFF8F0)
private val WarmShell = Color(0xFFFFECD2)
private val CoralOrange = Color(0xFFFF8A65)
private val DeepAmber = Color(0xFFE65100)
private val SoftInk = Color(0xFF4E342E)
private val PawAccent = Color(0xFFFFB74D)

private val PawLightScheme = lightColorScheme(
    primary = DeepAmber,
    onPrimary = Color.White,
    primaryContainer = WarmShell,
    onPrimaryContainer = SoftInk,
    secondary = CoralOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = SoftInk,
    tertiary = PawAccent,
    onTertiary = SoftInk,
    background = PeachCream,
    onBackground = SoftInk,
    surface = Color.White,
    onSurface = SoftInk,
    surfaceVariant = WarmShell,
    onSurfaceVariant = Color(0xFF6D4C41),
    outline = Color(0xFFD7CCC8),
)

@Composable
fun PawTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PawLightScheme,
        content = content,
    )
}
