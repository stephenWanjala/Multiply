package com.stephenwanjala.multiply.game.feat_quizmode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephenwanjala.multiply.core.data.GamePreferencesRepository
import com.stephenwanjala.multiply.game.models.MathQuestion
import com.stephenwanjala.multiply.game.models.QuizDifficulty
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuestionsViewModel(
    private val repository: GamePreferencesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(QuestionsState())
    val state = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private val _effects = Channel<QuizEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var timerJob: Job? = null
    private var autoAdvanceJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                repository.quizDifficulty,
                repository.quizTimedMode
            ) { level, timed -> level to timed }.collect { (level, timed) ->
                val prevLevel = _state.value.level
                val prevTimed = _state.value.timedMode
                _state.update { it.copy(level = level, timedMode = timed) }
                if (_state.value.questions.isEmpty() || prevLevel != level || prevTimed != timed) {
                    setUpQuestions()
                }
            }
        }
    }

    fun onEvent(event: QuizEvent) {
        when (event) {
            is QuizEvent.SelectAnswer -> onAnswerSelected(event.answer)

            QuizEvent.SubmitAnswers -> submitAnswers()

            QuizEvent.NextQuestion -> advanceToNext()

            QuizEvent.PreviousQuestion -> {
                if (_state.value.timedMode) return
                _state.update { state ->
                    val currentIndex = state.currentQuestionIndex
                    val prevIndex = (currentIndex - 1).coerceAtLeast(0)
                    state.copy(
                        currentQuestionIndex = prevIndex,
                        currentQuestion = state.questions.getOrNull(prevIndex),
                        selectedAnswer = state.selectedAnswers[prevIndex],
                        showDoneButton = false
                    )
                }
            }

            is QuizEvent.UpdateLevel -> setDifficulty(event.level)

            is QuizEvent.SetTimedMode -> setTimedMode(event.enabled)

            QuizEvent.RetryQuiz -> setUpQuestions()

            QuizEvent.DismissRecap -> {
                viewModelScope.launch { _effects.send(QuizEffect.NavigateHome) }
            }

            QuizEvent.PauseTimer -> pauseTimer()
            QuizEvent.ResumeTimer -> resumeTimer()
        }
    }

    private fun onAnswerSelected(answer: Int) {
        val s = _state.value
        if (s.isLocked) return
        val index = s.currentQuestionIndex
        val updatedMap = s.selectedAnswers + (index to answer)
        _state.update {
            it.copy(
                selectedAnswer = answer,
                selectedAnswers = updatedMap
            )
        }
        if (s.timedMode) {
            lockAndAutoAdvance()
        }
    }

    private fun lockAndAutoAdvance() {
        pauseTimer()
        _state.update { it.copy(isLocked = true) }
        autoAdvanceJob?.cancel()
        autoAdvanceJob = viewModelScope.launch {
            delay(600)
            advanceToNext()
        }
    }

    private fun advanceToNext() {
        autoAdvanceJob?.cancel()
        pauseTimer()
        val s = _state.value
        val lastIndex = s.questions.lastIndex
        if (s.currentQuestionIndex >= lastIndex) {
            submitAnswers()
            return
        }
        val nextIndex = s.currentQuestionIndex + 1
        _state.update {
            it.copy(
                currentQuestionIndex = nextIndex,
                currentQuestion = it.questions.getOrNull(nextIndex),
                selectedAnswer = it.selectedAnswers[nextIndex],
                showDoneButton = nextIndex == lastIndex,
                isLocked = false
            )
        }
        if (_state.value.timedMode) startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        val s = _state.value
        if (!s.timedMode || s.showRecap) return
        val limit = s.level.timeLimitSeconds
        _state.update { it.copy(timeLimit = limit, remainingTime = limit) }
        timerJob = viewModelScope.launch {
            var remaining = limit
            while (remaining > 0) {
                delay(1000)
                remaining--
                _state.update { it.copy(remainingTime = remaining) }
            }
            onTimeUp()
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun resumeTimer() {
        val s = _state.value
        if (!s.timedMode || s.showRecap || s.isLocked || s.remainingTime <= 0 || s.questions.isEmpty()) return
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = s.remainingTime
            while (remaining > 0) {
                delay(1000)
                remaining--
                _state.update { it.copy(remainingTime = remaining) }
            }
            onTimeUp()
        }
    }

    private fun onTimeUp() {
        val s = _state.value
        val index = s.currentQuestionIndex
        val hadAnswer = s.selectedAnswers[index] != null
        if (!hadAnswer) {
            _state.update {
                it.copy(
                    voidedQuestions = it.voidedQuestions + index,
                    isLocked = true
                )
            }
            viewModelScope.launch { _effects.send(QuizEffect.TimeUp) }
        }
        autoAdvanceJob?.cancel()
        autoAdvanceJob = viewModelScope.launch {
            delay(700)
            advanceToNext()
        }
    }

    private fun setUpQuestions() {
        pauseTimer()
        autoAdvanceJob?.cancel()
        val level = _state.value.level
        val questions = generateQuestions(level)
        _state.update {
            it.copy(
                questions = questions,
                currentQuestionIndex = 0,
                currentQuestion = questions.firstOrNull(),
                selectedAnswers = emptyMap(),
                selectedAnswer = null,
                results = emptyList(),
                showRecap = false,
                showDoneButton = false,
                showPreviousButton = false,
                nextButtonEnabled = false,
                voidedQuestions = emptySet(),
                isLocked = false,
                timeLimit = level.timeLimitSeconds,
                remainingTime = level.timeLimitSeconds
            )
        }
        if (_state.value.timedMode) startTimer()
    }

    private fun setDifficulty(difficulty: QuizDifficulty) {
        _state.update { it.copy(level = difficulty) }
        setUpQuestions()
        viewModelScope.launch { repository.saveQuizDifficulty(difficulty) }
    }

    private fun setTimedMode(enabled: Boolean) {
        viewModelScope.launch { repository.saveQuizTimedMode(enabled) }
    }

    private fun submitAnswers() {
        pauseTimer()
        autoAdvanceJob?.cancel()
        val currentState = _state.value
        val results = currentState.questions.mapIndexed { index, question ->
            val userAns = currentState.selectedAnswers[index]
            val voided = index in currentState.voidedQuestions
            GameResult(
                question = question.question,
                correctAnswer = question.answer,
                userAnswer = userAns ?: -1,
                isCorrect = !voided && userAns == question.answer,
                wasVoided = voided
            )
        }
        _state.update { it.copy(results = results, showRecap = true, isLocked = false) }
        val correctCount = results.count { it.isCorrect }
        viewModelScope.launch {
            _effects.send(QuizEffect.ShowScoreToast(correctCount, results.size))
        }
    }

    override fun onCleared() {
        pauseTimer()
        autoAdvanceJob?.cancel()
        super.onCleared()
    }
}

