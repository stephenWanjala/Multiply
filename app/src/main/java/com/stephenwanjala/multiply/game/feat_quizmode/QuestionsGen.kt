package com.stephenwanjala.multiply.game.feat_quizmode

import com.stephenwanjala.multiply.game.models.MathQuestion
import com.stephenwanjala.multiply.game.models.QuizDifficulty
import kotlin.math.pow
import kotlin.random.Random

private val random = Random.Default

/**
 * Generates a list of math questions for the given quiz difficulty.
 *
 * @param difficulty The difficulty level which determines the number and type of questions.
 * @return A list containing difficulty.questionCount MathQuestion items.
 */
fun generateQuestions(difficulty: QuizDifficulty): List<MathQuestion> {
    return List(difficulty.questionCount) { generateQuestion(difficulty) }
}

/**
 * Generates a single math question appropriate for the provided difficulty.
 *
 * Internally dispatches to specific generators for each difficulty tier.
 *
 * @param difficulty The quiz difficulty used to determine the question type and range.
 * @return A MathQuestion with computed correct answer and plausible wrong answers.
 */
fun generateQuestion(difficulty: QuizDifficulty): MathQuestion {
    return when (difficulty) {
        QuizDifficulty.BEGINNER -> generateBeginnerQuestion()
        QuizDifficulty.INTERMEDIATE -> generateIntermediateQuestion()
        QuizDifficulty.ADVANCED -> generateAdvancedQuestion()
        QuizDifficulty.EXPERT -> generateExpertQuestion()
    }
}

private fun generateBeginnerQuestion(): MathQuestion {
    val operation = listOf("+", "-").random(random)

    return when (operation) {
        "+" -> {
            val num1 = (1..20).random(random)
            val num2 = (1..20).random(random)
            val answer = num1 + num2
            MathQuestion(
                question = "$num1 + $num2",
                level = QuizDifficulty.BEGINNER,
                answer = answer,
                wrongAnswers = generateSmartWrongAnswers(answer, 5, 10)
            )
        }
        "-" -> {
            val num1 = (10..30).random(random)
            val num2 = (1 until num1).random(random) // Ensure positive result
            val answer = num1 - num2
            MathQuestion(
                question = "$num1 - $num2",
                level = QuizDifficulty.BEGINNER,
                answer = answer,
                wrongAnswers = generateSmartWrongAnswers(answer, 5, 10)
            )
        }
        else -> generateBeginnerQuestion() // Fallback
    }
}

private fun generateIntermediateQuestion(): MathQuestion {
    val operation = listOf("+", "-", "×", "÷").random(random)

    return when (operation) {
        "+", "-" -> generateBeginnerQuestion().copy(level = QuizDifficulty.INTERMEDIATE)
        "×" -> {
            val num1 = (2..12).random(random) // Multiplication tables
            val num2 = (2..12).random(random)
            val answer = num1 * num2
            MathQuestion(
                question = "$num1 × $num2",
                level = QuizDifficulty.INTERMEDIATE,
                answer = answer,
                wrongAnswers = generateSmartWrongAnswers(answer, 10, 20)
            )
        }
        "÷" -> {
            val divisor = (2..12).random(random)
            val result = (2..12).random(random)
            val dividend = divisor * result // Ensure clean division
            val answer = result
            MathQuestion(
                question = "$dividend ÷ $divisor",
                level = QuizDifficulty.INTERMEDIATE,
                answer = answer,
                wrongAnswers = generateSmartWrongAnswers(answer, 5, 15)
            )
        }
        else -> generateIntermediateQuestion()
    }
}

private fun generateAdvancedQuestion(): MathQuestion {
    val operation = listOf("×", "÷", "%", "^").random(random)

    return when (operation) {
        "×" -> {
            val num1 = (10..25).random(random)
            val num2 = (2..15).random(random)
            val answer = num1 * num2
            MathQuestion(
                question = "$num1 × $num2",
                level = QuizDifficulty.ADVANCED,
                answer = answer,
                wrongAnswers = generateSmartWrongAnswers(answer, 15, 30)
            )
        }
        "÷" -> {
            val divisor = (2..15).random(random)
            val result = (5..20).random(random)
            val dividend = divisor * result
            val answer = result
            MathQuestion(
                question = "$dividend ÷ $divisor",
                level = QuizDifficulty.ADVANCED,
                answer = answer,
                wrongAnswers = generateSmartWrongAnswers(answer, 8, 20)
            )
        }
        "%" -> {
            val num1 = (20..100).random(random)
            val num2 = (2..20).random(random)
            val answer = num1 % num2
            MathQuestion(
                question = "$num1 mod $num2",
                level = QuizDifficulty.ADVANCED,
                answer = answer,
                wrongAnswers = generateSmartWrongAnswers(answer, 3, 10)
            )
        }
        "^" -> {
            val base = (2..6).random(random)
            val exponent = (2..4).random(random)
            val answer = base.toDouble().pow(exponent).toInt()
            MathQuestion(
                question = "$base^$exponent",
                level = QuizDifficulty.ADVANCED,
                answer = answer,
                wrongAnswers = generateSmartWrongAnswers(answer, 5, 25)
            )
        }
        else -> generateAdvancedQuestion()
    }
}

