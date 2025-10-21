package com.stephenwanjala.multiply

import com.stephenwanjala.multiply.game.feat_quizmode.generateQuestion
import com.stephenwanjala.multiply.game.feat_quizmode.generateQuestions
import com.stephenwanjala.multiply.game.models.QuizDifficulty
import org.junit.Test
import kotlin.math.pow
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class QuizModeTest {

    // Helper functions to evaluate operations used by generator
    private fun applyOp(a: Int, op: String, b: Int): Int = when (op) {
        "+" -> a + b
        "-" -> a - b
        "×" -> a * b
        "÷" -> a / b
        else -> fail("Unsupported op: $op")
    }

    private fun evalLeftToRight(tokens: List<String>): Int {
        // tokens like [n1, op1, n2, op2, n3, ...]
        var acc = tokens[0].toInt()
        var i = 1
        while (i < tokens.size) {
            val op = tokens[i]
            val b = tokens[i + 1].toInt()
            acc = applyOp(acc, op, b)
            i += 2
        }
        return acc
    }

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

        val parts = question.question.split(" ")
        assertEquals(3, parts.size)
        assertTrue(parts[1] in listOf("+", "-"))

        val num1 = parts[0].toInt()
        val num2 = parts[2].toInt()
        assertTrue(num1 in QuizDifficulty.BEGINNER.numberRange)
        assertTrue(num2 in QuizDifficulty.BEGINNER.numberRange)

        val expectedAnswer = when (parts[1]) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            else -> fail("Invalid operation")
        }
        assertEquals(expectedAnswer, question.answer)

        // Wrong answers sanity
        assertEquals(3, question.wrongAnswers.size)
        assertEquals(question.wrongAnswers.distinct().size, question.wrongAnswers.size)
        assertTrue(question.wrongAnswers.none { it == question.answer })
        assertTrue(question.wrongAnswers.all { it >= 0 })
    }

    @Test
    fun `test generateQuestion for INTERMEDIATE difficulty`() {
        val question = generateQuestion(QuizDifficulty.INTERMEDIATE)

        val parts = question.question.split(" ")
        assertEquals(3, parts.size)
        assertTrue(parts[1] in listOf("+", "-", "×", "÷"))

        val num1 = parts[0].toInt()
        val num2 = parts[2].toInt()
        val expected = when (parts[1]) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "×" -> num1 * num2
            "÷" -> num1 / num2
            else -> fail("Invalid op")
        }
        assertEquals(expected, question.answer)

        assertEquals(3, question.wrongAnswers.size)
        assertEquals(question.wrongAnswers.distinct().size, question.wrongAnswers.size)
        assertTrue(question.wrongAnswers.none { it == question.answer })
        assertTrue(question.wrongAnswers.all { it >= 0 })
    }

    @Test
    fun `test generateQuestion for ADVANCED difficulty`() {
        val question = generateQuestion(QuizDifficulty.ADVANCED)
        val q = question.question

        when {
            q.contains(" mod ") -> {
                val parts = q.split(" ")
                val a = parts[0].toInt()
                val b = parts[2].toInt()
                assertEquals(a % b, question.answer)
            }
            q.contains(" × ") -> {
                val parts = q.split(" ")
                val a = parts[0].toInt(); val b = parts[2].toInt()
                assertEquals(a * b, question.answer)
            }
            q.contains(" ÷ ") -> {
                val parts = q.split(" ")
                val a = parts[0].toInt(); val b = parts[2].toInt()
                assertEquals(a / b, question.answer)
            }
            q.contains("^") -> {
                val (baseStr, expStr) = q.split("^")
                val base = baseStr.toInt(); val exp = expStr.toInt()
                assertEquals(base.toDouble().pow(exp).toInt(), question.answer)
            }
            else -> fail("Unexpected ADVANCED question format: $q")
        }

        assertEquals(3, question.wrongAnswers.size)
        assertEquals(question.wrongAnswers.distinct().size, question.wrongAnswers.size)
        assertTrue(question.wrongAnswers.none { it == question.answer })
        assertTrue(question.wrongAnswers.all { it >= 0 })
    }

    @Test
    fun `test generateQuestion for EXPERT difficulty`() {
        val question = generateQuestion(QuizDifficulty.EXPERT)
        val q = question.question

        when {
            // Complex expression: (a op b) op c
            q.startsWith("(") && q.contains(") ") -> {
                val cleaned = q.replace("(", "").replace(")", "")
                val tokens = cleaned.split(" ")
                // tokens: [a, op1, b, op2, c]
                val intermediate = applyOp(tokens[0].toInt(), tokens[1], tokens[2].toInt())
                val expected = applyOp(intermediate, tokens[3], tokens[4].toInt())
                assertEquals(expected, question.answer)
            }
            // Exponential: a^b
            q.contains("^") -> {
                val (a, b) = q.split("^")
                assertEquals(a.toInt().toDouble().pow(b.toInt()).toInt(), question.answer)
            }
            // Mixed division then multiplication: (a ÷ b) × c
            q.contains(" ÷ ") && q.contains(" × ") -> {
                val cleaned = q.replace("(", "").replace(")", "")
                val tokens = cleaned.split(" ") // [a, ÷, b, ×, c]
                val first = applyOp(tokens[0].toInt(), tokens[1], tokens[2].toInt())
                val expected = applyOp(first, tokens[3], tokens[4].toInt())
                assertEquals(expected, question.answer)
            }
            // Sequential operations: start op n op n ... (ops are +, -, ×)
            else -> {
                // Expect a sequence of alternating numbers and ops
                val tokens = q.split(" ")
                // Simple sanity: odd length with numbers on even indices
                assertTrue(tokens.size >= 3 && tokens.size % 2 == 1)
                // Evaluate left-to-right according to generator's approach
                val expected = evalLeftToRight(tokens)
                assertEquals(expected, question.answer)
            }
        }

        assertEquals(3, question.wrongAnswers.size)
        assertEquals(question.wrongAnswers.distinct().size, question.wrongAnswers.size)
        assertTrue(question.wrongAnswers.none { it == question.answer })
        assertTrue(question.wrongAnswers.all { it >= 0 })
    }

    @Test
    fun `test subtraction always results in non-negative answer for BEGINNER`() {
        repeat(50) {
            val question = generateQuestion(QuizDifficulty.BEGINNER)
            if (question.question.contains("-")) {
                assertTrue(question.answer >= 0)
            }
        }
    }

    @Test
    fun `test subtraction always results in non-negative answer for INTERMEDIATE`() {
        val diff = QuizDifficulty.INTERMEDIATE
        repeat(50) {
            val q = generateQuestion(diff)
            if (q.question.contains("-")) {
                assertTrue(q.answer >= 0)
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
                assertTrue(num1 >= num2)
            }
        }
    }

    @Test
    fun `test generateQuestions generates questions with unique answers`() {
        val difficulty = QuizDifficulty.INTERMEDIATE
        val questions = generateQuestions(difficulty)
        val answers = questions.map { it.answer }
        assertTrue(answers.distinct().size >= (questions.size * 0.6).toInt())
    }
}
