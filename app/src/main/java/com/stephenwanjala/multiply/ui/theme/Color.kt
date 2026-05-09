package com.stephenwanjala.multiply.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class MultiplyColors(
    val success: Color,
    val successContainer: Color,
    val onSuccess: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val onWarning: Color,
    val star: Color,
    val gameBackgroundLight: Brush,
    val gameBackgroundDark: Brush,
    val correctAnswer: Color,
    val wrongAnswer: Color,
    val bubbleBackground: Color,
    val pauseOverlay: Color,
)

val LocalMultiplyColors = staticCompositionLocalOf {
    AppTheme.SPACE.multiplyColors(darkTheme = false)
}
