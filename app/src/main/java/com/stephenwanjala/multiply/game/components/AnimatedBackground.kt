package com.stephenwanjala.multiply.game.components

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.stephenwanjala.multiply.game.utlis.randomOffset
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun AnimatedBackground() {
    val symbols = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-", "×", "÷")
    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Magenta, Color.Cyan, Color.Yellow)

    Box(modifier = Modifier.fillMaxSize()) {
        repeat(30) {
            var position by remember { mutableStateOf(randomOffset()) }
            val symbol = remember { symbols.random() }
            val color = remember { colors.random() }

            Text(
                text = symbol,
                color = color.copy(alpha = 0.6f),
                fontSize = 24.sp,
                modifier = Modifier
                    .graphicsLayer {
                        translationX = position.x
                        translationY = position.y
                    }
//                    .offset { IntOffset(position.x.toInt(), position.y.toInt()) }
            )

            LaunchedEffect(Unit) {
                while (true) {
                    delay(Random.nextLong(3000, 5000))
                    position = randomOffset()
                }
            }
        }
    }
}

@Composable
fun Modifier.animatedBackground(): Modifier = composed {
    val symbols = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-", "×", "÷")
    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Magenta, Color.Cyan, Color.Yellow)

    var size by remember { mutableStateOf(Size.Zero) }
    var particles by remember { mutableStateOf(emptyList<Particle>()) }

    LaunchedEffect(size) {
        if (size == Size.Zero) return@LaunchedEffect

        // Initial particles
        particles = List(30) {
            Particle(
                position = randomOffset(size),
                symbol = symbols.random(),
                color = colors.random()
            )
        }

        // Animation loop
        while (true) {
            delay(1000) // Update every second
            particles = List(30) {
                Particle(
                    position = randomOffset(size),
                    symbol = symbols.random(),
                    color = colors.random()
                )
            }
        }
    }

    this
        .onSizeChanged { newSize -> size = newSize.toSize() }
        .drawBehind {
            particles.forEach { particle ->
                drawContext.canvas.nativeCanvas.drawText(
                    particle.symbol,
                    particle.position.x,
                    particle.position.y,
                    Paint().apply {
                        this.color = particle.color.copy(alpha = 0.6f).toArgb()
                        textSize = 40f
                    }
                )
            }
        }
}

private data class Particle(
    val position: Offset,
    val symbol: String,
    val color: Color
)

private fun randomOffset(size: Size): Offset {
    return Offset(
        x = Random.nextFloat() * size.width,
        y = Random.nextFloat() * size.height
    )
}

