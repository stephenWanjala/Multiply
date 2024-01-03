package com.github.stephenwanjala.multiply.domain.usecase

import com.github.stephenwanjala.multiply.domain.model.Game
import com.github.stephenwanjala.multiply.domain.model.repository.GameRepository


class GameUseCase(private val gameRepository: GameRepository) {

    fun startNewGame(): Game {
        return gameRepository.startNewGame()
    }

    fun checkAnswer(mathProblem: String, userAnswer: Int): Game {
        return gameRepository.checkAnswer(mathProblem, userAnswer)
    }
}