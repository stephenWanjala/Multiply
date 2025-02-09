package com.stephenwanjala.multiply.game.utlis

import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

fun randomOffset(): Offset {
    return Offset(
        x = Random.nextFloat() * 1000,
        y = Random.nextFloat() * 2000
    )
}