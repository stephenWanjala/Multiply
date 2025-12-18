package com.stephenwanjala.multiply.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.stephenwanjala.multiply.game.feat_bubblemode.GameViewModel
import com.stephenwanjala.multiply.game.feat_quizmode.QuestionsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private const val MULTIPLY_PREFERENCES = "MULTIPLYPREFRENCES"

val appModule = module {
    single<DataStore<Preferences>> {
        val app: Application = get()
        PreferenceDataStoreFactory.create(
            produceFile = { app.preferencesDataStoreFile(MULTIPLY_PREFERENCES) }
        )
    }
    viewModelOf(::QuestionsViewModel)
    viewModelOf(::GameViewModel)
}