package com.stephenwanjala.multiply.game.utlis

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import kotlin.random.Random

class Particle {
    var x = Animatable(Random.nextFloat() * 1000)
    var y = Animatable(-Random.nextFloat() * 500 - 100f)
    private val speedX = Random.nextFloat() * 4 - 2
    private val speedY = Random.nextFloat() * 2 + 2
    var size = Random.nextFloat() * 15 + 5
    var color = Color(
        Random.nextFloat(), Random.nextFloat(), Random.nextFloat(),
        Random.nextFloat() * 0.5f + 0.5f
    )

    suspend fun update(canvasSize: IntSize) {
        x.snapTo(x.value + speedX)
        y.snapTo(y.value + speedY)

        if (y.value > canvasSize.height) {
            y.snapTo(-Random.nextFloat() * 500 - 100f)
            x.snapTo(Random.nextFloat() * canvasSize.width)
        }
    }
}