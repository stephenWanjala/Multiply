package com.github.stephenwanjala.multiply.di

import com.github.stephenwanjala.multiply.data.repository.GameRepositoryImpl
import com.github.stephenwanjala.multiply.domain.model.repository.GameRepository
import com.github.stephenwanjala.multiply.domain.usecase.CheckAnswer
import com.github.stephenwanjala.multiply.domain.usecase.GameUseCase
import com.github.stephenwanjala.multiply.domain.usecase.StartGame
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGameRepository(): GameRepository = GameRepositoryImpl()





    @Provides
    @Singleton
    fun provideGameUseCase(
        gameRepository: GameRepository
    ): GameUseCase = GameUseCase(StartGame(gameRepository), CheckAnswer(gameRepository))
}