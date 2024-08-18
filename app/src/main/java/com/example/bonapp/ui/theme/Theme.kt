package com.example.bonapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = TexasRose,
    onPrimary = Fiord,
    primaryContainer = Coral,
    onPrimaryContainer = Cream,
    secondary = RegentStBlue,
    onSecondary = Fiord,
    secondaryContainer = Flesh,
    onSecondaryContainer = Fiord,
    tertiary = Cream,
    onTertiary = Fiord,
    background = Fiord,
    onBackground = Cream,
    surface = Fiord,
    onSurface = Cream
)

private val LightColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = Cream,
    primaryContainer = TexasRose,
    onPrimaryContainer = Fiord,
    secondary = RegentStBlue,
    onSecondary = Fiord,
    secondaryContainer = Flesh,
    onSecondaryContainer = Fiord,
    tertiary = Fiord,
    onTertiary = Cream,
    background = Cream,
    onBackground = Fiord,
    surface = Flesh,
    onSurface = Fiord
)

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

@Composable
fun BonAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}