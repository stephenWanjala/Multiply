package com.stephenwanjala.multiply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.stephenwanjala.multiply.ui.navigation.MultiplyNav
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
            MultiplyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MultiplyNav(
                        navHostController = navHostController,
                        modifier = Modifier
                            .padding(paddingValues = innerPadding)
                            .consumeWindowInsets(paddingValues = innerPadding)
                    )
                }
            }
        }
    }
}

