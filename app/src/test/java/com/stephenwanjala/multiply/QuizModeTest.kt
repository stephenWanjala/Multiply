package com.stephenwanjala.multiply

import com.stephenwanjala.multiply.game.feat_quizmode.generateQuestion
import com.stephenwanjala.multiply.game.feat_quizmode.generateQuestions
import com.stephenwanjala.multiply.game.feat_quizmode.generateWrongAnswers
import com.stephenwanjala.multiply.game.models.QuizDifficulty
import org.junit.Test
import kotlin.math.pow
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class QuizModeTest {

    @Test
    fun `test generateQuestions returns correct number of questions for each difficulty`() {
        QuizDifficulty.entries.forEach { difficulty ->
            val questions = generateQuestions(difficulty)
            assertEquals(difficulty.questionCount, questions.size)
        }
    }

    @Test
    fun `test generateQuestion for BEGINNER difficulty`() {
        val question = generateQuestion(QuizDifficulty.BEGINNER)

        // Verify question format
        val parts = question.question.split(" ")
        assertEquals(3, parts.size)

        // Verify operation is either + or -
        assertTrue(parts[1] in listOf("+", "-"))

        // Verify numbers are within range
        val num1 = parts[0].toInt()
        val num2 = parts[2].toInt()
        assertTrue(num1 in QuizDifficulty.BEGINNER.numberRange)
        assertTrue(num2 in QuizDifficulty.BEGINNER.numberRange)

        // Verify answer is correct
        val expectedAnswer =
            when (parts[1]) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                else -> fail("Invalid operation")
            }
        assertEquals(expectedAnswer, question.answer)
    }

    @Test
    fun `test generateQuestion for INTERMEDIATE difficulty`() {
        val question = generateQuestion(QuizDifficulty.INTERMEDIATE)

        val parts = question.question.split(" ")
        assertTrue(parts[1] in listOf("+", "-", "*", "/"))

        val num1 = parts[0].toInt()
        val num2 = parts[2].toInt()
        assertTrue(num1 in QuizDifficulty.INTERMEDIATE.numberRange)
        assertTrue(num2 in 2..20)
    }

    @Test
    fun `test generateQuestion for ADVANCED difficulty`() {
        val question = generateQuestion(QuizDifficulty.ADVANCED)

        val parts = question.question.split(" ")
        assertTrue(parts[1] in listOf("+", "-", "*", "/", "%", "^"))

        val num1 = parts[0].toInt()
        val num2 = parts[2].toInt()
        assertTrue(num1 in QuizDifficulty.ADVANCED.numberRange)
        assertTrue(num2 in 2..10)
    }

    @Test
    fun `test generateQuestion for EXPERT difficulty`() {
        val question = generateQuestion(QuizDifficulty.EXPERT)

        val parts = question.question.split(" ")
        assertTrue(parts[1] in listOf("+", "-", "*", "/", "%", "^", "()", "exp"))

        val num1 = parts[0].toInt()
        val num2 = parts[2].toInt()
        assertTrue(num1 in QuizDifficulty.EXPERT.numberRange)
        assertTrue(num2 in 2..15)
    }

    @Test
    fun `test division by zero handling`() {
        // Force division by zero scenario
        val question = generateQuestion(QuizDifficulty.INTERMEDIATE)
        if (question.question.contains("/")) {
            val parts = question.question.split(" ")
            val num1 = parts[0].toInt()
            val num2 = parts[2].toInt()
            if (num2 == 0) {
                assertEquals(num1, question.answer)
            }
        }
    }

    @Test
    fun `test modulo by zero handling`() {
        val question = generateQuestion(QuizDifficulty.ADVANCED)
        if (question.question.contains("%")) {
            val parts = question.question.split(" ")
            val num2 = parts[2].toInt()
            if (num2 == 0) {
                assertEquals(0, question.answer)
            }
        }
    }

    @Test
    fun `test generateWrongAnswers returns correct number of answers`() {
        QuizDifficulty.entries.forEach { difficulty ->
            val wrongAnswers = generateWrongAnswers(42, difficulty)
            assertEquals(3, wrongAnswers.size)
        }
    }

    @Test
    fun `test wrong answers are unique`() {
        QuizDifficulty.entries.forEach { difficulty ->
            val wrongAnswers = generateWrongAnswers(42, difficulty)
            assertEquals(wrongAnswers.size, wrongAnswers.distinct().size)
        }
    }

    @Test
    fun `test wrong answers are different from correct answer`() {
        val correctAnswer = 42
        QuizDifficulty.entries.forEach { difficulty ->
            val wrongAnswers = generateWrongAnswers(correctAnswer, difficulty)
            assertFalse(wrongAnswers.contains(correctAnswer))
        }
    }

    @Test
    fun `test wrong answers are non-negative`() {
        QuizDifficulty.entries.forEach { difficulty ->
            val wrongAnswers = generateWrongAnswers(42, difficulty)
            assertTrue(wrongAnswers.all { it >= 0 })
        }
    }

    @Test
    fun `test exponentiation calculation`() {
        val question = generateQuestion(QuizDifficulty.ADVANCED)
        if (question.question.contains("^")) {
            val parts = question.question.split(" ")
            val num1 = parts[0].toInt()
            val num2 = parts[2].toInt()
            val expectedAnswer = num1.toDouble().pow(num2.toDouble()).toInt()
            assertEquals(expectedAnswer, question.answer)
        }
    }

    @Test
    fun `test parentheses operation calculation`() {
        val question = generateQuestion(QuizDifficulty.EXPERT)
        if (question.question.contains("()")) {
            val parts = question.question.split(" ")
            val num1 = parts[0].toInt()
            val num2 = parts[2].toInt()
            assertTrue(question.answer >= 0)
            // Since num3 is random, we can only verify the answer is within reasonable bounds
            assertTrue(question.answer <= (num1 + num2) * 10)
        }
    }

    @Test
    fun `test exponential operation calculation`() {
        val question = generateQuestion(QuizDifficulty.EXPERT)
        if (question.question.contains("exp")) {
            val parts = question.question.split(" ")
            val num1 = parts[0].toInt()
            assertTrue(question.answer >= 0)
            // Verify answer is within reasonable bounds for exponential operation
            assertTrue(question.answer <= 5.0.pow(num1.toDouble()).toInt())
        }
    }

    @Test
    fun `test wrong answers range for each difficulty`() {
        val testAnswer = 50
        val difficultyRanges =
            mapOf(
                QuizDifficulty.BEGINNER to (-10..10),
                QuizDifficulty.INTERMEDIATE to (-20..20),
                QuizDifficulty.ADVANCED to (-50..50),
                QuizDifficulty.EXPERT to (-100..100))

        difficultyRanges.forEach { (difficulty, range) ->
            val wrongAnswers = generateWrongAnswers(testAnswer, difficulty)
            wrongAnswers.forEach { wrongAnswer ->
                assertTrue(wrongAnswer >= 0)
                assertTrue(wrongAnswer - testAnswer in range)
            }
        }
    }

    @Test
    fun `test generateWrongAnswers with negative correct answer`() {
        val correctAnswer = -10
        val difficulty = QuizDifficulty.BEGINNER
        val wrongAnswers = generateWrongAnswers(correctAnswer, difficulty)
        assertTrue(wrongAnswers.isEmpty())
    }

    // New Tests Below

    @Test
    fun `test generateQuestion returns MathQuestion with correct level`() {
        QuizDifficulty.entries.forEach { difficulty ->
            val question = generateQuestion(difficulty)
            assertEquals(difficulty, question.level)
        }
    }


    @Test
    fun `test subtraction always results in non-negative answer for BEGINNER`() {
        repeat(50) { // Run multiple times to increase confidence
            val question = generateQuestion(QuizDifficulty.BEGINNER)
            if (question.question.contains("-")) {
                assertTrue(question.answer >= 0, "Subtraction should result in a non-negative answer")
            }
        }
    }

    @Test
    fun `test subtraction always results in non-negative answer for INTERMEDIATE`() {
        repeat(50) { // Run multiple times to increase confidence
            val question = generateQuestion(QuizDifficulty.INTERMEDIATE)
            if (question.question.contains("-")) {
                assertTrue(question.answer >= 0, "Subtraction should result in a non-negative answer")
            }
        }
    }

    @Test
    fun `test subtraction always results in non-negative answer for ADVANCED`() {
        repeat(50) { // Run multiple times to increase confidence
            val question = generateQuestion(QuizDifficulty.ADVANCED)
            if (question.question.contains("-")) {
                assertTrue(question.answer >= 0, "Subtraction should result in a non-negative answer")
            }
        }
    }

    @Test
    fun `test subtraction always results in non-negative answer for EXPERT`() {
        repeat(50) { // Run multiple times to increase confidence
            val question = generateQuestion(QuizDifficulty.EXPERT)
            if (question.question.contains("-")) {
                assertTrue(question.answer >= 0, "Subtraction should result in a non-negative answer")
            }
        }
    }

    @Test
    fun `test subtraction question has numbers swapped when necessary`() {
        repeat(20) {
            val question = generateQuestion(QuizDifficulty.BEGINNER)
            if (question.question.contains("-")) {
                val parts = question.question.split(" ")
                val num1 = parts[0].toInt()
                val num2 = parts[2].toInt()
                // Check if the numbers were swapped (num1 should be >= num2)
                assertTrue(num1 >= num2, "Numbers should be swapped to ensure non-negative result")
            }
        }
    }

    @Test
    fun `test generateWrongAnswers generates different sets of wrong answers on multiple calls`() {
        val correctAnswer = 42
        val difficulty = QuizDifficulty.INTERMEDIATE
        val wrongAnswers1 = generateWrongAnswers(correctAnswer, difficulty)
        val wrongAnswers2 = generateWrongAnswers(correctAnswer, difficulty)

        // Check if the sets are different (not guaranteed, but highly probable)
        assertNotEquals(wrongAnswers1, wrongAnswers2)
    }

    @Test
    fun `test generateWrongAnswers handles correctAnswer close to zero`() {
        val correctAnswer = 1
        val difficulty = QuizDifficulty.BEGINNER
        val wrongAnswers = generateWrongAnswers(correctAnswer, difficulty)

        assertTrue(wrongAnswers.isNotEmpty())
        assertFalse(wrongAnswers.contains(correctAnswer))
        assertTrue(wrongAnswers.all { it >= 0 })
    }

    @Test
    fun `test generateWrongAnswers handles correctAnswer within the range`() {
        val correctAnswer = 5
        val difficulty = QuizDifficulty.BEGINNER
        val wrongAnswers = generateWrongAnswers(correctAnswer, difficulty)

        assertTrue(wrongAnswers.isNotEmpty())
        assertFalse(wrongAnswers.contains(correctAnswer))
        assertTrue(wrongAnswers.all { it >= 0 })
    }

    @Test
    fun `test generateWrongAnswers generates numbers within reasonable bounds`() {
        val correctAnswer = 50
        QuizDifficulty.entries.forEach { difficulty ->
            val wrongAnswers = generateWrongAnswers(correctAnswer, difficulty)
            wrongAnswers.forEach { wrongAnswer ->
                when (difficulty) {
                    QuizDifficulty.BEGINNER -> assertTrue(wrongAnswer in 0..60)
                    QuizDifficulty.INTERMEDIATE -> assertTrue(wrongAnswer in 0..70)
                    QuizDifficulty.ADVANCED -> assertTrue(wrongAnswer in 0..100)
                    QuizDifficulty.EXPERT -> assertTrue(wrongAnswer in 0..150)
                }
            }
        }
    }


    @Test
    fun `test generateQuestions generates questions with unique answers`() {
        val difficulty = QuizDifficulty.INTERMEDIATE
        val questions = generateQuestions(difficulty)
        val answers = questions.map { it.answer }
        // Check if most answers are unique (allowing for some duplicates due to randomness)
        assertTrue(answers.distinct().size >= questions.size * 0.75)
    }
}
