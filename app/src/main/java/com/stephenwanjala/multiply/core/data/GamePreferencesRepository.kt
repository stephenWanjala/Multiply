package com.stephenwanjala.multiply.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.stephenwanjala.multiply.game.models.BubbleMathDifficulty
import com.stephenwanjala.multiply.game.models.QuizDifficulty
import com.stephenwanjala.multiply.ui.theme.AppTheme
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

    val quizTimedMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[QUIZ_TIMED_MODE_KEY] ?: true
    }

    val appTheme: Flow<AppTheme> = dataStore.data.map { preferences ->
        preferences[APP_THEME_KEY]
            ?.let { AppTheme.fromOrdinal(it) }
            ?: AppTheme.SPACE
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

    suspend fun saveQuizTimedMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[QUIZ_TIMED_MODE_KEY] = enabled
        }
    }

    suspend fun saveAppTheme(theme: AppTheme) {
        dataStore.edit { prefs ->
            prefs[APP_THEME_KEY] = theme.ordinal
        }
    }

    companion object {
        private val BUBBLE_DIFFICULTY_KEY = intPreferencesKey("difficulty")
        private val BUBBLE_HIGH_SCORE_KEY = intPreferencesKey("high_score")
        private val QUIZ_DIFFICULTY_KEY = intPreferencesKey("QuizDifficulty")
        private val QUIZ_TIMED_MODE_KEY = booleanPreferencesKey("QuizTimedMode")
        private val APP_THEME_KEY = intPreferencesKey("AppTheme")
    }
}
