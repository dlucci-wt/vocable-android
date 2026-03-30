package com.willowtree.vocable.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

// Mirrors app/src/main/res/values/colors.xml for Compose surfaces.
private val VocablePurple = Color(0xFF3831A0)
private val VocablePurpleDark = Color(0xFF201C5B)
private val VocableText = Color(0xFFD1EEE9)
private val VocableAccent = Color(0xFFFEA430)

private val VocableColorScheme = darkColorScheme(
    primary = VocablePurple,
    onPrimary = VocableText,
    secondary = VocableAccent,
    onSecondary = VocablePurpleDark,
    background = VocablePurpleDark,
    onBackground = VocableText,
    surface = VocablePurpleDark,
    onSurface = VocableText,
)

// Default family until fonts are added under res/font.
private val VocableDefaultFontFamily = FontFamily.SansSerif

private val VocableTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = VocableDefaultFontFamily,
        color = VocableText,
        fontSize = 16.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = VocableDefaultFontFamily,
        color = VocableText,
        fontSize = 22.sp,
    ),
)

@Composable
fun VocableTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VocableColorScheme,
        typography = VocableTypography,
        content = content,
    )
}
