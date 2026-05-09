package com.stephenwanjala.multiply.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class AppTheme(val displayName: String, val emoji: String) {
    SPACE("Space", "🚀"),
    JUNGLE("Jungle", "🌴"),
    OCEAN("Ocean", "🌊"),
    CANDY("Candy", "🍭"),
    DYNAMIC("Dynamic", "🎨");

    val isDynamic: Boolean get() = this == DYNAMIC

    /**
     * Returns the static palette for this theme. For [DYNAMIC] this falls back
     * to [SPACE] — callers that want true Material You colors should resolve
     * them with a [android.content.Context] in the composition.
     */
    fun colorScheme(darkTheme: Boolean): ColorScheme = when (this) {
        SPACE, DYNAMIC -> if (darkTheme) SpaceDarkColors else SpaceLightColors
        JUNGLE -> if (darkTheme) JungleDarkColors else JungleLightColors
        OCEAN -> if (darkTheme) OceanDarkColors else OceanLightColors
        CANDY -> if (darkTheme) CandyDarkColors else CandyLightColors
    }

    fun multiplyColors(darkTheme: Boolean): MultiplyColors = when (this) {
        SPACE, DYNAMIC -> if (darkTheme) SpaceDarkMultiplyColors else SpaceLightMultiplyColors
        JUNGLE -> if (darkTheme) JungleDarkMultiplyColors else JungleLightMultiplyColors
        OCEAN -> if (darkTheme) OceanDarkMultiplyColors else OceanLightMultiplyColors
        CANDY -> if (darkTheme) CandyDarkMultiplyColors else CandyLightMultiplyColors
    }

    companion object {
        val isDynamicSupported: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        fun fromOrdinal(ordinal: Int): AppTheme {
            val theme = entries.getOrNull(ordinal) ?: SPACE
            return if (theme == DYNAMIC && !isDynamicSupported) SPACE else theme
        }

        fun availableEntries(): List<AppTheme> =
            if (isDynamicSupported) entries.toList()
            else entries.filterNot { it == DYNAMIC }
    }
}

// region Space (default — cosmic purple/teal)
private val SpaceLightColors = lightColorScheme(
    primary = Color(0xFF7B2D8E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF3DAFF),
    onPrimaryContainer = Color(0xFF2E004E),
    secondary = Color(0xFF006B5E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF7CF8E2),
    onSecondaryContainer = Color(0xFF00201B),
    tertiary = Color(0xFFB25E00),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDCC2),
    onTertiaryContainer = Color(0xFF3A1C00),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1D1B1E),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1D1B1E),
    surfaceVariant = Color(0xFFEBDFEA),
    onSurfaceVariant = Color(0xFF4C444D),
    outline = Color(0xFF7D747E)
)

private val SpaceDarkColors = darkColorScheme(
    primary = Color(0xFFE3B0FF),
    onPrimary = Color(0xFF4A0066),
    primaryContainer = Color(0xFF620F75),
    onPrimaryContainer = Color(0xFFF3DAFF),
    secondary = Color(0xFF5DDBC6),
    onSecondary = Color(0xFF003730),
    secondaryContainer = Color(0xFF005047),
    onSecondaryContainer = Color(0xFF7CF8E2),
    tertiary = Color(0xFFFFB77C),
    onTertiary = Color(0xFF5E3000),
    tertiaryContainer = Color(0xFF874600),
    onTertiaryContainer = Color(0xFFFFDCC2),
    background = Color(0xFF1D1B1E),
    onBackground = Color(0xFFE7E0E5),
    surface = Color(0xFF1D1B1E),
    onSurface = Color(0xFFE7E0E5),
    surfaceVariant = Color(0xFF4C444D),
    onSurfaceVariant = Color(0xFFCFC3CE),
    outline = Color(0xFF988E98)
)

private val SpaceLightMultiplyColors = MultiplyColors(
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
    pauseOverlay = Color(0x40000000)
)

private val SpaceDarkMultiplyColors = MultiplyColors(
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
    pauseOverlay = Color(0x60000000)
)
// endregion

// region Jungle (lush greens)
private val JungleLightColors = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB8F0BC),
    onPrimaryContainer = Color(0xFF002106),
    secondary = Color(0xFF6A6F2D),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEEF4A8),
    onSecondaryContainer = Color(0xFF1F2200),
    tertiary = Color(0xFF8D6E63),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDBC9),
    onTertiaryContainer = Color(0xFF341100),
    background = Color(0xFFFBFDF6),
    onBackground = Color(0xFF1A1C18),
    surface = Color(0xFFFBFDF6),
    onSurface = Color(0xFF1A1C18),
    surfaceVariant = Color(0xFFDFE4D6),
    onSurfaceVariant = Color(0xFF43483E),
    outline = Color(0xFF73796D)
)

