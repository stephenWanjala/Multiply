package com.stephenwanjala.multiply.game.feat_quizmode

import com.stephenwanjala.multiply.game.models.MathQuestion
import com.stephenwanjala.multiply.game.models.QuizDifficulty
import kotlin.math.pow
import kotlin.random.Random

private val random = Random.Default

fun generateQuestions(difficulty: QuizDifficulty): List<MathQuestion> {
    return List(difficulty.questionCount) { generateQuestion(difficulty) }
}

fun generateQuestion(difficulty: QuizDifficulty): MathQuestion {
    val (num1, num2, operation) =
        when (difficulty) {
            QuizDifficulty.BEGINNER ->
                Triple(
                    difficulty.numberRange.random(random),
                    difficulty.numberRange.random(random),
                    listOf("+", "-").random(random)
                )

            QuizDifficulty.INTERMEDIATE ->
                Triple(
                    difficulty.numberRange.random(random),
                    (2..20).random(random),
                    listOf("+", "-", "*", "/").random(random)
                )

            QuizDifficulty.ADVANCED ->
                Triple(
                    difficulty.numberRange.random(random),
                    (2..10).random(random),
                    listOf("+", "-", "*", "/", "%", "^").random(random)
                )

            QuizDifficulty.EXPERT ->
                Triple(
                    difficulty.numberRange.random(random),
                    (2..15).random(random),
                    listOf("+", "-", "*", "/", "%", "^", "()", "exp").random(random)
                )
        }

    val (finalNum1, finalNum2) =
        if (operation == "-" && num2 > num1) {
            num2 to num1 // Swap numbers if subtraction and num2 is greater than num1
        } else {
            num1 to num2
        }

    val answer =
        when (operation) {
            "+" -> finalNum1 + finalNum2
            "-" -> finalNum1 - finalNum2
            "*" -> finalNum1 * finalNum2
            "/" -> {
                if (finalNum2 == 0) finalNum1 // Avoid division by zero in the question itself
                else finalNum1 / finalNum2
            }

            "%" -> {
                if (finalNum2 == 0) 0 // Avoid modulo by zero
                else finalNum1 % finalNum2
            }

            "^" -> finalNum1.toDouble().pow(finalNum2.toDouble()).toInt()
            "()" -> {
                val num3 = (1..10).random(random)
                val operation1 = listOf("+", "-").random(random)
                val operation2 = listOf("*", "/").random(random)

                // Ensure no division by zero
                val safeNum2 = if (finalNum2 == 0) 1 else finalNum2

                val question = "($finalNum1 $operation1 $safeNum2) $operation2 $num3"

                val answer =
                    when (operation2) {
                        "*" ->
                            when (operation1) {
                                "+" -> (finalNum1 + safeNum2) * num3
                                "-" -> (finalNum1 - safeNum2) * num3
                                else -> 0 // Should not happen
                            }

                        "/" ->
                            when (operation1) {
                                "+" -> (finalNum1 + safeNum2) / num3
                                "-" -> (finalNum1 - safeNum2) / num3
                                else -> 0 // Should not happen
                            }

                        else -> 0 // Should not happen
                    }

                val wrongAnswers = generateWrongAnswers(answer, difficulty)

                return MathQuestion(
                    question = question,
                    level = difficulty,
                    answer = answer,
                    wrongAnswers = wrongAnswers
                )
            }

            "exp" -> {
                val base = (2..5).random(random)
                base.toDouble().pow(finalNum1.toDouble()).toInt()
            }

            else -> finalNum1 + finalNum2
        }

    val wrongAnswers = generateWrongAnswers(answer, difficulty)

    return MathQuestion(
        question = "$finalNum1 $operation $finalNum2",
        level = difficulty,
        answer = answer,
        wrongAnswers = wrongAnswers
    )
}


fun generateWrongAnswers(correctAnswer: Int, difficulty: QuizDifficulty): List<Int> {
    if (correctAnswer < 0) {
        return emptyList()
    }

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
