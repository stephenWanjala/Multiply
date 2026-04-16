package com.stephenwanjala.multiply.game.feat_quizmode

import com.stephenwanjala.multiply.core.data.GamePreferencesRepository
import com.stephenwanjala.multiply.game.models.MathQuestion
import com.stephenwanjala.multiply.game.models.QuizDifficulty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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

    init {
        viewModelScope.launch {
            repository.quizDifficulty.collect { savedLevel ->
                _state.update { it.copy(level = savedLevel) }
                setUpQuestions()
            }
        }
    }

    fun onEvent(event: QuizEvent) {
        when (event) {
            is QuizEvent.SelectAnswer -> {
                _state.update { state ->
                    val index = state.currentQuestionIndex
                    val updatedMap = state.selectedAnswers + (index to event.answer)
                    state.copy(
                        selectedAnswer = event.answer,
                        selectedAnswers = updatedMap
                    )
                }
            }

            QuizEvent.SubmitAnswers -> submitAnswers()

            QuizEvent.NextQuestion -> {
                _state.update { state ->
                    val currentIndex = state.currentQuestionIndex
                    val lastIndex = state.questions.lastIndex
                    val safeCurrentIndex = currentIndex.coerceIn(0, maxOf(lastIndex, 0))
                    val nextIndex =
                        if (safeCurrentIndex < lastIndex) safeCurrentIndex + 1 else safeCurrentIndex
                    state.copy(
                        currentQuestionIndex = nextIndex,
                        currentQuestion = state.questions.getOrNull(nextIndex),
                        selectedAnswer = state.selectedAnswers[nextIndex],
                        showDoneButton = nextIndex == lastIndex
                    )
                }
            }

            QuizEvent.PreviousQuestion -> {
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

            QuizEvent.RetryQuiz -> {
                setUpQuestions()
            }

            QuizEvent.DismissRecap -> {
                viewModelScope.launch {
                    _effects.send(QuizEffect.NavigateHome)
                }
            }
        }
    }

    private fun setUpQuestions() {
        val questions = generateQuestions(state.value.level)
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
                nextButtonEnabled = false
            )
        }
    }

    private fun setDifficulty(difficulty: QuizDifficulty) {
        _state.update { it.copy(level = difficulty) }
        setUpQuestions()
        viewModelScope.launch {
            repository.saveQuizDifficulty(difficulty)
        }
    }

    private fun submitAnswers() {
        val currentState = state.value
        val results = currentState.questions.mapIndexed { index, question ->
            val userAns = currentState.selectedAnswers[index]
            GameResult(
                question = question.question,
                correctAnswer = question.answer,
                userAnswer = userAns ?: -1,
                isCorrect = userAns == question.answer
            )
        }
        _state.update { it.copy(results = results, showRecap = true) }
        val correctCount = results.count { it.isCorrect }
        viewModelScope.launch {
            _effects.send(QuizEffect.ShowScoreToast(correctCount, results.size))
        }
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
    val showRecap: Boolean = false
)

data class GameResult(
    val question: String,
    val correctAnswer: Int,
    val userAnswer: Int,
    val isCorrect: Boolean
)

sealed interface QuizEvent {
    data class SelectAnswer(val answer: Int) : QuizEvent
    data object SubmitAnswers : QuizEvent
    data object NextQuestion : QuizEvent
    data object PreviousQuestion : QuizEvent
    data class UpdateLevel(val level: QuizDifficulty) : QuizEvent
    data object RetryQuiz : QuizEvent
    data object DismissRecap : QuizEvent
}

sealed interface QuizEffect {
    data class ShowScoreToast(val score: Int, val total: Int) : QuizEffect
    data object NavigateHome : QuizEffect
}
