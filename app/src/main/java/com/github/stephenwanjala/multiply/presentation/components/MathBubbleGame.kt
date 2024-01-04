package com.github.stephenwanjala.multiply.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.stephenwanjala.multiply.presentation.generateMathProblem
import kotlinx.coroutines.launch

@Composable
fun MathBubbleGame() {
    var animationState by remember { mutableStateOf(MathBubbleGameState.INITIAL) }
    val coroutineScope = rememberCoroutineScope()

    val animatedValue = remember { Animatable(0f) }
    val gradientTransition = rememberInfiniteTransition(label = "gradientTransition")
    val gradientOffset by gradientTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )


    var mathProblem by remember { mutableStateOf(generateMathProblem()) }
    val fontResolver = LocalFontFamilyResolver.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val textMeasurer = TextMeasurer(
        fallbackFontFamilyResolver = fontResolver,
        fallbackDensity = density,
        fallbackLayoutDirection = layoutDirection
    )

    LaunchedEffect(key1 = animationState) {
        when (animationState) {
            MathBubbleGameState.INITIAL -> {
                coroutineScope.launch {
                    animatedValue.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = 10000, // Adjust the duration as needed
                            easing = LinearEasing
                        )
                    )
                    // Update state when animation reaches the bottom
                    animationState = MathBubbleGameState.FINISHED
                }
            }

            MathBubbleGameState.FINISHED -> {
                // Animation has reached the bottom, reset the state for the next animation
                animatedValue.snapTo(0f)
                animationState = MathBubbleGameState.INITIAL
                // Generate a new math problem
                mathProblem = generateMathProblem()

            }
        }
    }

    Box(
        modifier = Modifier.size(210.dp),
        contentAlignment = Alignment.Center
    ) {
        // Use animated value to move the bubble vertically

        val textLayoutResult = textMeasurer.measure(
            text = AnnotatedString(mathProblem),
            style = MaterialTheme.typography.displayMedium,

            )
        val paint = Paint().apply {
            color = MaterialTheme.colorScheme.onPrimaryContainer
            isAntiAlias = true
        }
        Canvas(
            modifier = Modifier
                .size(210.dp)
                .offset { IntOffset(0, (animatedValue.value * 800).toInt()) }
                .padding(16.dp)
        ) {

            drawIntoCanvas { canvas ->
                val circleRadius = size.width / 2
                val circleCenter = Offset(circleRadius, circleRadius)

                // Draw the circular background
                canvas.drawCircle(circleCenter, circleRadius, paint)

                // Draw the text at the center

                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        x = circleCenter.x - textLayoutResult.size.width / 2,
                        y = circleCenter.y - textLayoutResult.size.height / 2
                    ),
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF50C6E5),
                            Color(0xFF800080)
                        ),
                        startY = gradientOffset
                    )
                )
            }
        }
    }
}

enum class MathBubbleGameState {
    INITIAL,
    FINISHED
}