data class QuestionsState(
    val currentQuestion: MathQuestion? = null,
    val level: QuizDifficulty = QuizDifficulty.BEGINNER,
    val questions: List<MathQuestion> = emptyList(),
    val results: List<GameResult> = emptyList(),
    val nextButtonEnabled: Boolean = false,
    val showPreviousButton: Boolean = false,
    val showDoneButton: Boolean = false,
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val selectedAnswer: Int? = null,
    val showRecap: Boolean = false,
    val timedMode: Boolean = true,
    val timeLimit: Int = QuizDifficulty.BEGINNER.timeLimitSeconds,
    val remainingTime: Int = QuizDifficulty.BEGINNER.timeLimitSeconds,
    val voidedQuestions: Set<Int> = emptySet(),
    val isLocked: Boolean = false
)

data class GameResult(
    val question: String,
    val correctAnswer: Int,
    val userAnswer: Int,
    val isCorrect: Boolean,
    val wasVoided: Boolean = false
)

sealed interface QuizEvent {
    data class SelectAnswer(val answer: Int) : QuizEvent
    data object SubmitAnswers : QuizEvent
    data object NextQuestion : QuizEvent
    data object PreviousQuestion : QuizEvent
    data class UpdateLevel(val level: QuizDifficulty) : QuizEvent
    data class SetTimedMode(val enabled: Boolean) : QuizEvent
    data object RetryQuiz : QuizEvent
    data object DismissRecap : QuizEvent
    data object PauseTimer : QuizEvent
    data object ResumeTimer : QuizEvent
}

sealed interface QuizEffect {
    data class ShowScoreToast(val score: Int, val total: Int) : QuizEffect
    data object NavigateHome : QuizEffect
    data object TimeUp : QuizEffect
}
