package com.github.stephenwanjala.multiply.presentation

sealed interface GameEvent {
    data object CorrectAnswer : GameEvent
    data object IncorrectAnswer : GameEvent
    data object TimesUp : GameEvent
    data object GameOver : GameEvent
}
