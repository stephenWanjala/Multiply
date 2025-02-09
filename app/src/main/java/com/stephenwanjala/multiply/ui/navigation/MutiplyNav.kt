package com.stephenwanjala.multiply.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stephenwanjala.multiply.game.screens.gamescreen.GameScreen
import com.stephenwanjala.multiply.game.screens.InstructionsScreen
import com.stephenwanjala.multiply.game.screens.SettingsScreen
import com.stephenwanjala.multiply.game.screens.WelcomeScreen
import kotlinx.serialization.Serializable

@Composable
fun MultiplyNav(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = MultiplyDestination.WelComeDestination,
        modifier = modifier
    ) {
        composable<MultiplyDestination.WelComeDestination> {
            WelcomeScreen(
                onPlayClick = { navHostController.navigate(MultiplyDestination.GameDestination) },
                onHowToPlayClick = {
                    navHostController.navigate(MultiplyDestination.GameInstructionDestination)
                },
                onSettingsClick = {
                    navHostController.navigate(MultiplyDestination.SettingsDestination)
                })
        }

        composable<MultiplyDestination.SettingsDestination> {
            SettingsScreen(onBackClick = navHostController::navigateUp)
        }
        composable<MultiplyDestination.GameInstructionDestination> {
            InstructionsScreen(
                onStartGame = {
                    navHostController.navigate(MultiplyDestination.GameDestination) {
                        popUpTo<MultiplyDestination.GameInstructionDestination> {
                            inclusive = true
                        }
                    }
                },
                navigateUp = navHostController::navigateUp
            )
        }

        composable<MultiplyDestination.GameDestination> {
            GameScreen(viewModel = hiltViewModel(), onNavigateUp = navHostController::navigateUp, toSettings = {
                navHostController.navigate(MultiplyDestination.SettingsDestination)
            }, toHowToPlay ={navHostController.navigate(MultiplyDestination.GameInstructionDestination)})
        }
    }

}


sealed interface MultiplyDestination {
    @Serializable
    data object WelComeDestination : MultiplyDestination

    @Serializable
    data object SettingsDestination : MultiplyDestination

    @Serializable
    data object GameInstructionDestination : MultiplyDestination

    @Serializable
    data object GameDestination : MultiplyDestination
}