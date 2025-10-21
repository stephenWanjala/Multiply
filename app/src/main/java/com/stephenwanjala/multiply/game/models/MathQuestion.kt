package com.stephenwanjala.multiply.game.models

data class MathQuestion(
    val question: String,
    val level: QuizDifficulty,
    val answer: Int,
    val wrongAnswers: List<Int>
) {
    val allAnswers: List<Int> = (wrongAnswers + answer).shuffled()
}


fun Int.isLargeNumber(): Boolean = this > 1000
fun MathQuestion.hasLargeNumbers(): Boolean =
    answer.isLargeNumber() || allAnswers.any { it.isLargeNumber() }