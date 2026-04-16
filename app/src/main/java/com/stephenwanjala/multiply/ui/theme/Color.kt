package com.stephenwanjala.multiply.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Light theme colors - vibrant purple primary with teal secondary
val md_theme_light_primary = Color(0xFF7B2D8E)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFF3DAFF)
val md_theme_light_onPrimaryContainer = Color(0xFF2E004E)
val md_theme_light_secondary = Color(0xFF006B5E)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFF7CF8E2)
val md_theme_light_onSecondaryContainer = Color(0xFF00201B)
val md_theme_light_tertiary = Color(0xFFB25E00)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFDCC2)
val md_theme_light_onTertiaryContainer = Color(0xFF3A1C00)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_onBackground = Color(0xFF1D1B1E)
val md_theme_light_surface = Color(0xFFFFFBFF)
val md_theme_light_onSurface = Color(0xFF1D1B1E)
val md_theme_light_surfaceVariant = Color(0xFFEBDFEA)
val md_theme_light_onSurfaceVariant = Color(0xFF4C444D)
val md_theme_light_outline = Color(0xFF7D747E)
val md_theme_light_inverseOnSurface = Color(0xFFF6EFF3)
val md_theme_light_inverseSurface = Color(0xFF322F33)
val md_theme_light_inversePrimary = Color(0xFFE3B0FF)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFF7B2D8E)
val md_theme_light_outlineVariant = Color(0xFFCFC3CE)
val md_theme_light_scrim = Color(0xFF000000)

// Dark theme colors
val md_theme_dark_primary = Color(0xFFE3B0FF)
val md_theme_dark_onPrimary = Color(0xFF4A0066)
val md_theme_dark_primaryContainer = Color(0xFF620F75)
val md_theme_dark_onPrimaryContainer = Color(0xFFF3DAFF)
val md_theme_dark_secondary = Color(0xFF5DDBC6)
val md_theme_dark_onSecondary = Color(0xFF003730)
val md_theme_dark_secondaryContainer = Color(0xFF005047)
val md_theme_dark_onSecondaryContainer = Color(0xFF7CF8E2)
val md_theme_dark_tertiary = Color(0xFFFFB77C)
val md_theme_dark_onTertiary = Color(0xFF5E3000)
val md_theme_dark_tertiaryContainer = Color(0xFF874600)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFDCC2)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF1D1B1E)
val md_theme_dark_onBackground = Color(0xFFE7E0E5)
val md_theme_dark_surface = Color(0xFF1D1B1E)
val md_theme_dark_onSurface = Color(0xFFE7E0E5)
val md_theme_dark_surfaceVariant = Color(0xFF4C444D)
val md_theme_dark_onSurfaceVariant = Color(0xFFCFC3CE)
val md_theme_dark_outline = Color(0xFF988E98)
val md_theme_dark_inverseOnSurface = Color(0xFF1D1B1E)
val md_theme_dark_inverseSurface = Color(0xFFE7E0E5)
val md_theme_dark_inversePrimary = Color(0xFF7B2D8E)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFFE3B0FF)
val md_theme_dark_outlineVariant = Color(0xFF4C444D)
val md_theme_dark_scrim = Color(0xFF000000)

val seed = Color(0xFF7B2D8E)

// Semantic colors for game-specific use
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

val LightMultiplyColors = MultiplyColors(
    success = Color(0xFF2E7D32),
    successContainer = Color(0xFFC8E6C9),
    onSuccess = Color(0xFFFFFFFF),
    onSuccessContainer = Color(0xFF1B5E20),
    warning = Color(0xFFE65100),
    warningContainer = Color(0xFFFFE0B2),
    onWarning = Color(0xFFFFFFFF),
    star = Color(0xFFFFB300),
    gameBackgroundLight = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8EAF6), Color(0xFFF3E5F5), Color(0xFFE0F7FA))
    ),
    gameBackgroundDark = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
    ),
    correctAnswer = Color(0xFF2E7D32),
    wrongAnswer = Color(0xFFD32F2F),
    bubbleBackground = Color(0xFF87CEEB),
    pauseOverlay = Color(0x40000000),
)

val DarkMultiplyColors = MultiplyColors(
    success = Color(0xFF66BB6A),
    successContainer = Color(0xFF1B5E20),
    onSuccess = Color(0xFF003300),
    onSuccessContainer = Color(0xFFC8E6C9),
    warning = Color(0xFFFFB74D),
    warningContainer = Color(0xFF4E342E),
    onWarning = Color(0xFF3E2723),
    star = Color(0xFFFFD54F),
    gameBackgroundLight = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8EAF6), Color(0xFFF3E5F5), Color(0xFFE0F7FA))
    ),
    gameBackgroundDark = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
    ),
    correctAnswer = Color(0xFF66BB6A),
    wrongAnswer = Color(0xFFEF5350),
    bubbleBackground = Color(0xFF4682B4),
    pauseOverlay = Color(0x60000000),
)

val LocalMultiplyColors = staticCompositionLocalOf { LightMultiplyColors }
