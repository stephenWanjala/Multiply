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
    fun onAction(action: QuestionAction){
        when(action){
            QuestionAction.NextQuestion -> TODO()
            QuestionAction.PreviousQuestion -> TODO()
            is QuestionAction.SelectAnswer -> TODO()
            QuestionAction.SubmitAnswer -> TODO()
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
        println(questions)
        _state.update { it.copy(questions = questions.toSet(), currentQuestion = questions.first()) }
    }

    private fun setDifficulty(difficulty: QuizDifficulty) {
        _state.update { it.copy(level = difficulty) }
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[LEVEL_KEY] = difficulty.ordinal
            }
        }
    }
}


data class QuestionsState(
    val currentQuestion: MathQuestion? = null,
    val level: QuizDifficulty = QuizDifficulty.BEGINNER,
    val questions: Set<MathQuestion> = emptySet(),
    val results: List<GameResult> = emptyList(),
    val nextButtonEnabled: Boolean = false,
    val showPreviousButton: Boolean = false,
    val showDoneButton: Boolean = false,
    val currentQuestionIndex:Int =0
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