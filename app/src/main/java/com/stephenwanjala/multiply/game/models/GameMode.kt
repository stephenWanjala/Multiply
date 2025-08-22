package com.stephenwanjala.multiply.game.models


import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver

sealed class GameMode {
    data class BubbleMathBlitz(val difficulty: BubbleMathDifficulty) : GameMode()
    data class QuizGenius(val difficulty: QuizDifficulty) : GameMode()
}


val GameModeSaver: Saver<GameMode?, Any> = listSaver(
    save = { mode ->
        when (mode) {
            is GameMode.BubbleMathBlitz -> listOf("Bubble", mode.difficulty.name)
            is GameMode.QuizGenius -> listOf("Quiz", mode.difficulty.name)
            null -> emptyList()
        }
    },
    restore = { list ->
        if (list.isEmpty()) return@listSaver null
        when (list[0]) {
            "Bubble" -> GameMode.BubbleMathBlitz(
                BubbleMathDifficulty.valueOf(list[1])
            )

            "Quiz" -> GameMode.QuizGenius(
                QuizDifficulty.valueOf(list[1])
            )

            else -> null
        }
    }
)


val ModeDifficultySaver: Saver<ModeDifficulty?, Any> = listSaver(
    save = { diff ->
        when (diff) {
            is ModeDifficulty.Bubble -> listOf("Bubble", diff.difficulty.name)
            is ModeDifficulty.Quiz -> listOf("Quiz", diff.difficulty.name)
            null -> emptyList()
        }
    },
    restore = { list ->
        if (list.isEmpty()) return@listSaver null
        when (list[0]) {
            "Bubble" -> ModeDifficulty.Bubble(
                BubbleMathDifficulty.valueOf(list[1])
            )

            "Quiz" -> ModeDifficulty.Quiz(
                QuizDifficulty.valueOf(list[1])
            )

            else -> null
        }
    }
)
