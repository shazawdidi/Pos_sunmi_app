package com.altkamul.xpay.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.altkamul.xpay.R

val fonts = FontFamily(
    Font(R.font.montserrat_thin, weight = FontWeight.Thin),
    Font(R.font.montserrat_light, weight = FontWeight.Light),
    Font(R.font.montserrat_normal, weight = FontWeight.Normal),
    Font(R.font.montserrat_bold, weight = FontWeight.Bold),
    Font(R.font.montserrat_extra_bold, weight = FontWeight.ExtraBold),
)

// Set of Material typography styles to start with
val Typography = Typography(

    h1 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.ExtraBold,
        fontSize = FontSize.xxxl
    ),
    h2 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = FontSize.xxl
    ),
    h3 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = FontSize.x
    ),
    h4 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = FontSize.md
    ),
    h5 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Medium,
        fontSize = FontSize.lg
    ),
    h6 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Medium,
        fontSize = FontSize.md
    ),
    body1 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = FontSize.md
    ),
    body2 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = FontSize.smX
    ),
    button = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = FontSize.md
    ),
    caption = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = FontSize.sm
    ),
    subtitle1 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = FontSize.sm
    ),
    subtitle2 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = FontSize.sm
    ),
    defaultFontFamily = fonts,

    )