package com.github.stephenwanjala.multiply.presentation

sealed interface GameEvent {
    data object TimesUp : GameEvent
    data object GameOver : GameEvent
    data object StartGame : GameEvent
    data class Answer(val mathProblem:String, val answer: Int) : GameEvent
}
