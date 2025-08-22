package com.stephenwanjala.multiply.game.feat_quizmode

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephenwanjala.multiply.game.models.MathQuestion
import com.stephenwanjala.multiply.game.models.QuizDifficulty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _state = MutableStateFlow(QuestionsState())
    val state = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    init {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                val savedLevel = preferences[LEVEL_KEY]?.let { QuizDifficulty.entries[it] }
                    ?: QuizDifficulty.BEGINNER
                _state.update { it.copy(level = savedLevel) }
                setUpQuestions()
            }
        }
    }
    fun onAction(action: QuestionAction) {
        when (action) {
            is QuestionAction.SelectAnswer -> {
                _state.update { state ->
                    val index = state.currentQuestionIndex
                    val updatedMap = state.selectedAnswers + (index to action.answer)
                    state.copy(
                        selectedAnswer = action.answer,
                        selectedAnswers = updatedMap
                    )
                }
            }
            QuestionAction.SubmitAnswer -> {
                submitAnswers()
            }
            QuestionAction.NextQuestion -> {
                _state.update { state ->
                    val currentIndex = state.currentQuestionIndex
                    val lastIndex = state.questions.lastIndex
                    val safeCurrentIndex = currentIndex.coerceIn(0, maxOf(lastIndex, 0))
                    // Move to next only if not at last
                    val nextIndex = if (safeCurrentIndex < lastIndex) safeCurrentIndex + 1 else safeCurrentIndex
                    state.copy(
                        currentQuestionIndex = nextIndex,
                        currentQuestion = state.questions.getOrNull(nextIndex),
                        // restore previously selected answer for that question if any
                        selectedAnswer = state.selectedAnswers[nextIndex],
                        showDoneButton = nextIndex == lastIndex
                    )
                }
            }
            QuestionAction.PreviousQuestion -> {
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
            is QuestionAction.UpdateLevel -> {
                setDifficulty(action.level)
            }
        }
    }

    companion object {
        private val LEVEL_KEY = intPreferencesKey("QuizDifficulty")
    }
    private  fun setUpQuestions(){
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
        // Immediately set up questions for the chosen level to avoid empty UI while DataStore updates
        setUpQuestions()
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[LEVEL_KEY] = difficulty.ordinal
            }
        }
    }

    private fun submitAnswers() {
        viewModelScope.launch {
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

sealed interface QuestionAction {
    data class SelectAnswer(val answer: Int) : QuestionAction
    data object SubmitAnswer : QuestionAction
    data object NextQuestion : QuestionAction
    data object PreviousQuestion : QuestionAction
    data class UpdateLevel(val level: QuizDifficulty):QuestionAction
}