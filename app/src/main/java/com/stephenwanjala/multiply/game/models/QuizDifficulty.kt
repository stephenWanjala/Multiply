package com.stephenwanjala.multiply.game.models

enum class QuizDifficulty(val questionCount: Int, val numberRange: IntRange) {
    BEGINNER(15, 1..20),
    INTERMEDIATE(20, 5..50),
    ADVANCED(25, 10..100),
    EXPERT(30, 1..200)
}