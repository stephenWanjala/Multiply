package com.github.stephenwanjala.multiply.data.repository

import com.github.stephenwanjala.multiply.domain.model.Game
import com.github.stephenwanjala.multiply.domain.model.repository.GameRepository

class GameRepositoryImpl: GameRepository {
    override fun startNewGame(): Game {
        TODO("Not yet implemented")
    }

    override fun checkAnswer(mathProblem: String, userAnswer: Int): Game {
        TODO("Not yet implemented")
    }
}