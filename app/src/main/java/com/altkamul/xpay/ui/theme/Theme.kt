package com.altkamul.xpay.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = orange,
    primaryVariant = orangeDark,
    secondary = green,
    background = whiteBackground,
    onPrimary = white,
    onSecondary = white,
    onBackground = black,
    secondaryVariant = dark,
    surface = white
)

private val LightColorPalette = lightColors(
    primary = orange,
    primaryVariant = orangeDark,
    secondary = green,
    background = whiteBackground,
    onPrimary = white,
    onSecondary = white,
    onBackground = black,
    secondaryVariant = dark,
    surface = white,
)

@Composable
fun XPayAndroidTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}