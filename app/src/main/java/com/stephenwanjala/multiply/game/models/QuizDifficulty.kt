package com.stephenwanjala.multiply.game.models

enum class QuizDifficulty(
    val questionCount: Int,
    val numberRange: IntRange,
    val timeLimitSeconds: Int
) {
    BEGINNER(15, 1..20, 15),
    INTERMEDIATE(20, 5..50, 12),
    ADVANCED(25, 10..100, 15),
    EXPERT(30, 1..200, 20)
}