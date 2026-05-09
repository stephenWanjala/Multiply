@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.stephenwanjala.multiply.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.stephenwanjala.multiply.core.designsystem.theme.LocalSpacing
import com.stephenwanjala.multiply.core.designsystem.theme.Spacing

@Composable
fun MultiplyTheme(
    appTheme: AppTheme = AppTheme.SPACE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = remember(appTheme, darkTheme, context) {
        if (appTheme.isDynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            appTheme.colorScheme(darkTheme)
        }
    }
    val multiplyColors = remember(appTheme, darkTheme) { appTheme.multiplyColors(darkTheme) }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalMultiplyColors provides multiplyColors
    ) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
            motionScheme = MotionScheme.expressive(),
        )
    }
}