private fun generateExpertQuestion(): MathQuestion {
    val operationType = (1..4).random(random)

    return when (operationType) {
        1 -> generateComplexExpression()
        2 -> generateExponentialQuestion()
        3 -> generateMixedOperation()
        4 -> generateSequentialOperation()
        else -> generateExpertQuestion()
    }
}

private fun generateComplexExpression(): MathQuestion {
    val operations = listOf("+", "-", "×")
    val num1 = (5..15).random(random)
    val num2 = (3..12).random(random)
    val num3 = (2..8).random(random)

    val op1 = operations.random(random)
    val op2 = operations.random(random)

    // Calculate answer based on operation order (left to right for simplicity)
    val intermediate = when (op1) {
        "+" -> num1 + num2
        "-" -> num1 - num2
        "×" -> num1 * num2
        else -> num1 + num2
    }

    val answer = when (op2) {
        "+" -> intermediate + num3
        "-" -> intermediate - num3
        "×" -> intermediate * num3
        else -> intermediate + num3
    }

    return MathQuestion(
        question = "($num1 $op1 $num2) $op2 $num3",
        level = QuizDifficulty.EXPERT,
        answer = answer,
        wrongAnswers = generateSmartWrongAnswers(answer, 20, 50)
    )
}

private fun generateExponentialQuestion(): MathQuestion {
    val base = (2..5).random(random)
    val exponent = (3..5).random(random)
    val answer = base.toDouble().pow(exponent).toInt()

    return MathQuestion(
        question = "$base^$exponent",
        level = QuizDifficulty.EXPERT,
        answer = answer,
        wrongAnswers = generateSmartWrongAnswers(answer, 10, 40)
    )
}

private fun generateMixedOperation(): MathQuestion {
    val dividend = (30..100).random(random)
    val divisor = (3..12).random(random)
    val multiplier = (2..8).random(random)

    val result = dividend / divisor
    val answer = result * multiplier

    return MathQuestion(
        question = "($dividend ÷ $divisor) × $multiplier",
        level = QuizDifficulty.EXPERT,
        answer = answer,
        wrongAnswers = generateSmartWrongAnswers(answer, 15, 35)
    )
}

private fun generateSequentialOperation(): MathQuestion {
    val start = (10..25).random(random)
    val operations = (2..4).random(random)
    var current = start
    var question = start.toString()

    repeat(operations) {
        val op = listOf("+", "-", "×").random(random)
        val num = when (op) {
            "×" -> (2..5).random(random) // Keep multiplication reasonable
            else -> (5..15).random(random)
        }

        question += " $op $num"
        current = when (op) {
            "+" -> current + num
            "-" -> current - num
            "×" -> current * num
            else -> current + num
        }
    }

    return MathQuestion(
        question = question,
        level = QuizDifficulty.EXPERT,
        answer = current,
        wrongAnswers = generateSmartWrongAnswers(current, 25, 60)
    )
}

/**
 * Generates mathematically plausible wrong answers for a given correct answer.
 *
 * The strategy mixes:
 * - Small off-by-one and off-by-two errors
 * - Common calculation mistakes (doubling/halving, +/- 10)
 * - Randomized offsets with varying magnitudes
 *
 * All returned values are non-negative, distinct from the correct answer, and
 * the final selection is shuffled and trimmed to three items.
 *
 * @param correctAnswer The correct result to generate distractors for.
 * @param baseRange The base range used for small/medium offset generation.
 * @param maxOffset The maximum absolute offset considered for large errors.
 * @return A sorted list of three plausible wrong answers.
 */
private fun generateSmartWrongAnswers(
    correctAnswer: Int,
    baseRange: Int,
    maxOffset: Int
): List<Int> {
    val wrongAnswers = mutableSetOf<Int>()

    // Strategy 1: Off-by-one errors
    wrongAnswers.addAll(listOf(
        correctAnswer + 1,
        correctAnswer - 1,
        correctAnswer + 2,
        correctAnswer - 2
    ))

    // Strategy 2: Common calculation mistakes
    val commonMistakes = listOf(
        correctAnswer * 2,        // Doubling error
        correctAnswer / 2,        // Halving error
        correctAnswer + 10,       // Rounding error
        correctAnswer - 10
    )
    wrongAnswers.addAll(commonMistakes)

    // Strategy 3: Random offsets with decreasing probability
    while (wrongAnswers.size < 6) { // Generate more than needed
        val offset = when ((1..6).random(random)) {
            1, 2 -> (1..baseRange).random(random) // Small offset
            3, 4 -> (baseRange..maxOffset / 2).random(random) // Medium offset
            else -> (maxOffset / 2..maxOffset).random(random) // Large offset
        }

        val wrongAnswer = if (random.nextBoolean()) {
            correctAnswer + offset
        } else {
            correctAnswer - offset
        }

        if (wrongAnswer != correctAnswer && wrongAnswer >= 0) {
            wrongAnswers.add(wrongAnswer)
        }
    }

    // Strategy 4: Ensure variety in wrong answers
    return wrongAnswers
        .filter { it != correctAnswer && it >= 0 }
        .shuffled(random)
        .take(3)
        .sorted()
}