package com.vi5hnu.curvykids.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** Fixed light color scheme — no dynamic color so the kids palette is always applied. */
private val CurvyColorScheme = lightColorScheme(
    primary          = Teal,
    onPrimary        = CardWhite,
    primaryContainer = TintTeal,
    secondary        = Coral,
    onSecondary      = CardWhite,
    tertiary         = Grape,
    background       = BgTop,
    surface          = CardWhite,
    onBackground     = Ink,
    onSurface        = Ink,
    outline          = InkFaint,
)

@Composable
fun CurvyKidsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CurvyColorScheme,
        typography = Typography,
        content = content,
    )
}
