package com.stephenwanjala.multiply.game.feat_quizmode

import com.stephenwanjala.multiply.game.models.MathQuestion
import com.stephenwanjala.multiply.game.models.QuizDifficulty
import kotlin.math.pow
import kotlin.random.Random

private val random = Random.Default // Use a single Random instance

fun generateQuestions(difficulty: QuizDifficulty): List<MathQuestion> {
    return List(difficulty.questionCount) { generateQuestion(difficulty) }
}

private fun generateQuestion(difficulty: QuizDifficulty): MathQuestion {
    val (num1, num2, operation) =
        when (difficulty) {
            QuizDifficulty.BEGINNER ->
                Triple(
                    difficulty.numberRange.random(random),
                    difficulty.numberRange.random(random),
                    listOf("+", "-").random(random))
            QuizDifficulty.INTERMEDIATE ->
                Triple(
                    difficulty.numberRange.random(random),
                    (2..20).random(random),
                    listOf("+", "-", "*", "/").random(random))
            QuizDifficulty.ADVANCED ->
                Triple(
                    difficulty.numberRange.random(random),
                    (2..10).random(random),
                    listOf("+", "-", "*", "/", "%", "^").random(random))
            QuizDifficulty.EXPERT ->
                Triple(
                    difficulty.numberRange.random(random),
                    (2..15).random(random),
                    listOf("+", "-", "*", "/", "%", "^", "()", "exp").random(random))
        }

    val answer =
        when (operation) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> {
                if (num2 == 0) num1 // Avoid division by zero in the question itself
                else num1 / num2
            }
            "%" -> {
                if (num2 == 0) 0 // Avoid modulo by zero
                else num1 % num2
            }
            "^" -> num1.toDouble().pow(num2.toDouble()).toInt()
            "()" -> {
                val num3 = (1..10).random(random)
                ((num1 + num2) * num3) / num2
            }
            "exp" -> {
                val base = (2..5).random(random)
                base.toDouble().pow(num1.toDouble()).toInt()
            }
            else -> num1 + num2
        }

    val wrongAnswers = generateWrongAnswers(answer, difficulty)

    return MathQuestion(
        question = "$num1 $operation $num2",
        level = difficulty,
        answer = answer,
        wrongAnswers = wrongAnswers)
}

private fun generateWrongAnswers(
    correctAnswer: Int,
    difficulty: QuizDifficulty
): List<Int> {
    val range =
        when (difficulty) {
            QuizDifficulty.BEGINNER -> -10..10
            QuizDifficulty.INTERMEDIATE -> -20..20
            QuizDifficulty.ADVANCED -> -50..50
            QuizDifficulty.EXPERT -> -100..100
        }

    val candidates =
        List(100) { correctAnswer + range.random(random) } // Generate a larger pool

    return candidates
        .filter { it != correctAnswer && it >= 0 } // Filter out correct answers and negatives
        .distinct() // Remove duplicates
        .shuffled(random) // Shuffle to ensure randomness
        .take(3) // Take the first 3
}
