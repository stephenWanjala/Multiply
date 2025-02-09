package com.stephenwanjala.multiply.game.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp
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