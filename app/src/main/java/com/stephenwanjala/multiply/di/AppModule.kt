package com.stephenwanjala.multiply.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val MULTIPLYPREFRENCES ="MULTIPLYPREFRENCES"

    @Provides
    @Singleton
    fun provideDataStorePreferences(app: Application): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                app.preferencesDataStoreFile(MULTIPLYPREFRENCES)
            }
        )
}