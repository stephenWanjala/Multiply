package com.stephenwanjala.multiply.game.models

sealed class GameMode {
    data class BubbleMathBlitz(val difficulty: BubbleMathDifficulty) : GameMode()
    data class QuizGenius(val difficulty: QuizDifficulty) : GameMode()
}