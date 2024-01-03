package com.github.stephenwanjala.multiply.data.repository

import com.github.stephenwanjala.multiply.domain.model.Game
import com.github.stephenwanjala.multiply.domain.model.repository.GameRepository

class GameRepositoryImpl: GameRepository {
    private var currentGame: Game? = null
    override fun startNewGame(): Game {
        val mathProblem = generateMathProblem()
        currentGame = Game(lives = 3, points = 0, mathProblem = mathProblem)
        return currentGame!!
    }

    override fun checkAnswer(mathProblem: String, userAnswer: Int): Game {
        val correctAnswer = calculateCorrectAnswer(mathProblem)
        val newPoints = if (userAnswer == correctAnswer) 1 else 0
        val newGame = currentGame?.copy(
            points = currentGame!!.points + newPoints,
            lives = if (userAnswer != correctAnswer) currentGame!!.lives - 1 else currentGame!!.lives,
            correctAnswer = correctAnswer.toString(),
            options = generateOptions(correctAnswer)
        )
        currentGame = newGame
        return newGame ?: throw IllegalStateException("Game not started.")
    }

    private fun generateMathProblem(): String {
        // Logic to generate a new math problem (e.g., multiplication of two single-digit numbers)
        return "${(1..10).random()} * ${(1..10).random()}"
    }

    private fun calculateCorrectAnswer(mathProblem: String): Int {
        // Extract the two numbers from the math problem and calculate the correct answer
        val (operand1, operand2) = mathProblem.split(" * ")
        return operand1.toInt() * operand2.toInt()
    }

    override fun generateOptions(correctAnswer: Int): List<Int> {
        val options = mutableListOf(correctAnswer)

        // Generate three incorrect answers
        repeat(3) {
            var incorrectAnswer: Int
            do {
                // Generate a random incorrect answer
                incorrectAnswer = generateRandomAnswer()
            } while (options.contains(incorrectAnswer)) // Ensure uniqueness
            options.add(incorrectAnswer)
        }

        // Shuffle the options to randomize their order
        options.shuffle()

        return options
    }

    private fun generateRandomAnswer(): Int {
        // Logic to generate a random incorrect answer (e.g., within a specific range)
        return (1..100).random()
    }
}