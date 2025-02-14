package com.stephenwanjala.multiply.game.feat_quizmode

import com.stephenwanjala.multiply.game.models.MathQuestion
import com.stephenwanjala.multiply.game.models.QuizDifficulty
import kotlin.math.pow

fun generateQuestions(difficulty: QuizDifficulty): List<MathQuestion> {
    return List(difficulty.questionCount) { generateQuestion(difficulty) }
}

private fun generateQuestion(difficulty: QuizDifficulty): MathQuestion {
    val (num1, num2, operation) = when (difficulty) {
        QuizDifficulty.BEGINNER -> Triple(difficulty.numberRange.random(), difficulty.numberRange.random(), listOf("+", "-").random())
        QuizDifficulty.INTERMEDIATE -> Triple(difficulty.numberRange.random(), (2..20).random(), listOf("+", "-", "*", "/").random())
        QuizDifficulty.ADVANCED -> Triple(difficulty.numberRange.random(), (2..10).random(), listOf("+", "-", "*", "/", "%", "^").random())
        QuizDifficulty.EXPERT -> Triple(difficulty.numberRange.random(), (2..15).random(), listOf("+", "-", "*", "/", "%", "^", "()", "exp").random())
    }

    val answer = when (operation) {
        "+" -> num1 + num2
        "-" -> num1 - num2
        "*" -> num1 * num2
        "/" -> if (num2 != 0) num1 / num2 else num1
        "%" -> if (num2 != 0) num1 % num2 else 0
        "^" -> num1.toDouble().pow(num2.toDouble()).toInt()
        "()" -> {
            val num3 = (1..10).random()
            ((num1 + num2) * num3) / num2
        }
        "exp" -> {
            val base = (2..5).random()
            base.toDouble().pow(num1.toDouble()).toInt()
        }
        else -> num1 + num2
    }

    val wrongAnswers = generateWrongAnswers(answer, difficulty)

    return MathQuestion(
        question = "$num1 $operation $num2",
        level = difficulty,
        answer = answer,
        wrongAnswers = wrongAnswers
    )
}

private fun generateWrongAnswers(correctAnswer: Int, difficulty: QuizDifficulty): List<Int> {
    val range = when (difficulty) {
        QuizDifficulty.BEGINNER -> -10..10
        QuizDifficulty.INTERMEDIATE -> -20..20
        QuizDifficulty.ADVANCED -> -50..50
        QuizDifficulty.EXPERT -> -100..100
    }

    return buildList {
        while (size < 3) {
            val wrongAnswer = correctAnswer + range.random()
            if (wrongAnswer != correctAnswer && !contains(wrongAnswer) && wrongAnswer >= 0) {
                add(wrongAnswer)
            }
        }
    }
}
