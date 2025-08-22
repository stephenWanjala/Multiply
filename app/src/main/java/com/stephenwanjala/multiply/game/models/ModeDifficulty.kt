package com.stephenwanjala.multiply.game.models

sealed class ModeDifficulty {
    data class Bubble(val difficulty: BubbleMathDifficulty) : ModeDifficulty()
    data class Quiz(val difficulty: QuizDifficulty) : ModeDifficulty()
}
