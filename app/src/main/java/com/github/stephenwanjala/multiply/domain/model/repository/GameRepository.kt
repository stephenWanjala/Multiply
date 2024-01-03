package com.github.stephenwanjala.multiply.domain.model.repository

import com.github.stephenwanjala.multiply.domain.model.Game

interface GameRepository {
    fun startNewGame(): Game
    fun checkAnswer(mathProblem: String, userAnswer: Int): Game
}