package com.github.stephenwanjala.multiply.domain.usecase

import com.github.stephenwanjala.multiply.domain.model.repository.GameRepository

class CheckAnswer(private val gameRepository: GameRepository) {
    operator fun invoke(mathProblem: String, userAnswer: Int) =
        gameRepository.checkAnswer(mathProblem, userAnswer)
}