private val JungleDarkColors = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    onPrimary = Color(0xFF003910),
    primaryContainer = Color(0xFF005319),
    onPrimaryContainer = Color(0xFFC1F0C5),
    secondary = Color(0xFFD2D78E),
    onSecondary = Color(0xFF353A05),
    secondaryContainer = Color(0xFF4D5217),
    onSecondaryContainer = Color(0xFFEEF4A8),
    tertiary = Color(0xFFFFB59A),
    onTertiary = Color(0xFF552100),
    tertiaryContainer = Color(0xFF723500),
    onTertiaryContainer = Color(0xFFFFDBC9),
    background = Color(0xFF111611),
    onBackground = Color(0xFFE2E3DC),
    surface = Color(0xFF111611),
    onSurface = Color(0xFFE2E3DC),
    surfaceVariant = Color(0xFF43483E),
    onSurfaceVariant = Color(0xFFC3C8BA),
    outline = Color(0xFF8D9285)
)

private val JungleLightMultiplyColors = MultiplyColors(
    success = Color(0xFF388E3C),
    successContainer = Color(0xFFC8E6C9),
    onSuccess = Color(0xFFFFFFFF),
    onSuccessContainer = Color(0xFF1B5E20),
    warning = Color(0xFFEF6C00),
    warningContainer = Color(0xFFFFE0B2),
    onWarning = Color(0xFFFFFFFF),
    star = Color(0xFFFBC02D),
    gameBackgroundLight = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F5E9), Color(0xFFDCEDC8), Color(0xFFC5E1A5))
    ),
    gameBackgroundDark = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F2A14), Color(0xFF14401C), Color(0xFF1B5E20))
    ),
    correctAnswer = Color(0xFF388E3C),
    wrongAnswer = Color(0xFFC62828),
    bubbleBackground = Color(0xFF81C784),
    pauseOverlay = Color(0x40000000)
)

private val JungleDarkMultiplyColors = MultiplyColors(
    success = Color(0xFF81C784),
    successContainer = Color(0xFF1B5E20),
    onSuccess = Color(0xFF003300),
    onSuccessContainer = Color(0xFFC8E6C9),
    warning = Color(0xFFFFB74D),
    warningContainer = Color(0xFF4E342E),
    onWarning = Color(0xFF3E2723),
    star = Color(0xFFFFD54F),
    gameBackgroundLight = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F5E9), Color(0xFFDCEDC8), Color(0xFFC5E1A5))
    ),
    gameBackgroundDark = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F2A14), Color(0xFF14401C), Color(0xFF1B5E20))
    ),
    correctAnswer = Color(0xFF81C784),
    wrongAnswer = Color(0xFFEF5350),
    bubbleBackground = Color(0xFF4CAF50),
    pauseOverlay = Color(0x60000000)
)
// endregion

// region Ocean (deep blues + coral)
private val OceanLightColors = lightColorScheme(
    primary = Color(0xFF0277BD),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCBE6FF),
    onPrimaryContainer = Color(0xFF001E2E),
    secondary = Color(0xFF00838F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Color(0xFF002022),
    tertiary = Color(0xFFFF7043),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDBCE),
    onTertiaryContainer = Color(0xFF3A0A00),
    background = Color(0xFFF7FBFF),
    onBackground = Color(0xFF001F2A),
    surface = Color(0xFFF7FBFF),
    onSurface = Color(0xFF001F2A),
    surfaceVariant = Color(0xFFDCE3E9),
    onSurfaceVariant = Color(0xFF40484C),
    outline = Color(0xFF70787C)
)

private val OceanDarkColors = darkColorScheme(
    primary = Color(0xFF81D4FA),
    onPrimary = Color(0xFF00344C),
    primaryContainer = Color(0xFF004C6D),
    onPrimaryContainer = Color(0xFFCBE6FF),
    secondary = Color(0xFF80DEEA),
    onSecondary = Color(0xFF00373B),
    secondaryContainer = Color(0xFF005056),
    onSecondaryContainer = Color(0xFFB2EBF2),
    tertiary = Color(0xFFFFAB91),
    onTertiary = Color(0xFF5C1A00),
    tertiaryContainer = Color(0xFF7E2A00),
    onTertiaryContainer = Color(0xFFFFDBCE),
    background = Color(0xFF001019),
    onBackground = Color(0xFFC1E8FF),
    surface = Color(0xFF001019),
    onSurface = Color(0xFFC1E8FF),
    surfaceVariant = Color(0xFF40484C),
    onSurfaceVariant = Color(0xFFC0C7CD),
    outline = Color(0xFF8A9297)
)

private val OceanLightMultiplyColors = MultiplyColors(
    success = Color(0xFF00897B),
    successContainer = Color(0xFFB2DFDB),
    onSuccess = Color(0xFFFFFFFF),
    onSuccessContainer = Color(0xFF004D40),
    warning = Color(0xFFFF7043),
    warningContainer = Color(0xFFFFCCBC),
    onWarning = Color(0xFFFFFFFF),
    star = Color(0xFFFFCA28),
    gameBackgroundLight = Brush.verticalGradient(
        colors = listOf(Color(0xFFE1F5FE), Color(0xFFB3E5FC), Color(0xFF81D4FA))
    ),
    gameBackgroundDark = Brush.verticalGradient(
        colors = listOf(Color(0xFF001E33), Color(0xFF003355), Color(0xFF014F86))
    ),
    correctAnswer = Color(0xFF00897B),
    wrongAnswer = Color(0xFFD84315),
    bubbleBackground = Color(0xFF4FC3F7),
    pauseOverlay = Color(0x40000000)
)

