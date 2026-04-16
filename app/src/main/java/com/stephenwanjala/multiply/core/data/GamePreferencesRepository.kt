package com.stephenwanjala.multiply.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.stephenwanjala.multiply.game.models.BubbleMathDifficulty
import com.stephenwanjala.multiply.game.models.QuizDifficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GamePreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val bubbleDifficulty: Flow<BubbleMathDifficulty> = dataStore.data.map { preferences ->
        preferences[BUBBLE_DIFFICULTY_KEY]
            ?.let { BubbleMathDifficulty.entries.getOrNull(it) }
            ?: BubbleMathDifficulty.EASY
    }

    val bubbleHighScore: Flow<Int> = dataStore.data.map { preferences ->
        preferences[BUBBLE_HIGH_SCORE_KEY] ?: 0
    }

    val quizDifficulty: Flow<QuizDifficulty> = dataStore.data.map { preferences ->
        preferences[QUIZ_DIFFICULTY_KEY]
            ?.let { QuizDifficulty.entries.getOrNull(it) }
            ?: QuizDifficulty.BEGINNER
    }

    suspend fun saveBubbleDifficulty(difficulty: BubbleMathDifficulty) {
        dataStore.edit { prefs ->
            prefs[BUBBLE_DIFFICULTY_KEY] = difficulty.ordinal
        }
    }

    suspend fun saveBubbleHighScore(score: Int) {
        dataStore.edit { prefs ->
            prefs[BUBBLE_HIGH_SCORE_KEY] = score
        }
    }

    suspend fun saveQuizDifficulty(difficulty: QuizDifficulty) {
        dataStore.edit { prefs ->
            prefs[QUIZ_DIFFICULTY_KEY] = difficulty.ordinal
        }
    }

    companion object {
        private val BUBBLE_DIFFICULTY_KEY = intPreferencesKey("difficulty")
        private val BUBBLE_HIGH_SCORE_KEY = intPreferencesKey("high_score")
        private val QUIZ_DIFFICULTY_KEY = intPreferencesKey("QuizDifficulty")
    }
}
