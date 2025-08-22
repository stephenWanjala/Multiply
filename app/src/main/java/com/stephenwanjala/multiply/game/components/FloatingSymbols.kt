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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
private fun rememberRandomOffsetGenerator(
    screenSize: IntSize
): () -> Offset {
    return remember(screenSize) {
        {
            Offset(
                x = Random.nextFloat() * screenSize.width,
                y = Random.nextFloat() * screenSize.height
            )
        }
    }
}

private data class FloatingSymbolData(
    val id: Int,
    val symbol: String,
    var currentOffset: Offset,
    val rotationSeed: Float,
    val animationDelayMillis: Long
)

@Composable
fun FloatingSymbols() {
    val scope = rememberCoroutineScope()
    val symbols = listOf("+", "-", "×", "÷", "=")
    val numSymbols = 20

    // Measure the screen size to constrain symbol positions
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    val randomOffsetGenerator = rememberRandomOffsetGenerator(screenSize)

    // Remember the list of symbol data
    val floatingSymbols = remember(numSymbols, screenSize) {
        if (screenSize == IntSize.Zero) return@remember emptyList()

        List(numSymbols) { i ->
            FloatingSymbolData(
                id = i,
                symbol = symbols.random(),
                currentOffset = randomOffsetGenerator(),
                rotationSeed = Random.nextFloat() * 360f,
                animationDelayMillis = Random.nextLong(3000, 5000)
            )
        }
    }

    // Single infinite transition for rotation for ALL symbols
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteSymbolRotation")
    val baseRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "baseRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { screenSize = it }
    ) {
        // Update positions using a single LaunchedEffect for all symbols
        LaunchedEffect(floatingSymbols) {
            floatingSymbols.forEach { symbolData ->
                scope.launch {
                    while (true) {
                        delay(symbolData.animationDelayMillis)
                        symbolData.currentOffset = randomOffsetGenerator()
                    }
                }
            }
        }

        floatingSymbols.forEach { symbolData ->
            // Calculate individual rotation based on baseRotation and seed
            val individualRotation = (baseRotation + symbolData.rotationSeed) % 360f

            Text(
                text = symbolData.symbol,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                fontSize = 24.sp,
                modifier = Modifier.graphicsLayer {
                    translationX = symbolData.currentOffset.x
                    translationY = symbolData.currentOffset.y
                    rotationZ = individualRotation
                }
            )
        }
    }
}