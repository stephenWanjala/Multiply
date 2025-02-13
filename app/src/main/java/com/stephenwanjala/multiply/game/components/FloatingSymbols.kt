package com.stephenwanjala.multiply.game.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp
import com.stephenwanjala.multiply.game.utlis.randomOffset
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun FloatingSymbols() {
    val symbols = listOf("+", "-", "×", "÷", "=")
    Box(modifier = Modifier.fillMaxSize()) {
        repeat(20) {
            var position by remember { mutableStateOf(randomOffset()) }
            val symbol = remember { symbols.random() }
            val rotation by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            Text(
                text = symbol,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                fontSize = 24.sp,
                modifier = Modifier.graphicsLayer {
                    translationX = position.x
                    translationY = position.y
                    rotationZ = rotation
                }
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