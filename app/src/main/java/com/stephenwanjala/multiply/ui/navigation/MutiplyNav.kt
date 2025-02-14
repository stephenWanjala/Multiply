package com.stephenwanjala.multiply.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stephenwanjala.multiply.game.models.BubbleMathDifficulty
import com.stephenwanjala.multiply.game.models.GameMode
import com.stephenwanjala.multiply.game.GameModeSelectionScreen
import com.stephenwanjala.multiply.game.feat_bubblemode.InstructionsScreen
import com.stephenwanjala.multiply.game.feat_bubblemode.SettingsScreen
import com.stephenwanjala.multiply.game.feat_bubblemode.WelcomeScreen
import com.stephenwanjala.multiply.game.feat_bubblemode.Difficulty
import com.stephenwanjala.multiply.game.feat_bubblemode.GameAction
import com.stephenwanjala.multiply.game.feat_bubblemode.GameScreen
import com.stephenwanjala.multiply.game.feat_bubblemode.GameViewModel
import kotlinx.serialization.Serializable

@Composable
fun MultiplyNav(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<GameViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value
    NavHost(
        navController = navHostController,
        startDestination = MultiplyDestination.SelectGameMode,
        modifier = modifier
    ) {
        composable<MultiplyDestination.SelectGameMode> {
            GameModeSelectionScreen { gameMode: GameMode ->
                when (gameMode) {
                    is GameMode.BubbleMathBlitz -> {
                        when (gameMode.difficulty) {
                            BubbleMathDifficulty.EASY -> viewModel.onAction(
                                GameAction.UpdateDifficulty(
                                    difficulty = Difficulty.EASY
                                )
                            )

                            BubbleMathDifficulty.MEDIUM -> viewModel.onAction(
                                GameAction.UpdateDifficulty(
                                    difficulty = Difficulty.MEDIUM
                                )
                            )

                            BubbleMathDifficulty.HARD -> viewModel.onAction(
                                GameAction.UpdateDifficulty(
                                    difficulty = Difficulty.HARD
                                )
                            )
                        }
                        navHostController.navigate(MultiplyDestination.WelComeDestination)
                    }

                    is GameMode.QuizGenius -> {
                    }
                }

            }
        }
        composable<MultiplyDestination.WelComeDestination> {

            WelcomeScreen(
                onPlayClick = { navHostController.navigate(MultiplyDestination.GameDestination) },
                onHowToPlayClick = {
                    navHostController.navigate(MultiplyDestination.GameInstructionDestination)
                },
                onSettingsClick = {
                    navHostController.navigate(MultiplyDestination.SettingsDestination)
                }, onNavigateUp = navHostController::navigateUp)
        }

        composable<MultiplyDestination.SettingsDestination> {
            SettingsScreen(
                onBackClick = navHostController::navigateUp,
                state = state,
                onAction = { action ->
                    viewModel.onAction(action)
                })
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
            GameScreen(viewModel = hiltViewModel(),
                onNavigateUp = navHostController::navigateUp,
                toSettings = {
                    navHostController.navigate(MultiplyDestination.SettingsDestination)
                },
                toHowToPlay = { navHostController.navigate(MultiplyDestination.GameInstructionDestination) },
                ontoHome = {
                    navHostController.navigate(MultiplyDestination.WelComeDestination) {
                        popUpTo<MultiplyDestination.WelComeDestination> {
                            inclusive = true
                        }
                    }
                }
            )
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

    @Serializable
    data object SelectGameMode : MultiplyDestination
}