private val OceanDarkMultiplyColors = MultiplyColors(
    success = Color(0xFF4DB6AC),
    successContainer = Color(0xFF004D40),
    onSuccess = Color(0xFF00251A),
    onSuccessContainer = Color(0xFFB2DFDB),
    warning = Color(0xFFFFAB91),
    warningContainer = Color(0xFF5C1A00),
    onWarning = Color(0xFF3A0A00),
    star = Color(0xFFFFD54F),
    gameBackgroundLight = Brush.verticalGradient(
        colors = listOf(Color(0xFFE1F5FE), Color(0xFFB3E5FC), Color(0xFF81D4FA))
    ),
    gameBackgroundDark = Brush.verticalGradient(
        colors = listOf(Color(0xFF001E33), Color(0xFF003355), Color(0xFF014F86))
    ),
    correctAnswer = Color(0xFF4DB6AC),
    wrongAnswer = Color(0xFFFF8A65),
    bubbleBackground = Color(0xFF0288D1),
    pauseOverlay = Color(0x60000000)
)
// endregion

// region Candy (sweet pinks + mint)
private val CandyLightColors = lightColorScheme(
    primary = Color(0xFFC2185B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFD9E2),
    onPrimaryContainer = Color(0xFF3F0017),
    secondary = Color(0xFF00897B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF002B26),
    tertiary = Color(0xFFFBC02D),
    onTertiary = Color(0xFF3D2E00),
    tertiaryContainer = Color(0xFFFFE9A3),
    onTertiaryContainer = Color(0xFF241A00),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF22001A),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF22001A),
    surfaceVariant = Color(0xFFF3DDE0),
    onSurfaceVariant = Color(0xFF514347),
    outline = Color(0xFF837377)
)

private val CandyDarkColors = darkColorScheme(
    primary = Color(0xFFF48FB1),
    onPrimary = Color(0xFF5E0028),
    primaryContainer = Color(0xFF82003F),
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003731),
    secondaryContainer = Color(0xFF005048),
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = Color(0xFFFFE082),
    onTertiary = Color(0xFF3D2E00),
    tertiaryContainer = Color(0xFF584400),
    onTertiaryContainer = Color(0xFFFFE9A3),
    background = Color(0xFF1B0010),
    onBackground = Color(0xFFFFD8E4),
    surface = Color(0xFF1B0010),
    onSurface = Color(0xFFFFD8E4),
    surfaceVariant = Color(0xFF514347),
    onSurfaceVariant = Color(0xFFD6C2C5),
    outline = Color(0xFF9F8C90)
)

private val CandyLightMultiplyColors = MultiplyColors(
    success = Color(0xFF26A69A),
    successContainer = Color(0xFFB2DFDB),
    onSuccess = Color(0xFFFFFFFF),
    onSuccessContainer = Color(0xFF004D40),
    warning = Color(0xFFEC407A),
    warningContainer = Color(0xFFF8BBD0),
    onWarning = Color(0xFFFFFFFF),
    star = Color(0xFFFFC107),
    gameBackgroundLight = Brush.verticalGradient(
        colors = listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0), Color(0xFFE1BEE7))
    ),
    gameBackgroundDark = Brush.verticalGradient(
        colors = listOf(Color(0xFF311432), Color(0xFF4A1942), Color(0xFF6A1B45))
    ),
    correctAnswer = Color(0xFF26A69A),
    wrongAnswer = Color(0xFFD81B60),
    bubbleBackground = Color(0xFFF48FB1),
    pauseOverlay = Color(0x40000000)
)

private val CandyDarkMultiplyColors = MultiplyColors(
    success = Color(0xFF4DB6AC),
    successContainer = Color(0xFF004D40),
    onSuccess = Color(0xFF00251A),
    onSuccessContainer = Color(0xFFB2DFDB),
    warning = Color(0xFFF48FB1),
    warningContainer = Color(0xFF5E0028),
    onWarning = Color(0xFF3F0017),
    star = Color(0xFFFFD54F),
    gameBackgroundLight = Brush.verticalGradient(
        colors = listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0), Color(0xFFE1BEE7))
    ),
    gameBackgroundDark = Brush.verticalGradient(
        colors = listOf(Color(0xFF311432), Color(0xFF4A1942), Color(0xFF6A1B45))
    ),
    correctAnswer = Color(0xFF4DB6AC),
    wrongAnswer = Color(0xFFFF80AB),
    bubbleBackground = Color(0xFFC2185B),
    pauseOverlay = Color(0x60000000)
)
// endregion
