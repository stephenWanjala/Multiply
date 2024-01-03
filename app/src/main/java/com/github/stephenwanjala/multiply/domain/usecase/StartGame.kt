package com.github.stephenwanjala.multiply.domain.usecase

import com.github.stephenwanjala.multiply.domain.model.Game
import com.github.stephenwanjala.multiply.domain.model.repository.GameRepository

class StartGame (private val gameRepository: GameRepository) {
    operator fun invoke(): Game =
        gameRepository.startNewGame()
}