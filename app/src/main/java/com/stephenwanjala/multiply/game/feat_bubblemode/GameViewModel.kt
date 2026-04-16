package com.stephenwanjala.multiply.game.feat_bubblemode

import com.stephenwanjala.multiply.core.data.GamePreferencesRepository
import com.stephenwanjala.multiply.game.models.BubbleMathDifficulty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(
    private val repository: GamePreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )

    private val _effects = Channel<BubbleGameEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var gameJob: Job? = null
    private var pauseStartTime: Long = 0L

    fun onEvent(event: BubbleGameEvent) {
        when (event) {
            BubbleGameEvent.StartGame -> startGame()
            BubbleGameEvent.PauseGame -> pauseGame()
            BubbleGameEvent.ResumeGame -> resumeGame()
            BubbleGameEvent.RestartGame -> restartGame()
            is BubbleGameEvent.SubmitAnswer -> submitAnswer(event.answer)
            is BubbleGameEvent.UpdateGameAreaHeight -> updateGameAreaHeight(event.height)
            is BubbleGameEvent.UpdateScreenHeight -> updateScreenHeight(event.height)
            is BubbleGameEvent.UpdateDifficulty -> setDifficulty(event.difficulty)
            BubbleGameEvent.ResetSettings -> { /* reserved for future use */ }
            BubbleGameEvent.DismissGameOver -> _state.update { it.copy(showGameOverDialog = false) }
        }
    }

    init {
        viewModelScope.launch {
            launch {
                repository.bubbleDifficulty.collect { difficulty ->
                    _state.update { it.copy(selectedDifficulty = difficulty) }
                }
            }
            launch {
                repository.bubbleHighScore.collect { score ->
                    _state.update { it.copy(highScore = score) }
                }
            }
        }
    }

    private fun setDifficulty(difficulty: BubbleMathDifficulty) {
        _state.update { it.copy(selectedDifficulty = difficulty) }
        viewModelScope.launch {
            repository.saveBubbleDifficulty(difficulty)
        }
    }

    private suspend fun updateHighScore(newScore: Int) {
        if (newScore > _state.value.highScore) {
            repository.saveBubbleHighScore(newScore)
            _state.update { it.copy(highScore = newScore) }
        }
    }

    private fun startGame() {
        if (gameJob?.isActive == true) return

        gameJob?.cancel()
        val speed = when (_state.value.selectedDifficulty) {
            BubbleMathDifficulty.EASY -> 0.15f
            BubbleMathDifficulty.MEDIUM -> 0.2f
            BubbleMathDifficulty.HARD -> 0.21f
        }

        _state.update {
            it.copy(
                gameActive = true,
                score = 0,
                lives = 3,
                problemCounter = 0,
                currentProblem = null,
                isPaused = false,
                showGameOverDialog = false,
                gameSpeed = speed
            )
        }
        pauseStartTime = 0L

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
            BubbleMathDifficulty.EASY -> Pair(1, 9)
            BubbleMathDifficulty.MEDIUM -> Pair(1, 12)
            BubbleMathDifficulty.HARD -> Pair(1, 15)
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
                BubbleMathDifficulty.EASY -> generateEasyAnswer(correctAnswer)
                BubbleMathDifficulty.MEDIUM -> generateMediumAnswer(correctAnswer)
                BubbleMathDifficulty.HARD -> generateHardAnswer(correctAnswer)
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

    private fun updateGameAreaHeight(height: Float) {
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
                showGameOverDialog = true
            )
        }
        gameJob?.cancel()
        pauseStartTime = 0L
        viewModelScope.launch {
            updateHighScore(_state.value.score)
            _effects.send(BubbleGameEffect.PlayGameOverHaptic)
        }
    }

    private fun submitAnswer(selectedAnswer: Int) {
        if (!_state.value.gameActive || _state.value.isPaused) return
        _state.value.currentProblem?.let { problem ->
            if (selectedAnswer == problem.answer) {
                _state.update {
                    it.copy(
                        score = it.score + 1,
                        currentProblem = null
                    )
                }
                viewModelScope.launch { _effects.send(BubbleGameEffect.PlayCorrectAnswerHaptic) }
            } else {
                _state.update {
                    it.copy(
                        lives = it.lives - 1,
                        currentProblem = null
                    )
                }
                viewModelScope.launch { _effects.send(BubbleGameEffect.PlayWrongAnswerHaptic) }
                if (_state.value.lives <= 0) {
                    endGame()
                }
            }
        }
    }

    private fun updateScreenHeight(height: Float) {
        _state.update {
            it.copy(
                screenHeight = height,
                gameAreaHeight = height - 200,
                safeAreaHeight = height * 0.8f - 300
            )
        }
    }

    private fun pauseGame() {
        if (!_state.value.gameActive || _state.value.isPaused) return
        pauseStartTime = System.currentTimeMillis()
        _state.update { it.copy(isPaused = true) }
    }

    private fun resumeGame() {
        if (!_state.value.gameActive || !_state.value.isPaused) return
        val pauseDuration = System.currentTimeMillis() - pauseStartTime
        _state.update { state ->
            val updatedProblem = state.currentProblem?.let { problem ->
                problem.copy(startTime = problem.startTime + pauseDuration)
            }
            state.copy(
                isPaused = false,
                currentProblem = updatedProblem
            )
        }
        pauseStartTime = 0L
    }

    private fun restartGame() {
        gameJob?.cancel()
        _state.update {
            it.copy(
                gameActive = false,
                score = 0,
                lives = 3,
                problemCounter = 0,
                currentProblem = null,
                isPaused = false,
                showGameOverDialog = false
            )
        }
        pauseStartTime = 0L
        startGame()
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
    val showGameOverDialog: Boolean = false,
    val selectedDifficulty: BubbleMathDifficulty = BubbleMathDifficulty.EASY
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

sealed interface BubbleGameEvent {
    data object StartGame : BubbleGameEvent
    data object PauseGame : BubbleGameEvent
    data object ResumeGame : BubbleGameEvent
    data object RestartGame : BubbleGameEvent
    data class SubmitAnswer(val answer: Int) : BubbleGameEvent
    data class UpdateGameAreaHeight(val height: Float) : BubbleGameEvent
    data class UpdateScreenHeight(val height: Float) : BubbleGameEvent
    data class UpdateDifficulty(val difficulty: BubbleMathDifficulty) : BubbleGameEvent
    data object ResetSettings : BubbleGameEvent
    data object DismissGameOver : BubbleGameEvent
}

sealed interface BubbleGameEffect {
    data object PlayCorrectAnswerHaptic : BubbleGameEffect
    data object PlayWrongAnswerHaptic : BubbleGameEffect
    data object PlayGameOverHaptic : BubbleGameEffect
}
