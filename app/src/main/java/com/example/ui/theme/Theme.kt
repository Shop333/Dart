package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BatikGold,
    secondary = BatikBrown,
    background = Color(0xFF120A04),
    surface = Color(0xFF1F1208),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color(0xFFFBF4E7),
    onSurface = Color(0xFFFBF4E7),
    outline = Color(0xFF3A2414)
)

private val LightColorScheme = lightColorScheme(
    primary = BatikBrown,
    secondary = BatikGold,
    background = WarmCreamBg,
    surface = PureCreamSurface,
    onPrimary = Color.White,
    onSecondary = TextCharcoal,
    onBackground = TextCharcoal,
    onSurface = TextCharcoal,
    outline = SoftBorder,
    error = Color(0xFFC62828)
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
