package com.stephenwanjala.multiply

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )

    private var gameJob: Job? = null
    var showGameOverDialog by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                _state.update { it.copy(highScore = preferences[HIGH_SCORE_KEY] ?: 0) }
            }
        }
    }

    private suspend fun updateHighScore(newScore: Int) {
        if (newScore > _state.value.highScore) {
            dataStore.edit { preferences ->
                preferences[HIGH_SCORE_KEY] = newScore
            }
            _state.update { it.copy(highScore = newScore) }
        }
    }

    fun startGame() {
        if (gameJob?.isActive == true) return

        showGameOverDialog = false
        gameJob?.cancel()

        _state.update {
            it.copy(
                gameActive = true,
                score = 0,
                lives = 3,
                problemCounter = 0,
                currentProblem = null
            )
        }

        gameJob = viewModelScope.launch {
            while (_state.value.gameActive) {
                if (_state.value.currentProblem == null) {
                    generateNewProblem()
                }
                delay(16)
                updateProblemPosition()
            }
        }
    }

    private fun generateNewProblem() {
        val num1 = Random.nextInt(1, 10)
        val num2 = Random.nextInt(1, 10)
        val answer = num1 * num2
        val choices = generateChoices(answer)

        _state.update { state ->
            val newCounter = state.problemCounter + 1
            state.copy(
                problemCounter = newCounter,
                currentProblem = Problem(
                    id = newCounter,
                    num1 = num1,
                    num2 = num2,
                    answer = answer,
                    choices = choices,
                    startTime = System.currentTimeMillis()
                )
            )
        }
    }

    private fun generateChoices(correctAnswer: Int): List<Int> {
        val choices = mutableSetOf(correctAnswer)
        while (choices.size < 4) {
            val wrongAnswer = when (Random.nextInt(3)) {
                0 -> correctAnswer + Random.nextInt(1, 4)
                1 -> correctAnswer - Random.nextInt(1, 4)
                else -> Random.nextInt(1, 101)
            }
            if (wrongAnswer > 0) choices.add(wrongAnswer)
        }
        return choices.toList().shuffled()
    }

    fun updateGameAreaHeight(height: Float) {
        _state.update {
            it.copy(
                gameAreaHeight = height - 200,
                safeAreaHeight = height * 0.8f - 300
            )
        }
    }

    private fun updateProblemPosition() {
        val currentState = _state.value
        currentState.currentProblem?.let { problem ->
            val elapsed = (System.currentTimeMillis() - problem.startTime) / 1000f
            val newPosition = elapsed * currentState.gameSpeed * currentState.gameAreaHeight

            if (newPosition >= currentState.safeAreaHeight) {
                handleMissedProblem()
            } else {
                _state.update {
                    it.copy(currentProblem = problem.copy(position = newPosition))
                }
            }
        }
    }

    private fun handleMissedProblem() {
        _state.update { it.copy(lives = it.lives - 1) }

        if (_state.value.lives <= 0) {
            endGame()
        } else {
            _state.update { it.copy(currentProblem = null) }
        }
    }

    private fun endGame() {
        _state.update { it.copy(gameActive = false) }
        gameJob?.cancel()
        showGameOverDialog = true
        viewModelScope.launch {
            updateHighScore(_state.value.score)
        }
    }

    fun submitAnswer(selectedAnswer: Int) {
        _state.value.currentProblem?.let { problem ->
            if (selectedAnswer == problem.answer) {
                _state.update {
                    it.copy(
                        score = it.score + 1,
                        currentProblem = null
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        lives = it.lives - 1,
                        currentProblem = null
                    )
                }
                if (_state.value.lives <= 0) {
                    endGame()
                }
            }
        }
    }

    fun updateScreenHeight(height: Float) {
        _state.update {
            it.copy(
                screenHeight = height,
                gameSpeed = 0.15f
            )
        }
    }

    companion object {
        private val HIGH_SCORE_KEY = intPreferencesKey("high_score")
    }
}

data class GameState(
    val currentProblem: Problem? = null,
    val score: Int = 0,
    val lives: Int = 3,
    val gameActive: Boolean = false,
    val screenHeight: Float = 0f,
    val gameAreaHeight: Float = 0f,
    val safeAreaHeight: Float = 80f,
    val problemCounter: Int = 0,
    val highScore: Int = 0,
    val gameSpeed: Float = 0f
)

data class Problem(
    val id: Int,
    val num1: Int,
    val num2: Int,
    val answer: Int,
    val choices: List<Int>,
    val startTime: Long,
    val position: Float = 0f
)