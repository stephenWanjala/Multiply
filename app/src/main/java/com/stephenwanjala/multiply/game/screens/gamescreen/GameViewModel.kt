package com.stephenwanjala.multiply.game.screens.gamescreen

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

    fun onAction(action: GameAction){
        when(action){
            GameAction.ResetGameSettings -> {

            }
            is GameAction.UpdateDifficulty -> {
                setDifficulty(action.difficulty)
            }
        }
    }

    init {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                val savedDifficulty = preferences[DIFFICULTY_KEY]?.let { Difficulty.entries[it] } ?: Difficulty.EASY
                val score =preferences[HIGH_SCORE_KEY] ?: 0
                _state.update { it.copy(selectedDifficulty = savedDifficulty, highScore = score) }

            }
        }
    }

    private fun setDifficulty(difficulty: Difficulty) {
        _state.update { it.copy(selectedDifficulty = difficulty) }
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[DIFFICULTY_KEY] = difficulty.ordinal
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
        val speed = when (_state.value.selectedDifficulty) {
            Difficulty.EASY -> 0.15f
            Difficulty.MEDIUM -> 0.2f
            Difficulty.HARD -> 0.21f
        }

        _state.update {
            it.copy(
                gameActive = true,
                score = 0,
                lives = 3,
                problemCounter = 0,
                currentProblem = null,
                isPaused = false,
                pauseStartTime = 0L,
                gameSpeed = speed
            )
        }

        gameJob = viewModelScope.launch {
            while (_state.value.gameActive) {
                if (!_state.value.isPaused) {
                    if (_state.value.currentProblem == null) {
                        generateNewProblem()
                    }
                    updateProblemPosition()
                }
                delay(16)
            }
        }
    }

    private fun generateNewProblem() {
        val difficulty = _state.value.selectedDifficulty
        val (min, max) = when (difficulty) {
            Difficulty.EASY -> Pair(1, 9)
            Difficulty.MEDIUM -> Pair(1, 12)
            Difficulty.HARD -> Pair(1, 15)
        }
        val num1 = Random.nextInt(min, max + 1)
        val num2 = Random.nextInt(min, max + 1)
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
        val difficulty = _state.value.selectedDifficulty
        val choices = mutableSetOf(correctAnswer)
        while (choices.size < 4) {
            val wrongAnswer = when (difficulty) {
                Difficulty.EASY -> generateEasyAnswer(correctAnswer)
                Difficulty.MEDIUM -> generateMediumAnswer(correctAnswer)
                Difficulty.HARD -> generateHardAnswer(correctAnswer)
            }
            if (wrongAnswer > 0) choices.add(wrongAnswer)
        }
        return choices.shuffled()
    }

    private fun generateEasyAnswer(correct: Int): Int {
        return when (Random.nextInt(3)) {
            0 -> correct + Random.nextInt(1, 5)
            1 -> correct - Random.nextInt(1, 5)
            else -> Random.nextInt(1, 101)
        }
    }

    private fun generateMediumAnswer(correct: Int): Int {
        return when (Random.nextInt(3)) {
            0 -> correct + Random.nextInt(1, 4)
            1 -> correct - Random.nextInt(1, 4)
            else -> Random.nextInt(maxOf(1, correct - 5), correct + 6)
        }
    }

    private fun generateHardAnswer(correct: Int): Int {
        return when (Random.nextInt(3)) {
            0 -> correct + 1
            1 -> correct - 1
            else -> (correct + listOf(-2, 2).random()).coerceAtLeast(1)
        }
    }

    fun updateGameAreaHeight(height: Float) {
        _state.update {
            it.copy(
                screenHeight = height,
                gameAreaHeight = height - 40,
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
        _state.update {
            it.copy(
                gameActive = false,
                isPaused = false,
                pauseStartTime = 0L
            )
        }
        gameJob?.cancel()
        showGameOverDialog = true
        viewModelScope.launch {
            updateHighScore(_state.value.score)
        }
    }

    fun submitAnswer(selectedAnswer: Int) {
        if (!_state.value.gameActive || _state.value.isPaused) return
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
                gameAreaHeight = height - 200,
                safeAreaHeight = height * 0.8f - 300
            )
        }
    }

    fun pauseGame() {
        if (!_state.value.gameActive || _state.value.isPaused) return
        _state.update {
            it.copy(
                isPaused = true,
                pauseStartTime = System.currentTimeMillis()
            )
        }
    }

    fun resumeGame() {
        if (!_state.value.gameActive || !_state.value.isPaused) return
        val pauseDuration = System.currentTimeMillis() - _state.value.pauseStartTime
        _state.update { state ->
            val updatedProblem = state.currentProblem?.let { problem ->
                problem.copy(startTime = problem.startTime + pauseDuration)
            }
            state.copy(
                isPaused = false,
                pauseStartTime = 0L,
                currentProblem = updatedProblem
            )
        }
    }
    /*
    Ensure any running game loop is stopped
     Stop current game
     Reset game speed
     Start a new game with fresh parameters
     */

    fun reStartGame() {
        gameJob?.cancel()

        _state.update {
            it.copy(
                gameActive = false,
                score = 0,
                lives = 3,
                problemCounter = 0,
                currentProblem = null,
                isPaused = false,
                pauseStartTime = 0L
            )
        }
        startGame()
    }




    companion object {
        private val HIGH_SCORE_KEY = intPreferencesKey("high_score")
        private val DIFFICULTY_KEY = intPreferencesKey("difficulty")
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
    val gameSpeed: Float = 0f,
    val isPaused: Boolean = false,
    val pauseStartTime: Long = 0L,
    val selectedDifficulty: Difficulty =Difficulty.EASY
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

enum class Difficulty {
    EASY, MEDIUM, HARD
}


sealed interface GameAction{
    data object ResetGameSettings:GameAction
    data class UpdateDifficulty(val difficulty: Difficulty):GameAction
}