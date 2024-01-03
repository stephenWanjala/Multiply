package com.github.stephenwanjala.multiply.domain.model

data class Game(
    val lives: Int,
    val points: Int,
    val mathProblem: String,
    val options: List<Int> = emptyList(),
    val correctAnswer: String = ""
)