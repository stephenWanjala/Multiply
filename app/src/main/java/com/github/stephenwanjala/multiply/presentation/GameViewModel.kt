package com.github.stephenwanjala.multiply.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.stephenwanjala.multiply.domain.model.Game
import com.github.stephenwanjala.multiply.domain.usecase.GameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameUseCase: GameUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state = _state.stateIn(viewModelScope, SharingStarted.Lazily, GameState())

    init {
        startGame()
    }
    fun onEvent(event: GameEvent) {
        when (event) {
            GameEvent.GameOver -> handleGameOver()
            is GameEvent.Answer -> {
                val game  = gameUseCase.checkAnswer(event.mathProblem, event.answer)
                _state.update { it.copy(game = game) }
            }
            GameEvent.TimesUp -> handleTimesUp()
            GameEvent.StartGame ->startGame()
        }
    }

    private fun startGame() {
        gameUseCase.startGame()
        _state.update { it.copy(gameStarted = true) }
    }

    private fun handleGameOver() {
        // Handle logic for game over (e.g., navigate to game over screen)
        _state.update { it.copy(gameFinished = true)  }
    }



    private fun handleTimesUp() {
        // Handle logic for times up (e.g., navigate to game over screen)
        handleGameOver()
    }
}


data class GameState(
    val gameStarted: Boolean = false,
    val gameFinished: Boolean = false,
    val game: Game = Game(
        lives = 3,
        points = 0,
        mathProblem = "",
        options = emptyList(),
        correctAnswer = ""
    )
)