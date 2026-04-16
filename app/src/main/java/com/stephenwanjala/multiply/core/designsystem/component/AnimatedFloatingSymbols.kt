package com.stephenwanjala.multiply.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun AnimatedFloatingSymbolsBackground(
    modifier: Modifier = Modifier,
    symbolCount: Int = 15,
    symbols: List<String> = listOf("+", "-", "x", "/", "=", "*", "1", "2", "3"),
    alpha: Float = 0.6f
) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
    )

    val screenWidth = LocalWindowInfo.current.containerSize.width.toFloat()
    val screenHeight = LocalWindowInfo.current.containerSize.height.toFloat()
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alpha }
    ) {
        repeat(symbolCount) {
            val symbol = remember { symbols.random() }
            val color = remember { colors.random() }
            val initialOffset = remember {
                Offset(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight
                )
            }
            val animatableOffsetX = remember { Animatable(initialOffset.x) }
            val animatableOffsetY = remember { Animatable(initialOffset.y) }
            val animatableScale = remember { Animatable(Random.nextFloat() * 0.5f + 0.5f) }
            val animatableRotation = remember { Animatable(Random.nextFloat() * 360f) }
            val animatableAlpha = remember { Animatable(0.3f + Random.nextFloat() * 0.4f) }

            LaunchedEffect(Unit) {
                scope.launch {
                    val targetX = if (Random.nextBoolean()) -50f else screenWidth + 50f
                    animatableOffsetX.animateTo(
                        targetValue = targetX,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(8000, 15000),
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Restart
                        )
                    )
                }

                scope.launch {
                    val targetY = initialOffset.y + Random.nextFloat() * 100 - 50
                    animatableOffsetY.animateTo(
                        targetValue = targetY,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(7000, 12000),
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }

                scope.launch {
                    animatableScale.animateTo(
                        targetValue = animatableScale.value * 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(1500, 2500),
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }

                scope.launch {
                    animatableRotation.animateTo(
                        targetValue = animatableRotation.value + 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(10000, 20000),
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Restart
                        )
                    )
                }

                scope.launch {
                    animatableAlpha.animateTo(
                        targetValue = animatableAlpha.value * 0.8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(2000, 4000),
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }
            }

            Text(
                text = symbol,
                color = color,
                fontSize = (28.sp * animatableScale.value),
                modifier = Modifier
                    .graphicsLayer {
                        translationX = animatableOffsetX.value
                        translationY = animatableOffsetY.value
                        this.alpha = animatableAlpha.value
                        rotationZ = animatableRotation.value
                        scaleX = animatableScale.value
                        scaleY = animatableScale.value
                    }
            )
        }
    }
}
