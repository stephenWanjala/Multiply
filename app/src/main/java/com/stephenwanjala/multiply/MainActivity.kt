package com.stephenwanjala.multiply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.stephenwanjala.multiply.core.data.AppPreferencesViewModel
import com.stephenwanjala.multiply.ui.navigation.MultiplyNav
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color.Transparent.toArgb()
            )
        )

        setContent {
            val navHostController = rememberNavController()
            val appPrefsVm = koinViewModel<AppPreferencesViewModel>()
            val appTheme by appPrefsVm.appTheme.collectAsStateWithLifecycle()
            MultiplyTheme(appTheme = appTheme) {
                MultiplyNav(
                    navHostController = navHostController,
                    modifier = Modifier
                )
            }
        }
    }
}
